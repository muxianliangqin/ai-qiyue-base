package com.qiyue.redis.cache.list;

public abstract class AbstractSpecificKeyListCache<K, V> extends ListCache<K, V> {

    protected AbstractSpecificKeyListCache() {
        this.keySerializer = k -> getKey();
    }

    /**
     * 获取指定的key
     *
     * @return key
     */
    public abstract String getKey();

}
