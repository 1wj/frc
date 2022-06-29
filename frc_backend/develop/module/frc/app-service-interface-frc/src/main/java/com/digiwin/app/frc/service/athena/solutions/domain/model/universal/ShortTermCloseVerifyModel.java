package com.digiwin.app.frc.service.athena.solutions.domain.model.universal;

import com.digiwin.app.frc.service.athena.config.annotation.CheckNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShortTermCloseVerifyModel implements Serializable {


    private static final long serialVersionUID = 4062107129606460641L;
    //驗收說明
    @JsonProperty(value = "verify_illustrate")
    @CheckNull(message = "verify_illustrate is null")
    private String verifyIllustrate;

    //驗收員id
    @JsonProperty(value = "inspector_id")
    @CheckNull(message = "inspector_id is null")
    private String inspectorId;

    //驗收員名稱
    @JsonProperty(value = "inspector_name")
    @CheckNull(message = "inspector_name is null")
    private String inspectorName;

    //驗證時間
    @JsonProperty(value = "verify_date")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    @CheckNull(message = "verify_date is null")
    private Date verifyDate;
}
