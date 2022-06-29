package com.digiwin.app.frc.service.athena.qdh.domain.model.toBase;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName KnowledgeModel
 * @Description 知识入库model
 * @Author HeX
 * @Date 2022/2/22 15:37
 * @Version 1.0
 **/
@Data
public class KnowledgeModel {
    /**
     * 知识编号
     */
    @JsonProperty(value = "knowledge_no")
    private String knowledgeNo;

    /**
     * 知识作者
     */
    @JsonProperty(value = "knowledge_author")
    private String knowledgeAuthor;

    /**
     * 知识名称
     */
    @JsonProperty(value = "knowledge_name")
    private String knowledgeName;

    /**
     * 知识描述
     */
    @JsonProperty(value = "knowledge_desc")
    private String knowledgeDesc;

    /**
     * 知识类别信息
     */
    @JsonProperty(value = "knowledge_type_info")
    private List<KnowledgeTypeModel> knowledgeTypeModels;

    /**
     * 知识来源信息
     */
    @JsonProperty(value = "knowledge_source_info")
    private List<KnowledgeSourceModel> knowledgeSourceModels;

    /**
     * 知识标签信息
     */
    @JsonProperty(value = "knowledge_tag_info")
    private List<KnowledgeTagModel> knowledgeTagModels;

    /**
     * 附件信息
     */
    @JsonProperty(value = "attachment_info")
    private List<AttachmentModel> attachmentModels;



}
