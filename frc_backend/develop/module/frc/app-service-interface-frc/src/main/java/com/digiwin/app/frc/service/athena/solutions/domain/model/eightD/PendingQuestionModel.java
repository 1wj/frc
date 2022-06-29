package com.digiwin.app.frc.service.athena.solutions.domain.model.eightD;

import com.digiwin.app.frc.service.athena.config.InputConverter;
import com.digiwin.app.frc.service.athena.config.annotation.NotNull;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName pendingQuestionModel
 * @Description generate.pending.question.info.create 入参model
 * {
 *     "question_info": [
 *         {
 *            "question_record_id":"",
 *            "question_no":"",
 *            "question_description":"",
 *            "question_process_step":"",
 *            "question_solve_step":"",
 *            "expect_complete_date":"",
 *            "liable_person_id":"",
 *            "liable_person_name":"",
 *            "question_id":"",
 *            "return_flag_id":"",
 *            "return_flag_name":"",
 *            "return_person_id":"",
 *            "return_person_name":",
 *            "return_no":"",
 *            "liable_person_position_id":"",
 *            "liable_person_position_name":""
 *         }
 *     ]
 * }
 * @Author HeX
 * @Date 2022/3/8 15:35
 * @Version 1.0
 **/
@Data
public class PendingQuestionModel implements InputConverter<QuestionActionTraceEntity> {


    /**
     * 问题记录oid
     */
    @JsonProperty(value = "question_record_id")
    @NotNull(message = "question_record_id is null")
    private String questionRecordOid;



    /**
     * 问题号
     */
    @JsonProperty(value = "question_no")
    @NotNull(message = "question_no is null")
    private String questionNo;

    /**
     * 问题描述
     */
    @JsonProperty(value = "question_description")
    @NotNull(message = "question_description is null")
    private String questionDescription;

    /**
     * 问题处理步骤 问题反馈(qf) 问题识别处理(qi) 问题识别审核(qir) 问题解决(qs) 问题验收(qa)
     */
    @JsonProperty(value = "question_process_step")
    @NotNull(message = "question_process_step is null")
    private String questionProcessStep;

    /**
     * 问题解决步骤 问题分配 分配遏制 遏制 遏制审核 问题关闭 .... （方案编号三位流水号）
     */
    @JsonProperty(value = "question_solve_step")
    @NotNull(message = "question_solve_step is null")
    private String questionSolveStep;

    @JsonProperty(value = "liable_person_id")
    @NotNull(message = "liable_person_id is null")
    private String liablePersonId;

    /**
     * 处理人
     */
    @JsonProperty(value = "liable_person_name")
    @NotNull(message = "liable_person_name is null")
    private String liablePersonName;

    /**
     * 用于验证卡退回执行卡，需传入验证卡里需退回的执行任务卡的主键
     */
    @JsonProperty(value = "question_id")
    private String oid;

    @JsonProperty(value = "return_flag_id")
    private String returnFlagId;

    @JsonProperty(value = "return_flag_name")
    private String returnFlagName;

    @JsonProperty(value = "return_person_id")
    private String returnId;

    @JsonProperty(value = "return_person_name")
    private String returnName;

    /**
     * 问题处理实际完成时间
     */
    @JsonProperty(value = "expect_complete_date")
    private Date actualCompleteDate;

    /**
     * 负责人职能id
     */
    @JsonProperty(value = "liable_person_position_id")
    private String liablePersonPositionId;

    /**
     * 负责人职能名称
     */
    @JsonProperty(value = "liable_person_position_name")
    private String liablePersonPositionName;

    @JsonProperty(value = "return_no")
    private String returnNo;

    @JsonProperty(value = "skip")
    private String skip;

    @JsonProperty(value = "return_step_no")
    private String returnStepNo;

    /**
     * 冲刺六新增字段
     */
    @JsonProperty(value = "return_reason")
     private String returnReason;
}
