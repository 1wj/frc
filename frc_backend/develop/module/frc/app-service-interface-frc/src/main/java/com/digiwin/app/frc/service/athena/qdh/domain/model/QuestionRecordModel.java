package com.digiwin.app.frc.service.athena.qdh.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName QuestionRecordModel
 * @Description 问题记录model
 * @Author author
 * @Date 2021/11/15 22:12
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRecordModel {

    @JsonProperty(value = "question_record_id")
    private String oid;

    /**
     * 问题处理阶段 问题处理(qh)、问题解决(qs)
     */
    @JsonProperty(value = "question_process_stage")
    private String questionProcessStage;

    @JsonProperty(value = "question_source_no")
    private String questionSourceNo;

    @JsonProperty(value = "question_attribution_no")
    private String questionSourceType;

    @JsonProperty(value = "question_no")
    private String questionNo;

    @JsonProperty(value = "question_description")
    private String questionDescription;

    /**
     * 问题当前处理状态(0-未开始 1=进行中，2=已完成)
     */
    @JsonProperty(value = "current_question_process_status")
    private Integer currentQuestionProcessStatus;

    @JsonProperty(value = "expect_complete_date")
    private Date expectCompleteDate;

    /**
     * 负责人id
     */
    @JsonProperty(value = "liable_person_id")
    private String liablePersonId;

    /**
     * 负责人姓名
     */
    @JsonProperty(value = "liable_person_name")
    private String liablePersonName;

    /**
     * 职能id
     */
    @JsonProperty(value = "liable_person_position_id")
    private String liablePersonPositionId;

    /**
     * 职能名称
     */
    @JsonProperty(value = "liable_person_position_name")
    private String liablePersonPositionName;
}
