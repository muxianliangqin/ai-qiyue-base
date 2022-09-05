package com.qiyue.redis.cache.value;

import com.qiyue.redis.cache.RedisCache;

public abstract class ValueCache<K, V> extends RedisCache<K, V> {

    public V get(K k) {
        return redisWrapper.vGet(k, this.keySerializer, this.vType, this.valueParser);
    }

    public void set(K k, V v) {
        redisWrapper.vSet(k, this.keySerializer, v, this.valueSerializer);
    }
}
