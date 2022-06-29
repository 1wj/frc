package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/11 14:56
 * @Version 1.0
 * @Description 问题来源实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSourceEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户id
     */
    private Long tenantSid;

    /**
     * 来源编号
     */
    private String sourceNo;

    /**
     * 来源名称
     */
    private String sourceName;

    /**
     * 来源分类
     */
    private String sourceCategory;

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


    private String classificationInfo;
}
