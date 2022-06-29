package com.digiwin.app.frc.service.athena.qdh.domain.vo.solution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName RecordDetailVo
 * @Description 取得问题记录信息 -vo
 * @Author author
 * @Date 2021/11/15 22:41
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordDetailVo {
    @JsonProperty(value = "progress_status")
    private String progressStatus;

    @JsonProperty(value = "question_no")
    private String questionNo;

    @JsonProperty(value = "step_name")
    private String stepName;

    private String questionProcessStep;

    /**
     * 问题解决步骤 问题分配 分配遏制 遏制 遏制审核 问题关闭 .... （方案编号三位流水号）
     */
    private String questionSolveStep;

    @JsonProperty(value = "actual_complete_date")
    private Date actualCompleteDate;

    @JsonProperty(value = "expect_complete_date")
    private Date expectCompleteDate;

    @JsonProperty(value = "attachment_status")
    private Date attachmentStatus;

    @JsonProperty(value = "process_person_id")
    private String liablePersonId;

    @JsonProperty(value = "process_person_name")
    private String liablePersonName;

}
