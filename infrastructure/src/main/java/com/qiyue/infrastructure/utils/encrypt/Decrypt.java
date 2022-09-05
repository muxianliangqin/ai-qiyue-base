package com.qiyue.infrastructure.utils.encrypt;

/**
 * 解密函数
 *
 * @param <String>
 */
@FunctionalInterface
public interface Decrypt<String> {

    String decrypt();
}
