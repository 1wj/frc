package com.digiwin.app.frc.service.athena.qdh.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName QuestionDetailVo
 * @Description 查询详情Vo
 * @Author author
 * @Date 2021/11/15 10:11
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDetailVo {
    private String questionId;

    private int questionProcessStatus;

    private int questionProcessResult;

    private String dataContent;

    private String questionNo;

    private String questionRecordId;

    private String returnFlagId;

    private String returnFlagName;

    private String returnNo;

    private String returnId;

    private String returnName;

    private String content;

    private String dataInstanceOid;

    private Long tenantsid;

    private String questionSolveStep;

    private String questionProcessStep;

    private String questionDescription;

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

    private String closeReason;

    private Date createDate;

    /**
     * 冲刺六新增字段
     */
    private String returnReason;

    private String returnStepNo;

    /**
     * 冲刺六 新增字段
     */
    private Date expectCompleteDate;
}
