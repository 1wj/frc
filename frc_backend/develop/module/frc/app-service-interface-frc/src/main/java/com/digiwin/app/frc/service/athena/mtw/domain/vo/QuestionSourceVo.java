package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2021/11/15 13:33
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSourceVo {


    /**
     * 主键
     */
    @JsonProperty(value = "source_id")
    @JSONField(name = "source_id")
    private String oid;

    /**
     * 来源编号
     */
    @JsonProperty(value = "source_no")
    @JSONField(name = "source_no")
    private String sourceNo;

    /**
     * 来源名称
     */
    @JsonProperty(value = "source_name")
    @JSONField(name = "source_name")
    private String sourceName;

    /**
     * 来源分类
     */
    @JsonProperty(value = "source_classification_no")
    @JSONField(name = "source_classification_no")
    private String sourceCategory;

    /**
     * 是否生效
     */
    @JsonProperty(value = "manage_status")
    @JSONField(name = "manage_status")
    private String manageStatus;

    /**
     * 备注
     */
    @JsonProperty(value = "remarks")
    @JSONField(name = "remarks")
    private String remarks;



    @JsonProperty(value = "classification_name")
    private String classificationName;

}
