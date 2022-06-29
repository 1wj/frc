package com.digiwin.app.frc.service.athena.common.enums.qdh;

/**
 * @Author: xieps
 * @Date: 2022/4/6 15:05
 * @Version 1.0
 * @Description  通用解决方案枚举类
 */
public enum QuestionUniversalSolveEnum {

    /**
     *
     */
    plan_arrange("SE003001","原因分析&计划安排"),
    temporary_measures("SE003002","临时措施"),
    temporary_measures_execute("SE003003","临时措施执行"),
    temporary_measures_execute_verify("SE003004","临时措施执行验证"),
    short_term_closing_acceptance("SE003005","短期结案验收"),
    permanent_measures("SE003006","恒久措施"),
    permanent_measures_execute("SE003007","恒久措施执行"),
    permanent_measures_execute_verify("SE003008","恒久措施执行验证"),
    process_confirmation("SE003009","处理确认");

    private final String code;
    private final String message;

    QuestionUniversalSolveEnum(String code , String message) {
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
