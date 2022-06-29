package com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QF;

import com.digiwin.app.frc.service.athena.qdh.domain.model.init.DetailModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName UpdateModel
 * @Description 问题确认-更新入参
 * @Author HeX
 * @Date 2022/2/15 13:30
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateModel {

    /**
     * 问题信息
     */
    @JsonProperty(value = "question_info")
    List<QuestionInfoModel> questionInfo;

    /**
     * 基础信息
     */
    @JsonProperty(value = "question_basic_info")
    List<QuestionBasicModel> questionBasicInfo;

    /**
     * 详情
     */
    @JsonProperty(value = "question_detail_info")
    List<DetailModel> questionDetailInfo;

}
