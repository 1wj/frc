package com.digiwin.app.frc.service.athena.strategy.iamservicestrategy.impl;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.app.mapper.FrcDingdingUserMapper;
import com.digiwin.app.frc.service.athena.strategy.iamservicestrategy.UserRegisterStrategy;
import com.digiwin.app.frc.service.athena.app.entity.FrcDingdingUser;
import com.digiwin.app.frc.service.athena.app.model.UserRegisterModel;
import com.digiwin.app.frc.service.athena.util.doucment.SecurityUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class GeneralUsersRegister implements UserRegisterStrategy {


    private static final String TELEPHONE = "telephone";

    @Autowired
    FrcDingdingUserMapper frcDingdingUserMapper =  SpringContextHolder.getBean(FrcDingdingUserMapper.class);

    @Override
    public JSONObject register(FrcDingdingUser frcDingdingUser, UserRegisterModel userRegisterModel) throws Exception {
        // mobile为null,暂无权限
        Assert.notNull(userRegisterModel.getMobile(),"mobile is null , No permission yet");
        // 管理员尚未注册，暂无权限
        Assert.notNull(userRegisterModel.getCorpId(),"corpId is null,can not select admin by corpId");
        FrcDingdingUser adminMessage = frcDingdingUserMapper.selectAdminMessage(userRegisterModel.getCorpId());
        Assert.notNull(adminMessage,"admin message is null , No permission yet");
        Assert.notNull(adminMessage.getMobile(),"admin phone is null , No permission yet");
        // 注册
        Map<String,Object> registerMap =RegisterCommonTool.registerUser(userRegisterModel);
        String token = (String) registerMap.get("token");
        registerMap.put("tenantsid",adminMessage.getTenantsid());
        // 绑定租户
        RegisterCommonTool.currentJoinTenant(userRegisterModel,token);
        // 刷新token,绑定租户
        String newToken = RegisterCommonTool.tokenRefreshTenant(userRegisterModel.getTenantId(),token);
        // 校验用户是否已经加入到租户下
        RegisterCommonTool.tenantCheck(userRegisterModel.getMobile(),newToken);
        // 授权用户
        RegisterCommonTool.countingUserAdd(userRegisterModel,newToken);
        // 赋予普通用户角色
        RegisterCommonTool.addRoleForUser(userRegisterModel.getMobile(),newToken);
        // update - frc_DingDing_user
        updateUserAfterRegister(registerMap,userRegisterModel);
        // response
        return new JSONObject()
                .fluentPut("userId",registerMap.get("userId"))
                .fluentPut("tenantsid",registerMap.get("tenantsid"))
                .fluentPut("token",token)
                .fluentPut("mobile",registerMap.get(TELEPHONE));
    }

    /**
     * 注册后更新表
     * @param registerMap iam-response
     * @param userRegisterModel 用户传入的注册信息
     */
    private void updateUserAfterRegister(Map<String,Object> registerMap,UserRegisterModel userRegisterModel){
        FrcDingdingUser updateMessage = new FrcDingdingUser();

        updateMessage.setTenantsid((Long) registerMap.get("tenantsid"));
        updateMessage.setMobile((String) registerMap.get(TELEPHONE));
        updateMessage.setPassword(SecurityUtil.getSha256((String) registerMap.get(TELEPHONE)));
        updateMessage.setIamId((String) registerMap.get("userId"));
        // 根据钉钉id&crop_id更新
        updateMessage.setId(userRegisterModel.getUserId());
        updateMessage.setCropId(userRegisterModel.getCorpId());
        int result = frcDingdingUserMapper.updateByPrimaryKeySelective(updateMessage);
        if (result < 0) {
            throw new DWRuntimeException("update error after register user");
        }
    }
}
