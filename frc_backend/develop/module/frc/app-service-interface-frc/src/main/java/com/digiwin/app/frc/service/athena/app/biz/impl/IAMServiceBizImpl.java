package com.digiwin.app.frc.service.athena.app.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.app.biz.IAMServiceBiz;
import com.digiwin.app.frc.service.athena.app.mapper.FrcDingdingUserMapper;
import com.digiwin.app.frc.service.athena.strategy.iamservicestrategy.UserRegisterFactory;
import com.digiwin.app.frc.service.athena.strategy.iamservicestrategy.UserRegisterStrategy;
import com.digiwin.app.frc.service.athena.app.entity.FrcDingdingUser;
import com.digiwin.app.frc.service.athena.app.model.UserRegisterModel;
import com.digiwin.app.frc.service.athena.config.annotation.ValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Service
public class IAMServiceBizImpl implements IAMServiceBiz {

    @Autowired
    FrcDingdingUserMapper frcDingdingUserMapper;

    @Override
    public JSONObject userRegister(UserRegisterModel userRegisterModel) throws Exception {
        // 必要参数校验
        ValidationHandler.doValidator(userRegisterModel);
        // userId+corpId校验
        FrcDingdingUser frcDingdingUser = frcDingdingUserMapper
                .selectByIdAndCropId(userRegisterModel.getUserId(), userRegisterModel.getCorpId());
        Assert.notNull(frcDingdingUser, "frc_dingDing_user data is null,no permission");
        Assert.notNull(frcDingdingUser.getType(), "user role is null");
        if (!StringUtils.isEmpty(frcDingdingUser.getMobile())) {
            throw new DWRuntimeException("already register");
        }
        // 注册
        userRegisterModel.setTenantName(frcDingdingUser.getCropName());
        UserRegisterStrategy registerStrategy = UserRegisterFactory.getStrategy(frcDingdingUser.getType());
        return  registerStrategy.register(frcDingdingUser,userRegisterModel);
    }
}
