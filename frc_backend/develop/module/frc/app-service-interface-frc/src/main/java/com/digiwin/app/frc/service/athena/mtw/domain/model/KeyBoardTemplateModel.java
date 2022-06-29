package com.digiwin.app.frc.service.athena.mtw.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/22 9:52
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyBoardTemplateModel {

    /**
     * 主键
     */
    @JsonProperty(value = "kanban_template_id")
    private String oid;

    /**
     * 租户
     */
    @JsonProperty(value = "tenantsid")
    private Long tenantSid;

    /**
     * 模板编号
     */
    @JsonProperty(value = "template_no")
    private String modelNo;

    /**
     * 模板名称
     */
    @JsonProperty(value = "template_name")
    private String modelName;

    /**
     * 是否生效  Y:是  N:否
     */
    @JsonProperty(value = "manage_status")
    private String manageStatus;

    /**
     * 是否默认  0:是 1:否
     */
    @JsonProperty(value = "is_default")
    private Integer defaultChoice;

    /**
     * 备注
     */
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

    @JsonProperty(value = "__DATA_KEY")
    private String redundantField;
}
