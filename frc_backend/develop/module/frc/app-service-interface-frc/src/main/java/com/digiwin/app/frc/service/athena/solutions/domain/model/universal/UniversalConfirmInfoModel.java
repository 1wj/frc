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
 * @Date: 2022/4/12 16:13
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UniversalConfirmInfoModel {


    @JsonProperty(value = "question_info")
    private QuestionInfoUniversalModel questionInfoUniversalModel;

    @JsonProperty(value = "question_confirm")
    private QuestionConfirmModel questionConfirmModel;


    @JsonProperty(value = "attachment_info")
    private List<QuestionAttachmentModel> attachmentModels;

    @JsonProperty(value = "process_confirm_verify")
    private ConfirmVerifyModel confirmVerifyModel;
}
