package com.digiwin.app.frc.service.athena.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 钉钉用户类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FrcDingdingUser {
    /**
     * 管理员类型
     */
    public static final Integer TYPE_ADMIN = 1;
    /**
     * 普通用户类型
     */
    public static final Integer TYPE_USER = 0;
    /**
     * 钉钉用户id
     */
    private String id;

    /**
     * 钉钉企业id
     */
    private String cropId;

    /**
     * 钉钉企业名称
     */
    private String cropName;

    /**
     * 鼎捷云用户id
     */
    private String iamId;

    /**
     * 钉钉用户昵称
     */
    private String nick;

    /**
     * 头像URL
     */
    private String avatarurl;

    /**
     * 用户的手机号
     */
    private String mobile;

    /**
     * 用户的openId
     */
    private String openid;

    /**
     * 用户的unionId
     */
    private String unionid;

    /**
     * 用户的个人邮箱
     */
    private String email;

    /**
     * 手机号对应的国家号
     */
    private String statecode;

    /**
     * 1代表管理员,0代表普通用户
     */
    private Integer type;

    /**
     * 租户sid
     */
    private Long tenantsid;

    /**
     * 租户id
     */
    private String tenantid;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 鼎捷云登录密码
     */
    private String password;

}