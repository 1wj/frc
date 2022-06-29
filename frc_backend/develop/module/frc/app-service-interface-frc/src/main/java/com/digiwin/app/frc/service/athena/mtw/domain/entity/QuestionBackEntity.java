package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/15 17:02
 * @Version 1.0
 * @Description 问题退回维护实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionBackEntity {

    /**
     * 主键
     */
    private String oid;

    /**
     * 租户id
     */
    private Long tenantSid;

    /**
     * 退回编号
     */
    private String backId;

    /**
     * 退回原因
     */
    private String backReason;

    /**
     * 节点编号
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

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
