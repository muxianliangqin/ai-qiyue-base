package com.qiyue.redis.cache;

import com.qiyue.redis.RedisWrapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RedisCache<K, V> {
    @Autowired
    protected RedisWrapper redisWrapper;

    protected Function<K, String> keySerializer = RedisWrapper::defaultObjectSerializer;
    protected Function<V, String> valueSerializer = RedisWrapper::defaultObjectSerializer;
    protected Type kType = String.class;
    protected Type vType = String.class;
    protected BiFunction<String, Type, K> keyParser = RedisWrapper::defaultStringParser;
    protected BiFunction<String, Type, V> valueParser = RedisWrapper::defaultStringParser;

    public Boolean expire(K k) {
        return redisWrapper.expire(k, keySerializer);
    }

    public Boolean hasKey(K k) {
        return redisWrapper.hashKey(k, keySerializer);
    }

    public Boolean delete(K k) {
        return redisWrapper.delete(k, keySerializer);
    }
}
