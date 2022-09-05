package com.qiyue.config.advice;

import com.alibaba.fastjson.JSON;
import com.qiyue.config.io.Response;
import com.qiyue.infrastructure.enums.ExceptionEnum;
import com.qiyue.infrastructure.exceptions.LogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler{

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Response<String> exception(Exception e) {
        String errorMsg;
        if (e instanceof LogicException) {
            log.warn("LogicException：{}", ((LogicException) e).getErrorMsg());
            errorMsg = JSON.toJSONString(e);
        } else {
            log.error("LogicException：{}", e.getMessage(), e);
            errorMsg = JSON.toJSONString(new LogicException(ExceptionEnum.UNKNOWN_ERROR, e));
        }
        return Response.fail(ExceptionEnum.UNKNOWN_ERROR.getCode(), errorMsg);
    }
}
