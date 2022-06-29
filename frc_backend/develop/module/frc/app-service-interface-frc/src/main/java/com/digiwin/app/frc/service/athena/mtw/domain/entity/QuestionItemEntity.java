package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/11 11:08
 * @Version 1.0
 * @Description 物料实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionItemEntity {

    /**
     * 主键
     */
    private  String oid;

    /**
     * 租户id
     */
    private Long tenantSid;

    /**
     * 物料编号
     */
    private String itemNo;

    /**
     * 物料名称
     */
    private String itemName;

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
