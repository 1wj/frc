package com.digiwin.app.frc.service.athena.mtw.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.xml.transform.Source;
import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/15 9:12
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentModel{
    /**
     * 主键
     */
    @JsonProperty(value = "workstation_key_id")
    private String oid;

    /**
     * 租户id
     */
    @JsonProperty(value = "tenantsid")
    private Long tenantSid;

    /**
     * 设备编号
     */
    @Length(max = 32,  message = "workstation_id_overLength")
    @JSONField(name = "workstation_id")
    @JsonProperty(value = "workstation_id")
    private String equipmentNo;

    /**
     * 设备编号名称
     */
    @JSONField(name = "workstation_name")
    @JsonProperty(value = "workstation_name")
    private String equipmentName;

    /**
     * 设备类别
     */
    @Length(max = 32,  message = "equipment_category_overLength")
    @JSONField(name = "equipment_category")
    @JsonProperty(value = "equipment_category")
    private String equipmentType;

    /**
     * 所属部门id
     */
    @Length(max = 32,  message = "belong_department_id_overLength")
    @JSONField(name = "belong_department_id")
    @JsonProperty(value = "belong_department_id")
    private String departmentId;

    /**
     * 所属部门name
     */
    @JSONField(name = "belong_department_name")
    @JsonProperty(value = "belong_department_name")
    private String departmentName;

    /**
     * 是否生效
     */
    @JSONField(name = "manage_status")
    @JsonProperty(value = "manage_status")
    private String manageStatus;

    /**
     * 备注
     */
    @JSONField(name = "remarks")
    @JsonProperty(value = "remarks")
    private String remarks;

    /**
     * 创建时间
     */
    @JsonProperty(value = "create_time")
    private Date createTime;

    /**
     * 创建人
     */
    @JsonProperty(value = "create_name")
    private String createName;

    /**
     * 修改时间
     */
    @JsonProperty(value = "update_time")
    private Date updateTime;

    /**
     * 修改人
     */
    @JsonProperty(value = "update_name")
    private String updateName;

}
