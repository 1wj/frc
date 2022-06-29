package com.digiwin.app.frc.service.athena.app.service;

import com.digiwin.app.container.exceptions.DWException;
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
public interface IDingLoginServer extends DWService {
    /**
     * 查看用户是否注册+获取用户信息
     * @param messageBody
     * @return
     * @throws DWException
     */
    @DWRequestMapping(path = "/app/dingding/getUser", method = {DWRequestMethod.POST})
    @AllowAnonymous
    public DWServiceResult getDingUser(String messageBody) throws DWException;

    /**
     * 钉钉用户授权登录
     * @param messageBody
     * @return
     * @throws DWException
     */
    @DWRequestMapping(path = "/app/dingding/login", method = {DWRequestMethod.POST})
    @AllowAnonymous
    public DWServiceResult loginUser(String messageBody) throws DWException;
}
