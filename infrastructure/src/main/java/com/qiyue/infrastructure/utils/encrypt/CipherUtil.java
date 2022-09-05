package com.qiyue.infrastructure.utils.encrypt;

import com.qiyue.infrastructure.enums.ExceptionEnum;
import com.qiyue.infrastructure.exceptions.LogicException;
import com.qiyue.infrastructure.utils.Strings;
import org.springframework.security.crypto.codec.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加解密
 */
public class CipherUtil {

    enum CipherEnum {
        SHA,
        MD5
    }

    public static final Encrypt<String, String> MD5 = plainText -> {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance(CipherEnum.MD5.name());
            byte[] byteArray = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] md5Bytes = md5.digest(byteArray);
            StringBuilder hexValue = new StringBuilder();
            for (byte bt : md5Bytes) {
                if (bt < 16)
                    hexValue.append("0");
                hexValue.append(Integer.toHexString(bt));
            }
            return hexValue.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new LogicException(ExceptionEnum.QSG_CIPHER_EXCEPTION,
                    Strings.format("加密方式：{}", CipherEnum.MD5.name()), e);
        }
    };

    public static final SaltEncrypt<String, String, String> SHA = (plainText, salt) -> {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(CipherEnum.SHA.name());
            byte[] bytes = (plainText + salt).getBytes(StandardCharsets.UTF_8);
            byte[] resultBytes = messageDigest.digest(bytes);
            char[] chars = Hex.encode(resultBytes);
            return String.valueOf(chars);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new LogicException(ExceptionEnum.QSG_CIPHER_EXCEPTION,
                    Strings.format("加密方式：{}", CipherEnum.SHA.name()), e);
        }
    };
}
