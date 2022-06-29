package com.digiwin.app.frc.service.athena.ppc.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/15 17:27
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionConfirmConfigVo {

    /**
     * 问题确认阶段id
     */
    @JsonProperty(value = "confirm_config_id")
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


    @JsonProperty(value = "classification_id")
    private String classificationId;

    @JsonProperty(value = "classification_name")
    private String classificationName;


    @JsonProperty(value = "liable_person_id")
    private String liablePersonId;

    @JsonProperty(value = "liable_person_name")
    private String liablePersonName;


    private String feedbackDepartmentMessage;

    @JsonProperty(value = "risk_level_info")
    private List<RiskLevelVo> riskVos;
}
