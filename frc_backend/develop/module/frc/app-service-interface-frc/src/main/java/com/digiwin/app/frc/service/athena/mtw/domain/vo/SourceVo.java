package com.digiwin.app.frc.service.athena.mtw.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/2/18 18:20
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceVo {

    @JsonProperty(value = "question_source_id")
    private String oid;

    @JsonProperty(value = "question_source_name")
    private String sourceName;

    @JsonProperty(value = "question_source_no")
    private String sourceNo;
}
