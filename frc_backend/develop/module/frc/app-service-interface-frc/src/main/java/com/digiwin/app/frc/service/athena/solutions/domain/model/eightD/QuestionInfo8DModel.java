package com.digiwin.app.frc.service.athena.solutions.domain.model.eightD;

import com.digiwin.app.frc.service.athena.config.annotation.NotNull;
import com.digiwin.app.frc.service.athena.config.annotation.ValidateType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/3/10 14:35
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true )
public class QuestionInfo8DModel {

    /**
     * 主键
     */
    //    @CheckNull(message = "question_id is null")
    @JsonProperty(value = "question_id")
    @NotNull(message = "question_id is null")
    private String oid;


    @JsonProperty(value = "question_no")
    @NotNull(message = "question_no is null")
    private String questionNo;

    /**
     * 问题处理状态
     */
    @JsonProperty(value = "question_process_status")
    @NotNull(message = "question_process_status is not 0 ",value = ValidateType.TYPE_INTEGER,isPositive = true)
    private Integer questionProcessStatus;

    /**
     * 问题处理结果
     */
    @JsonProperty(value = "question_process_result")
    @NotNull(message = "question_process_result is not 0 ",value = ValidateType.TYPE_INTEGER,isPositive = true)
    private Integer questionProcessResult;



    @JsonProperty(value = "close_reason")
    private String close_reason;


    @JsonProperty(value = "question_description")
    private String questionDescription;



    @JsonProperty(value = "question_record_id")
    private String questionRecordOid;

    /**
     * 退回原因
     */
    @JsonProperty(value = "return_reason_no")
    private String returnNo;

    /**
     * 冲刺六新增信息  退回步骤编号
     */
    @JsonProperty(value = "return_step_no")
    private String returnStepNo;

    /**
     * 冲刺六新增信息  退回原因
     */
    @JsonProperty(value = "return_reason")
    private String returnReason;
}
