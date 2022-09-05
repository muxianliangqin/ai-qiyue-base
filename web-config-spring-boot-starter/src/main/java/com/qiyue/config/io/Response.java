package com.qiyue.config.io;

import com.qiyue.infrastructure.enums.ExceptionEnum;
import lombok.Data;

@Data
public class Response<T> {
    public static final String SUCCESS = "success";

    private String code;

    private String message;

    private T content;

    public static Response<String> success() {
        Response<String> response = new Response<>();
        response.code = ExceptionEnum.SUCCESS.getCode();
        response.message = ExceptionEnum.SUCCESS.getMsg();
        response.content = SUCCESS;
        return response;
    }

    public static <T> Response<T> success(T content) {
        Response<T> response = new Response<>();
        response.code = ExceptionEnum.SUCCESS.getCode();
        response.message = ExceptionEnum.SUCCESS.getMsg();
        response.content = content;
        return response;
    }

    public static Response<String> fail(String errorCode, String errorMsg) {
        Response<String> response = new Response<>();
        response.code = errorCode;
        response.message = errorMsg;
        return response;
    }

    public static Response<String> fail(ExceptionEnum exceptionEnum) {
        Response<String> response = new Response<>();
        response.code = exceptionEnum.getCode();
        response.message = exceptionEnum.getMsg();
        return response;
    }
}
