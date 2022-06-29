package com.digiwin.app.frc.service.athena.qdh.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName QuestionRecordEntity
 * @Description 问题记录实体
 * @Author author
 * @Date 2021/11/15 21:54
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
public class QuestionRecordEntity extends BaseEntity{

    private String oid;

    /**
     * 租户
     */
    private Long tenantsid;

    /**
     * 问题号
     */
    private String questionNo;

    /**
     * 问题处理阶段 问题处理(qh)、问题解决(qs)
     */
    private String questionProcessStage;

    /**
     * 问题来源编号
     */
    private String questionSourceNo;
    /**
     * 问题来源分类1-内部 2-外部
     */
    private String questionSourceType;

    /**
     * 问题来源主键 2022.1.24新增，用于议题管理矩阵报表
     */
    private String questionSourceOId;

    /**
     * 问题分类主键 2022.1.24新增，用于议题管理矩阵报表
     */
    private String questionClassificationOId;

    /**
     * 重要性 1-重要 2-不重要 (2022.1.24新增，用于议题管理矩阵报表)
     */
    private int important;

    /**
     * 紧急度 1-紧急 2-不紧急 (2022.1.24新增，用于议题管理矩阵报表)
     */
    private int urgency;

    /**
     * 期望完成时间 问题解决预计完成日(2022.1.24新增，用于议题管理矩阵报表)
     */
    private Date expectFinishTime;

    /**
     * 问题描述
     */
    private String questionDescription;

    /**
     * 问题当前处理状态(0-未开始 1=进行中，2=已完成)
     */
    private Integer currentQuestionProcessStatus;

    /**
     * 负责人id
     */
    private String liablePersonId;

    /**
     * 负责人姓名
     */
    private String liablePersonName;

    /**
     * 职能id
     */
    private String liablePersonPositionId;

    /**
     * 职能名称
     */
    private String liablePersonPositionName;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 问题处理实际结束时间
     */
    private Date actualEndDate;

    /**
     * 是否录入知识库(0-入库 1-否)
     */
    private Integer isKnowledgeBase;

    /**
     * 是否上板(0-上板 1-否)
     */
    private Integer isKeyboard;

    /**
     * 项目代号 迭代三新增
     */
    private String projectNo;

    /**
     * 风险等级主键
     */
    private String riskLevelOId;

    /**
     * 任务预计完成天数(天),在问题分配阶段，提交后更新此字段
     */
    private int taskExpectFinishDays;





    public QuestionRecordEntity(){
        this.important = -1;
        this.urgency = -1;
        this.isKeyboard = -1;
        this.isKnowledgeBase = -1;
        this.currentQuestionProcessStatus = -1;
    }


}
