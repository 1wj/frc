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
 * @Date: 2022/5/9 14:37
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShortTermVerifyModel {

    @JsonProperty(value = "verify_illustrate")
    @CheckNull(message = "verify_illustrate is null")
    private String verifyIllustrate;

    @JsonProperty(value = "inspector_id")
    @CheckNull(message = "inspector_id is null")
    private String inspectorId;

    @JsonProperty(value = "inspector_name")
    @CheckNull(message = "inspector_name is null")
    private String inspectorName;

    @JsonProperty(value = "verify_date")
    @CheckNull(message = "verify_date is null")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date verifyDate;



}
