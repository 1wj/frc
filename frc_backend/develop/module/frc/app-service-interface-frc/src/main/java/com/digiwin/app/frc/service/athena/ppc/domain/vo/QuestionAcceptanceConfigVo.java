package com.digiwin.app.frc.service.athena.ppc.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/17 17:06
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAcceptanceConfigVo {

    /**
     * 问题确认阶段id
     */
    @JsonProperty(value = "acceptance_config_id")
    private String oid;


    /**
     * 问题归属编号
     */
    @JsonProperty(value = "attribution_no")
    private String attributionNo;


    @JsonProperty(value = "source_id")
    private String sourceId;

    @JsonProperty(value = "source_name")
    private String sourceName;

    @JsonProperty(value = "role")
    private String role;

    private String feedbackDepartmentMessage;

    @JsonProperty(value = "classification_info")
    private List<ClassificationVo> classificationVos;

    @JsonProperty(value = "risk_level_info")
    private List<RiskLevelVo> riskVos;




}
