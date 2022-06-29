package com.digiwin.app.frc.service.athena.rqi.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2022/1/3 22:47
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionTrackProposerModel {

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
     * 来源编号
     */
    @JSONField(name = "source_no")
    private String sourceNo;

    /**
     * 来源名称
     */
    @JSONField(name = "source_name")
    private String sourceName;

    /**
     * 分类编号
     */
    @JSONField(name = "classification_no")
    private String classificationNo;

    /**
     * 分类名称
     */
    @JSONField(name = "classification_name")
    private String classificationName;

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
     * 问题提出人姓名
     */
    private String proposerName;


    /**
     * 迭代六新增模糊查询 sn号
     */
    @JSONField(name = "sn")
    private String sn;
}
