package com.digiwin.app.frc.service.athena.mtw.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/15 13:33
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSourceModel {

    /**
     * 主键
     */
    @JSONField(name = "source_id")
    private String oid;



    /**
     * 来源编号
     */
    @Length(max = 32,message = "source_no_overLength")
    @JSONField(name = "source_no")
    private String sourceNo;

    /**
     * 来源名称
     */
    @JSONField(name = "source_name")
    private String sourceName;


    /**
     * 来源分类
     */
    @JSONField(name = "source_classification_no")
    private String sourceCategory;

    /**
     * 是否生效
     */
    @JSONField(name = "manage_status")
    private String manageStatus;

    /**
     * 备注
     */
    @JSONField(name = "remarks")
    private String remarks;


}
