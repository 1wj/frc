package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/23 10:10
 * @Version 1.0
 * @Description 看板栏位匹配实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyBoardDisplayEntity {

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
     *解决方案编号
     */
    private String solutionNo;

    /**
     * 解决方案名称
     */
    private String solutionName;

    /**
     * 模板栏位id
     */
    private String fieldId;

    /**
     * 模板栏位名称
     */
    private String fieldName;

    /**
     * 方案步骤编号
     */
    private String measureNo;

    /**
     * 方案步骤名称
     */
    private String measureName;

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
