package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author: xieps
 * @Date: 2021/11/15 9:54
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSeriesVo {

    /**
     * 主键
     */
    @JsonProperty(value = "product_id")
    private String oid;

    /**
     * 系列编号
     */
    @JsonProperty(value = "product_no")
    private String seriesNo;

    /**
     * 系列名称
     */
    @JsonProperty(value = "product_name")
    private String seriesName;

    /**
     * 是否生效
     */
    @JsonProperty(value = "manage_status")
    private String manageStatus;

    /**
     * 备注
     */
    @JsonProperty(value = "remarks")
    private String remarks;


}
