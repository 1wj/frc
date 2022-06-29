package com.digiwin.app.frc.service.athena.ppc.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
*@Author Jiangyw
*@Date 2022/3/11
*@Time 13:59
*@Version 1.0
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionLiablePersonDepartmentLatitudeConfigModel {
    /**
     * 主键
     */
    @JSONField(name = "config_id")
    private String oid;

    /**
     * 租户
     */
    @JSONField(name = "tenantsid")
    private Long tenantsid;

    /**
     * 配置标识
     */
    @JSONField(name = "config_flag")
    private String configFlag;

    /**
     * 问题归属编号
     */
    @JSONField(name = "attribution_no")
    private String attributionNo;

    /**
     * 风险等级主键
     */
    @JSONField(name = "risk_level_id")
    private String riskLevelId;

    /**
     * 反馈部门信息
     */
    @JSONField(name = "feedback_Departments")
    private String feedbackDepartments;

    /**
     * 负责人id
     */
    @JSONField(name = "liable_person_id")
    private String liablePersonId;

    /**
     * 负责人name
     */
    @JSONField(name = "liable_person_name")
    private String liablePersonName;

    /**
     * 验收人角色
     */
    @JSONField(name = "acceptance_role")
    private String acceptanceRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createName;

    /**
     *修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateName;
}
