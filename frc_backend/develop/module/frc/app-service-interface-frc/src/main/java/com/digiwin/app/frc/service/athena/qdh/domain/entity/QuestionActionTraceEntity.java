package com.digiwin.app.frc.service.athena.qdh.domain.entity;

import com.digiwin.app.frc.service.athena.config.annotation.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName QuestionActionTraceEntity
 * @Description 问题追踪处理实体类
 * @Author author
 * @Date 2021/11/10 08:49
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
public class QuestionActionTraceEntity extends BaseEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 问题记录oid
     */
    private String questionRecordOid;

    /**
     * 问题处理步骤 问题反馈(qf) 问题识别处理(qi) 问题识别审核(qir) 问题解决(qs) 问题验收(qa)
     */
    private String questionProcessStep;

    /**
     * 问题解决步骤 问题分配 分配遏制 遏制 遏制审核 问题关闭 .... （方案编号三位流水号）
     */
    private String questionSolveStep;

    /**
     * 问题号
     */
    private String questionNo;

    /**
     * 问题描述
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
     * 问题处理意见
     */
    private String handleComment;

    /**
     * 退回标识id 识别退回(qir) 问题解决退回(qsr) 验收退回(qar) 问题关闭退回(sc002005r) 遏制审核退回(sc002004r)
     */
    private String returnFlagId;

    /**
     * 退回标识name 识别退回(qir) 问题解决退回(qsr) 验收退回(qar) 问题关闭退回(sc002005r) 遏制审核退回(sc002004r)
     */
    private String returnFlagName;

    private String returnNo;

    private String returnId;

    private String returnName;


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
    private Date expectCompleteDate;

    /**
     * 问题处理实际完成时间
     */
    private Date actualCompleteDate;

    /**
     * 处理人处理顺序
     */
    private Integer principalStep;

    private String liablePersonId;

    /**
     * 处理人姓名
     */
    private String liablePersonName;

    /**
     * 负责人职能id
     */
    private String liablePersonPositionId;

    /**
     * 负责人职能名称
     */
    private String liablePersonPositionName;

    /**
     * 预留字段1
     */
    private String res01;

    /**
     * 预留字段2
     */
    private String res02;

    /**
     * 预留字段3
     */
    private String res03;

    /**
     * 预留字段4
     */
    private String res04;

    /**
     * 预留字段5
     */
    private String res05;

    @NotNull(message = "skip is null")
    private String skip;

    private String closeReason;

    /**
     * 迭代六新增字段 return_reason
     */
    private String returnReason;

    /**
     * 迭代五新增字段  return_step_no  退回节点编号
     */
    private String returnStepNo;

    public QuestionActionTraceEntity(){
        this.setUpdateTime(new Date());
    }


    public Date getActualCompleteDate() {
        return actualCompleteDate;
    }

    public void setActualCompleteDate(Date actualCompleteDate) {
        this.actualCompleteDate = actualCompleteDate;
    }
}