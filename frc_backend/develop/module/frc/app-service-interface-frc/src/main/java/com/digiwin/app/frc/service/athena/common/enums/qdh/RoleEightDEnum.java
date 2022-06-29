package com.digiwin.app.frc.service.athena.common.enums.qdh;

/**
 * @Author: xieps
 * @Date: 2022/3/8 21:23
 * @Version 1.0
 * @Description 角色枚举类
 */
public enum RoleEightDEnum {

    /**
     * GROUP_LEADER  1  组长
     * TEAM_MEMBER   2  组员
     */
    GROUP_LEADER("1","组长"),
    TEAM_MEMBER("2","组员");

    private final String code;
    private final String message;

    RoleEightDEnum(String code , String message) {
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
