package com.digiwin.app.frc.service.athena.rqi.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2022/1/4 9:19
 * @Version 1.0
 * @Description  任务提出者问题追踪数据实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionTrackProposerEntity {

    /**
     * 问题单号
     */
    private String questionNo;


    /**
     * 数据实例数据JSON
     */
    private String dataContent;

    /**
     * 问题描述
     */
    private String questionDescription;

    /**
     * 问题发起时间
     */
    private Date questionInitiateDate;

    /**
     * 问题接受/结案时间
     */
    private Date questionReceiveDate;



    /**
     * 问题处理阶段
     */
    private String questionProcessStep;

    /**
     * 问题解决步骤
     */
    private String questionSolveStep;



    /**
     * 以下这三个字段   单据的状态需要根据该三个字段进行过滤
     *
     * 问题处理状态
     */
    private Integer questionProcessStatus;

    /**
     * 问题退回处理结果
     */
    private Integer questionProcessResult;

    /**
     * 问题退回标识
     */
    private String returnFlagId;




    /**
     * 问题处理状态名称
     */
    private String questionProcessStatusName;

    /**
     * 处理人ID
     */
    private String processPersonId;

    /**
     * 处理人名称
     */
    private String processPersonName;


    /**
     * 问题处理预计完成时间
     */
    private Date expectCompleteTime;

    private Date startTime;

    private String questionId;
}
