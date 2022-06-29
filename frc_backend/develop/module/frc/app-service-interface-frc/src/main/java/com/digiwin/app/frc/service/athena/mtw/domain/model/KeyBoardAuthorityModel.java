package com.digiwin.app.frc.service.athena.mtw.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/24 9:59
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyBoardAuthorityModel {

    /**
     * 主键
     */
    @JsonProperty(value = "kanban_permissions_id")
    private String oid;

    /**
     * 租户
     */
    @JsonProperty(value = "tenantsid")
    private Long tenantSid;

    /**
     * 看板模板主键
     */
    @JsonProperty(value = "kanban_template_id")
    private String modelOid;

    /**
     * 看板模板名称
     */
    @JsonProperty(value = "kanban_template_name")
    private String modelName;

    /**
     * 指定查看人
     */
    @JsonProperty(value = "designation_inspector")
    private String specifyViewer;

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
