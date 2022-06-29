package com.digiwin.app.frc.service.athena.app.biz;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.service.DWServiceResult;

public interface IDingLoginServerBiz {
    /**
     * 查看用户是否注册+获取用户信息
     * @param messageBody
     * @return
     * @throws DWException
     */
    public JSONObject getDingUser(String messageBody) throws DWException;

    /**
     * 钉钉用户授权登录
     * @param messageBody
     * @return
     * @throws DWException
     */
    public JSONObject loginUser(String messageBody) throws DWException;
}
