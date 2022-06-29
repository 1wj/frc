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
 * @Date: 2021/11/14 23:01
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefectCodeModel {

    /**
     * 主键
     */
    @JsonProperty(value = "defect_id")
    private String oid;

    /**
     * 租户id
     */
    @JsonProperty(value = "tenantsid")
    private Long tenantSid;

    /**
     *缺陷类别编号
     */
    @Length(max = 32,message = "category_no_overLength")
    @JSONField(name = "category_no")
    @JsonProperty(value = "category_no")
    private String defectTypeNo;

    /**
     * 缺陷类别名称
     */
    @JSONField(name = "category_name")
    @JsonProperty(value = "category_name")
    private String defectTypeName;

    /**
     * 缺陷代码
     */
    @Length(max = 32,message = "defect_no_overLength")
    @JSONField(name = "defect_no")
    @JsonProperty(value = "defect_no")
    private String defectCode;

    /**
     * 缺陷名称
     */
    @JSONField(name = "defect_name")
    @JsonProperty("defect_name")
    private String defectName;

    /**
     * 缺陷等级
     */
    @Length(max = 32,message = "defect_grade_overLength")
    @JSONField(name = "defect_grade")
    @JsonProperty("defect_grade")
    private String defectGrade;

    /**
     * 是否有效  Y:生效  N:未生效
     */
    @JSONField(name = "manage_status")
    @JsonProperty(value = "manage_status")
    private String  manageStatus;

    /**
     * 缺陷图片
     */
    @JsonProperty(value = "defect_picture_id")
    private String imageDmcId;

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
