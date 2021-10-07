package com.qiyue.standard.utils;

import com.qiyue.standard.constant.Constant;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Strings {

    /**
     * 格式化字符串
     *
     * @param source        待格式化字符串，以{}为占位符，
     * @param replaceValues 替换值数组
     * @return 格式化后的字符串
     */
    public static String format(String source, Object... replaceValues) {
        Pattern pattern = Pattern.compile("\\{}");
        Matcher matcher = pattern.matcher(source);
        StringBuffer sb = new StringBuffer();
        // 从开始替换占位符
        for (Object v : replaceValues) {
            if (matcher.find() && null != v) {
                // 所有替换值都转为String形式
                matcher.appendReplacement(sb, v.toString());
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public enum RandomStringTypeEnum {
        NUMBER,
        LETTER_UPPERCASE,
        LETTER_LOWERCASE,
        LETTER_MIX,
        MIX
    }

    public static String getRandomString(int length, RandomStringTypeEnum typeEnum) {
        return getRandomString(length, typeEnum.name());
    }

    public static String getRandomString(int length, String type) {
        Asserts.expression(length > 0, "随机数长度必须大于0");
        Asserts.exists(type, RandomStringTypeEnum.values(), RandomStringTypeEnum::name);
        StringBuilder sb = new StringBuilder();
        final char[] chars;
        if (RandomStringTypeEnum.NUMBER.name().equals(type)) {
            chars = new char[10];
            System.arraycopy(Constant.CHARACTERS, 0, chars, 0, 10);
        } else if (RandomStringTypeEnum.LETTER_UPPERCASE.name().equals(type)) {
            chars = new char[26];
            System.arraycopy(Constant.CHARACTERS, 10, chars, 0, 26);
        } else if (RandomStringTypeEnum.LETTER_LOWERCASE.name().equals(type)) {
            chars = new char[26];
            System.arraycopy(Constant.CHARACTERS, 36, chars, 0, 26);
        } else {
            chars = new char[Constant.CHARACTERS.length];
            System.arraycopy(Constant.CHARACTERS, 0, chars, 0, Constant.CHARACTERS.length);
        }
        SecureRandom random = new SecureRandom();
        byte[] seeds = SecureRandom.getSeed(length);
        for (int i = 0; i < length; i++) {
            random.setSeed(seeds); //设置种子
            int randomIndex = random.nextInt(chars.length); //随机生成0-charArr.length的数字
            sb.append(chars[randomIndex]);
            random.nextBytes(seeds); //随机获取新的byte数组用以作为下次的种子，不断循环
        }
        return sb.toString();
    }

    /*
    首先攻击者准备256个字符串，它们的哈希值的第一字节包含了所有可能的情况。
    他将每个字符串发送给在线系统尝试登陆，并记录系统响应所消耗的时间。
    耗时最长的字符串就是第一字节相匹配的。攻击者知道第一字节后，并可以用同样的方式继续猜测第二字节、
    第三字节等等。一旦攻击者获得足够长的哈希值片段，他就可以在自己的机器上来破解，不受在线系统的限制。
    在网络上进行这种攻击似乎不可能。然而，有人已经实现了，并已证明是实用的。
    这就是为什么本文提到的代码，它利用固定时间去比较字符串，而不管有多大的字符串。
     */
    public static boolean slowEquals(String aStr, String bStr) {
        byte[] a = aStr.getBytes(StandardCharsets.UTF_8);
        byte[] b = bStr.getBytes(StandardCharsets.UTF_8);
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    public static String strToUnicode(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            sb.append(Constant.SYMBOL_UNICODE_PREFIX);
            String u = Integer.toHexString(c);
            sb.append(u);
        }
        return sb.toString();
    }

    public static String unicodeToStr(String unicode) {
        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile(Constant.REGEX_UNICODE);
        Matcher m = p.matcher(unicode);
        while (m.find()) {
            String u = m.group();
            char letter = (char) Integer.parseInt(u.substring(2), 16);
            sb.append(letter);
        }
        return sb.toString();
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }
}
