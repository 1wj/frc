package com.digiwin.app.frc.service.athena.ppc.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/17 11:19
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionProcessConfigVo {

    /**
     * 问题确认阶段id
     */
    @JsonProperty(value = "process_config_id")
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


    @JsonProperty(value = "liable_person_id")
    private String liablePersonId;

    @JsonProperty(value = "liable_person_name")
    private String liablePersonName;


    @JsonProperty(value = "solution_step_id")
    private String solutionStepId;

    @JsonProperty(value = "solution_step_name")
    private String solutionStepName;


    @JsonProperty(value = "risk_level_info")
    private List<RiskLevelVo> riskVos;

    @JsonProperty(value = "classification_info")
    private List<ClassificationVo> classificationVos;


}
