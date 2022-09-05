package com.qiyue.redis.cache.hash;

public abstract class AbstractSpecificKeyHashCache<K, HK, HV> extends HashCache<K, HK, HV> {

    protected AbstractSpecificKeyHashCache() {
        this.keySerializer = k -> getKey();
    }

    /**
     * 获取指定的key
     *
     * @return key
     */
    public abstract String getKey();

}
