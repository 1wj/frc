package com.digiwin.app.frc.service.athena.ppc.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/2/17 15:03
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnalysisConfigModel {

    /**
     * 主键
     */
    @JSONField(name = "analysis_config_id")
    private String oid;

    /**
     * 租户id
     */
    @JSONField(name = "tenantsid")
    private Long tenantSid;

    /**
     * 问题归属
     */
    @JSONField(name = "attribution_no")
    private String attributionNo;

    /**
     * 问题来源主键
     */
    @JSONField(name = "source_id")
    private String sourceId;


    @JSONField(name = "risk_level_id")
    private String riskLevelId;


    @JSONField(name = "classification_id")
    private String classificationId;

    /**
     * 负责人id
     */
    @JSONField(name = "liable_person_id")
    private String liablePersonId;


    /**
     * 负责人名称
     */
    @JSONField(name = "liable_person_name")
    private String liablePersonName;




}
