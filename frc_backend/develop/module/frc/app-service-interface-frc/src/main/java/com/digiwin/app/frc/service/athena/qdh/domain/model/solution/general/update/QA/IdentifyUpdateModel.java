package com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QA;

import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QuestionAttachmentModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.other.QuestionInfoModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdentifyUpdateModel{

    /**
     * question_info
     */
    @JsonProperty(value = "question_info")
    private List<QuestionInfoModel> questionInfos;

    /**
     * 附件
     */
    @JsonProperty(value = "attachment_info")
    private List<QuestionAttachmentModel> attachmentModels;
    /**
     * 问题识别信息
     */
    @JsonProperty(value = "question_identify_info")
    private List<QuestionIdentifyDetailModel> questionIdentifyInfo;

}
