package com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD;

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
@JsonIgnoreProperties(ignoreUnknown = true )
public class CorrectiveMeasureVerifyVo implements Serializable {
    private static final long serialVersionUID = -5446783042793262354L;

    //糾正内容
    @JsonProperty(value = "corrective_content")
    private String correctiveContent;
    //糾正執行說明
    @JsonProperty(value = "corrective_execute_illustrate")
    private String correctiveExecuteIllustrate;

    //糾正人ID
    @JsonProperty(value = "corrective_person_id")
    private String correctPersonId;

    //糾正人名稱
    @JsonProperty(value = "corrective_person_name")
    private String correctPersonName;
    //糾正人名稱
    @JsonProperty(value = "corrective_status")
    private String correctiveStatus;
    //验收时间
    @JsonProperty(value = "verify_date")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date verifyDate;

    @JsonProperty(value = "process_work_date")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date processWorkDate;

    @JsonProperty(value = "process_work_hours")
    private String processWorkHours;

    //"verify_illustrate": "驗證說明",
    @JsonProperty(value = "verify_illustrate")
    private String verifyIllustrate;
    //验收状态
    @JsonProperty(value = "verify_status")
    private String verifyStatus;
}
