package com.digiwin.app.frc.service.athena.ppc.domain.entity;

import com.digiwin.app.frc.service.athena.ppc.domain.vo.FeedbackDepartmentVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionLiablePersonDepartmentLatitudeConfigEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户
     */
    private Long tenantsid;

    /**
     * 配置标识
     */
    private String configFlag;

    /**
     * 问题归属编号
     */
    private String attributionNo;

    /**
     * 风险等级主键
     */
    private String riskLevelId;

    /**
     * 反馈部门信息
     */
    private String feedbackDepartments;

    /**
     * 负责人id
     */
    private String liablePersonId;

    /**
     * 负责人name
     */
    private String liablePersonName;

    /**
     * 验收人角色
     */
    private String acceptanceRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createName;

    /**
     *修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateName;
}
