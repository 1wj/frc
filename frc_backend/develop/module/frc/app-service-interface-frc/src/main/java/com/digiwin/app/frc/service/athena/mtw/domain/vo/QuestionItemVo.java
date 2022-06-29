package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: xieps
 * @Date: 2021/11/15 11:23
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionItemVo {


    /**
     * 主键
     */
    @JsonProperty(value = "item_id")
    private  String oid;

    /**
     * 物料编号
     */
    @JsonProperty(value = "item_no")
    private String itemNo;

    /**
     * 物料名称
     */
    @JsonProperty(value = "item_name")
    private String itemName;



}
