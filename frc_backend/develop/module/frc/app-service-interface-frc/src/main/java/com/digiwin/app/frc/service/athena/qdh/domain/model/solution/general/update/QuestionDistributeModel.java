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
public class QuestionDistributeModel {
    /**
     * 问题分配要求
     */
    @JsonProperty(value = "question_distribute_request")
    private String questionDistributeRequest;

    @JsonProperty(value = "question_distribute_detail")
    List<QuestionDistributeDetailModel> questionDistributeDetailModels;

}
