package com.digiwin.app.frc.service.athena.solutions.domain.model.eightD;

import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QuestionAttachmentModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName PreventionMeasureExecuteModel
 * @Description TODO
 * @Author HeX
 * @Date 2022/3/19 1:19
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PreventionMeasureExecuteInfoModel {

    @JsonProperty(value = "question_info")
    private List<QuestionInfo8DModel> questionInfos;

    @JsonProperty(value = "attachment_info")
    private List<QuestionAttachmentModel> attachmentModels;

    @JsonProperty(value = "prevention_measure_execute")
    private List<PreventionMeasureExecuteModel> prevention_measure_execute;

}
