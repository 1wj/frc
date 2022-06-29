package com.digiwin.app.frc.service.athena.qdh.domain.model.toBase;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @ClassName KnowledgeTypeInfo
 * @Description TODO
 * @Author HeX
 * @Date 2022/2/22 16:02
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
public class KnowledgeTypeModel {
    @JsonProperty(value = "knowledge_type_id")
    private String knowledgeTypeId;

    @JsonProperty(value = "knowledge_type_no")
    private String knowledgeTypeNo;
}
