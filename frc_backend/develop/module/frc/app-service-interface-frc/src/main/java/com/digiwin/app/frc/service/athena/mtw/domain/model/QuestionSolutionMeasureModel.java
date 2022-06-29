package com.digiwin.app.frc.service.athena.mtw.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/18 14:18
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSolutionMeasureModel {

    /**
     * 主键
     */
    @JsonProperty(value = "solution_step_id")
    private String oid;

    /**
     * 租户
     */
    @JsonProperty(value = "tenantsid")
    private Long tenantSid;

    /**
     * 解决方案维护oid
     */
    @JsonProperty(value = "solution_key_id")
    private String solutionEditOid;

    /**
     * 步骤编号
     */
    @JsonProperty(value = "step_id")
    private String measureNo;

    /**
     * 步骤名称
     */
    @JsonProperty(value = "step_name")
    private String measureName;

    /**
     * 处理人id
     */
    @JsonProperty(value = "process_person_id")
    private  String principalId;

    /**
     * 处理人名字
     */
    @JsonProperty(value = "process_person_name")
    private String principalName;

    /**
     * 预计完成天数
     * 迭代6：字段名不变，前端传回单位改为精度为0.1的小时数，数据库存储改为整数的分钟数
     */
    @JsonProperty(value = "expect_complete_days")
    private String expectCompleteTime;

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
