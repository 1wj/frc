package com.digiwin.app.frc.service.athena.qdh.domain.model;

import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QuestionFeedbackDetailModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @ClassName AnapproveModel
 * @Description 洗澡能待审核数据入参
 * @Author author
 * @Date 2021/11/14 23:31
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnapprovedModel {

    /**
     * 问题记录表主键
     */
    @JsonProperty(value = "question_record_id")
    private String questionRecordOid;

    /**
     * 问题处理步骤 问题反馈(qf) 问题识别处理(qi) 问题识别审核(qir) 问题解决(qs) 问题验收(qa)
     */
    @JsonProperty(value = "question_process_step")
    private String questionProcessStep;

    /**
     * 问题解决步骤 问题分配 分配遏制 遏制 遏制审核 问题关闭 .... （方案编号三位流水号）
     */
    @JsonProperty(value = "question_solve_step")
    private String questionSolveStep;

    /**
     * 问题号
     */
    @JsonProperty(value = "question_no")
    private String questionNo;

    /**
     * 问题描述
     */
    @JsonProperty(value = "question_description")
    private String questionDescription;

    /**
     * 问题处理状态
     */
    @JsonProperty(value = "question_process_status")
    private Integer questionProcessStatus;

    /**
     * 问题处理结果
     */
    @JsonProperty(value = "question_process_result")
    private Integer questionProcessResult;

    /**
     * 问题处理意见
     */
    private String handleComment;

    /**
     * 退回标识id 识别退回(qir) 问题解决退回(qsr) 验收退回(qar) 问题关闭退回(sc002005r) 遏制审核退回(sc002004r)
     */
    @JsonProperty(value = "return_flag_id")
    private String returnFlagId;

    /**
     * 退回标识name 识别退回(qir) 问题解决退回(qsr) 验收退回(qar) 问题关闭退回(sc002005r) 遏制审核退回(sc002004r)
     */
    @JsonProperty(value = "return_flag_name")
    private String returnFlagName;

    /**
     * 退回原因编号
     */
    @JsonProperty(value = "return_no")
    private String returnNo;

    /**
     * 数据实例oid
     */
    private String dataInstanceOid;

    /**
     * 问题处理开始时间
     */
    private Date startTime;

    /**
     * 问题处理预计完成时间
     */
    @JsonProperty(value = "expect_complete_date")
    private String expectCompleteDate;

    /**
     * 问题处理实际完成时间
     */
    private Date actualCompleteDate;

    /**
     * 处理人员id
     */
    @JsonProperty(value = "liable_person_id")
    private String liablePersonId;

    /**
     * 处理人姓名
     */
    @JsonProperty(value = "liable_person_name")
    private String liablePersonName;

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


    @JsonProperty(value = "question_detail_info")
    private List<QuestionFeedbackDetailModel> questionFeedbackDetailModels;

    /**
     * 1-不跳过 2-跳过
     */
    @JsonProperty(value = "skip")
    private String skip;

    /**
     * 迭代六新增字段  退回原因
     */
    @JsonProperty(value = "return_reason")
    private String returnReason;
}
