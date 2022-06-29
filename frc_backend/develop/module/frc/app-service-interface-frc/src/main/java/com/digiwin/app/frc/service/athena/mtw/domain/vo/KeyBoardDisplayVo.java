package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author: xieps
 * @Date: 2021/11/23 10:24
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyBoardDisplayVo {

    /**
     * 主键
     */
    @JsonProperty(value = "kanban_display_id")
    private String oid;

    /**
     * 看板模板主键
     */
    @JsonProperty(value = "kanban_template_id")
    private String modelOid;

    /**
     * 看板模板名称
     */
    @JsonProperty(value = "kanban_template_name")
    private String modelName;

    /**
     *解决方案编号
     */
    @JsonProperty(value = "solution_id")
    private String solutionNo;

    /**
     * 解决方案名称
     */
    @JsonProperty(value = "solution_name")
    private String solutionName;

    /**
     * 模板栏位id
     */
    @JsonProperty(value = "field_no")
    private String fieldId;

    /**
     * 模板栏位名称
     */
    @JsonProperty(value = "field_name")
    private String fieldName;

    /**
     * 方案步骤编号
     */
    @JsonProperty(value = "step_id")
    private String measureNo;

    /**
     * 方案步骤名称
     */
    @JsonProperty(value = "step_name")
    private String measureName;


}
