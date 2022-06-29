package com.digiwin.app.frc.service.athena.solutions.domain.model.eightD;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *功能描述:根本原因分析
 * @author cds
 * @date 2022/3/9
 * @param
 * @return
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true )
public class KeyReasonAnalysisModel implements Serializable {

    private static final long serialVersionUID = 298166598915995743L;

    //流出原因
    @JsonProperty(value = "outflow_reason")
    private String outflowReason;

    //产出原因
    @JsonProperty(value = "output_reason")
    private String outputReason;

    //系统原因
    @JsonProperty(value = "system_reason")
    private String systemReason;

}
