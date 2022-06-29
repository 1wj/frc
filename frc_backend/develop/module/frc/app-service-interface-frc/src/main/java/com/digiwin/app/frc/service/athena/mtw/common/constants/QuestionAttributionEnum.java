package com.digiwin.app.frc.service.athena.mtw.common.constants;

/**
 * @Author: xieps
 * @Date: 2022/1/10 11:10
 * @Version 1.0
 * @Description  问题归属枚举类
 */
public enum QuestionAttributionEnum {

    /**
     * 问题归属
     *
     * 1-内部  2-外部   3-全部
     */
    INTERNAL("1","内部"),

    EXTERNAL("2","外部"),

    ALL("3","全部");

    private final String code;
    private final String message;


    QuestionAttributionEnum(String code , String message) {
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
