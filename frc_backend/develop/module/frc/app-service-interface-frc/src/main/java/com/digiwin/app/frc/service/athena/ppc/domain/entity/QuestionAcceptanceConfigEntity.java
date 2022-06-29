package com.digiwin.app.frc.service.athena.ppc.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2022/2/17 16:39
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAcceptanceConfigEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户id
     */
    private Long tenantSid;

    private String attributionNo;

    /**
     * 来源主键
     */
    private String sourceId;


    /**
     * 验收人角色   1-问题反馈人;2-问题确认人;3-问题分析人
     */
    private String role;


    /**
     * 反馈部门信息
     */
    private String feedbackDepartmentMessage;

    /**
     * 负责人id
     */
    private String liablePersonId;

    /**
     * 负责人名称
     */
    private String liablePersonName;



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

    private String riskLevelInfo;

    private String feedBackDepartmentInfo;

    private String classificationInfo;


}
