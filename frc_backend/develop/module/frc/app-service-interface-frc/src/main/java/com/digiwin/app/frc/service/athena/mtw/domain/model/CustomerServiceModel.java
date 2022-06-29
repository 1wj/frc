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
 * @Date: 2021/11/14 22:33
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerServiceModel {
    /**
     * 主键
     */
    @JsonProperty(value = "dealer_contact_person_id")
    private String oid;

    /**
     * 租户id
     */
    @JsonProperty(value = "tenantsid")
    private Long tenantSid;

    /**
     * 客服人员编号
     */
    @Length(max = 32,message = "customer_service_employee_id_overLength")
    @JSONField(name = "customer_service_employee_id")
    @JsonProperty(value = "customer_service_employee_id")
    private String customerServiceId;

    /**
     * 客服人员姓名
     */
    @Length(max = 32,message = "customer_service_employee_name_overLength")
    @JSONField(name = "customer_service_employee_name")
    @JsonProperty(value = "customer_service_employee_name")
    private String customerServiceName;

    /**
     * 供应商编号
     */
    @JSONField(name = "dealer_id")
    @JsonProperty(value = "dealer_id")
    private String supplierId;

    /**
     * 供应商名称
     */
    @JSONField(name = "dealer_name")
    @JsonProperty(value = "dealer_name")
    private String supplierName;

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
