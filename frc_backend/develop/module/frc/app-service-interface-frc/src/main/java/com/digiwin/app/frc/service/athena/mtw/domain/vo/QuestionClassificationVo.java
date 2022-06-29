package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2021/11/8 23:20
 * @Version 1.0
 * @Description  回传前端实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionClassificationVo {

    /**
     * 问题分类  主键
     */
    @JsonProperty(value = "classification_id")
    private String oid;

    /**
     *问题分类编号
     */
    @JsonProperty(value = "classification_no")
    private String classificationNo;

    /**
     *分类名称
     */
    @JsonProperty(value = "classification_name")
    private String classificationName;

    /**
     * 问题归属
     */
    @JsonProperty(value = "question_attribution")
    private String questionAttribution;

    /**
     * 是否生效  1:是  0:否
     */
    @JsonProperty(value = "manage_status")
    private String  manageStatus;

    /**
     * 备注
     */
    @JsonProperty(value = "remarks")
    private String remarks;

    @JsonProperty(value = "source_name")
    private String sourceName;

    @JsonProperty(value = "source_no")
    private String sourceNo;
}
