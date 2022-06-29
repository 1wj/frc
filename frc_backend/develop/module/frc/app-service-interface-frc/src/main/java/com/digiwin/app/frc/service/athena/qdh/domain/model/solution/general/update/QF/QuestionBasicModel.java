package com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QF;

import com.digiwin.app.frc.service.athena.config.annotation.CheckNull;
import com.digiwin.app.frc.service.athena.qdh.domain.model.QuestionPictureModel;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName BasicModel
 * @Description 问题确认单-更新
 * "question_basic_info": [
 *                 {
 *                     "question_source_name": "生产部",
 *                     "question_source_oid": "93656fb410fc4b8fbcfac2fa2aa42a17",
 *                     "risk_level_name": "S",
 *                     "occur_stage_name": "",
 *                     "risk_level_oid": "123",
 *                     "question_classification_no": "Q3",
 *                     "important": 1,
 *                     "question_classification_name": "安全",
 *                     "urgency": 1,
 *                     "occur_stage_no": "",
 *                     "risk_level_no": "Q1",
 *                     "question_picture": [
 *                         {
 *                             "picture_id": ""
 *                         }
 *                     ],
 *                     "question_description": "2",
 *                     "question_attribution_no": "1",
 *                     "question_source_no": "02001",
 *                     "question_classification_oid": "a5328a4abe4c44e8bf2282ee3b9587e0"
 *                 }
 *             ],
 * @Author author
 * @Date 2022/2/10 18:36
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBasicModel {

    /**
     * 问题归属编号
     */
    @JsonProperty(value = "question_attribution_no")
    @CheckNull(message = "question_attribution_no is null")
    private String questionAttributionNo;

    /**
     * 问题来源编号
     */
    @JsonProperty(value = "question_source_no")
    private String questionSourceNo;

    /**
     * 问题来源名称
     */
    @JsonProperty(value = "question_source_name")
    private String questionSourceName;

    /**
     * 问题来源主键 2022.1.24新增，用于议题管理矩阵报表
     */
    @JsonProperty(value = "question_source_id")
    @CheckNull(message = "question_source_id is null")
    private String questionSourceOId;

    /**
     * 问题分类主键 2022.1.24新增，用于议题管理矩阵报表
     */
    @JsonProperty(value = "question_classification_id")
    @CheckNull(message = "question_classification_id is null")
    private String questionClassificationOId;

    /**
     * 问题分类编号
     */
    @JsonProperty(value = "question_classification_no")
    private String questionClassificationNo;

    /**
     * 问题分类名称
     */
    @JsonProperty(value = "question_classification_name")
    private String questionClassificationName;

    /**
     * 风险等级主键
     */
    @JsonProperty(value = "risk_level_id")
    @CheckNull(message = "risk_level_id is null")
    private String riskLevelOId;

    /**
     * 风险等级编号
     */
    @JsonProperty(value = "risk_level_no")
    private String riskLevelNo;

    /**
     * 风险等级名称
     */
    @JsonProperty(value = "risk_level_name")
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
     * 问题描述
     */
    @JsonProperty(value = "question_description")
    @CheckNull(message = "question_description is null")
    private String questionDescription;

    /**
     * 问题发生阶段编号
     */
    @JsonProperty(value = "occur_stage_no")
    private String occurStageNo;

    /**
     * 问题发生阶段名称
     */
    @JsonProperty(value = "occur_stage_name")
    private String occurStageName;

    /**
     * 问题图片集合
     */
    @JsonProperty(value = "question_picture")
    private List<QuestionPictureModel> pictureModels;
}
