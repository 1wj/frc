package com.digiwin.app.frc.service.athena.qdh.domain.model.init;

import com.digiwin.app.frc.service.athena.config.annotation.CheckNull;
import com.digiwin.app.frc.service.athena.qdh.domain.model.QuestionPictureModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @ClassName BasicModel
 * @Description 问题发起-问题基础信息结构
 * @Author author
 * @Date 2022/2/10 18:36
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicModel {

    /**
     * 问题归属编号
     */
    @JsonProperty(value = "question_attribution_no")
    @CheckNull(message = "question_attribution_no is null")
    private String questionAttributionNo;

    /**
     * 问题来源编号
     */
    @JsonProperty(value = "question_source_no")
    @CheckNull(message = "question_source_no is null")
    private String questionSourceNo;

    /**
     * 问题来源名称
     */
    @JsonProperty(value = "question_source_name")
    private String questionSourceName;

    /**
     * 问题来源主键 2022.1.24新增，用于议题管理矩阵报表
     */
    @JsonProperty(value = "question_source_oid")
    private String questionSourceOId;



    /**
     * 问题分类主键 2022.1.24新增，用于议题管理矩阵报表
     */
    @JsonProperty(value = "question_classification_oid")
    @CheckNull(message = "question_classification_oid is null")
    private String questionClassificationOId;

    /**
     * 问题分类编号
     */
    @JsonProperty(value = "question_classification_no")
    @CheckNull(message = "question_classification_no is null")
    private String questionClassificationNo;

    /**
     * 问题分类名称
     */
    @JsonProperty(value = "question_classification_name")
    @CheckNull(message = "question_classification_name is null")
    private String questionClassificationName;

    /**
     * 风险等级编号
     */
    @JsonProperty(value = "risk_level_no")
    @CheckNull(message = "risk_level_no is null")
    private String riskLevelNo;

    /**
     * 风险等级名称
     */
    @JsonProperty(value = "risk_level_name")
    @CheckNull(message = "risk_level_name is null")
    private String riskLevelName;

    /**
     * 重要性
     */
    @JsonProperty(value = "important")
    private int important;

    /**
     * 紧急度
     */
    @JsonProperty(value = "urgency")
    private int urgency;

    /**
     * 问题提出人id
     */
    @JsonProperty(value = "question_proposer_id")
    @CheckNull(message = "question_proposer_id is null")
    private String questionProposerId;

    /**
     * 问题提出人name
     */
    @JsonProperty(value = "question_proposer_name")
    @CheckNull(message = "question_proposer_name is null")
    private String questionProposerName;

    /**
     * 提出人部门id
     */
    @JsonProperty(value = "proposer_department_id")
    @CheckNull(message = "proposer_department_id is null")
    private String proposerDepartmentId;

    /**
     * 提出人部门name
     */
    @JsonProperty(value = "proposer_department_name")
    @CheckNull(message = "proposer_department_name is null")
    private String proposerDepartmentName;

    /**
     * 问题描述
     */
    @JsonProperty(value = "question_description")
    @CheckNull(message = "question_description is null")
    private String questionDescription;

    /**
     * 问题发生阶段编号
     */
    @JsonProperty(value = "occur_stage_no")
    private String occurStageNo;

    /**
     * 问题发生阶段名称
     */
    @JsonProperty(value = "occur_stage_name")
    private String occurStageName;

    /**
     * 发生时间
     */
    @JsonProperty(value = "happen_date")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date questionHappenDate;

    /**
     * 期望解决时间
     */
    @JsonProperty(value = "expect_solve_date")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date expectFinishTime;


    /**
     * 风险等级主键
     */
    @JsonProperty(value = "risk_level_oid")
    private String riskLevelOId;

    /**
     * 问题图片集合
     */
    @JsonProperty(value = "question_picture")
    private List<QuestionPictureModel> pictureModels;

}
