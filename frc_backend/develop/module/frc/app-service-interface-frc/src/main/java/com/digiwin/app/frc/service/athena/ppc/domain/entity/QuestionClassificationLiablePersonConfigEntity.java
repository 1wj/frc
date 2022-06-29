package com.digiwin.app.frc.service.athena.ppc.domain.entity;

import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
*@ClassName: QuestionClassificationLiablePersonConfigEntity
*@Description 问题分类和问题责任人配置关系实体类
*@Author Jiangyw
*@Date 2022/3/28
*@Version 1.0
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionClassificationLiablePersonConfigEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户id
     */
    private Long tenantSid;

    /**
     * 分类主键
     */
    private String classificationOid;

    /**
     * 问题责任人配置主键
     */
    private String liablePersonConfigOid;

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

    public QuestionClassificationLiablePersonConfigEntity(String liablePersonConfigOid, String classificationOid){
        setOid(IdGenUtil.uuid());
        setTenantSid(TenantTokenUtil.getTenantSid());
        setCreateTime(new Date());
        setCreateName(TenantTokenUtil.getUserName());
        setClassificationOid(classificationOid);
        setLiablePersonConfigOid(liablePersonConfigOid);
    }
}
