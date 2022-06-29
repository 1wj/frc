package com.digiwin.app.frc.service.athena.mtw.common.enums;

/**
 * @Author: xieps
 * @Date: 2022/2/21 9:55
 * @Version 1.0
 * @Description
 */
public enum RoleEnum {

    /**
     * 角色
     *
     * "1"-问题反馈人;
     * "2"-问题确认人;
     * "3"-问题分析人
     */
    FEEDBACK_ROLE("1","问题反馈人"),

    CONFIRM_ROLE("2","问题确认人"),

    ANALYSIS_ROLE("3","问题分析人");

    private final String code;
    private final String message;


    RoleEnum(String code , String message) {
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
