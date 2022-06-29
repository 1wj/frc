package com.digiwin.app.frc.service.athena.rqi.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2022/2/11 10:39
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueManagementEntity {

    private Date createDate;

    private Date actualEndDate;

    private Integer processStatus;

    private Integer important;

    private Integer urgency;

    private String projectNo;

}
