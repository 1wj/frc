package com.digiwin.app.frc.service.athena.rqi.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2022/1/25 16:27
 * @Version 1.0
 * @Description 问题看板信息实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KanbanInfoEntity {

    /**
     * 问题号主键
     */
    private String questionId;

    /**
     * 问题单号
     */
    private String questionNo;

    /**
     * 问题处理步骤
     */
    private String questionProcessStep;

    /**
     * 问题解决步骤
     */
    private String questionSolveStep;

    /**
     * 问题描述信息
     */
    private String questionDescription;

    /**
     * 问题处理状态
     */
    private Integer questionProcessStatus;

    /**
     * 问题处理结果
     */
    private Integer questionProcessResult;

    /**
     * 问题处理预计完成时间
     */
    private Date exceptCompleteDate;

    /**
     * 问题处理实际关闭时间
     */
    private Date actualCompleteDate;

    /**
     * 负责人id
     */
    private String liablePersonId;

    /**
     * 负责rent名称
     */
    private String liablePersonName;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 问题实例表信息
     */
    private String dataContent;


    /**
     * 处理人处理顺序
     */
    private Integer principalStep;


    /**
     * 更新时间
     */
    private Date updateDate;


    private String returnFlagId;
}
