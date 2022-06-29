package com.digiwin.app.frc.service.athena.solutions.domain.model.universal;

import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QuestionAttachmentModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/4/6 20:22
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanArrangeModel {

    @JsonProperty(value = "question_info")
    private QuestionInfoUniversalModel questionInfos;

    @JsonProperty(value = "attachment_info")
    private List<QuestionAttachmentModel> attachmentModels;

    @JsonProperty(value = "reason_analysis")
    private ReasonAnalysisModel reasonAnalysisModel;


    @JsonProperty(value = "plan_arrange")
    private List<UniversalPlanArrangeModel> universalPlanArrangeModel;

    @JsonProperty(value = "question_confirm")
    private QuestionConfirmModel questionConfirmModel;
}
