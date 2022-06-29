package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/16 15:29
 * @Version 1.0
 * @Description 解决方案配置实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSolutionEditEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户
     */
    private Long tenantSid;

    /**
     * 方案编号
     */
    private String solutionNo;

    /**
     * 方案名称
     */
    private String solutionName;

    /**
     * 是否生效  Y:生效  N:未生效
     */
    private String manageStatus;

    /**
     * 是否默认  0:是   1:否
     */
    private Integer defaultChoice;

    /**
     * 负责人id
     */
    private String directorId;

    /**
     * 负责人名字
     */
    private String directorName;

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
