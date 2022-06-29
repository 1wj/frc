package com.digiwin.app.frc.service.athena.common.enums.qdh;

/**
 * @ClassName QuestionProcessStage
 * @Description 问题记录处理步骤
 * @Author author
 * @Date 2022/2/10 21:58
 * @Version 1.0
 **/
public enum QuestionProcessStage {

    QH("QH","问题处理"),
    QS("QS","问题解决");

    private final String code;
    private final String message;

    QuestionProcessStage(String code , String message) {
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
