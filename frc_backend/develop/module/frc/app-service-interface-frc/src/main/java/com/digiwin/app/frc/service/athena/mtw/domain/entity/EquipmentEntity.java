package com.digiwin.app.frc.service.athena.mtw.domain.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/12 14:14
 * @Version 1.0
 * @Description 设备信息实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户id
     */
    private Long tenantSid;

    /**
     * 设备编号
     */
    private String equipmentNo;

    /**
     * 设备编号名称
     */
    private String equipmentName;

    /**
     * 设备类别
     */
    private String equipmentType;

    /**
     * 所属部门id
     */
    private String departmentId;

    /**
     * 所属部门name
     */
    private String departmentName;

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
