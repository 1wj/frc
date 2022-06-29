package com.digiwin.app.frc.service.athena.qdh.domain.vo.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QuestionIdentifyVo
 * @Description 问题详情之问题识别(迭代一叫法)问题分析(迭代三)信息
 * @Author author
 * @Date 2021/11/12 16:23
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionIdentifyVo {
    /**
     * 重复次数
     */
    @JsonProperty(value = "repeat_times")
    private int repeatTimes;


    /**
     * 解决方案id
     */
    @JsonProperty(value = "solution_id")
    private String solutionId;

    /**
     * 解决方案名称
     */
    @JsonProperty(value = "solution_name")
    private String solutionName;

    /**
     * 负责人id
     */
    @JsonProperty(value = "liable_person_id")
    private String liablePersonId;

    /**
     * 负责人名称
     */
    @JsonProperty(value = "liable_person_name")
    private String liablePersonName;

    /**
     * 是否上版
     */
    @JsonProperty(value = "is_upload_kanban")
    private int isUploadKanban;
}
