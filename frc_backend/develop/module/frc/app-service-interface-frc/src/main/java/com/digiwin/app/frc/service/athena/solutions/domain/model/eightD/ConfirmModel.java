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
 * @ClassName ConfirmModel
 * @Description 处理确认
 * @Author HeX
 * @Date 2022/3/20 11:33
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfirmModel {

    @JsonProperty(value = "action_confirm_illustrate")
    @CheckNull(message = "action_confirm_illustrate is null")
    private String actionConfirmIllustrate;

    @JsonProperty(value = "liable_person_name")
    @CheckNull(message = "liable_person_name is null")
    private String liablePersonName;

    @JsonProperty(value = "liable_person_id")
    @CheckNull(message = "liable_person_id is null")
    private String liablePersonId;

    @JsonProperty(value = "confirm_date")
    @CheckNull(message = "confirm_date is null")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date confirmDate;

}
