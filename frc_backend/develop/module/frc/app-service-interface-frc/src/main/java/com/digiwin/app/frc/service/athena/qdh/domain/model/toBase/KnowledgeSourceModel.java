package com.digiwin.app.frc.service.athena.qdh.domain.model.toBase;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName knowledgeSourceModel
 * @Description TODO
 * @Author HeX
 * @Date 2022/2/22 16:03
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
public class KnowledgeSourceModel {

    /**
     * 知识来源id
     */
    @JsonProperty(value = "knowledge_source_name")
    private String knowledgeSourceName;

}
