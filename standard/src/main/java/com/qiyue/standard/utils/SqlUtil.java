package com.qiyue.standard.utils;


import com.qiyue.standard.constant.Constant;

public class SqlUtil {

    public static String like(String value) {
        return Constant.SYMBOL_PERCENT + value + Constant.SYMBOL_PERCENT;
    }

    public static String lLike(String value) {
        return Constant.SYMBOL_PERCENT + value;
    }

    public static String rLike(String value) {
        return value + Constant.SYMBOL_PERCENT;
    }

}
