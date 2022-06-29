package com.digiwin.app.frc.service.athena.rqi.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/1/12 9:46
 * @Version 1.0
 * @Description 当责者数据追踪Vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionTrackResponsibleVo {

    /**
     * 问题单号
     */
    @JsonProperty(value = "question_no")
    private String questionNo;

    /**
     * 来源名称
     */
    @JsonProperty(value = "source_name")
    private String sourceName;

    /**
     * 分类名称
     */
    @JsonProperty(value = "classification_name")
    private String classificationName;

    /**
     * 问题描述
     */
    @JsonProperty(value = "question_description")
    private String questionDescription;

    /**
     * 问题发起时间
     */
    @JsonProperty(value = "question_start_date")
    private String questionInitiateDate;

    /**
     * 问题接受/结案时间
     */
    @JsonProperty(value = "question_receive_date")
    private String questionReceiveDate;

    /**
     * 问题处理阶段
     */
    @JsonProperty(value = "question_process_stage")
    private String questionProcessStage;


    /**
     * 当前处理步骤
     */
    @JsonProperty(value = "question_process_step")
    private String questionProcessStep;

    /**
     * 问题处理状态
     */
    @JsonProperty(value = "question_process_status")
    private Integer questionProcessStatus;

    /**
     * 问题处理状态名称
     */
    @JsonProperty(value = "question_process_status_name")
    private String questionProcessStatusName;

    /**
     * 反馈者ID
     */
    @JsonProperty(value = "feedback_person_id")
    private String proposerPersonId;

    /**
     * 反馈者名称
     */
    @JsonProperty(value = "feedback_person_name")
    private String proposerPersonName;

    /**
     * 处理人ID
     */
    @JsonProperty(value = "process_person_id")
    private String processPersonId;


    /**
     * 处理人名称
     */
    @JsonProperty(value = "process_person_name")
    private String processPersonName;

    /**
     * 重要性
     */
    @JsonProperty(value = "important")
    private Integer important;

    /**
     * 紧急度
     */
    @JsonProperty(value = "urgency")
    private Integer urgency;

    /**
     * 是否逾期   Y 逾期    N  未逾期
     */
    @JsonProperty(value = "is_overdue")
    private String isOverDue;

    /**
     * 问题号  问题追踪表主键
     */
    @JsonProperty(value = "question_id")
    private String questionId;


    @JsonProperty(value = "task_no")
    private String taskCode;

    @JsonProperty(value = "app_no")
    private String appCode;
}
