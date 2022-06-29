package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/24 9:23
 * @Version 1.0
 * @Description 看板权限配置实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyBoardAuthorityEntity {

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
    private String modelOid;

    /**
     * 看板模板名称
     */
    private String modelName;


    /**
     * 指定查看人
     */
    private String specifyViewer;


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
