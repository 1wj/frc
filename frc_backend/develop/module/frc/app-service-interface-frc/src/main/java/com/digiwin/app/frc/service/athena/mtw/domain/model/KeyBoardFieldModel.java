package com.digiwin.app.frc.service.athena.mtw.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/22 10:09
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyBoardFieldModel {

    /**
     * 主键
     */
    @JsonProperty(value = "kanban_field_id")
    private String oid;

    /**
     * 租户
     */
    @JsonProperty(value = "tenantsid")
    private String tenantSid;

    /**
     * 看板模板主键
     */
    @JsonProperty(value = "kanban_template_id")
    private String keyBoardTemplateOid;

    /**
     * 栏位id
     */
    @JsonProperty(value = "field_no")
    private String fieldId;

    /**
     * 栏位名称
     */
    @JsonProperty(value = "field_name")
    private String fieldName;

    /**
     * 是否生效  Y:是  N:否
     */
    @JsonProperty(value = "manage_status")
    private String manageStatus;

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

}
