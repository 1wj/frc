package com.digiwin.app.frc.service.athena.ppc.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/2/15 17:30
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskLevelVo {

    @JsonProperty(value = "risk_level_id")
    private String riskLevelId;

    @JsonProperty(value = "risk_level_name")
    private String riskLevelName;

}
