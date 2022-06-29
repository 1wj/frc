package com.digiwin.app.frc.service.athena.solutions.domain.model.eightD;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/3/8 22:43
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true )
public class TeamMemberModel {

    @JsonProperty(value = "member_id")
    private String memberId;

    @JsonProperty(value = "member_name")
    private String memberName;

    @JsonProperty(value = "department_id")
    private String departmentId;

    @JsonProperty(value = "department_name")
    private String departmentName;

    @JsonProperty(value = "duty_no")
    private String jobNo;

    @JsonProperty(value = "duty_name")
    private String jobName;

    @JsonProperty(value = "role_no")
    private String roleNo;

    @JsonProperty(value = "role_name")
    private String roleName;

    @JsonProperty(value = "remark")
    private String remark;



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
