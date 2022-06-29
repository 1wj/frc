package com.digiwin.app.frc.service.athena.mtw.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

/**
 * @Author: xieps
 * @Date: 2022/1/4 12:45
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionIdentifyConfigModel {

    /**
     * 主键
     */
    @JSONField(name = "identify_config_id")
    @JsonProperty(value = "identify_config_id")
    private String oid;


    /**
     * 分类编号
     */
    @Length(max = 32,message = "classification_no_overLength")
    @JSONField(name = "classification_no")
    @JsonProperty(value = "classification_no")
    private String classificationNo;

    /**
     * 分类名称
     */
    @JSONField(name = "classification_name")
    @JsonProperty(value = "classification_name")
    private String classificationName;

    /**
     * 负责人id
     */
    @Length(max = 32,message = "liable_person_id_overLength")
    @JSONField(name = "liable_person_id")
    @JsonProperty(value = "liable_person_id")
    private String liablePersonId;

    /**
     * 负责人名称
     */
    @Length(max = 32,message = "liable_person_name_overLength")
    @JSONField(name = "liable_person_name")
    @JsonProperty(value = "liable_person_name")
    private String liablePersonName;


}
