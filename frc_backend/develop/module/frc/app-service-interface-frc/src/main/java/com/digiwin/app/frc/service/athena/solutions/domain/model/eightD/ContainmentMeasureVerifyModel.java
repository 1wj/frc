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
 * @Date: 2022/3/14 16:00
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainmentMeasureVerifyModel {

    @JsonProperty(value = "containment_place")
    @CheckNull(message = "containment_place is null")
    private String containmentPlace;

    @JsonProperty(value = "liable_person_id")
    @CheckNull(message = "liable_person_id is null")
    private String liablePersonId;


    @JsonProperty(value = "liable_person_name")
    @CheckNull(message = "liable_person_name is null")
    private String liablePersonName;

    @JsonProperty(value = "containment_illustrate")
    @CheckNull(message = "containment_illustrate is null")
    private String containmentIllustrate;

    @JsonProperty(value = "actual_finish_date")
    @CheckNull(message = "actual_finish_date is null")
    private Date actualFinishDate;

    @JsonProperty(value = "containment_status")
    @CheckNull(message = "containment_status is null")
    private String containmentStatus;

    @JsonProperty(value = "verify_illustrate")
    @CheckNull(message = "verify_illustrate is null")
    private String verifyIllustrate;

    @JsonProperty(value = "verify_date")
    @CheckNull(message = "verify_date is null")
    private Date verifyDate;

    @JsonProperty(value = "verify_status")
    @CheckNull(message = "verify_status is null")
    private String verifyStatus;



}
