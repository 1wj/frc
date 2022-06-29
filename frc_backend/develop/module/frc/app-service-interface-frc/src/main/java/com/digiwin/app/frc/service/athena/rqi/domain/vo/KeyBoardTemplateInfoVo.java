package com.digiwin.app.frc.service.athena.rqi.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/1/25 11:01
 * @Version 1.0
 * @Description 看板模板信息Vo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyBoardTemplateInfoVo {


    /**
     * 模板主键
     */
    @JsonProperty(value = "kanban_template_id")
    private String oid;


    /**
     * 模板名称
     */
    @JsonProperty(value = "kanban_template_name")
    private String modelName;

}
