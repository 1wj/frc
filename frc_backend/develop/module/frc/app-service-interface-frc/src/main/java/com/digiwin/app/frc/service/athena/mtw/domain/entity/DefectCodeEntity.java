package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/10 10:21
 * @Version 1.0
 * @Description 缺陷代码实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefectCodeEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户id
     */
    private Long tenantSid;

    /**
     *缺陷类别编号
     */
    private String defectTypeNo;

    /**
     * 缺陷类别名称
     */
    private String defectTypeName;

    /**
     * 缺陷代码
     */
    private String defectCode;

    /**
     * 缺陷名称
     */
    private String defectName;

    /**
     * 缺陷等级
     */
    private String defectGrade;

    /**
     * 是否有效  Y:生效  N:未生效
     */
    private String  manageStatus;

    /**
     * 缺陷图片
     */
    private String imageDmcId;

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
