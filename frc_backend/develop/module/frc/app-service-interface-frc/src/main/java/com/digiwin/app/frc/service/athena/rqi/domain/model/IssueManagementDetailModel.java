package com.digiwin.app.frc.service.athena.rqi.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/2/11 15:42
 * @Version 1.0
 * @Description 议题矩阵管理详情model
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueManagementDetailModel {

    /**
     * 项目编号
     */
    @JSONField(name = "project_no")
    private String projectNo;

    /**
     * 议题状态
     */
    @JSONField(name = "issue_status")
    private String issueStatus;

    /**
     * 重要性标识
     */
    @JSONField(name = "important_flag")
    private Integer importantFlag;

    /**
     * 紧急性标识
     */
    @JSONField(name = "urgency_flag")
    private Integer urgencyFlag;

    /**
     * 总问题数
     */
    @JSONField(name = "total_question_count")
    private Integer allQuestionQty;

    /**
     * 处理时间占比
     */
    @JSONField(name = "process_time_rate")
    private String processTimeRatio;

    /**
     * 未解决问题数
     */
    @JSONField(name = "unsolved_question_count")
    private Integer unsolveQuestionQty;

    /**
     * 议题年月
     */
    @JSONField(name = "issue_year_month")
    private String issueDate;

}
