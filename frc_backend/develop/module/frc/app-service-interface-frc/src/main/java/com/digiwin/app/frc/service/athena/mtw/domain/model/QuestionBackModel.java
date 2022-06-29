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
 * @Date: 2021/11/15 17:16
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionBackModel {

    /**
     * 主键
     */
    @JsonProperty(value = "return_id")
    private String oid;

    /**
     * 租户id
     */
    @JsonProperty(value = "tenantsid")
    private Long tenantSid;

    /**
     * 退回编号
     */
    @Length(max = 32,message = "return_no_overLength")
    @JSONField(name = "return_no")
    @JsonProperty(value = "return_no")
    private String backId;

    /**
     * 退回原因
     */
    @JSONField(name = "return_reason")
    @JsonProperty(value = "return_reason")
    private String backReason;

    /**
     * 节点编号
     */
    @Length(max = 32,message = "node_no_overLength")
    @JSONField(name = "node_no")
    @JsonProperty(value = "node_no")
    private String nodeId;

    /**
     * 节点名称
     */
    @JSONField(name = "node_name")
    @JsonProperty(value = "node_name")
    private String nodeName;

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
