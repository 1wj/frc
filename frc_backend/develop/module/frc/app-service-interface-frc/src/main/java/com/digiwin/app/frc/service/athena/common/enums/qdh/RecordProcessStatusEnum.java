package com.digiwin.app.frc.service.athena.common.enums.qdh;

/**
 * @ClassName RecordEnum
 * @Description 问题记录相关枚举
 * @Author author
 * @Date 2022/2/10 22:00
 * @Version 1.0
 **/
public enum RecordProcessStatusEnum {

    unStart(0,"未开始"),
    doing(1,"进行中"),
    finish(2,"已完成");

    private final int code;
    private final String message;

    RecordProcessStatusEnum(int code , String message) {
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
