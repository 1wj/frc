package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2021/11/22 9:58
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyBoardTemplateVo {

    /**
     * 主键
     */
    @JsonProperty(value = "kanban_template_id")
    private String oid;

    /**
     * 模板编号
     */
    @JsonProperty(value = "template_no")
    private String modelNo;

    /**
     * 模板名称
     */
    @JsonProperty(value = "template_name")
    private String modelName;

    /**
     * 是否生效  Y:是  N:否
     */
    @JsonProperty(value = "manage_status")
    private String manageStatus;

    /**
     * 是否默认  0:是 1:否
     */
    @JsonProperty(value = "is_default")
    private Integer defaultChoice;

    /**
     * 备注
     */
    @JsonProperty(value = "remarks")
    private String remarks;


}
