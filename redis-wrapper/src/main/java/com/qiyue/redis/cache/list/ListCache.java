package com.qiyue.redis.cache.list;

import com.qiyue.redis.cache.RedisCache;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListCache<K, V> extends RedisCache<K, V> {

    public List<V> getAll(K k) {
        return redisWrapper.lReadAll(k, this.keySerializer, this.vType, this.valueParser);
    }

    public Long size(K k) {
        return redisWrapper.lSize(k, this.keySerializer);
    }

    public Long remove(K k, V v) {
        return redisWrapper.lRemove(k, this.keySerializer, v, this.valueSerializer);
    }

    public Long leftPush(K k, V v) {
        Long l = redisWrapper.lPush(k, this.keySerializer, v, this.valueSerializer);
        this.expire(k);
        return l;
    }

    public Long rightPush(K k, V v) {
        Long l = redisWrapper.rPush(k, this.keySerializer, v, this.valueSerializer);
        this.expire(k);
        return l;
    }

    public Long leftPushAll(K k, List<V> vList) {
        Long l = redisWrapper.lPushAll(k, this.keySerializer, vList, this.valueSerializer);
        this.expire(k);
        return l;
    }

    public Long rightPushAll(K k, List<V> vList) {
        Long l = redisWrapper.rPushAll(k, this.keySerializer, vList, this.valueSerializer);
        this.expire(k);
        return l;
    }

    public V leftPop(K k) {
        return redisWrapper.lPop(k, this.keySerializer, this.vType);
    }

    public V rightPush(K k) {
        return redisWrapper.rPop(k, this.keySerializer, this.vType);
    }

    public V leftPop(K k, Long timeout, TimeUnit timeUnit) {
        return redisWrapper.lPop(k, this.keySerializer, this.vType, this.valueParser, timeout, timeUnit);
    }

    public V rightPush(K k, Long timeout, TimeUnit timeUnit) {
        return redisWrapper.rPop(k, this.keySerializer, this.vType, this.valueParser, timeout, timeUnit);
    }
}
