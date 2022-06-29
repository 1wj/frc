package com.digiwin.app.frc.service.athena.solutions.domain.model.universal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true )
public class TemporaryMeasureModel implements Serializable {
    private static final long serialVersionUID = -2343050038701057338L;

    //措施内容
    @JsonProperty(value = "measure_content")
    private String measureContent;

    //负责人id
    @JsonProperty(value = "liable_person_id")
    private String liablePersonId;

    //负责人名称
    @JsonProperty(value = "liable_person_name")
    private String liablePersonName;

    //处理部门id
    @JsonProperty(value = "process_department_id")
    private String processDepartmentId;

    //处理部门名称
    @JsonProperty(value = "process_department_name")
    private String processDepartmentName;

    //预计完成时间
    @JsonProperty(value = "expect_solve_date")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date expectSolveDate;

    //附件必传标识
    @JsonProperty(value = "attachment_upload_flag")
    private String attachmentUploadFlag;

    //人力瓶颈分析
    @JsonProperty(value = "human_bottleneck_analysis")
    private String humanBottleneckAnalysis;
}
