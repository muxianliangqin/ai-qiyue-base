package com.qiyue.distributed.lock;

import com.qiyue.infrastructure.utils.RetryUtil;
import com.qiyue.infrastructure.utils.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Slf4j
@Component
public class DistributedLockUtil {
    private static StringRedisTemplate redisTemplate;

    @PostConstruct
    public synchronized void init(StringRedisTemplate redisTemplate) {
        DistributedLockUtil.redisTemplate = redisTemplate;
    }

    /**
     * 加锁失败的key
     */
    private static final Map<String, AtomicInteger> LOCK_FAIL = new ConcurrentHashMap<>(32);
    public static final int FAILED_TIMES_MAX = 3;
    public static final long KEY_EXPIRES_DEFAULT = 1000L;
    /**
     * <pre>
     * 数据结构
     * {
     *     "key":{
     *         "tag": "",
     *         "value": 1
     *     }
     * }
     * key：加锁的key，默认有效期 24h
     * tag：key的标签，为了实现可重入功能。tag默认为当前线程的线程ID
     * value：加锁次数，默认 1。每重复加锁一次，value+1，每解锁一次，value-1。value==0时，删除key
     * </pre>
     */
    public static final String LOCK_SCRIPT = "" +
            "local key = KEYS[1]\n" +
            "local tag = ARGV[1]\n" +
            "if redis.call('EXISTS', key) == 0 then\n" +
            "    redis.call('HSET', key, 'tag', tag)\n" +
            "    redis.call('HSET', key, 'value', 1)\n" +
            "    redis.call('EXPIRE', key, 86400000)\n" +
            "    return 1\n" +
            "else\n" +
            "    if redis.call('HEXISTS', key, 'tag') == 0 then\n" +
            "        redis.call('HSET', key, 'tag', tag)\n" +
            "    end\n" +
            "    if redis.call('HEXISTS', key, 'value') == 0 then\n" +
            "        redis.call('HSET', key, 'value', 1)\n" +
            "    end\n" +
            "    if redis.call('HGET', key, 'tag') == tag then\n" +
            "        return redis.call('HINCRBY', key, 'value', 1)\n" +
            "    else\n" +
            "        return -1\n" +
            "    end\n" +
            "end";

    public static boolean lock(String key, String tag, RetryTemplate retryTemplate) {
        return retryTemplate.execute((RetryCallback<Boolean, IllegalStateException>) context -> {
            byte[][] keysAndArgs = new byte[2][];
            keysAndArgs[0] = key.getBytes();
            keysAndArgs[1] = tag.getBytes();
            Long lockedTimes = redisTemplate.execute((RedisConnection connection) ->
                    connection.eval(LOCK_SCRIPT.getBytes(StandardCharsets.UTF_8), ReturnType.INTEGER, 1, keysAndArgs));
            if (Objects.isNull(lockedTimes) || lockedTimes < 0) {
                throw new IllegalStateException(Strings.format("The lock status is incorrect, key :{}, tag:{}", key, tag));
            }
            LOCK_FAIL.remove(key);
            return true;
        }, context -> {
            log.error("distributed lock locked failed, key:{}, tag:{}", key, tag);
            LOCK_FAIL.putIfAbsent(key, new AtomicInteger(1));
            AtomicInteger atomicInteger = LOCK_FAIL.get(key);
            Long expire = redisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
            if (atomicInteger.incrementAndGet() > FAILED_TIMES_MAX && Objects.nonNull(expire) && expire == -1) {
                redisTemplate.expire(key, KEY_EXPIRES_DEFAULT, TimeUnit.MILLISECONDS);
                LOCK_FAIL.remove(key);
            }
            return false;
        });
    }

    public static boolean lock(String key) {
        String tag = String.valueOf(Thread.currentThread().getId());
        return lock(key, tag, RetryUtil.defaultFixRetry());
    }

    public static final String UNLOCK_SCRIPT = "" +
            "local key = KEYS[1]\n" +
            "local tag = ARGV[1]\n" +
            "if redis.call('EXISTS', key) == 0 then\n" +
            "    return -1\n" +
            "elseif redis.call('HEXISTS', key, 'tag') == 0 then\n" +
            "     return -1\n" +
            "elseif redis.call('HEXISTS', key, 'value') == 0 then\n" +
            "    return -1\n" +
            "elseif redis.call('HGET', key, 'tag') != tag then\n" +
            "    return -1\n" +
            "else\n" +
            "    value = redis.call('HINCRBY', key, 'value', -1) \n" +
            "    if value <= 0 then\n" +
            "        redis.call('DEL', key)\n" +
            "        return 0\n" +
            "    else\n" +
            "        return value\n" +
            "    end\n" +
            "end";

    public static boolean unlock(String key, String tag, RetryTemplate retryTemplate) {
        return retryTemplate.execute((RetryCallback<Boolean, IllegalStateException>) context -> {
            try {
                byte[][] keysAndArgs = new byte[2][];
                keysAndArgs[0] = key.getBytes();
                keysAndArgs[1] = tag.getBytes();
                redisTemplate.execute((RedisConnection connection) ->
                        connection.eval(UNLOCK_SCRIPT.getBytes(StandardCharsets.UTF_8), ReturnType.INTEGER, 1, keysAndArgs));
            } catch (Exception e) {
                log.error("unlock failed, exception:{}", ExceptionUtils.getMessage(e));
                throw new IllegalStateException(Strings.format("unlock failed. key:{}, tag:{}", key, tag));
            }
            return true;
        }, context -> {
            log.error("unlock failed. key:{}, tag:{}", key, tag);
            return false;
        });
    }

    public static boolean unlock(String key) {
        String tag = String.valueOf(Thread.currentThread().getId());
        return unlock(key, tag, RetryUtil.defaultExponentialRetry());
    }

    public enum NotGetLockActionEnum {
        /**
         * 跳过执行
         */
        SKIP,
        THROW,
        CONTINUE
    }

    public static <R> R aroundLock(String key, Supplier<R> supplier, NotGetLockActionEnum actionEnum) {
        if (Objects.isNull(supplier)) {
            return null;
        }
        if (StringUtils.isEmpty(key)) {
            log.warn("key is empty, No distributed locking was performed");
            return supplier.get();
        }
        boolean locked = lock(key);
        if (!locked) {
            switch (actionEnum) {
                case SKIP:
                    return null;
                case THROW:
                    throw new IllegalStateException(Strings.format("get lock failed, key:{}", key));
                case CONTINUE:
                default:
                    break;
            }
        }
        try {
            return supplier.get();
        } finally {
            unlock(key);
        }
    }

}
