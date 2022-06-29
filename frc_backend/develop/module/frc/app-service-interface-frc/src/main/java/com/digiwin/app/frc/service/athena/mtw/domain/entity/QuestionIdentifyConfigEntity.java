package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2022/1/4 12:41
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionIdentifyConfigEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户id
     */
    private Long tenantSid;

    /**
     * 分类编号
     */
    private String classificationNo;

    /**
     * 分类名称
     */
    private String classificationName;

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
     * 创建时间
     */
    private Date updateTime;

    /**
     * 创建人
     */
    private String updateName;
}
