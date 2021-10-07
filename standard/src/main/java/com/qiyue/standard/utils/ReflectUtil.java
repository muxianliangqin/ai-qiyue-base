package com.qiyue.standard.utils;

import com.qiyue.standard.enums.ExceptionEnum;
import com.qiyue.standard.exceptions.LogicException;

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
            throw new LogicException(ExceptionEnum.QSG_CATCH_EXCEPTION,
                    Strings.format("fileName:{}, fieldValue:{}", fileName, fieldValue), e);
        }
    }
}
