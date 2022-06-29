package com.digiwin.app.frc.service.athena.qdh.domain.model.toBase;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName AttachmentModel
 * @Description TODO
 * @Author HeX
 * @Date 2022/2/22 16:12
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
public class AttachmentModel {

    @JsonProperty(value = "attachment_id")
    private String attachmentId;

    @JsonProperty(value = "dmc_bucket")
    private String dmcBucket;

    @JsonProperty(value = "upload_user_id")
    private String uploadUserId;

    @JsonProperty(value = "upload_user_name")
    private String uploadUserName;

    @JsonProperty(value = "attachment_info")
    private Map<String,Object> attachmentInfo;

}
