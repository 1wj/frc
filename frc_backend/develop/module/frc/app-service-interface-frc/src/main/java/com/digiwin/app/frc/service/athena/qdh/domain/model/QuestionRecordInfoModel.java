package com.digiwin.app.frc.service.athena.qdh.domain.model;

import com.digiwin.app.frc.service.athena.config.annotation.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QuestionRecordInfoModel
 * @Description 用于获取问题记录详情
 * @Author author
 * @Date 2021/12/2 16:03
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionRecordInfoModel {

    /**
     * 项目卡主键
     */
    @JsonProperty(value = "question_record_id")
    @NotNull(message = "question_record_id is null")
    private String questionRecordId;

    private Long tenantsid;

    /**
     * QF-问题确认 QI-问题识别 QA-问题验收
     */
    @JsonProperty(value = "question_process_step")
    @NotNull(message = "question_process_step is null")
    private String questionProcessStep;

    /**
     * 问题解决-QS
     */
    @JsonProperty(value = "question_solve_step")
    private String questionSolveStep;

    /**
     * 问题当前处理状态 0.未开始;
     * 1.进行中;
     * 2.已完成
     */
    @JsonProperty(value = "question_status")
    @NotNull(message = "question_status is null")
//    @JsonProperty(value = "current_question_process_status")
    private String currentQuestionProcessStatus;

    @JsonProperty(value = "question_id")
    private String questionId;

    private int questionProcessResult;

    /**
     * 问题处理标识字段
     */
    @JsonProperty(value = "solution_id")
    private String solutionId;
}
