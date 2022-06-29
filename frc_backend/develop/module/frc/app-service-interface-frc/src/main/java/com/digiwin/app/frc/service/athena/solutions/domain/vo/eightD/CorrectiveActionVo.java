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
public class CorrectiveActionVo implements Serializable {
    private static final long serialVersionUID = -1830133090594238426L;
    //糾正內容
    @JsonProperty(value = "corrective_content")
    private String correctContent;

    //糾正人ID
    @JsonProperty(value = "corrective_person_id")
    private String correctPersonId;

    //糾正人名稱
    @JsonProperty(value = "corrective_person_name")
    private String correctPersonName;

    //糾正部門ID
    @JsonProperty(value = "corrective_department_id")
    private String correctPositionId;

    //糾正部門名稱
    @JsonProperty(value = "corrective_department_name")
    private String correctPositionName;

    //預計完成時間
    @JsonProperty(value = "expect_solve_date")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date expectSolveDate;

    @JsonProperty(value = "process_work_hours")
    private String processWorkHours;

    @JsonProperty(value = "corrective_execute_illustrate")
    private String correctExecuteIllustrate;

    @JsonProperty(value = "corrective_date")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date correctDate;

    @JsonProperty(value = "complete_status")
    private String finishStatus;
}
