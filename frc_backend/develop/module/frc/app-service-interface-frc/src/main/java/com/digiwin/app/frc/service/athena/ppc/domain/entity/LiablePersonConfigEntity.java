package com.digiwin.app.frc.service.athena.ppc.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:zhangzlz
 * @Date 2022/3/10   17:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiablePersonConfigEntity {
    /**
     * 主键
     */
    private String oid;

    /**
     * 租户
     */
    private Long tenantSid;

    /**
     * 配置标识
     */
    private String configFlag;

    /**
     * 问题归属编号
     */
    private String attributionNo;

    /**
     * 风险等级主键
     */
    private String riskLevelId;

    /**
     * 问题来源主键
     */
    private String sourceOid;

    /**
     * 解决方案主键
     */
    private String solutionOid;

    /**
     * 负责人id
     */
    private String liablePersonId;

    /**
     * 负责人名称
     */
    private String liablePersonName;

    /**
     * 验收人角色
     */
    private String acceptanceRole;

    /**
     * 是否生效
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
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateName;
}
