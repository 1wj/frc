package com.digiwin.app.frc.service.athena.solutions.domain.model.eightD;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 *功能描述:糾正措施
 * @author cds
 * @date 2022/3/9
 * @param  
 * @return 
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true )
public class CorrectiveActionModel implements Serializable {
    
    private static final long serialVersionUID = -5542092068879857742L;

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

    //附件必传标志
    @JsonProperty(value = "attachment_upload_flag")
    private String attachmentUploadFlag;



}
