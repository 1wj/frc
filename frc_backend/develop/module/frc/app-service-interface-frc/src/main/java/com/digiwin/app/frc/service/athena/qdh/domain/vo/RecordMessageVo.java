package com.digiwin.app.frc.service.athena.qdh.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName RecordMessageVo
 * @Description TODO
 * @Author author
 * @Date 2021/12/2 16:30
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordMessageVo {

    private String questionNo;

    /**
     * 问题处理步骤 问题反馈(qf) 问题识别处理(qi) 问题识别审核(qir) 问题解决(qs) 问题验收(qa)
     */
    private String questionProcessStep;

    /**
     * 问题解决步骤 问题分配 分配遏制 遏制 遏制审核 问题关闭 .... （方案编号三位流水号）
     */
    private String questionSolveStep;

    /**
     * 问题处理实际完成时间
     */
    private Date actualCompleteDate;

    private String dataContent;


    private Integer questionProcessResult;

    /**
     * 处理人名称
     */
    private String liablePersonName;

    private String questionId;


    private Date expectCompleteDate;
}
