package com.digiwin.app.frc.service.athena.app.model;

import com.digiwin.app.frc.service.athena.config.annotation.CheckNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterModel {
    /**
     * 用户ID(钉钉用户ID)
     */
    @CheckNull(message = "userId is null")
    private String userId;

    /**
     * 用户名称
     */
    @CheckNull(message = "userName is null")
    private String userName;

    /**
     * 钉钉企业id
     */
    @CheckNull(message = "corpId is null")
    private String corpId;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 租户sid
     */
    private Long tenantSid;

    /**
     * 租户id
     */
//    @CheckNull(message = "tenantId is null")
    private String tenantId;

    /**
     * 租户name
     */
//    @CheckNull(message = "tenantName is null")
    private String tenantName;

    /**
     * 租户来源
     */
//    @CheckNull(message = "tenantSource is null")
    private String tenantSource;

    /**
     * 客户代号
     */
    private String customerId;
}
