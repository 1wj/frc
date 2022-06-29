package com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QuestionAttachmentModel
 * @Description TODO
 * @Author author
 * @Date 2021/11/24 19:40
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAttachmentModel {

    @JsonProperty(value = "attachment_name")
    private String attachmentName;

    @JsonProperty(value = "attachment_id")
    private String attachmentId;

    @JsonProperty(value = "upload_person_name")
    private String uploadPersonName;

    @JsonProperty(value = "upload_person_id")
    private String uploadPersonId;

    @JsonProperty(value = "attachment_belong_stage")
    private String attachmentBelongStage;
}
