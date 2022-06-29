package com.digiwin.app.frc.service.athena.mtw.common.enums;

/**
 * @Author: xieps
 * @Date: 2021/11/29 16:41
 * @Version 1.0
 * @Description 问题退回节点编号枚举
 */
public enum QuestionBackNodeNoEnum {

    /**
     * 问题退回节点编号
     *
     * QF-问题确认
     * QIA-问题识别
     * SE002001-问题分配
     * SE002002-问题遏制
     * QIR-问题识别审核
     * QA-问题验收
     * SE002005-问题关闭
     */


    IDENTIFYVIEW("QIR","问题识别审核"),

    FEEDBACK("QF","问题确认"),

    DISCERN("QIA","问题识别处理"),

    DISTRIBUTION("SE002001","问题分配"),

    CONTAIN("SE002002","任务分配"),

    CLOSE("SE002005","问题关闭"),

    ACCEPTANCE("QA","问题验收"),

    EightD1ANDD2("SE001001","问题描述和组建团队");

    private final String code;
    private final String message;


    QuestionBackNodeNoEnum(String code , String message) {
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
