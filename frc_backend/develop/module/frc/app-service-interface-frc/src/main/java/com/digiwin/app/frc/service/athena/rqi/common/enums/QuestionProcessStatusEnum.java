package com.digiwin.app.frc.service.athena.rqi.common.enums;

/**
 * @Author: xieps
 * @Date: 2022/1/3 23:09
 * @Version 1.0
 * @Description 问题处理状态枚举
 */
public enum  QuestionProcessStatusEnum {

    /**
     * 问题处理状态
     *
     * 0： 全部
     * 1： 处理中
     * 2： 已处理
     */


    ALL("0","全部"),

    PROCESSING("1","处理中"),

    FINISH("2","已处理");

    private final String code;
    private final String message;


    QuestionProcessStatusEnum(String code , String message) {
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
