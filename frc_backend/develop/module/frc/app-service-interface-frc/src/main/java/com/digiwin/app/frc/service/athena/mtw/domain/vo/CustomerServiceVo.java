package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2021/11/14 22:33
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerServiceVo {

    /**
     * 主键
     */
    @JsonProperty(value = "dealer_contact_person_id")
    private String oid;

    /**
     * 客服人员编号
     */
    @JsonProperty(value = "customer_service_employee_id")
    private String customerServiceId;

    /**
     * 客服人员姓名
     */
    @JsonProperty(value = "customer_service_employee_name")
    private String customerServiceName;

    /**
     * 供应商编号
     */
    @JsonProperty(value = "dealer_id")
    private String supplierId;

    /**
     * 供应商名称
     */
    @JsonProperty(value = "dealer_name")
    private String supplierName;


}
