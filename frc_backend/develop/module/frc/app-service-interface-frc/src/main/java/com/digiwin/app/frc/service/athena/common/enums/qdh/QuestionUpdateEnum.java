package com.digiwin.app.frc.service.athena.common.enums.qdh;

/**
 * @ClassName QuestionUpdateEnum
 * @Description 枚举 更新问题追踪状态
 * @Author author
 * @Date 2021/11/11 22:47
 * @Version 1.0
 **/
public enum QuestionUpdateEnum {

    // 为了减少更新代码量，故命名采用较早版本命名
    question_feedbackL("QFL","问题反馈"),

    question_feedback("QF","问题确认"),

    question_identification("QIA","问题识别"),

    question_identification_review("QIR","问题识别审核"),

    question_solve("QS","问题解决"),

    question_acceptance("QA","问题验收");

    private final String code;
    private final String message;

    QuestionUpdateEnum(String code , String message) {
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
