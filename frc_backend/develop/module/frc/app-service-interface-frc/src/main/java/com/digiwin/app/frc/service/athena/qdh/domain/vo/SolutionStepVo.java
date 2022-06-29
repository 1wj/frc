package com.digiwin.app.frc.service.athena.qdh.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName SolutionStepVo
 * @Description 解决方案配置出参
 * @Author author
 * @Date 2021/11/25 14:07
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolutionStepVo {

    /**
     * 解决方案步骤id
     */
    @JsonProperty(value = "solution_step_id")
    private String solutionStepId;

    /**
     * 步骤id
     */
    @JsonProperty(value = "step_id")
    private String stepId;

    /**
     * 步骤名称
     */
    @JsonProperty(value = "step_name")
    private String stepName;

    /**
     * 处理人id
     */
    @JsonProperty(value = "process_person_id")
    private String processPersonId;

    /**
     * 处理人名称
     */
    @JsonProperty(value = "process_person_name")
    private String processPersonName;

    /**
     * 预计完成天数
     */
    @JsonProperty(value = "expect_complete_days")
    private String expectCompleteDays;
}
