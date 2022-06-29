package com.digiwin.app.frc.service.athena.mtw.common.enums;

/**
 * @Author: xieps
 * @Date: 2021/11/29 10:21
 * @Version 1.0
 * @Description 问题来源分类编号枚举
 */
public enum SourceClassificationNoEnum {

    /**
     * 问题来源分类编号
     *
     * 1-内部  2-外部   3-其他
     */
    INTERNAL("1","内部"),

    EXTERNAL("2","外部"),

    OTHER("3","其他");

    private final String code;
    private final String message;


    SourceClassificationNoEnum(String code , String message) {
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
