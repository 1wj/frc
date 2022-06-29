package com.digiwin.app.frc.service.athena.app.biz;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.app.model.UserRegisterModel;

/**
 * @ClassName IAMServiceBiz
 * @Description 移动端-用户与IAM交互服务
 * @Author heX
 * @Date 2022/06/08 21:28
 * @Version 1.0
 **/
public interface IAMServiceBiz {

    /**
     * 用户注册
     * @param userRegisterModel 用户注册model
     * @return userId、token、tenantSid、phone
     */
    JSONObject userRegister(UserRegisterModel userRegisterModel) throws Exception;
}
