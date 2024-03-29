package com.qiyue.infrastructure.utils.encrypt;

/**
 * 加密函数
 *
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface Encrypt<T, R> {

    R encrypt(T t);
}
