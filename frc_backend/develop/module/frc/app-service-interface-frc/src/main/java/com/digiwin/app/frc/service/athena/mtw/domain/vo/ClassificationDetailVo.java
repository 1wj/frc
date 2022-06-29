package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/2/23 17:42
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassificationDetailVo {

    @JsonProperty(value = "classification_id")
    private String classificationId;

    @JsonProperty(value = "classification_name")
    private String classificationName;

    @JsonProperty(value = "classification_no")
    private String classificationNo;

}
