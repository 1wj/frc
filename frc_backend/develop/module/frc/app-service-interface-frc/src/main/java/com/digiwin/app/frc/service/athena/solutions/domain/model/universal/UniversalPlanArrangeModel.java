package com.digiwin.app.frc.service.athena.solutions.domain.model.universal;

import com.digiwin.app.frc.service.athena.config.annotation.CheckNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2022/4/7 10:26
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UniversalPlanArrangeModel {

    @JsonProperty(value = "step_no")
    @CheckNull(message = "measure_no is null")
    private String measureNo;

    @JsonProperty(value = "step_name")
    @CheckNull(message = "measure_name is null")
    private String measureName;

    @JsonProperty(value = "liable_person_id")
    @CheckNull(message = "liable_person_id is null")
    private String liablePersonId;

    @JsonProperty(value = "liable_person_name")
//    @CheckNull(message = "liable_person_name is null")
    private String liablePersonName;

    @JsonProperty(value = "expect_solve_date")
    @CheckNull(message = "expect_solve_date is null")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date expectSolveDate;

    @JsonProperty(value = "attachment_upload_flag")
    @CheckNull(message = "attachment_upload_flag is null")
    private String attachmentUploadFlag;

    @JsonProperty(value = "human_bottleneck_analysis")
//    @CheckNull(message = "human_bottleneck_analysis is null")
    private String humanBottleneckAnalysis;

    @JsonProperty(value = "process_department_name")
    @CheckNull(message = "process_department_name is null")
    private String processDepartmentName;

    @JsonProperty(value = "process_department_id")
    @CheckNull(message = "process_department_id is null")
    private String processDepartmentId;

}
