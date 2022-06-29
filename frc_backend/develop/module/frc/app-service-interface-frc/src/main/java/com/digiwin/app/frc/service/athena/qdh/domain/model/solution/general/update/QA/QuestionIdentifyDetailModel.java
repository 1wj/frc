package com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QA;

import com.digiwin.app.frc.service.athena.config.annotation.CheckNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ActionTraceModel
 * @Description 用于更新问题识别或问题分析
 * @Author author
 * @Date 2021/11/11 22:18
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionIdentifyDetailModel {

    /**
     * 重复次数
     */
    @JsonProperty(value = "repeat_times")
    private int repeatTimes;

    /**
     * 风险等级主键
     */
    @JsonProperty(value = "risk_level_id")
    @CheckNull(message = "risk_level_id is  null")
    private String riskLevelOId;

    /**
     * 风险等级编号
     */
    @JsonProperty(value = "risk_level_no")
    @CheckNull(message = "risk_level_no is  null")
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
     * 解决方案id
     */
    @JsonProperty(value = "solution_id")
    @CheckNull(message = "solution_id is null")
    private String solutionId;

    /**
     * 解決方案名称
     */
    @JsonProperty(value = "solution_name")
    @CheckNull(message = "solution_name is null")
    private String solutionName;

    /**
     * 处理人员id
     */
    @JsonProperty(value = "liable_person_id")
    @CheckNull(message = "liable_person_id is null")
    private String liablePersonId;

    /**
     * 处理人姓名
     */
    @JsonProperty(value = "liable_person_name")
    @CheckNull(message = "liable_person_name is null")
    private String liablePersonName;

    /**
     * 是否上版	0.上版; 1.不上版
     */
    @JsonProperty(value = "is_upload_kanban")
    private int isUploadKanban;


}
