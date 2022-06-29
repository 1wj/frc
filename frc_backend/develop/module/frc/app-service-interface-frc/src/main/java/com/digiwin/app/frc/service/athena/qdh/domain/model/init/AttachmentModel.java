package com.digiwin.app.frc.service.athena.qdh.domain.model.init;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName AttachmentModel
 * @Description 问题发起-附件model
 * @Author author
 * @Date 2022/2/10 23:27
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentModel {
    /**
     * 附件名称
     */
    @JsonProperty(value = "attachment_name")
    private String attachmentName;

    /**
     * 附件dmc id
     */
    @JsonProperty(value = "attachment_id")
    private String attachmentId;

    /**
     * 上传者id
     */
    @JsonProperty(value = "upload_person_id")
    private String uploadPersonId;

    /**
     * 上传者name
     */
    @JsonProperty(value = "upload_person_name")
    private String uploadPersonName;

    /**
     * 所属阶段
     */
    @JsonProperty(value = "attachment_belong_stage")
    private String attachmentBelongStage;
}
