package com.digiwin.app.frc.service.athena.ppc.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/2/17 16:58
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAcceptanceConfigModel {

    /**
     * 主键
     */
    @JSONField(name = "acceptance_config_id")
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
     * 验收人角色
     */
    @JSONField(name = "role")
    private String role;



}
