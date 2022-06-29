package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/22 9:17
 * @Version 1.0
 * @Description 看板模板实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyBoardTemplateEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户
     */
    private Long tenantSid;

    /**
     * 模板编号
     */
    private String modelNo;

    /**
     * 模板名称
     */
    private String modelName;

    /**
     * 是否生效  Y:是  N:否
     */
    private String manageStatus;

    /**
     * 是否默认  0:是 1:否
     */
    private Integer defaultChoice;

    /**
     * 备注
     */
    private String remarks;

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
