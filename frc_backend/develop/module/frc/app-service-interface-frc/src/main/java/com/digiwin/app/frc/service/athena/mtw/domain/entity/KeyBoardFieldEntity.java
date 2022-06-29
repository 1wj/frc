package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/22 10:02
 * @Version 1.0
 * @Description  模板栏位实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyBoardFieldEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户
     */
    private Long tenantSid;

    /**
     * 看板模板主键
     */
    private String keyBoardTemplateOid;

    /**
     * 栏位id
     */
    private String fieldId;

    /**
     * 栏位名称
     */
    private String fieldName;

    /**
     * 是否生效  Y:是  N:否
     */
    private String manageStatus;

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
