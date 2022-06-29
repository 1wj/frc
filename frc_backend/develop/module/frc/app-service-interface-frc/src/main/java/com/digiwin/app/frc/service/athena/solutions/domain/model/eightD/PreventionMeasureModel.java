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
 * @Author: xieps
 * @Date: 2022/3/17 1:17
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PreventionMeasureModel {

    @JsonProperty(value = "prevention_measure_content")
    @CheckNull(message = "prevention_measure_content is null")
    private String preventionMeasureContent;

    @JsonProperty(value = "liable_person_id")
    @CheckNull(message = "liable_person_id is null")
    private String liablePersonId;

    @JsonProperty(value = "liable_person_name")
    @CheckNull(message = "liable_person_name is null")
    private String liablePersonName;

    @JsonProperty(value = "expect_solve_date")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    @CheckNull(message = "expect_solve_date is null")
    private Date expectSolveDate;

    @JsonProperty(value = "attachment_upload_flag")
    @CheckNull(message = "attachment_upload_flag is null")
    private String attachmentUploadFlag;
}
