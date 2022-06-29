package com.digiwin.app.frc.service.athena.app.common.enums;

/**
 * @Author: heX
 * @Date: 2022/06/08 22:32
 * @Version 1.0
 * @Description IAM-FRC角色枚举类
 */
public enum IAMRoleEnum {

    GENERAL("r001","普通用户"),

    GENERAL_CATEGORY("defaultRoleCatalog","普通用户分类编号");

    private final String code;
    private final String message;


    IAMRoleEnum(String code , String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
