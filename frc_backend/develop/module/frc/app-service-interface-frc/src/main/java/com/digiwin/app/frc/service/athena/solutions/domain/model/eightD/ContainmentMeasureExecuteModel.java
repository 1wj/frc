package com.digiwin.app.frc.service.athena.solutions.domain.model.eightD;

import com.digiwin.app.frc.service.athena.config.annotation.CheckNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2022/3/13 22:27
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainmentMeasureExecuteModel {

    @JsonProperty(value = "containment_place")
    @CheckNull(message = "containment_place is null")
    private String containmentPlace;

    @JsonProperty(value = "liable_person_id")
    @CheckNull(message = "liable_person_id is null")
    private String liablePersonId;

    @JsonProperty(value = "liable_person_name")
    @CheckNull(message = "liable_person_name is null")
    private String liablePersonName;

    @JsonProperty(value = "expect_solve_date")
    @CheckNull(message = "expect_solve_date is null")
    private Date expectSolveDate;

    @JsonProperty(value = "containment_illustrate")
    @CheckNull(message = "containment_illustrate is null")
    private String containmentInstruction;

    @JsonProperty(value = "actual_finish_date")
    @CheckNull(message = "actual_finish_date is null")
    private Date actualFinishDate;

    @JsonProperty(value = "containment_status")
    @CheckNull(message = "containment_status is null")
    private String containmentStatus;

}
