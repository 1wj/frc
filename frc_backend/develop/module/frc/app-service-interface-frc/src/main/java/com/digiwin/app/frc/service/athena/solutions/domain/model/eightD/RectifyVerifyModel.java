package com.digiwin.app.frc.service.athena.solutions.domain.model.eightD;

import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QuestionAttachmentModel;
import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.CorrectiveMeasureVerifyVo;
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
public class RectifyVerifyModel implements Serializable {
    private static final long serialVersionUID = 57317374098200335L;
    @JsonProperty(value = "question_info")
    private List<QuestionInfo8DSecondModel> questionInfos;

    @JsonProperty(value = "attachment_info")
    private List<QuestionAttachmentModel> attachmentModels;

    @JsonProperty(value = "corrective_measure_verify")
    private List<CorrectiveMeasureVerifyVo> correctiveMeasureVerifyVos;
}
