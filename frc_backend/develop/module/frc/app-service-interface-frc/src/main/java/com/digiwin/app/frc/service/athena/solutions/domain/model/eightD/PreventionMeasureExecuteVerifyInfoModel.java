package com.digiwin.app.frc.service.athena.solutions.domain.model.eightD;

import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QuestionAttachmentModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName PreventionMeasureExecuteVerifyModel
 * @Description 预防措施验证
 * @Author HeX
 * @Date 2022/3/20 10:09
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PreventionMeasureExecuteVerifyInfoModel {

    @JsonProperty(value = "question_info")
    private List<QuestionInfo8DSecondModel> questionInfos;

    @JsonProperty(value = "attachment_info")
    private List<QuestionAttachmentModel> attachmentModels;

    @JsonProperty(value = "prevention_measure_execute_verify")
    private List<PreventionMeasureExecuteVerifyModel> preventionMeasureExecuteVerify;

}
