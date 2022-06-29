package com.digiwin.app.frc.service.athena.ppc.domain.vo;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author: xieps
 * @Date: 2022/2/10 13:05
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRiskLevelVo {

    /**
     * 主键
     */
    @JsonProperty(value = "risk_level_id")
    private String oid;


    /**
     * 风险等级编号
     */
    @JsonProperty(value = "risk_level_no")
    private String riskLevelNo;

    /**
     * 风险等级名称
     */
    @JsonProperty(value = "risk_level_name")
    private String riskLevelName;

    /**
     * 重要性    1：重要    2：不重要
     */
    @JsonProperty(value = "important")
    private Integer important;

    /**
     * 紧急度  1：紧急 2：不紧急
     */
    @JsonProperty(value = "urgency")
    private Integer urgency;

    /**
     * 是否可操作   Y:可修改  N:不可修改
     */
    @JsonProperty("is_edit")
    private String isModify;


    /**
     * 是否生效  Y 生效  V 失效
     */
    @JsonProperty(value = "manage_status")
    private String manageStatus;


    /**
     * 备注
     */
    @JsonProperty(value = "remarks")
    private String remarks;

    /**
     * 是否上版     Y:上版   N:不上版
     */
    @JsonProperty(value = "is_upload_kanban")
    private String isUpload;

}
