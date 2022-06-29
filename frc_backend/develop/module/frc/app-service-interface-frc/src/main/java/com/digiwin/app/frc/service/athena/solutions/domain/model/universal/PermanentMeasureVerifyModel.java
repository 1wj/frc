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
 * @Date: 2022/4/11 14:49
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PermanentMeasureVerifyModel {

    @JsonProperty(value = "measure_content")
    @CheckNull(message = "measure_content is null")
    private String measureContent;

    @JsonProperty(value = "liable_person_id")
    @CheckNull(message = "liable_person_id is null")
    private String liablePersonId;

    @JsonProperty(value = "liable_person_name")
    @CheckNull(message = "liable_person_name is null")
    private String liablePersonName;

    @JsonProperty(value = "process_department_id")
    @CheckNull(message = "process_department_id is null")
    private String processDepartmentId;

    @JsonProperty(value = "process_department_name")
    @CheckNull(message = "process_department_name is null")
    private String processDepartmentName;


    @JsonProperty(value = "expect_solve_date")
    @CheckNull(message = "expect_solve_date is null")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date expectSolveDate;

    @JsonProperty(value = "execute_illustrate")
    @CheckNull(message = "execute_illustrate is null")
    private String executeIllustrate;

    @JsonProperty(value = "actual_finish_date")
    @CheckNull(message = "actual_finish_date is null")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date actualFinishDate;

    @JsonProperty(value = "process_work_hours")
    @CheckNull(message = "process_work_hours is null")
    private String processWorkHours;

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

    @JsonProperty(value = "verify_status")
    @CheckNull(message = "verify_status is null")
    private String verifyStatus;



}
