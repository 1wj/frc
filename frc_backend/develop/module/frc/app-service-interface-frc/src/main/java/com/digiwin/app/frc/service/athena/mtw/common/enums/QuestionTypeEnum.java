package com.digiwin.app.frc.service.athena.mtw.common.enums;

/**
 * @Author: xieps
 * @Date: 2022/1/11 11:18
 * @Version 1.0
 * @Description 问题类型枚举类
 */
public enum QuestionTypeEnum {

    /**
     * 问题类型
     *
     * 1-质量问题
     * 2-事务问题
     */
    QualityIssues("1","质量问题"),

    BusinessProblem("2","事务问题");

    private final String code;
    private final String message;


    QuestionTypeEnum(String code , String message) {
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
