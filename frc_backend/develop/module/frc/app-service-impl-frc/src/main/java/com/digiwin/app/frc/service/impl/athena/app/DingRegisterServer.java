package com.digiwin.app.frc.service.impl.athena.app;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.app.biz.IAMServiceBiz;
import com.digiwin.app.frc.service.athena.app.model.UserRegisterModel;
import com.digiwin.app.frc.service.athena.app.service.IDingRegisterServer;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;

public class DingRegisterServer implements IDingRegisterServer {

    @Autowired
    private IAMServiceBiz iamServiceBiz;

    @Override
    public DWServiceResult register(JSONObject userRegisterModel) throws Exception {
        UserRegisterModel model = new UserRegisterModel();
        model.setUserId(userRegisterModel.getString("userId"));
        model.setCorpId(userRegisterModel.getString("corpId"));
        model.setUserName(userRegisterModel.getString("userName"));
        model.setMobile(userRegisterModel.getString("mobile"));
        model.setTenantSid(userRegisterModel.getLong("tenantSid"));
        model.setTenantId(userRegisterModel.getString("tenantId"));
        model.setTenantName(userRegisterModel.getString("tenantName"));
        model.setTenantSource(userRegisterModel.getString("tenantSource"));
        model.setCustomerId(userRegisterModel.getString("customerId"));
        return new DWServiceResult(true, iamServiceBiz.userRegister(model));
    }
}

