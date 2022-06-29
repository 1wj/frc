package com.digiwin.app.frc.service.athena.qdh.domain.vo.solution;

import com.digiwin.app.frc.service.athena.qdh.domain.vo.solution.QuestionPictureVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName BaseInfoVo
 * @Description 问题详情之基础信息
 * @Author author
 * @Date 2021/11/11 10:18
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicInfoVo {

    @JsonProperty(value = "question_classification_no")
    private String questionClassificationNo;

    @JsonProperty(value = "question_classification_name")
    private String questionClassificationName;

    @JsonProperty(value = "question_source_no")
    private String questionSourceNo;

    @JsonProperty(value = "question_source_name")
    private String questionSourceName;

    @JsonProperty(value = "question_happen_date")
    private String questionHappenDate;

    @JsonProperty(value = "expect_solve_date")
    private String expectSolveDate;

    @JsonProperty(value = "question_proposer_id")
    private String questionProposerId;

    @JsonProperty(value = "question_proposer_name")
    private String questionProposerName;

    @JsonProperty(value = "proposer_department_name")
    private String proposerDepartmentName;

    @JsonProperty(value = "proposer_department_id")
    private String proposerDepartmentId;

    @JsonProperty(value = "question_description")
    private String questionDescription;

    @JsonProperty(value = "question_picture")
    private List<QuestionPictureVo> questionPictureVos;

}
