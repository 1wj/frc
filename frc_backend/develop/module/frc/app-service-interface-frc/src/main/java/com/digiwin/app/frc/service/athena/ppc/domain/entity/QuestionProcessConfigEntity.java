package com.digiwin.app.frc.service.athena.ppc.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2022/2/17 10:44
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionProcessConfigEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户id
     */
    private Long tenantSid;

    /**
     * 问题归属编号
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
     * 解决方案主键
     */
    private String solutionStepId;


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

    private String classificationInfo;

}
