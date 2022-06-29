package com.digiwin.app.frc.service.athena.strategy.iamservicestrategy.impl;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.app.common.Constant;
import com.digiwin.app.frc.service.athena.app.mapper.FrcDingdingUserMapper;
import com.digiwin.app.frc.service.athena.strategy.iamservicestrategy.UserRegisterStrategy;
import com.digiwin.app.frc.service.athena.app.entity.FrcDingdingUser;
import com.digiwin.app.frc.service.athena.app.model.UserRegisterModel;
import com.digiwin.app.frc.service.athena.util.doucment.SecurityUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.sun.org.apache.regexp.internal.RE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class AdminRegister implements UserRegisterStrategy {

    public static final Logger logger = LoggerFactory.getLogger(AdminRegister.class);

    private static final String TELEPHONE = "telephone";

    @Autowired
    FrcDingdingUserMapper frcDingdingUserMapper =  SpringContextHolder.getBean(FrcDingdingUserMapper.class);

    @Override
    public JSONObject register(FrcDingdingUser frcDingdingUser, UserRegisterModel userRegisterModel) throws Exception {
        parameterVerification(userRegisterModel);
        // 注册管理员
        Map<String,Object> registerMap = RegisterCommonTool.registerUser(userRegisterModel);
        // 获取token
        String token = (String) registerMap.get("token");
        logger.info("2->管理员token为{}",token);
        // 创建租户(用户创建租户时会创建超管角色并绑定用户)
        Long tenantSid = RegisterCommonTool.tenantRegister(userRegisterModel,token);
        // 刷新token,绑定租户
        String newToken = RegisterCommonTool.tokenRefreshTenant(userRegisterModel.getTenantId(),token);
        userRegisterModel.setTenantSid(tenantSid);
        // 校验用户是否已经加入到租户下
        RegisterCommonTool.tenantCheck(userRegisterModel.getMobile(),newToken);
//        String token = "480936b8-69f0-4434-a592-68c66134240d";
//        Map<String,Object> registerMap = new HashMap<>();
//        registerMap.put("Id",userRegisterModel.getMobile());
//        registerMap.put("name",userRegisterModel.getUserName());
//        registerMap.put("telephone",userRegisterModel.getMobile());
//        // 密码默认手机号
//        registerMap.put("password",userRegisterModel.getMobile());
//        String newToken = RegisterCommonTool.tokenRefreshTenant(userRegisterModel.getTenantId(),token);
//        Long tenantSid = 452223197307456L;
//        userRegisterModel.setTenantSid(tenantSid);

        //获取商品信息
        JSONObject frc = RegisterCommonTool.getCloudgoods(Constant.APPID);
        //租户应用授权
        RegisterCommonTool.authorizedTenant(userRegisterModel,frc,newToken);
        // 授权用户
        RegisterCommonTool.countingUserAdd(userRegisterModel,newToken);
        // 创建普通角色
        RegisterCommonTool.createRole(newToken);
        // update
        updateUserAfterRegister(tenantSid,registerMap,userRegisterModel);
        // response
        return new JSONObject().fluentPut("userId",registerMap.get("userId"))
                .fluentPut("tenantsid",tenantSid)
                .fluentPut("token",newToken)
                .fluentPut("mobile",registerMap.get(TELEPHONE));
    }

    /**
     * 注册后更新表
     * @param tenantSid 新租户
     * @param userRegisterModel 用户传入的注册信息
     */
    private void updateUserAfterRegister(Long tenantSid,Map<String,Object> registerMap,UserRegisterModel userRegisterModel){
        FrcDingdingUser updateMessage = new FrcDingdingUser();
        updateMessage.setTenantsid(tenantSid);
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

    /**
     * 参数校验
     * @param userRegisterModel 注册信息
     */
    private void parameterVerification(UserRegisterModel userRegisterModel) {
        Assert.notNull(userRegisterModel.getTenantId(),"tenantId not null");
        Assert.notNull(userRegisterModel.getTenantName(),"tenantName not null");
        Assert.notNull(userRegisterModel.getTenantSource(),"tenantSource not null");
        Assert.notNull(userRegisterModel.getCustomerId(),"customerId not null");
    }
}
