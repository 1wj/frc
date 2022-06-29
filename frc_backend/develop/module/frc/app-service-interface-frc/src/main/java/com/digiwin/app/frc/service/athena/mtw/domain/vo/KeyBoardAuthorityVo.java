package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2021/11/24 10:02
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyBoardAuthorityVo {

    /**
     * 主键
     */
    @JsonProperty(value = "kanban_permissions_id")
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




}
