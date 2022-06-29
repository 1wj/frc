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
 * @ClassName PreventionMeasureExecuteModel
 * @Description TODO
 * @Author HeX
 * @Date 2022/3/19 1:21
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PreventionMeasureExecuteModel {
    @JsonProperty(value = "prevention_measure_content")
    @CheckNull(message = "prevention_measure_content is null")
    private String preventionMeasureContent;

    @JsonProperty(value = "liable_person_name")
    @CheckNull(message = "liable_person_name is null")
    private String liablePersonName;

    @JsonProperty(value = "liable_person_id")
    @CheckNull(message = "liable_person_id is null")
    private String liablePersonId;

    @JsonProperty(value = "expect_solve_date")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    @CheckNull(message = "expect_solve_date is null")
    private Date expectSolveDate;

    @JsonProperty(value = "prevention_execute_illustrate")
    @CheckNull(message = "prevention_execute_illustrate is null")
    private String preventionExecuteIllustrate;

    @JsonProperty(value = "complete_date")
    @CheckNull(message = "complete_date is null")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date completeDate;

    @JsonProperty(value = "execute_status")
    @CheckNull(message = "execute_status is null")
    private String executeStatus;

    @JsonProperty(value = "process_work_hours")
    private String processWorkHours;
}
