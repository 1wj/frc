package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author: xieps
 * @Date: 2021/11/22 10:13
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyBoardFieldVo {

    /**
     * 主键
     */
    @JsonProperty(value = "kanban_field_id")
    private String oid;


    /**
     * 栏位id
     */
    @JsonProperty(value = "field_no")
    private String fieldId;

    /**
     * 栏位名称
     */
    @JsonProperty(value = "field_name")
    private String fieldName;

    /**
     * 是否生效  Y:是  N:否
     */
    @JsonProperty(value = "manage_status")
    private String manageStatus;

    /**
     * 备注
     */
    @JsonProperty(value = "remarks")
    private String remarks;
}
