package com.digiwin.app.frc.service.athena.qdh.domain.model.init;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DetailModel
 * @Description 问题发起-问题详情结构
 * @Author author
 * @Date 2022/2/10 22:50
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailModel {
    /**
     * 问题代号
     */
    @JsonProperty(value = "project_no")
    private String projectNo;

    /**
     * 来源单号
     */
    @JsonProperty(value = "source_no")
    private String sourceNo;

    /**
     * 产品系列
     */
    @JsonProperty(value = "product_no")
    private String productNo;

    /**
     * product_name 产品系列名称
     */
    @JsonProperty(value = "product_name")
    private String productName;

    /**
     * 料号
     */
    @JsonProperty(value = "item_no")
    private String itemNo;

    @JsonProperty(value = "item_name")
    private String itemName;

    /**
     * 工序Id
     */
    @JsonProperty(value = "process_id")
    private String processId;

    /**
     * process_name 工序name
     */
    @JsonProperty(value = "process_name")
    private String processName;

    /**
     * 生产线id
     */
    @JsonProperty(value = "workstation_id")
    private String workstationId;

    /**
     * 生产线name
     */
    @JsonProperty(value = "workstation_name")
    private String workstationName;

    /**
     * 本批数量
     */
    @JsonProperty(value = "batch_qty")
    private int batchQty;

    /**
     * 问题发现数量
     */
    @JsonProperty(value = "discover_question_qty")
    private String discoverQuestionQty;

    @JsonProperty(value = "defect_no")
    private String defectNo;

    /**
     * 缺陷图片
     */
    @JsonProperty(value = "defect_name")
    private String defectName;

    /**
     * 缺陷图片dmcId
     */
    @JsonProperty(value = "defect_picture_id")
    private String defectPictureId;


    /**
     * 冲刺六 追加SN_no 号
     */
    @JsonProperty(value = "sn")
    private String sn;
}
