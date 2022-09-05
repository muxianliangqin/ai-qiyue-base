package com.qiyue.infrastructure.enums;

/**
 * [Q][B][U]-[LOGIN]-[00001]
 * Q:系统简称->ai-qiyue
 * B:异常类型简称->business exception
 * U:子系统简称,U->user,C->crawler,G-global(全局的)
 * <p>
 * 异常类型有如下几类：
 * A-系统运行时异常，runtime exception
 * B-业务异常，business exception 主动抛出
 * S-编译时异常 compile exception，主动包装
 */
public enum ExceptionEnum {
    /**
     *
     */
    SUCCESS("00000", "success"),
    /**
     * 运行时异常
     */
    RUNTIME_EXCEPTION("QGS_RUNTIME_000001", "服务异常"),
    /**
     * 登录
     */
    QBU_LOGIN_USERNAME_OR_PASSWORD_IS_WRONG("QUB_LOGIN_000001", "用户名或密码不正确"),
    QBU_LOGIN_ONE_BROWSER_ONLY_ONE_USER("QUB_LOGIN_000002", "同一个浏览器只允许一个用户登录"),
    QBU_LOGIN_TOKEN_IS_WRONG("QUB_LOGIN_000003", "token码错误"),
    /**
     * 参数检验 param verify
     */
    QBG_VALID_OBJECT_NON_NULL("QGB_VALID_000001", "参数不能为空"),
    QBG_VALID_COLLECTION_NOT_EMPTY("QGB_VALID_000002", "集合不能为空"),
    QBG_VALID_VALUE_NOT_IN("QGB_VALID_000003", "值不在集合中"),
    QBG_VALID_EXPRESSION_COME_OUT_FALSE("QGB_VALID_000004", "表达式结果为false"),
    QBG_VALID_ENUM_NOT_EXISTS("QGB_VALID_000005", "枚举中不存在目标值"),
    /**
     *
     *  数据库数据异常
     */
    QBG_DATABASE_RECORD_NOT_FOUND("QGB_DATABASE_000001", "记录不存在"),
    QBG_DATABASE_RECORD_HAS_EXIST("QGB_DATABASE_000002", "记录已存在"),
    QBG_DATABASE_INSERT_EXCEPTION("QGB_DATABASE_000003", "记录插入失败"),
    /**
     * 加解密
     */
    QSG_CIPHER_EXCEPTION("QSG_CIPHER_000001", "加密失败"),

    /* 自定义类 */
    RANDOM_STRING_NO_SUCH_TYPE("A1000", "生成随机字符串没有这个类型：{}"),
    /* 转换TreeNode相关的错误 */
    ELEMENT_NOT_NULL_DATA("A1100", "转换树节点元素时，值不能为空"),
    ELEMENT_NOT_NULL_NODE_ID("A1101", "转换树节点元素时，nodeId的值不能为空"),

    /*枚举转换映射相关的错误 */
    // 第一个参数枚举名称，第二参数枚举值属性，第三个参数 当前值
    ENUM_VALUE_NOT_FOUND("A1200", "枚举对象【{}:{}】没有这个值【{}】"),

    /*通用分类 */
    UNKNOWN_ERROR("S9999", "系统未知错误");

    private final String code;

    private final String msg;

    ExceptionEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
