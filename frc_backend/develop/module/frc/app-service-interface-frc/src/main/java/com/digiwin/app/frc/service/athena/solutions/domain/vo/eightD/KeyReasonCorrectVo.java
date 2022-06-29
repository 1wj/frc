package com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD;

import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QuestionAttachmentModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.other.QuestionInfoModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.KeyReasonAnalysisModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.QuestionInfo8DSecondModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyReasonCorrectVo implements Serializable {
    private static final long serialVersionUID = 5515046588120412103L;


    @JsonProperty(value = "question_info")
    private List<QuestionInfo8DSecondModel> questionInfos;

    @JsonProperty(value = "attachment_info")
    private List<QuestionAttachmentModel> attachmentModels;

    @JsonProperty(value = "key_reason_analysis")
    private List<KeyReasonAnalysisModel> keyReasonAnalysisModels;

    @JsonProperty(value = "corrective_measure_execute")
    private List<CorrectiveActionVo> correctiveActionVos;
}
