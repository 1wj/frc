package com.digiwin.app.frc.service.athena.qdh.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @ClassName AttachmentEntity
 * @Description TODO
 * @Author author
 * @Date 2021/11/22 15:44
 * @Version 1.0
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class AttachmentEntity extends BaseEntity{

    private String oid;

    /**
     * data_instance_oid
     */
    private String dataInstanceOid;

    /**
     * question_id
     */
    private String questionNo;

    /**
     * attachment_title
     */
    private String attachmentTitle;

    /**
     * extension_name
     */
    private String extensionName;

    /**
     * attachment_description
     */
    private String attachmentDescription;

    /**
     * attachment_type
     */
    private Integer attachmentType;

    /**
     * dmcid
     */
    private String dmcId;
}
