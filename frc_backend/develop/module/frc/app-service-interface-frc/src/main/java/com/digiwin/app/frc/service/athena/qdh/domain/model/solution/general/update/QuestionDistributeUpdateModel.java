package com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName ActionTraceModel
 * @Description 用于-更新问题分配单
 * @Author author
 * @Date 2021/11/21 0:03
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDistributeUpdateModel {
    /**
     * 主键
     */
    @JsonProperty(value = "question_id")
    private String oid;

    /**
     * 问题处理状态
     */
    @JsonProperty(value = "question_process_status")
    private Integer questionProcessStatus;

    /**
     * 问题处理结果
     */
    @JsonProperty(value = "question_process_result")
    private Integer questionProcessResult;

    @JsonProperty(value = "question_distribute_info")
    private List<QuestionDistributeModel> questionDistributeModels;


}
