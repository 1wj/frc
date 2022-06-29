package com.digiwin.app.frc.service.athena.app.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dingtalkcontact_1_0.models.GetUserResponseBody;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponseBody;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.frc.service.athena.app.biz.IAMServiceBiz;
import com.digiwin.app.frc.service.athena.app.biz.IDingLoginServerBiz;
import com.digiwin.app.frc.service.athena.app.cache.TokenCache;
import com.digiwin.app.frc.service.athena.app.common.CacheCommonUtils;
import com.digiwin.app.frc.service.athena.app.common.DingDingHttpUtils;
import com.digiwin.app.frc.service.athena.app.entity.FrcDingdingUser;
import com.digiwin.app.frc.service.athena.app.job.CreateNoticeHandler;
import com.digiwin.app.frc.service.athena.app.mapper.FrcDingdingUserMapper;
import com.digiwin.app.frc.service.athena.app.model.UserRegisterModel;
import com.digiwin.app.frc.service.athena.strategy.iamservicestrategy.impl.RegisterCommonTool;
import com.digiwin.app.service.DWServiceResult;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Slf4j
@Service
public class DingLoginServerBizImpl implements IDingLoginServerBiz {

    @Autowired
    private CacheCommonUtils cacheCommonUtils;

    @Autowired
    private FrcDingdingUserMapper dingdingUserMapper;

    @Autowired
    private IAMServiceBiz iamServiceBiz;
    @Override
    public JSONObject getDingUser(String messageBody) throws DWException {

        String code = JSON.parseObject(messageBody).getString("code");
        String language = JSON.parseObject(messageBody).getString("language");
        String corpId = JSON.parseObject(messageBody).getString("corpId");
        try {
            String accessToken = cacheCommonUtils.getAccessToken(corpId);
            //获取用户id
            String userId = DingDingHttpUtils.getUserId(code, accessToken).getResult().getUserid();

            FrcDingdingUser dingUser = dingdingUserMapper.selectByPrimaryKey(userId, corpId);
            boolean flag = false;
            if(dingUser == null){
                return new JSONObject().fluentPut("isGrant",flag).fluentPut("user",null);
            }
            JSONObject user = new JSONObject();
            if(StringUtils.isEmpty(dingUser.getMobile())){
                //通知前台进行授权
                flag = true;
            }else{
                // 进行登录
                Map<String, Object> stringObjectMap = RegisterCommonTool.loginUser(dingUser);
                //获取用户详情
                OapiV2UserGetResponse result = DingDingHttpUtils.getUserInfo(accessToken, userId, language);
                //更新用户头像
                FrcDingdingUser updateEntity = FrcDingdingUser.builder().id(dingUser.getId()).cropId(dingUser.getCropId())
                        .avatarurl(result.getResult().getAvatar()).build();
                dingdingUserMapper.updateByPrimaryKeySelective(updateEntity);
                stringObjectMap.put("avatarUrl",updateEntity.getAvatarurl());
                return new JSONObject().fluentPut("isGrant",flag).fluentPut("user",stringObjectMap);
            }
            return new JSONObject().fluentPut("isGrant",flag).fluentPut("user",null);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new DWException("获取用户信息出错："+e.getMessage());
        }
    }

    @Override
    public JSONObject loginUser(String messageBody) throws DWException {
        String code = JSON.parseObject(messageBody).getString("auth_code");
        String corpId = JSON.parseObject(messageBody).getString("corpId");
        GetUserTokenResponseBody userToken = DingDingHttpUtils.getUserToken(code, null);
        GetUserResponseBody user = DingDingHttpUtils.getUsersByUnionId("me", userToken.getAccessToken());
        String userId = DingDingHttpUtils.getUserIdByUnionId(user.getUnionId(), cacheCommonUtils.getAccessToken(corpId));
        String tenantid = corpId.substring(4);
        FrcDingdingUser dingUsert = FrcDingdingUser.builder().id(userId).cropId(corpId).avatarurl(user.getAvatarUrl()).email(user.getEmail())
                .nick(user.getNick()).openid(user.getOpenId()).statecode(user.getStateCode()).unionid(user.getUnionId()).tenantid(tenantid).build();
        dingdingUserMapper.updateByPrimaryKeySelective(dingUsert);
        //租户id不能超出16位，钉钉的corpid是由ding+唯一id生成的
        UserRegisterModel frc = UserRegisterModel.builder().userId(userId).userName(user.getNick()).corpId(corpId)
                .tenantId(tenantid).mobile(user.getMobile()).tenantSource("FRC").customerId(tenantid).build();
        try {
            JSONObject userJson = iamServiceBiz.userRegister(frc);
            userJson.fluentPut("avatarUrl",dingUsert.getAvatarurl());
            return userJson;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("IAM用户注册错误");
            return new JSONObject().fluentPut("login",false).fluentPut("message","IAM用户注册失败:"+e.getMessage());
        }

    }


}
