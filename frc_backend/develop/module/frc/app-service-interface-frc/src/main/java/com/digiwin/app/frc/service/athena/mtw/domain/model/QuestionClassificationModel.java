package com.digiwin.app.frc.service.athena.mtw.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.internal.xml.mapping.MappingXmlParser;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/14 22:04
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionClassificationModel {

    /**
     * 问题分类  主键
     */
    @JSONField(name = "classification_id")
    private String oid;


    /**
     *问题分类编号
     */
    @Length(max = 32,message = "classification_no_overLength")
    @JSONField(name = "classification_no")
    private String classificationNo;

    /**
     *分类名称
     */
    @JSONField(name = "classification_name")
    private String classificationName;

    /**
     * 问题归属
     */
    @JSONField(name = "question_attribution")
    private String questionAttribution;

    /**
     * 是否生效  Y:生效  N:未生效
     */
    @JSONField(name = "manage_status")
    private String manageStatus;

    /**
     * 备注
     */
    @JSONField(name = "remarks")
    private String remarks;



}
