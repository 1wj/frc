package com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName QuestionDistributeDetailModel
 * @Description 用于-更新问题分配单
 * @Author author
 * @Date 2021/11/21 0:03
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDistributeDetailModel {
    /**
     * 步骤id
     */
    @JsonProperty(value = "step_id")
    private int stepId;

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
     * 预计完成时间	yyyyMMdd
     */
    @JsonProperty(value = "expect_complete_date")
    private Date expectCompleteDate;

    /**
     * 完成状态
     */
    @JsonProperty(value = "complete_status")
    private int completeStatus;

}
