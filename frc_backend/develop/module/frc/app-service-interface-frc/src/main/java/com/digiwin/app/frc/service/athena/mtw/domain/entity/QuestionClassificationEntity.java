package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/5 13:48
 * @Version 1.0
 * @Description 问题分类实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionClassificationEntity {

    /**
     * 问题分类  主键
     */
    private String oid;

    /**
     * 租户id
     */
    private Long tenantSid;

    /**
     *问题分类编号
     */
    private String classificationNo;

    /**
     *分类名称
     */
    private String classificationName;

    /**
     * 问题归属  1 内部  2外部  3 其他
     */
    private String questionAttribution;

    /**
     * 是否生效  Y:生效  N:未生效
     */
    private String manageStatus;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 创建人
     */
    private Date createTime;

    /**
     * 创建时间
     */
    private String createName;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     *修改人
     */
    private String updateName;


    private String sourceInfo;
}
