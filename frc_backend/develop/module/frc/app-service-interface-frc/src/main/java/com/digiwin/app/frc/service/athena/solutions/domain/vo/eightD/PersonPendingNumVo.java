package com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/3/16 16:49
 * @Version 1.0
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonPendingNumVo {

    @JsonProperty(value = "user_id")
    private String userId;

    @JsonProperty(value = "user_name")
    private String userName;

    @JsonProperty(value = "pending_approve_count")
    private Integer pendingNums;

}
