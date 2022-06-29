package com.digiwin.app.frc.service.athena.solutions.domain.model.universal;

import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QuestionAttachmentModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true )
public class TemporaryMeasuresExecuteVerifyInfoModel implements Serializable {

    private static final long serialVersionUID = 4157685849750624159L;

    @JsonProperty(value = "question_info")
    private QuestionInfoUniversalModel questionInfoUniversalModels;

    @JsonProperty(value = "attachment_info")
    private List<QuestionAttachmentModel> attachmentModels;

    @JsonProperty(value = "temporary_measure_execute_verify")
    private List<TemporaryMeasuresExecuteVerifyModel> temporaryMeasureModels;

    @JsonProperty(value = "question_confirm")
    private QuestionConfirmModel questionConfirmModel;
}
