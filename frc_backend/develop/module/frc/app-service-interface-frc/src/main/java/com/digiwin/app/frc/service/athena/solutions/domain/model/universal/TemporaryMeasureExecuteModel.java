package com.digiwin.app.frc.service.athena.solutions.domain.model.universal;

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
public class TemporaryMeasureExecuteModel implements Serializable {
    private static final long serialVersionUID = -4969224943828439097L;

    @JsonProperty(value = "measure_content")
    private String measureContent;

    @JsonProperty(value = "liable_person_id")
    private String liablePersonId;

    @JsonProperty(value = "liable_person_name")
    private String liablePersonName;

    @JsonProperty(value = "process_department_id")
    private String processDepartmentId;

    @JsonProperty(value = "process_department_name")
    private String processDepartmentName;

    @JsonProperty(value = "expect_solve_date")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date expectSolveDate;

    @JsonProperty(value = "execute_illustrate")
    private String executeIllustrate;

    @JsonProperty(value = "actual_finish_date")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date actualFinishDate;

    @JsonProperty(value = "process_work_hours")
    private String processWorkHours;

    @JsonProperty(value = "execute_status")
    private String executeStatus;

    //附件必传标识
    @JsonProperty(value = "attachment_upload_flag")
    private String attachmentUploadFlag;

}
