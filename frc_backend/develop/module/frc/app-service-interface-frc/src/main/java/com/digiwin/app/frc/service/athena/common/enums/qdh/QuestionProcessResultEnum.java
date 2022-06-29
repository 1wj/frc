package com.digiwin.app.frc.service.athena.common.enums.qdh;

/**
 * @ClassName QuestionProcessTesultEnum
 * @Description TODO
 * @Author author
 * @Date 2021/12/15 10:49
 * @Version 1.0
 **/
public enum QuestionProcessResultEnum {
    Pending(1,"待处理"),
    Consent_to_processing(2,"同意处理"),
    Termination(3,"终止"),
    Will_be_done(4,"会办完成"),
    Returned(5,"已退回");

    private final int code;
    private final String message;

    QuestionProcessResultEnum(int code , String message) {
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
