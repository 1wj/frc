package com.digiwin.app.frc.service.athena.mtw.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2022/2/18 15:27
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassificationSourceMidEntity {

    private String oid;


    private Long tenantSid;

    private String classificationOid;

    private String sourceOid;


    private Date createTime;


    private String createName;

    private Date updateTime;

    private String updateName;



}
