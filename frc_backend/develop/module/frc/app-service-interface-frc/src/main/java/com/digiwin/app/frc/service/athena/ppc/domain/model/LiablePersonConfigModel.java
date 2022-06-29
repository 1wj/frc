package com.digiwin.app.frc.service.athena.ppc.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.ClassificationVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * @Author:zhangzlz
 * @Date 2022/3/11   10:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiablePersonConfigModel {

    /**
     * 主键
     */
    @JSONField(name = "config_id")
    private String oid;

    /**
     * 租户id
     */
    @JSONField(name = "tenantsid")
    private Long tenantSid;

    /**
     * 配置标识
     */
    @JSONField(name = "config_flag")
    private String configFlag;

    /**
     * 问题归属
     */
    @JSONField(name = "attribution_no")
    private String attributionNo;

    /**
     * 问题来源主键
     */
    @JSONField(name = "source_id")
    private String sourceOid;


    /**
     * 问题分类主键
     */
    @JSONField(name = "classification_info")
    private List<ClassificationVo> classificationInfo;


    @JSONField(name = "solution_id")
    private String solutionOid;

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


    /**
     * 风险等级名称
     */
    @JSONField(name = "risk_level_id")
    private String riskLevelId;

    /**
     * 验收人角色
     */
    @JSONField(name = "acceptance_role")
    private String acceptanceRole;
}
