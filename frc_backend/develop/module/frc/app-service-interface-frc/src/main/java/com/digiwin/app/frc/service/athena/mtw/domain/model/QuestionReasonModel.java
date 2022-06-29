package com.digiwin.app.frc.service.athena.mtw.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.naming.NamingEnumeration;
import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/16 17:23
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionReasonModel {

    /**
     * 主键
     */
    @JsonProperty(value = "reason_id")
    private String oid;

    /**
     * 租户
     */
    @JsonProperty(value = "tenantsid")
    private Long tenantSid;

    /**
     * 分类编号
     */
    @Length(max = 32,message = "category_no_overLength")
    @JSONField(name = "category_no")
    @JsonProperty(value = "category_no")
    private String classificationNo;

    /**
     * 分类名称
     */
    @JSONField(name = "category_name")
    @JsonProperty(value = "category_name")
    private String classificationName;

    /**
     * 原因代码
     */
    @Length(max = 32,message = "reason_code_overLength")
    @JSONField(name = "reason_code")
    @JsonProperty(value = "reason_code")
    private String reasonCode;

    /**
     * 原因名称
     */
    @JSONField(name = "reason_name")
    @JsonProperty(value = "reason_name")
    private String reasonName;

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
