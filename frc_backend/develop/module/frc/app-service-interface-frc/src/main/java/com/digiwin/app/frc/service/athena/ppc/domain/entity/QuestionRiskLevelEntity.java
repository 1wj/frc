package com.digiwin.app.frc.service.athena.ppc.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2022/2/10 13:04
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRiskLevelEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户id
     */
    private Long tenantSid;

    /**
     * 风险等级编号
     */
    private String riskLevelNo;

    /**
     * 风险等级名称
     */
    private String riskLevelName;

    /**
     * 重要性    1：重要    2：不重要
     */
    private Integer important;

    /**
     * 紧急度  1：紧急 2：不紧急
     */
    private Integer urgency;

    /**
     * 是否可操作   Y:可修改  N:不可修改
     */
    private String isModify;

    /**
     * 问题确认阶段主键
     */
    private String questionConfirmConfigOid;

    /**
     * 问题分析阶段主键
     */
    private String questionAnalysisConfigOid;

    /**
     * 问题处理阶段主键
     */
    private String questionProcessConfigOid;

    /**
     * 问题验收阶段主键
     */
    private String questionAcceptanceConfigOid;

    /**
     * 是否生效  Y 生效  V 失效
     */
    private String manageStatus;

    /**
     * 备注
     */
    private String remarks;

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

    /**
     * 是否上版  Y:上版  N:不上版
     */
    private String isUpload;
}
