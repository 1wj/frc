package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/16 13:38
 * @Version 1.0
 * @Description 工艺信息实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CraftDataEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户
     */
    private Long tenantSid;

    /**
     * 工艺编号
     */
    private String craftNo;

    /**
     * 工艺名称
     */
    private String craftName;

    /**
     * 类别
     */
    private String craftType;

    /**
     * 所属部门id
     */
    private String departmentId;

    /**
     * 所属部门名称
     */
    private String departmentName;

    /**
     * 是否生效  Y 生效  N 未生效
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
}
