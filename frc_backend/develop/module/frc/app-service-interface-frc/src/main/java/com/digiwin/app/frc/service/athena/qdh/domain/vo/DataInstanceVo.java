package com.digiwin.app.frc.service.athena.qdh.domain.vo;

import com.digiwin.app.frc.service.athena.qdh.domain.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DataInstanceVo
 * @Description TODO
 * @Author author
 * @Date 2021/11/26 0:04
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataInstanceVo extends BaseEntity {
    /**
     * 主键
     */
    private String oid;

    /**
     * 关联问题处理追踪表
     */
    private String questionTraceOid;

    private String recordOId;

    /**
     * 单据数据(json)
     */
    private String dataContent;

    /**
     * 问题编号
     */
    private String questionNo;

    /**
     * 问题描述
     */
    private String description;

    /**
     * 处理步骤
     */
    private String questionProcessStep;
}
