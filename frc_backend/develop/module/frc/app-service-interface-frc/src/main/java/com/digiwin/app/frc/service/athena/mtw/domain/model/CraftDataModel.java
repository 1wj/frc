package com.digiwin.app.frc.service.athena.mtw.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/16 13:46
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CraftDataModel {

    /**
     * 主键
     */
    @JsonProperty(value = "op_id")
    private String oid;

    /**
     * 租户
     */
    @JsonProperty(value = "tenantsid")
    private Long tenantSid;

    /**
     * 工艺编号
     */
    @Length(max = 32,message = "op_no_overLength")
    @JSONField(name = "op_no")
    @JsonProperty(value = "op_no")
    private String craftNo;

    /**
     * 工艺名称
     */
    @JSONField(name = "op_name")
    @JsonProperty(value = "op_name")
    private String craftName;

    /**
     * 类别
     */
    @Length(max = 32,message = "op_type_overLength")
    @JSONField(name = "op_type")
    @JsonProperty(value = "op_type")
    private String craftType;

    /**
     * 所属部门id
     */
    @Length(max = 32,message = "belong_department_id_overLength")
    @JSONField(name = "belong_department_id")
    @JsonProperty(value = "belong_department_id")
    private String departmentId;

    /**
     * 所属部门名称
     */
    @JSONField(name = "belong_department_name")
    @JsonProperty(value = "belong_department_name")
    private String departmentName;

    /**
     * 是否生效  Y 生效  N 未生效
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
     *修改时间
     */
    @JsonProperty(value = "update_time")
    private Date updateTime;

    /**
     * 修改人
     */
    @JsonProperty(value = "update_name")
    private String updateName;

}
