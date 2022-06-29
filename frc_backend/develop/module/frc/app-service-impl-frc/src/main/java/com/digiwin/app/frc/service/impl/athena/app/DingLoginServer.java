package com.digiwin.app.frc.service.impl.athena.app;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.frc.service.athena.app.biz.IDingLoginServerBiz;
import com.digiwin.app.frc.service.athena.app.service.IDingLoginServer;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;

public class DingLoginServer implements IDingLoginServer {

    @Autowired
    private IDingLoginServerBiz iDingLoginServerBiz;
    @Override
    public DWServiceResult getDingUser(String messageBody) throws DWException {
        JSONObject dingUser = iDingLoginServerBiz.getDingUser(messageBody);
        return new DWServiceResult(true,dingUser);
    }

    @Override
    public DWServiceResult loginUser(String messageBody) throws DWException {
        JSONObject user = iDingLoginServerBiz.loginUser(messageBody);
        return new DWServiceResult(true,user);
    }
}
