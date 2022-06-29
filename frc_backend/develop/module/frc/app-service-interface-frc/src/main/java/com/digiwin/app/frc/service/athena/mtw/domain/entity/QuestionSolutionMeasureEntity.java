package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/18 13:21
 * @Version 1.0
 * @Description 解决方案步骤实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSolutionMeasureEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户
     */
    private Long tenantSid;

    /**
     * 解决方案维护oid
     */
    private String solutionEditOid;

    /**
     * 步骤编号
     */
    private String measureNo;

    /**
     * 步骤名称
     */
    private String measureName;

    /**
     * 处理人id
     */
    private  String principalId;

    /**
     * 处理人名字
     */
    private String principalName;

    /**
     * 预计完成天数
     */
    private String expectCompleteTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createName;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateName;
}
