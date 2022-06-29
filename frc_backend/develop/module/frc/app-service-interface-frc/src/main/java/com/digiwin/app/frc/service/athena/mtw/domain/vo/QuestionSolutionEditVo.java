package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author: xieps
 * @Date: 2021/11/16 15:54
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSolutionEditVo {

    /**
     * 主键
     */
    @JsonProperty(value = "solution_key_id")
    private String oid;

    /**
     * 方案编号
     */
    @JsonProperty(value = "solution_id")
    private String solutionNo;

    /**
     * 方案名称
     */
    @JsonProperty(value = "solution_name")
    private String solutionName;

    /**
     * 是否生效  Y:生效  N:未生效
     */
    @JsonProperty(value = "manage_status")
    private String manageStatus;

    /**
     * 是否默认  0:是   1:否
     */
    @JsonProperty(value = "is_default")
    private Integer defaultChoice;

    /**
     * 负责人id
     */
    @JsonProperty(value = "liable_person_id")
    private String directorId;

    /**
     * 负责人名字
     */
    @JsonProperty(value = "liable_person_name")
    private String directorName;


}
