package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/18 17:28
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionClassificationQueryVo {

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
     * 问题归属  1 内部  2外部  3 其他
     */
    @JsonProperty(value = "question_attribution")
    private String questionAttribution;

    /**
     * 是否生效  Y:生效  N:未生效
     */
    @JsonProperty(value = "manage_status")
    private String manageStatus;

    /**
     * 备注
     */
    @JsonProperty(value = "remarks")
    private String remarks;


    @JsonProperty(value = "question_source_info")
    private List<SourceVo> sourceVos;





}
