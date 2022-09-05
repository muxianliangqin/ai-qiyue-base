package com.qiyue.infrastructure.utils;

import com.qiyue.infrastructure.constant.Constant;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class BaseUtil {
    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim()) || "null".equals(str);
    }

    public static boolean isInt(String str) {
        return str.matches(Constant.IS_INT);
    }

    public static String formatAmount(String str) throws Exception {
        if (isEmpty(str)) {
            str = "0.00";
        }
        if (!str.matches(Constant.AMOUNT_FORMAT)) {
            throw new Exception("金额格式不正确");
        }
        BigDecimal b_amount = new BigDecimal(str);
        return b_amount.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
    }

    public static String YuanToFen(String yuan) throws Exception {
        if (isEmpty(yuan)) {
            yuan = "0.00";
        }
        if (!yuan.matches(Constant.AMOUNT_FORMAT)) {
            throw new Exception("金额格式不正确");
        }
        BigDecimal b_yuan = new BigDecimal(yuan);
        return b_yuan.multiply(new BigDecimal(100)).toPlainString();
    }


    public static String mapToString(Map map) {
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                mapToString((Map) value);
            } else {
                System.out.println(key.toString() + ":" + value.toString());
            }
        }
        return null;
    }

    public static String encode(String str, String encode) {
        String output = "";
        try {
            byte[] bt = str.getBytes();
            output = new String(bt, encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return output;
    }

}
