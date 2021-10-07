package com.qiyue.standard.utils;

import com.qiyue.standard.enums.ExceptionEnum;
import com.qiyue.standard.exceptions.LogicException;

import java.util.function.Function;

public class Asserts {

    public static void nonNull(Object obj, String msg) {
        if (null == obj) {
            throw new LogicException(ExceptionEnum.QBG_ASSERT_OBJECT_NON_NULL, msg);
        }
    }

    public static void expression(boolean expression, String msg) {
        if (!expression) {
            throw new LogicException(ExceptionEnum.QBG_ASSERT_EXPRESSION_COME_OUT_FALSE, msg);
        }
    }

    public static <S, E> void exists(S s, E[] eEnum, Function<E, S> getSource) {
        nonNull(s, "参数s不能为空");
        nonNull(eEnum, "参数eEnum不能为空");
        nonNull(getSource, "参数getSource不能为空");
        if (!EnumUtil.include(s, eEnum, getSource)) {
            throw new LogicException(ExceptionEnum.QBG_ASSERT_ENUM_NOT_EXISTS,
                    Strings.format("枚举：{},目标值：{}", EnumUtil.toList(eEnum, getSource), s));
        }
    }
}
