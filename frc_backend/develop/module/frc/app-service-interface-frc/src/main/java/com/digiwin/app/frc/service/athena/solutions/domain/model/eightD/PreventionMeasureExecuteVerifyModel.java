package com.digiwin.app.frc.service.athena.solutions.domain.model.eightD;

import com.digiwin.app.frc.service.athena.config.annotation.CheckNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName PreventionMeasureExecuteVerifyModel
 * @Description 预防措施执行验证model
 * @Author HeX
 * @Date 2022/3/20 10:16
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PreventionMeasureExecuteVerifyModel {
    @JsonProperty(value = "precaution_measure_content")
    @CheckNull(message = "precaution_measure_content is null")
    private String preventionMeasureContent;

    @JsonProperty(value = "liable_person_name")
    @CheckNull(message = "liable_person_name is null")
    private String liablePersonName;

    @JsonProperty(value = "liable_person_id")
    @CheckNull(message = "liable_person_id is null")
    private String liablePersonId;

    @JsonProperty(value = "expect_solve_date")
    @CheckNull(message = "expect_solve_date is null")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date expectSolveDate;

    @JsonProperty(value = "prevention_measure_execute_illustrate")
    @CheckNull(message = "prevention_measure_execute_illustrate is null")
    private String preventionMeasureExecuteIllustrate;

    @JsonProperty(value = "execute_status")
    @CheckNull(message = "execute_status is null")
    private String executeStatus;

    @JsonProperty(value = "verify_illustrate")
    @CheckNull(message = "verify_illustrate is null")
    private String verifyIllustrate;

    @JsonProperty(value = "verify_date")
    @CheckNull(message = "verify_date is null")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date verifyDate;

    @JsonProperty(value = "process_work_date")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date processWorkDate;

    @JsonProperty(value = "process_work_hours")
    private String processWorkHours;

    @JsonProperty(value = "verify_status")
    @CheckNull(message = "verify_status is null")
    private String verifyStatus;
}
