package com.digiwin.app.frc.service.athena.ppc.domain.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author:zhangzlz
 * @Date 2022/3/11   14:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiablePersonConfigVo {
    /**
     * 问题责任人配置主键
     */
    @JsonProperty(value = "config_id")
    private String oid;

    /**
     * 配置标识
     */
    @JsonProperty(value = "config_flag")
    private String configFlag;

    /**
     * 问题归属编号
     */
    @JsonProperty(value = "attribution_no")
    private String attributionNo;

    /**
     * 风险等级主键
     */
    @JsonProperty(value = "risk_level_id")
    private String riskLevelId;


    @JsonProperty(value = "source_id")
    private String sourceId;

    /**
     * 问题分类主键
     */
    @JsonProperty(value = "classification_info")
    private List<ClassificationVo> classificationInfo;

    @JsonProperty(value = "solution_id")
    private String solutionId;

    @JsonProperty(value = "solution_name")
    private String solutionName;

    @JsonProperty(value = "liable_person_id")
    private String liablePersonId;

    @JsonProperty(value = "liable_person_name")
    private String liablePersonName;

    @JsonProperty(value = "acceptance_role")
    private String acceptanceRole;


}
