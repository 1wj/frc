package com.digiwin.app.frc.service.athena.common.enums.qdh;

/**
 * @ClassName QuestionTraceActionEnum
 * @Description TODO
 * @Author author
 * @Date 2021/12/15 10:12
 * @Version 1.0
 **/
public enum QuestionProcessStatusEnum {
    Not_sent(1,"未传送"),
    Sent(2,"已传送"),
    Already_read(3,"已读取"),
    Processed(4,"已处理"),
    Terminated(5,"已终止"),
    Return_processing(6,"退回处理"),
    Will_do(7,"已会办"),
    Closed(8,"已结案");


    private final int code;
    private final String message;

    QuestionProcessStatusEnum(int code , String message) {
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
