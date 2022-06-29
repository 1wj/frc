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
 * @Date: 2021/11/15 11:23
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionItemModel {

    /**
     * 主键
     */
    @JsonProperty(value = "item_id")
    private  String oid;

    /**
     * 租户id
     */
    @JsonProperty(value = "tenantsid")
    private Long tenantSid;

    /**
     * 物料编号
     */
    @Length(max = 32,message = "item_no_overLength")
    @JSONField(name = "item_no")
    @JsonProperty(value = "item_no")
    private String itemNo;

    /**
     * 物料名称
     */
    @JSONField(name = "item_name")
    @JsonProperty(value = "item_name")
    private String itemName;

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
