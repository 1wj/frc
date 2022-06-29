package com.digiwin.app.frc.service.athena.solutions.domain.model.universal;

import com.digiwin.app.frc.service.athena.config.annotation.CheckNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/4/7 10:30
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionConfirmModel {

    @JsonProperty(value = "status")
    @CheckNull(message = "status is null")
    private String status;

    @JsonProperty(value = "return_step_no")
    @CheckNull(message = "return_step_no is null")
    private String returnStepNo;

    @JsonProperty(value = "return_reason")
    @CheckNull(message = "return_reason is null")
    private String returnReason;


}
