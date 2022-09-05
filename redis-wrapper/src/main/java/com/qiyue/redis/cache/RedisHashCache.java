package com.qiyue.redis.cache;

import com.qiyue.redis.RedisWrapper;

import java.lang.reflect.Type;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RedisHashCache<K, HK, HV> extends RedisCache<K, HV> {

    protected Function<HK, String> hashKeySerializer = RedisWrapper::defaultObjectSerializer;
    protected Function<HV, String> hashValueSerializer = RedisWrapper::defaultObjectSerializer;
    protected Type hkType = String.class;
    protected Type kvType = String.class;
    protected BiFunction<String, Type, HK> hashKeyParser = RedisWrapper::defaultStringParser;
    protected BiFunction<String, Type, HV> hashValueParser = RedisWrapper::defaultStringParser;
}
