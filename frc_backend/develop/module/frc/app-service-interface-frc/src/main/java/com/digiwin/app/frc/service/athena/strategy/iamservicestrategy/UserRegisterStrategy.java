package com.digiwin.app.frc.service.athena.strategy.iamservicestrategy;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.app.entity.FrcDingdingUser;
import com.digiwin.app.frc.service.athena.app.model.UserRegisterModel;

/**
 * @ClassName UserRegisterStrategy
 * @Description 普通用户、管理员处理
 * @Author HeX
 * @Date 2022/06/08 22:03
 * @Version 1.0
 **/
public interface UserRegisterStrategy {
    /**
     * 注册处理
     * @param frcDingdingUser userRegisterModel
     * @return userId+token+tenantSid+mobile
     * @throws Exception 异常处理
     */
    JSONObject register(FrcDingdingUser frcDingdingUser, UserRegisterModel userRegisterModel) throws Exception;
}
