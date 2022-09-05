package com.qiyue.redis;

import com.alibaba.fastjson.JSON;
import com.qiyue.infrastructure.utils.Streams;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import java.util.stream.Stream;

@Component
public class RedisWrapper {
    private final StringRedisTemplate redisTemplate;
    private final HashOperations<String, String, String> hashOperations;
    private final ListOperations<String, String> listOperations;
    private final SetOperations<String, String> setOperations;

    private static final List<Class<?>> BASIC_TYPE_LIST;

    public static final Long TIMEOUT_DEFAULT = 3 * 24 * 60 * 60L;
    public static final TimeUnit TIME_UNIT_DEFAULT = TimeUnit.SECONDS;
    public static final Long RETURN_DEFAULT = -1L;

    public RedisWrapper(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
        this.listOperations = redisTemplate.opsForList();
        this.setOperations = redisTemplate.opsForSet();
    }

    /*
     * 直接操作 redisTemplate
     * key：String
     * value: String
     * hashKey: String
     * hashValue: String
     */

    public boolean delete(@NonNull String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public boolean hashKey(@NonNull String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public boolean expire(@NonNull String key, @NonNull Long timeout, @NonNull TimeUnit timeUnit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, timeUnit));
    }

    static {
        BASIC_TYPE_LIST = new ArrayList<>();
        BASIC_TYPE_LIST.add(Byte.class);
        BASIC_TYPE_LIST.add(Boolean.class);
        BASIC_TYPE_LIST.add(Short.class);
        BASIC_TYPE_LIST.add(Character.class);
        BASIC_TYPE_LIST.add(Integer.class);
        BASIC_TYPE_LIST.add(Float.class);
        BASIC_TYPE_LIST.add(Long.class);
        BASIC_TYPE_LIST.add(Double.class);
        BASIC_TYPE_LIST.add(String.class);
    }

    public String vGet(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Long vSet(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
        return RETURN_DEFAULT;
    }

    public String hGet(String key, String hashKey) {
        return hashOperations.get(key, hashKey);
    }

    public Map<String, String> hGetAll(String key) {
        return hashOperations.entries(key);
    }


    public void hPut(String key, String hashKey, String hashValue) {
        hashOperations.put(key, hashKey, hashValue);
    }

    public void hPutAll(String key, Map<String, String> map) {
        hashOperations.putAll(key, map);
    }

    public Long hDelete(String key, String hashKey) {
        return hashOperations.delete(key, hashKey);
    }

    public String lPop(String key) {
        return listOperations.leftPop(key);
    }

    public String lPop(@NonNull String key, @NonNull Long timeout, @NonNull TimeUnit timeUnit) {
        return listOperations.leftPop(key, timeout, timeUnit);
    }

    public String rPop(String key) {
        return listOperations.rightPop(key);
    }

    public String rPop(@NonNull String key, @NonNull Long timeout, @NonNull TimeUnit timeUnit) {
        return listOperations.rightPop(key, timeout, timeUnit);
    }

    public Long lPush(String key, String value) {
        return listOperations.leftPush(key, value);
    }

    public Long rPush(String key, String value) {
        return listOperations.rightPush(key, value);
    }

    public Long lPushAll(String key, List<String> values) {
        return listOperations.leftPushAll(key, values);
    }

    public Long rPushAll(String key, Collection<String> values) {
        return listOperations.rightPushAll(key, values);
    }

    public Long lSize(String key) {
        return listOperations.size(key);
    }

    public List<String> lReadAll(String key) {
        Long size = lSize(key);
        if (size <= 0) {
            return new ArrayList<>();
        }
        return listOperations.range(key, 0, size);
    }

    /**
     * count > 0 :从头至尾移除第一个相等的
     * count < 0 :从尾至头移除第一个相等的
     * count = 0 :移除所有相等的
     *
     * @param key   key
     * @param value value
     * @return result num
     */
    public Long lRemove(String key, String value) {
        return listOperations.remove(key, 0, value);
    }

    public Long sAdd(String key, String... values) {
        return setOperations.add(key, values);
    }

    public String sPop(String key) {
        return setOperations.pop(key);
    }

    public Set<String> sReadAll(String key) {
        return setOperations.members(key);
    }

    public Long sRemove(String key, Object... values) {
        return setOperations.remove(key, values);
    }

    public Long sSize(String key) {
        return setOperations.size(key);
    }

    public boolean sExist(String key, String value) {
        return Boolean.TRUE.equals(setOperations.isMember(key, value));
    }

    public static <K> String defaultObjectSerializer(@NonNull K k) {
        if (BASIC_TYPE_LIST.contains(k.getClass())) {
            return k.toString();
        }
        return JSON.toJSONString(k);
    }

    @SuppressWarnings("unchecked")
    public static <V> V defaultStringParser(String value, @NonNull Type type) {
        if (Objects.isNull(value)) {
            return null;
        }
        if (String.class.equals(type)) {
            return (V) value;
        } else if (Byte.class.equals(type)) {
            return (V) Byte.valueOf(value);
        } else if (Short.class.equals(type)) {
            return (V) Short.valueOf(value);
        } else if (Character.class.equals(type)) {
            return (V) Character.valueOf(value.charAt(0));
        } else if (Integer.class.equals(type)) {
            return (V) Integer.valueOf(value);
        } else if (Long.class.equals(type)) {
            return (V) Long.valueOf(value);
        } else if (Float.class.equals(type)) {
            return (V) Float.valueOf(value);
        } else if (Double.class.equals(type)) {
            return (V) Double.valueOf(value);
        }
        return JSON.parseObject(value, type);
    }

    /**
     * TeFunction
     *
     * @param <T> first argument
     * @param <U> second argument
     * @param <E> third argument
     * @param <R> result
     */
    @FunctionalInterface
    public interface TeFunction<T, U, E, R> {
        R apply(T t, U u, E e);
    }

    /**
     * TeConsumer
     *
     * @param <T> first argument
     * @param <U> second argument
     * @param <E> third argument
     */
    @FunctionalInterface
    public interface TeConsumer<T, U, E> {
        void accept(T t, U u, E e);
    }

    /**
     * TePredicate
     *
     * @param <T> first argument
     * @param <U> second argument
     * @param <E> third argument
     */
    @FunctionalInterface
    public interface TePredicate<T, U, E> {
        boolean test(T t, U u, E e);
    }

    /*
     * 操作泛型参数
     */
    public <K> boolean operateKey(K k,
                                  Function<K, String> keySerializer,
                                  Predicate<String> operate) {
        String key = keySerializer.apply(k);
        return operate.test(key);
    }

    public <K> boolean operateKey(K k,
                                  Function<K, String> keySerializer,
                                  Long timeout,
                                  TimeUnit timeUnit,
                                  TePredicate<String, Long, TimeUnit> operate) {
        String key = keySerializer.apply(k);
        return operate.test(key, timeout, timeUnit);
    }

    public <K, V> V get(K k,
                        Function<K, String> keySerializer,
                        Type vType,
                        BiFunction<String, Type, V> valueParser,
                        UnaryOperator<String> valueGetter) {
        String key = keySerializer.apply(k);
        String value = valueGetter.apply(key);
        return valueParser.apply(value, vType);
    }

    public <K, V> Long put(K k,
                           Function<K, String> keySerializer,
                           V v,
                           Function<V, String> valueSerializer,
                           BiFunction<String, String, Long> valueSetter) {
        String key = keySerializer.apply(k);
        String value = valueSerializer.apply(v);
        return valueSetter.apply(key, value);
    }

    public <K, V> V lGetTimeout(K k,
                                Function<K, String> keySerializer,
                                Type vType,
                                BiFunction<String, Type, V> valueParser,
                                Long timeout,
                                TimeUnit timeUnit,
                                TeFunction<String, Long, TimeUnit, String> valueGetter) {
        String key = keySerializer.apply(k);
        String value = valueGetter.apply(key, timeout, timeUnit);
        return valueParser.apply(value, vType);
    }

    public <K, V> Long lPutAll(K k,
                               Function<K, String> keySerializer,
                               Collection<V> vCollection,
                               Function<V, String> valueSerializer,
                               BiFunction<String, List<String>, Long> valueSetter) {
        String key = keySerializer.apply(k);
        List<String> valueList = Streams.of(vCollection).map(valueSerializer).toList();
        return valueSetter.apply(key, valueList);
    }


    public <K, HK, HV> HV hGet(K k,
                               Function<K, String> keySerializer,
                               HK hk,
                               Function<HK, String> hashKeySerializer,
                               Type hvType,
                               BiFunction<String, Type, HV> valueParser,
                               BinaryOperator<String> valueGetter) {
        String key = keySerializer.apply(k);
        String hashKey = hashKeySerializer.apply(hk);
        String hashValue = valueGetter.apply(key, hashKey);
        return valueParser.apply(hashValue, hvType);
    }

    public <K, HK, HV> void hPut(K k,
                                 Function<K, String> keySerializer,
                                 HK hk,
                                 Function<HK, String> hashKeySerializer,
                                 HV hv,
                                 Function<HV, String> hashValueSerializer,
                                 TeConsumer<String, String, String> valueSetter) {
        String key = keySerializer.apply(k);
        String hashKey = hashKeySerializer.apply(hk);
        String hashValue = hashValueSerializer.apply(hv);
        valueSetter.accept(key, hashKey, hashValue);
    }

    public <K, HK, HV> Map<HK, HV> hGetAll(K k,
                                           Function<K, String> keySerializer,
                                           Type hkType,
                                           BiFunction<String, Type, HK> hasKeyParser,
                                           Type hvType,
                                           BiFunction<String, Type, HV> hasValueParser,
                                           Function<String, Map<String, String>> valueGetter) {
        String key = keySerializer.apply(k);
        Map<String, String> map = valueGetter.apply(key);
        Map<HK, HV> kvMap = new HashMap<>(32);
        map.forEach((m, n) -> kvMap.put(hasKeyParser.apply(m, hkType), hasValueParser.apply(n, hvType)));
        return kvMap;
    }

    public <K, HK, HV> void hPutAll(K k,
                                    Function<K, String> keySerializer,
                                    Map<HK, HV> kvMap,
                                    Function<HK, String> hashKeySerializer,
                                    Function<HV, String> hashValueSerializer,
                                    BiConsumer<String, Map<String, String>> valueSetter) {
        String key = keySerializer.apply(k);
        Map<String, String> map = new HashMap<>(32);
        kvMap.forEach((m, n) -> map.put(hashKeySerializer.apply(m), hashValueSerializer.apply(n)));
        valueSetter.accept(key, map);
    }

    public <K, HK> Long hDelete(K k,
                                Function<K, String> keySerializer,
                                HK hk,
                                Function<HK, String> hashKeySerializer,
                                BiFunction<String, String, Long> hDelete) {
        String key = keySerializer.apply(k);
        String hashKey = hashKeySerializer.apply(hk);
        return hDelete.apply(key, hashKey);
    }

    public <K, V> List<V> lReadAll(K k,
                                   Function<K, String> keySerializer,
                                   Type vType,
                                   BiFunction<String, Type, V> valueParser,
                                   Function<String, List<String>> valueGetter) {
        String key = keySerializer.apply(k);
        List<String> valueList = valueGetter.apply(key);
        return Streams.of(valueList).map(v -> valueParser.apply(v, vType)).toList();
    }

    public <K, V> Long lRemove(K k,
                               Function<K, String> keySerializer,
                               V v,
                               Function<V, String> valueSerializer,
                               BiFunction<String, String, Long> lRemove) {
        String key = keySerializer.apply(k);
        String value = valueSerializer.apply(v);
        return lRemove.apply(key, value);
    }

    public <K, V> Long sAdd(K k,
                            Function<K, String> keySerializer,
                            Function<V, String> valueSerializer,
                            Stream<V> vStream,
                            BiFunction<String, String[], Long> sOperate) {
        String key = keySerializer.apply(k);
        String[] values = vStream.map(valueSerializer).toArray(String[]::new);
        return sOperate.apply(key, values);
    }

    public <K, V> Long sRemove(K k,
                               Function<K, String> keySerializer,
                               Function<V, String> valueSerializer,
                               BiFunction<String, Object[], Long> sOperate,
                               Stream<V> vStream) {
        String key = keySerializer.apply(k);
        String[] values = vStream.map(valueSerializer).toArray(String[]::new);
        return sOperate.apply(key, values);
    }

    public <K, V> Set<V> sReadAll(K k,
                                  Function<K, String> keySerializer,
                                  Type vType,
                                  BiFunction<String, Type, V> valueParser,
                                  Function<String, Set<String>> valueGetter) {
        String key = keySerializer.apply(k);
        Set<String> valueSet = valueGetter.apply(key);
        return Streams.of(valueSet).map(v -> valueParser.apply(v, vType)).toSet();
    }

    public <K> Long size(K k,
                         Function<K, String> keySerializer,
                         ToLongFunction<String> valueGetter) {
        String key = keySerializer.apply(k);
        return valueGetter.applyAsLong(key);
    }

    public <K, V> Boolean exist(K k,
                                Function<K, String> keySerializer,
                                V v,
                                Function<V, String> valueSerializer,
                                BiPredicate<String, String> valueGetter) {
        String key = keySerializer.apply(k);
        String value = valueSerializer.apply(v);
        return valueGetter.test(key, value);
    }

    /*
     * 操作任意类型读写redis
     * key：K
     * value: V
     * hashKey: HK
     * hashValue: HV
     */

    public <K> boolean delete(K k, Function<K, String> keySerializer) {
        return operateKey(k, keySerializer, this::delete);
    }

    public <K> boolean delete(K k) {
        return operateKey(k, RedisWrapper::defaultObjectSerializer, this::delete);
    }

    public <K> boolean hashKey(K k, Function<K, String> keySerializer) {
        return operateKey(k, keySerializer, this::hashKey);
    }

    public <K> boolean hashKey(K k) {
        return operateKey(k, RedisWrapper::defaultObjectSerializer, this::hashKey);
    }

    public <K> boolean expire(K k, Function<K, String> keySerializer, Long timeout, TimeUnit timeUnit) {
        return operateKey(k, keySerializer, timeout, timeUnit, this::expire);
    }

    public <K> boolean expire(K k, Long timeout, TimeUnit timeUnit) {
        return operateKey(k, RedisWrapper::defaultObjectSerializer, timeout, timeUnit, this::expire);
    }

    public <K> boolean expire(K k, Function<K, String> keySerializer) {
        return operateKey(k, keySerializer, TIMEOUT_DEFAULT, TIME_UNIT_DEFAULT, this::expire);
    }

    public <K> boolean expire(K k) {
        return operateKey(k, RedisWrapper::defaultObjectSerializer, TIMEOUT_DEFAULT, TIME_UNIT_DEFAULT, this::expire);
    }

    public <K, V> V vGet(K k, Class<V> vClass) {
        return get(k, RedisWrapper::defaultObjectSerializer, vClass, RedisWrapper::defaultStringParser, this::vGet);
    }

    public <K, V> V vGet(K k, Function<K, String> keySerializer, @NonNull Type vType, BiFunction<String, Type, V> valueParser) {
        return get(k, keySerializer, vType, valueParser, this::vGet);
    }

    public <K, V> Long vSet(K k, V v) {
        return put(k, RedisWrapper::defaultObjectSerializer, v, RedisWrapper::defaultObjectSerializer, this::vSet);
    }

    public <K, V> void vSet(K k, Function<K, String> keySerializer, V v, Function<V, String> valueSerializer) {
        put(k, keySerializer, v, valueSerializer, this::vSet);
    }

    public <K, HK, HV> HV hGet(K k, HK hk, Class<HV> vClass) {
        return hGet(k, RedisWrapper::defaultObjectSerializer, hk, RedisWrapper::defaultObjectSerializer,
                vClass, RedisWrapper::defaultStringParser, this::hGet);
    }

    public <K, HK, HV> HV hGet(K k, Function<K, String> keySerializer, HK hk, Function<HK, String> hashKeySerializer,
                               Type hvType, BiFunction<String, Type, HV> hashValueParser) {
        return hGet(k, keySerializer, hk, hashKeySerializer, hvType, hashValueParser, this::hGet);
    }

    public <K, HK, HV> Map<HK, HV> hGetAll(K k, Function<K, String> keySerializer,
                                           Type hkType, BiFunction<String, Type, HK> hashKeyParser,
                                           Type hvType, BiFunction<String, Type, HV> hashValueParser) {
        return hGetAll(k, keySerializer, hkType, hashKeyParser, hvType, hashValueParser, this::hGetAll);
    }

    public <K, HK, HV> void hPut(K k, HK hk, HV hv) {
        hPut(k, RedisWrapper::defaultObjectSerializer, hk, RedisWrapper::defaultObjectSerializer,
                hv, RedisWrapper::defaultObjectSerializer, this::hPut);
    }

    public <K, HK, HV> void hPut(K k, Function<K, String> keySerializer,
                                 HK hk, Function<HK, String> hashKeySerializer,
                                 HV hv, Function<HV, String> hashValueSerializer) {
        hPut(k, keySerializer, hk, hashKeySerializer, hv, hashValueSerializer, this::hPut);
    }

    public <K, HK, HV> void hPutAll(K k, Function<K, String> keySerializer,
                                    Map<HK, HV> kvMap,
                                    Function<HK, String> hashKeySerializer,
                                    Function<HV, String> hashValueSerializer) {
        hPutAll(k, keySerializer, kvMap, hashKeySerializer, hashValueSerializer, this::hPutAll);
    }

    public <K, HK> Long hDelete(K k, Function<K, String> keySerializer,
                                HK hk, Function<HK, String> hashKeySerializer) {
        return hDelete(k, keySerializer, hk, hashKeySerializer, this::hDelete);
    }

    public <K, V> V lPop(K k, Function<K, String> keySerializer, Type vType) {
        return get(k, keySerializer, vType, RedisWrapper::defaultStringParser, this::lPop);
    }

    public <K, V> V rPop(K k, Function<K, String> keySerializer, Type vType) {
        return get(k, keySerializer, vType, RedisWrapper::defaultStringParser, this::rPop);
    }

    public <K, V> V lPop(K k, Function<K, String> keySerializer,
                         Type vType, BiFunction<String, Type, V> valueParser, Long timeout, TimeUnit timeUnit) {
        return lGetTimeout(k, keySerializer, vType, valueParser, timeout, timeUnit, this::lPop);
    }

    public <K, V> V rPop(K k, Function<K, String> keySerializer,
                         Type vType, BiFunction<String, Type, V> valueParser, Long timeout, TimeUnit timeUnit) {
        return lGetTimeout(k, keySerializer, vType, valueParser, timeout, timeUnit, this::rPop);
    }

    public <K, V> Long lPush(K k, Function<K, String> keySerializer, V v, Function<V, String> valueSerializer) {
        return put(k, keySerializer, v, valueSerializer, this::lPush);
    }

    public <K, V> Long rPush(K k, Function<K, String> keySerializer,
                             V v, Function<V, String> valueSerializer) {
        return put(k, keySerializer, v, valueSerializer, this::rPush);
    }

    public <K, V> Long lPushAll(K k, Function<K, String> keySerializer,
                                List<V> vList, Function<V, String> valueSerializer) {
        return lPutAll(k, keySerializer, vList, valueSerializer, this::lPushAll);
    }

    public <K, V> Long rPushAll(K k, Function<K, String> keySerializer,
                                List<V> vList, Function<V, String> valueSerializer) {
        return lPutAll(k, keySerializer, vList, valueSerializer, this::rPushAll);
    }

    public <K> Long lSize(K k, Function<K, String> keySerializer) {
        return size(k, keySerializer, this::lSize);
    }

    public <K, V> List<V> lReadAll(K k, Function<K, String> keySerializer,
                                   Type vType, BiFunction<String, Type, V> valueParser) {
        return lReadAll(k, keySerializer, vType, valueParser, this::lReadAll);
    }

    public <K, V> Long lRemove(K k, Function<K, String> keySerializer,
                               V v, Function<V, String> valueSerializer) {
        return lRemove(k, keySerializer, v, valueSerializer, this::lRemove);
    }

    @SafeVarargs
    public final <K, V> Long sAdd(K k, Function<K, String> keySerializer,
                                  Function<V, String> valueSerializer, V... vs) {
        return sAdd(k, keySerializer, valueSerializer, Arrays.stream(vs), this::sAdd);
    }

    public final <K, V> Long sAdd(K k, Function<K, String> keySerializer,
                                  Collection<V> vCollection,
                                  Function<V, String> valueSerializer) {
        return sAdd(k, keySerializer, valueSerializer, Streams.stream(vCollection), this::sAdd);
    }

    public <K, V> V sPop(K k, Function<K, String> keySerializer,
                         Type vType, BiFunction<String, Type, V> valueParser) {
        return get(k, keySerializer, vType, valueParser, this::sPop);
    }

    public <K, V> Set<V> sReadAll(K k, Function<K, String> keySerializer, Type vType, BiFunction<String, Type, V> valueParser) {
        return sReadAll(k, keySerializer, vType, valueParser, this::sReadAll);
    }

    @SafeVarargs
    public final <K, V> Long sRemove(K k, Function<K, String> keySerializer,
                                     Function<V, String> valueSerializer, V... vs) {
        return sRemove(k, keySerializer, valueSerializer, this::sRemove, Arrays.stream(vs));
    }

    public final <K, V> Long sRemove(K k, Function<K, String> keySerializer,
                                     Set<V> vSet, Function<V, String> valueSerializer) {
        return sRemove(k, keySerializer, valueSerializer, this::sRemove, Streams.stream(vSet));
    }

    public <K> Long sSize(K k, Function<K, String> keySerializer) {
        return size(k, keySerializer, this::sSize);
    }

    public <K, V> Boolean sExist(K k, Function<K, String> keySerializer, V v, Function<V, String> valueSerializer) {
        return exist(k, keySerializer, v, valueSerializer, this::sExist);
    }

}
