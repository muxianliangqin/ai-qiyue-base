package com.qiyue.standard.exceptions;

import com.qiyue.standard.constant.Constant;
import com.qiyue.standard.enums.ExceptionEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LogicException extends RuntimeException {

    private String errorCode;

    private String errorMsg;

    private Throwable cause;

    public LogicException(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public LogicException(ExceptionEnum exceptionEnum) {
        this.errorCode = exceptionEnum.getCode();
        this.errorMsg = exceptionEnum.getMsg();
    }

    public LogicException(ExceptionEnum exceptionEnum, String msg) {
        this.errorCode = exceptionEnum.getCode();
        this.errorMsg = this.assembleMsg(exceptionEnum, msg);
    }

    public LogicException(ExceptionEnum exceptionEnum, String msg, Throwable cause) {
        this.errorCode = exceptionEnum.getCode();
        this.errorMsg = this.assembleMsg(exceptionEnum, msg);
        this.cause = cause;
    }

    private String assembleMsg(ExceptionEnum exceptionEnum, String msg) {
        return exceptionEnum.getMsg() + Constant.LOGIC_EXCEPTION_MSG_PREFIX + msg;
    }
}
