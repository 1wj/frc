package com.digiwin.app.frc.service.athena.solutions.domain.model.eightD;

import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QuestionAttachmentModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 *功能描述:问题信息
 * @author cds
 * @date 2022/3/9
 * @param
 * @return
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true )
public class KeyReasonCorrectModel implements Serializable {
    private static final long serialVersionUID = 5289337492204361452L;

    @JsonProperty(value = "question_info")
    private List<QuestionInfo8DSecondModel> questionInfos;

    @JsonProperty(value = "attachment_info")
    private List<QuestionAttachmentModel> attachmentModels;

    @JsonProperty(value = "key_reason_analysis")
    private List<KeyReasonAnalysisModel> keyReasonAnalysisModels;

    @JsonProperty(value = "corrective_measure")
    private List<CorrectiveActionModel> correctiveActionModels;

}
