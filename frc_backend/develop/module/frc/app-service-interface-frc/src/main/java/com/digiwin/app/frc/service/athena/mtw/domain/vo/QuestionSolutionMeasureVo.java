package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2021/11/18 14:23
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSolutionMeasureVo {

    /**
     * 主键
     */
    @JsonProperty(value = "solution_step_id")
    private String oid;


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
     */
    @JsonProperty(value = "expect_complete_days")
    private String expectCompleteTime;



}
