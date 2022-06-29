package com.digiwin.app.frc.service.athena.ppc.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/2/15 16:59
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionConfirmConfigQueryModel {

    /**
     * 问题归属编号
     */
    @JSONField(name = "attribution_no")
    private String attributionNo;

    /**
     * 风险等级主键
     */
    @JSONField(name = "risk_level_id")
    private String riskLevelId;

    /**
     * 问题来源主键
     */
    @JSONField(name = "source_id")
    private String sourceId;

    /**
     * 问题分类主键
     */
    @JSONField(name = "classification_id")
    private String classificationId;

    /**
     * 风险等级名称
     */
    @JSONField(name = "risk_level_name")
    private String riskLevelName;

    /**
     * 问题分类名称
     */
    @JSONField(name = "classification_name")
    private String classificationName;

    /**
     * 问题来源名称
     */
    @JSONField(name = "source_name")
    private String sourceName;

    /**
     * 反馈部门信息
     */
    @JSONField(name = "feedback_department_message")
    private String feedbackDepartmentMessage;

    /**
     * 负责人信息
     */
    @JSONField(name = "liable_person_message")
    private String liablePersonMessage;

}
