package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author: xieps
 * @Date: 2021/11/16 13:52
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CraftDataVo {
    /**
     * 主键
     */
    @JsonProperty(value = "op_id")
    private String oid;


    /**
     * 工艺编号
     */
    @JsonProperty(value = "op_no")
    private String craftNo;

    /**
     * 工艺名称
     */
    @JsonProperty(value = "op_name")
    private String craftName;

    /**
     * 类别
     */
    @JsonProperty(value = "op_type")
    private String craftType;

    /**
     * 所属部门id
     */
    @JsonProperty(value = "belong_department_id")
    private String departmentId;

    /**
     * 所属部门名称
     */
    @JsonProperty(value = "belong_department_name")
    private String departmentName;

    /**
     * 是否生效  Y 生效  N 未生效
     */
    @JsonProperty(value = "manage_status")
    private String manageStatus;

    /**
     * 备注
     */
    @JsonProperty(value = "remarks")
    private String remarks;

}
