package com.digiwin.app.frc.service.athena.ppc.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/2/17 11:20
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassificationVo {


    @JsonProperty(value = "classification_id")
    private String classificationId;

    @JsonProperty(value = "classification_name")
    private String classificationName;


}
