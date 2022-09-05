package com.qiyue.infrastructure.utils;

import com.qiyue.infrastructure.enums.ExceptionEnum;
import com.qiyue.infrastructure.exceptions.LogicException;

import java.lang.reflect.Field;

public class ReflectUtil {

    public static void setFieldValue(Object object, String fileName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        try {
            Field field = clazz.getDeclaredField(fileName);
            field.setAccessible(true);
            field.set(object, fieldValue);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            throw new LogicException(ExceptionEnum.RUNTIME_EXCEPTION,
                    Strings.format("fileName:{}, fieldValue:{}", fileName, fieldValue), e);
        }
    }
}
