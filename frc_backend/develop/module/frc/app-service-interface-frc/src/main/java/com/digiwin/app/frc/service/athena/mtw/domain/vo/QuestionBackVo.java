package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author: xieps
 * @Date: 2021/11/15 17:16
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionBackVo {

    /**
     * 主键
     */
    @JsonProperty(value = "return_id")
    private String oid;


    /**
     * 退回编号
     */
    @JsonProperty(value = "return_no")
    private String backId;

    /**
     * 退回原因
     */
    @JsonProperty(value = "return_reason")
    private String backReason;

    /**
     * 节点编号
     */
    @JsonProperty(value = "node_no")
    private String nodeId;

    /**
     * 节点名称
     */
    @JsonProperty(value = "node_name")
    private String nodeName;

    /**
     * 备注
     */
    @JsonProperty(value = "remarks")
    private String remarks;

}
