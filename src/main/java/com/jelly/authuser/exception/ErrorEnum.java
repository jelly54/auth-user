package com.jelly.authuser.exception;

/**
 * @author guodongzhang
 */

public enum ErrorEnum {

    /**
     * 通用错误
     */
    COMMON_EXCEPT(10000, "unknown error"),
    /**
     * 非法请求
     */
    INVALID_REQUEST(10001, "invalid request"),
    /**
     * 参数错误
     */
    INVALID_PARAMS(10002, "invalid params"),
    /**
     * 非法用户
     */
    INVALID_USER(10003, "invalid user"),
    /**
     * 权限不足
     */
    NO_PERMISSION(10004, "no permission"),
    /**
     * 用户状态异常
     */
    USER_STATUS_EXCEPT(10005, "user status except"),
    /**
     * 业务错误
     */
    BUSINESS_EXCEPT(10006, "business failed"),
    /**
     * 逻辑错误
     */
    LOGICAL_EXCEPT(10007, "logical error"),
    /**
     * 数据库错误
     */
    DATABASE_EXCEPT(10008, "database error"),
    /**
     * 系统内部错误
     */
    INTERNAL_EXCEPT(10009, "internal error"),
    /**
     * Token 过期
     */
    TOKEN_EXPIRED(10010, "token expired"),
    /**
     * Token 错误
     */
    TOKEN_EXCEPT(10011, "token error");

    private final int code;
    private final String message;

    ErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return this.code;
    }

    public String message() {
        return this.message;
    }


}