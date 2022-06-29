package com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QuestionIdentifyInfoModel
 * @Description TODO
 * @Author author
 * @Date 2022/2/8 11:30
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionIdentifyInfoModel {
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
}
