package com.qiyue.config.advice;

import com.alibaba.fastjson.JSON;
import com.qiyue.config.io.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.annotation.Annotation;

@RestControllerAdvice
public class CustomResponseAdvice implements ResponseBodyAdvice<Object> {
    private static final Class<? extends Annotation> IGNORE_TYPE = IgnoreRestResponse.class;

    @Override
    public boolean supports(MethodParameter methodParameter, @NotNull Class clazz) {
        return !(AnnotatedElementUtils.hasAnnotation(methodParameter.getClass(), IGNORE_TYPE)
                || methodParameter.hasMethodAnnotation(IGNORE_TYPE));
    }

    /**
     * 返回类型为String时，调用  {@link StringHttpMessageConverter} 处理，
     * 在 super.addDefaultHeaders() 时会转换报错
     * <p>
     * 返回值为null时，不会调用本类处理，因此应返回不为空的默认值
     * AbstractMessageConverterMethodProcessor#writeWithMessageConverters()
     *
     * @param body               返回内容
     * @param methodParameter    方法参数
     * @param mediaType          mediaType
     * @param aClass             aClass
     * @param serverHttpRequest  serverHttpRequest
     * @param serverHttpResponse serverHttpResponse
     * @return 包装的返回参数
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (body instanceof String) {
            return JSON.toJSONString(Response.success());
        }
        if (body instanceof Response) {
            return body;
        }
        return Response.success(body);
    }
}
