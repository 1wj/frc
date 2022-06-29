package com.digiwin.app.frc.service.athena.app.service;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.frc.service.athena.app.model.UserRegisterModel;
import com.digiwin.app.service.AllowAnonymous;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: jiangzhou
 * @Date: 2022/06/08
 * @Version 1.0
 * @Description 为App提供用户注册登录接口
 */
@DWRestfulService
public interface IDingRegisterServer extends DWService {
    /**
     * 钉钉注册测试
     * @param userRegisterModel 注册入参
     * @return
     * @throws Exception
     */
    @DWRequestMapping(path = "/app/dingding/register", method = {DWRequestMethod.POST})
    public DWServiceResult register(JSONObject userRegisterModel) throws Exception;
}

