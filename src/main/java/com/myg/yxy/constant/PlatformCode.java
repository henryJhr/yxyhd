package com.myg.yxy.constant;

import com.hxsoft.hsf.boot.framework.constant.MessageCode;

public enum PlatformCode implements MessageCode {
    /**
     * 基础编码定义
     */
    NO_LOGIN("1001", "用户未登录"),
    NO_AUTHORITY("1002", "用户没有权限"),
    NO_USER_EXIST("1003", "用户不存在"),
    PASSWORD_ERROR("1004","密码不正确"),
    USER_LOCKED("1005","账号已被锁定"),
    CLEAR_SUCCESS("1006","清空成功"),
    CLEAR_ERROR("1006","清空失败");

    private String code;
    private String message;

    PlatformCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return message;
    }
}
