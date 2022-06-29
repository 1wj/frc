package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.digiwin.app.frc.service.athena.ppc.domain.vo.ClassificationVo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/19 16:25
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSourceQueryVo {

    /**
     * 主键
     */
    @JsonProperty(value = "source_id")
    private String oid;

    /**
     * 来源编号
     */
    @JsonProperty(value = "source_no")
    private String sourceNo;

    /**
     * 来源名称
     */
    @JsonProperty(value = "source_name")
    private String sourceName;

    /**
     * 来源分类
     */
    @JsonProperty(value = "source_classification_no")
    private String sourceCategory;

    /**
     * 是否生效
     */
    @JsonProperty(value = "manage_status")
    private String manageStatus;

    /**
     * 备注
     */
    @JsonProperty(value = "remarks")
    private String remarks;

    @JsonProperty(value = "question_classification_info")
    private List<ClassificationDetailVo> classificationVos;

}
