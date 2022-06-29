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
 * @Date: 2022/4/12 16:18
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfirmVerifyModel {

    @JsonProperty(value = "verify_illustrate")
    @CheckNull(message = "verify_illustrate is null")
    private String verifyIllustrate;

    @JsonProperty(value = "verify_person_id")
    @CheckNull(message = "verify_person_id is null")
    private String verifyPersonId;

    @JsonProperty(value = "verify_person_name")
    @CheckNull(message = "verify_person_name is null")
    private String verifyPersonName;

    @JsonProperty(value = "verify_date")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    @CheckNull(message = "verify_date is null")
    private Date verifyDate;


}
