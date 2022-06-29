package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author: xieps
 * @Date: 2021/11/16 17:29
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionReasonVo {

    /**
     * 主键
     */
    @JsonProperty(value = "reason_id")
    private String oid;


    /**
     * 分类编号
     */
    @JsonProperty(value = "category_no")
    private String classificationNo;

    /**
     * 分类名称
     */
    @JsonProperty(value = "category_name")
    private String classificationName;

    /**
     * 原因代码
     */
    @JsonProperty(value = "reason_code")
    private String reasonCode;

    /**
     * 原因名称
     */
    @JsonProperty(value = "reason_name")
    private String reasonName;

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
