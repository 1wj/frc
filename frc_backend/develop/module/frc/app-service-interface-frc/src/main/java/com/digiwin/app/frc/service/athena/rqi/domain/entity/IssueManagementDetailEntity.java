package com.digiwin.app.frc.service.athena.rqi.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2022/2/11 16:59
 * @Version 1.0
 * @Description 议题管理详细信息实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueManagementDetailEntity {

    private String projectNo;

    private String sourceName;

    private String classificationName;

    private Integer important;

    private Date actualEndDate;

    private Date createDate;

    private Date expectFinishTime;

    private String questionNo;

    private Integer taskFinishTime;

}
