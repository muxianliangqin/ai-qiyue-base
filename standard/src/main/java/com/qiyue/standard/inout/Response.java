package com.qiyue.standard.inout;

import com.qiyue.standard.enums.ExceptionEnum;
import lombok.Data;

@Data
public class Response<T> {
    private String code;

    private String message;

    private T content;

    public static Response<String> success() {
        Response<String> response = new Response<>();
        response.code = ExceptionEnum.SUCCESS.getCode();
        response.message = ExceptionEnum.SUCCESS.getMsg();
        response.content = "ok";
        return response;
    }

    public static <T> Response<T> success(T content) {
        Response<T> response = new Response<>();
        response.code = ExceptionEnum.SUCCESS.getCode();
        response.message = ExceptionEnum.SUCCESS.getMsg();
        response.content = content;
        return response;
    }

    public static Response fail(String errorCode, String errorMsg) {
        Response response = new Response();
        response.code = errorCode;
        response.message = errorMsg;
        return response;
    }

    public static Response fail(ExceptionEnum exceptionEnum) {
        Response response = new Response();
        response.code = exceptionEnum.getCode();
        response.message = exceptionEnum.getMsg();
        return response;
    }
}
