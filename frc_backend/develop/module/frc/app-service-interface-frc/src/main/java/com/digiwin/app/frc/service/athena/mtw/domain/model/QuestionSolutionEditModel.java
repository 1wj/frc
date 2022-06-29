package com.digiwin.app.frc.service.athena.mtw.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/16 15:50
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSolutionEditModel {

    /**
     * 主键
     */
    @JsonProperty(value = "solution_key_id")
    private String oid;

    /**
     * 租户
     */
    @JsonProperty("tenantsid")
    private Long tenantSid;

    /**
     * 方案编号
     */
    @JsonProperty(value = "solution_id")
    private String solutionNo;

    /**
     * 方案名称
     */
    @JsonProperty(value = "solution_name")
    private String solutionName;

    /**
     * 是否生效  Y:生效  N:未生效
     */
    @JsonProperty(value = "manage_status")
    private String manageStatus;

    /**
     * 是否默认  0:是   1:否
     */
    @JsonProperty(value = "is_default")
    private Integer defaultChoice;

    /**
     * 负责人id
     */
    @JsonProperty(value = "liable_person_id")
    private String directorId;

    /**
     * 负责人名字
     */
    @JsonProperty(value = "liable_person_name")
    private String directorName;

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
