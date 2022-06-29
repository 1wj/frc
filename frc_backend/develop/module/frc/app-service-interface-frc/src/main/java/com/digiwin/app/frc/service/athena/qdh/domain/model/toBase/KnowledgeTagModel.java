package com.digiwin.app.frc.service.athena.qdh.domain.model.toBase;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName knowledgeTagModel
 * @Description TODO
 * @Author HeX
 * @Date 2022/2/22 16:05
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
public class KnowledgeTagModel {
    @JsonProperty(value = "knowledge_tag_id")
    private String knowledgeTagId;
}
