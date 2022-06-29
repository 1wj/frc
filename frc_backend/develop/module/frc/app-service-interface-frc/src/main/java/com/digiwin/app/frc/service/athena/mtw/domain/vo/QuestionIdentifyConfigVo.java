package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/1/4 13:06
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionIdentifyConfigVo {

    /**
     * 主键
     */
    @JsonProperty(value = "identify_config_id")
    private String oid;


    /**
     * 分类编号
     */
    @JsonProperty(value = "classification_no")
    private String classificationNo;

    /**
     * 分类名称
     */
    @JsonProperty(value = "classification_name")
    private String classificationName;

    /**
     * 负责人id
     */
    @JsonProperty(value = "liable_person_id")
    private String liablePersonId;

    /**
     * 负责人名称
     */
    @JsonProperty(value = "liable_person_name")
    private String liablePersonName;



}
