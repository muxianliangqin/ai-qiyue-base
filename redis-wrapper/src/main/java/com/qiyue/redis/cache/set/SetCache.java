package com.qiyue.redis.cache.set;

import com.qiyue.redis.cache.RedisCache;

import java.util.Collection;
import java.util.Set;

public class SetCache<K, V> extends RedisCache<K, V> {

    public V pop(K k) {
        return redisWrapper.sPop(k, this.keySerializer, this.vType, this.valueParser);
    }

    public Set<V> getAll(K k) {
        return redisWrapper.sReadAll(k, this.keySerializer, this.vType, this.valueParser);
    }

    public Long add(K k, V v) {
        return redisWrapper.sAdd(k, this.keySerializer, this.valueSerializer, v);
    }

    public Long add(K k, Collection<V> vCollection) {
        return redisWrapper.sAdd(k, this.keySerializer, vCollection, this.valueSerializer);
    }

    public Long remove(K k, V v) {
        return redisWrapper.sRemove(k, this.keySerializer, this.valueSerializer, v);
    }

    public Boolean exist(K k, V v) {
        return redisWrapper.sExist(k, this.keySerializer, v, this.valueSerializer);
    }
}
