package com.digiwin.app.frc.service.athena.common.enums.qdh;

/**
 * @ClassName QuestionSloveEnum
 * @Description TODO
 * @Author author
 * @Date 2021/11/19 0:55
 * @Version 1.0
 **/
public enum QuestionSolveEnum {
    /**
     *
     */
    question_distribution("SE002001","问题分配"),
    question_curb_distribution("SE002002","任务分配"),
    question_curb("SE002003","任务处理"),
    question_verify("SE002004","任务处理验收"),
    question_close("SE002005","任务关闭");

    private final String code;
    private final String message;

    QuestionSolveEnum(String code , String message) {
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
