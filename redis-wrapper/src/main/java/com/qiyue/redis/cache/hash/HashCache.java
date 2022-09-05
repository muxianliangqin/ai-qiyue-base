package com.qiyue.redis.cache.hash;

import com.qiyue.redis.cache.RedisHashCache;

import java.util.Map;

public class HashCache<K, HK, HV> extends RedisHashCache<K, HK, HV> {

    public HV get(K k, HK hk) {
        return redisWrapper.hGet(k, this.keySerializer, hk, this.hashKeySerializer, this.kvType, this.hashValueParser);
    }

    public Map<HK, HV> getAll(K k) {
        return redisWrapper.hGetAll(k, this.keySerializer, this.hkType, this.hashKeyParser, this.kvType, this.hashValueParser);
    }

    public void put(K k, HK hk, HV hv) {
        redisWrapper.hPut(k, this.keySerializer, hk, this.hashKeySerializer, hv, this.hashValueSerializer);
    }

    public void putAll(K k, Map<HK, HV> kvMap) {
        redisWrapper.hPutAll(k, this.keySerializer, kvMap, this.hashKeySerializer, this.hashValueSerializer);
    }

    public Long delete(K k, HK hk) {
        return redisWrapper.hDelete(k, this.keySerializer, hk, this.hashKeySerializer);
    }
}
