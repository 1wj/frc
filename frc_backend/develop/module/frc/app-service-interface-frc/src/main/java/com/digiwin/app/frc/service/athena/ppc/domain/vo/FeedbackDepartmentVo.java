package com.digiwin.app.frc.service.athena.ppc.domain.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDepartmentVo {
    @JsonProperty("feedback_department_id")
    private String feedbackDepartmentId;

    @JsonProperty("feedback_department_name")
    private String feedbackDepartmentName;
}
