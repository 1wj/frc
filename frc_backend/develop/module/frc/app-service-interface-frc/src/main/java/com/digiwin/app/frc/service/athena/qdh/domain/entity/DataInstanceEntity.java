package com.digiwin.app.frc.service.athena.qdh.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName DataInstanceEntity
 * @Description 问题实例实体类
 * @Author author
 * @Date 2021/11/11 1:47
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataInstanceEntity extends BaseEntity {


    /**
     * 主键
     */
    private String oid;

    /**
     * 关联问题处理追踪表
     */
    private String questionTraceOid;

    /**
     * 单据数据(json)
     */
    private String dataContent;
}
