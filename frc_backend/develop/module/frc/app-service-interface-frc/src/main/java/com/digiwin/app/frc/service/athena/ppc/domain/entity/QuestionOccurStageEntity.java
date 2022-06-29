package com.digiwin.app.frc.service.athena.ppc.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:zhangzlz
 * @Date 2022/2/11   9:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionOccurStageEntity {
    /**
     * 主键
     */
    private String oid;

    /**
     * 租户
     */
    private Long tenantSid;

    /**
     * 问题发生阶段编号
     */
    private String occurStageNo;

    /**
     * 问题发生阶段名称
     */
    private String occurStageName;

    /**
     * 问题归属编号
     */
    private String attributionNo;

    /**
     * 问题来源主键
     */
    private String sourceOid;

    /**
     * 问题分类主键
     */
    private String classificationOid;

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
