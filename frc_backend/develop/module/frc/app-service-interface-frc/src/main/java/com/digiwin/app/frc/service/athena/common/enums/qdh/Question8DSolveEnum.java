package com.digiwin.app.frc.service.athena.common.enums.qdh;

/**
 * @ClassName Question8DSolveEnum
 * @Description 8D-枚举
 * @Author HeX
 * @Date 2022/3/8 2:54
 * @Version 1.0
 **/
public enum Question8DSolveEnum {
    /**
     *
     */
    form_team("SE001001","问题描述&团队组建"),
    containment_measure("SE001002","围堵措施"),
    containment_measure_execute("SE001003","围堵措施执行"),
    containment_measure_verify("SE001004","围堵措施执行验证"),
    key_reason("SE001005","根本原因"),
    reason_correct("SE001006","纠正措施"),
    key_reason_correct("SE001007","根本原因&纠正措施"),
    correct_execute("SE001008","纠正措施执行"),
    correct_verify("SE001009","纠正措施执行验证"),
    precaution("SE001010","预防措施"),
    precaution_execute("SE001011","预防措施执行"),
    precaution_verify("SE001012","预防措施执行验证"),
    confirm("SE001013","处理确认"),
    feedback_person_verify("SE001014","短期结案验收");

    private final String code;
    private final String message;

    Question8DSolveEnum(String code , String message) {
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
