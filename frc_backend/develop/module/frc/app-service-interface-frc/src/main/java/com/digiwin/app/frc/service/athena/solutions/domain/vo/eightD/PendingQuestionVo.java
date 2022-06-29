package com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2022/3/8 17:25
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingQuestionVo {

    /**
     * 主键
     */
    @JsonProperty(value = "pending_approve_question_id")
    private String oid;


    /**
     * 问题号
     */
    @JsonProperty(value = "question_no")
    private String questionNo;

    /**
     * 问题描述
     */
    @JsonProperty(value = "question_description")
    private String questionDescription;


    /**
     * 退回标识id 识别退回(qir) 问题解决退回(qsr) 验收退回(qar) 问题关闭退回(sc002005r) 遏制审核退回(sc002004r)
     */
    @JsonProperty(value = "return_flag_id")
    private String returnFlagId;

    /**
     * 退回标识name 识别退回(qir) 问题解决退回(qsr) 验收退回(qar) 问题关闭退回(sc002005r) 遏制审核退回(sc002004r)
     */
    @JsonProperty(value = "return_flag_name")
    private String returnFlagName;

    /**
     * 问题处理预计完成时间
     */
    @JsonProperty(value = "expect_complete_date")
    private Date expectCompleteDate;


    @JsonProperty(value = "liable_person_id")
    private String liablePersonId;

    /**
     * 处理人姓名
     */
    @JsonProperty(value = "liable_person_name")
    private String liablePersonName;


    /**
     * athena員工id
     */
    @JsonProperty(value = "employee_id")
    private String empId;


    /**
     * athena員工name
     */
    @JsonProperty(value = "employee_name")
    private String empName;





}
