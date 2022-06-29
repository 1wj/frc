package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/10 10:21
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefectCodeVo {

    /**
     * 主键
     */
    @JsonProperty(value = "defect_id")
    private String oid;

    /**
     *缺陷类别编号
     */
    @JsonProperty(value = "category_no")
    private String defectTypeNo;

    /**
     * 缺陷类别名称
     */
    @JsonProperty(value = "category_name")
    private String defectTypeName;

    /**
     * 缺陷代码
     */
    @JsonProperty(value = "defect_no")
    private String defectCode;

    /**
     * 缺陷名称
     */
    @JsonProperty("defect_name")
    private String defectName;

    /**
     * 缺陷等级
     */
    @JsonProperty("defect_grade")
    private String defectGrade;

    /**
     * 是否有效  Y:生效  N:未生效
     */
    @JsonProperty(value = "manage_status")
    private String  manageStatus;

    /**
     * 备注
     */
    @JsonProperty(value = "remarks")
    private String remarks;

}
