package com.digiwin.app.frc.service.athena.rqi.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/1/7 11:15
 * @Version 1.0
 * @Description 处理者数据追踪model
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionTrackProcessorModel {

    /**
     * 问题单号
     */
    @JSONField(name = "question_no")
    private String questionNo;

    /**
     * 单据状态  0:全部  1:处理中   2:已处理
     */
    @JSONField(name = "question_status")
    private String questionStatus;


    /**
     * 问题处理阶段
     */
    @JSONField(name = "question_process_stage")
    private String questionProcessStage;

    /**
     * 问题描述
     */
    @JSONField(name = "question_description")
    private String questionDescription;

    /**
     * 反馈开始时间
     */
    @JSONField(name = "feedback_start_date")
    private String feedbackStartDate;


    /**
     * 反馈结束时间
     */
    @JSONField(name = "feedback_end_date")
    private String feedbackEndDate;


    /**
     * 处理者姓名
     */
    private String processorName;

    /**
     * 处理者ID
     */
    private String processorId;


    /**
     * 迭代六新增模糊查询 sn号
     */
    @JSONField(name = "sn")
    private String sn;
}
