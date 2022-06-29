package com.digiwin.app.frc.service.athena.ppc.domain.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionLiablePersonDepartmentLatitudeConfigVo {

    /**
     * 主键
     */
    @JsonProperty("config_id")
    private String oid;

    /**
     * 配置标识
     */
    @JsonProperty("config_flag")
    private String configFlag;

    /**
     * 问题归属编号
     */
    @JsonProperty("attribution_no")
    private String attributionNo;

    /**
     * 风险等级主键
     */
    @JsonProperty("risk_level_id")
    private String riskLevelId;

    /**
     * 反馈部门信息
     */
    @JsonProperty("feedback_department_message")
    private List<FeedbackDepartmentVo> feedbackDepartmentMessage;

    /**
     * 负责人id
     */
    @JsonProperty("liable_person_id")
    private String liablePersonId;

    /**
     * 负责人name
     */
    @JsonProperty("liable_person_name")
    private String liablePersonName;

    /**
     * 验收人角色
     */
    @JsonProperty("acceptance_role")
    private String acceptanceRole;

}
