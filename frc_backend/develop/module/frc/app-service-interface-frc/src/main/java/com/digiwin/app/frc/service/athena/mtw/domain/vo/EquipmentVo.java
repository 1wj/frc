package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author: xieps
 * @Date: 2021/11/15 9:12
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentVo {

    /**
     * 主键
     */
    @JsonProperty(value = "workstation_key_id")
    private String oid;

    /**
     * 设备编号
     */
    @JsonProperty(value = "workstation_id")
    private String equipmentNo;

    /**
     * 设备编号名称
     */
    @JsonProperty(value = "workstation_name")
    private String equipmentName;

    /**
     * 设备类别
     */
    @JsonProperty(value = "equipment_category")
    private String equipmentType;

    /**
     * 所属部门id
     */
    @JsonProperty(value = "belong_department_id")
    private String departmentId;

    /**
     * 所属部门name
     */
    @JsonProperty(value = "belong_department_name")
    private String departmentName;

    /**
     * 是否生效
     */
    @JsonProperty(value = "manage_status")
    private String manageStatus;

    /**
     * 备注
     */
    @JsonProperty(value = "remarks")
    private String remarks;


}
