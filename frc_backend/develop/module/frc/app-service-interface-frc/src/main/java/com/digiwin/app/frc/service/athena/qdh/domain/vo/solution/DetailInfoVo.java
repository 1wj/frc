package com.digiwin.app.frc.service.athena.qdh.domain.vo.solution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName DetailInfoVo
 * @Description TODO
 * @Author author
 * @Date 2021/11/12 0:52
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailInfoVo {
    @JsonProperty(value = "discover_department_id")
    private String discoverDepartmentId;

    @JsonProperty(value = "discover_department_name")
    private String discoverDepartmentName;

    @JsonProperty(value = "source_no")
    private String sourceNo;

    @JsonProperty(value = "item_no")
    private String itemNo;

    @JsonProperty(value = "item_name")
    private String itemName;

    @JsonProperty(value = "product_no")
    private String productNo;

    @JsonProperty(value = "product_name")
    private String productName;

    @JsonProperty(value = "process_id")
    private String processId;

    @JsonProperty(value = "process_name")
    private String processName;

    @JsonProperty(value = "workstation_id")
    private String workstationId;

    @JsonProperty(value = "workstation_name")
    private String workstationName;

    @JsonProperty(value = "batch_qty")
    private int batchQty;

    @JsonProperty(value = "discover_question_qty")
    private int discoverQuestionQty;

    @JsonProperty(value = "defect_name")
    private String defectName;

    @JsonProperty(value = "defect_no")
    private String defectNo;

    @JsonProperty(value = "defect_picture_id")
    private String defectPictureId;
}
