package com.digiwin.app.frc.service.athena.app.common.enums;

/**
 * @Author: heX
 * @Date: 2022/06/08 22:32
 * @Version 1.0
 * @Description 用户角色枚举类
 */
public enum UserRoleEnum {

    GENERAL(0,"普通用户"),

    ADMIN(1,"管理员");

    private final int code;
    private final String message;


    UserRoleEnum(int code , String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
