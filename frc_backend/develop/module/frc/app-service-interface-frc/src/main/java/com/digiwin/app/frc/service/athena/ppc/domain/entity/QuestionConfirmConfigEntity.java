package com.digiwin.app.frc.service.athena.ppc.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2022/2/15 15:16
 * @Version 1.0
 * @Description 问题确认配置entity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionConfirmConfigEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户id
     */
    private Long tenantSid;

    /**
     * 归属编号
     */
    private String attributionNo;

    /**
     * 来源主键
     */
    private String sourceId;

    /**
     * 分类主键
     */
    private String classificationId;



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
}
