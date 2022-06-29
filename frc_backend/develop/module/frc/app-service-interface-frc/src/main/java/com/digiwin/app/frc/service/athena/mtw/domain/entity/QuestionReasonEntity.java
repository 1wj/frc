package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/16 17:10
 * @Version 1.0
 * @Description 原因代码实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionReasonEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户
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
     * 原因代码
     */
    private String reasonCode;

    /**
     * 原因名称
     */
    private String reasonName;

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
