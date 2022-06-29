package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/12 11:00
 * @Version 1.0
 * @Description 客服对接供应商维护表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerServiceEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户id
     */
    private Long tenantSid;

    /**
     * 客服人员编号
     */
    private String customerServiceId;

    /**
     * 客服人员姓名
     */
    private String customerServiceName;

    /**
     * 供应商编号
     */
    private String supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

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
