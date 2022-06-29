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
 * @Date: 2022/3/9 13:26
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true )
public class ContainmentMeasureModel {

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

    @JsonProperty(value = "attachment_upload_flag")
    @CheckNull(message = "attachment_upload_flag is null")
    private String attachmentUploadFlag;


    @JsonProperty(value = "disabled")
    private Boolean redundantField1;

    @JsonProperty(value = "uuid")
    private Long redundantField2;

    @JsonProperty(value = "uibot_checked")
    private Boolean redundantField3;

    @JsonProperty(value = "__DATA_KEY")
    private String redundantField4;

    @JsonProperty(value = "edit_type")
    private Integer redundantField5;
}
