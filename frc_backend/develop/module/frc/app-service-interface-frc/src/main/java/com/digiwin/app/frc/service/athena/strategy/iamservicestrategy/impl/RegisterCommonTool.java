package com.digiwin.app.frc.service.athena.strategy.iamservicestrategy.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.app.common.Constant;
import com.digiwin.app.frc.service.athena.app.common.enums.IAMRoleEnum;
import com.digiwin.app.frc.service.athena.app.entity.FrcDingdingUser;
import com.digiwin.app.frc.service.athena.app.model.UserRegisterModel;
import com.digiwin.app.frc.service.athena.util.IamClient;
import com.digiwin.app.service.restful.DWRequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName RegisterCommonTool
 * @Description 用户注册-iam-request工具类
 * @Author HeX
 * @Date 2022/22/01 22：01
 * @Version 1.0
 **/
public class RegisterCommonTool {

    private RegisterCommonTool(){
    }

    public static final Logger logger = LoggerFactory.getLogger(RegisterCommonTool.class);

    public static final String IAMURL = DWApplicationConfigUtils.getProperty("iamUrl");

    private static final String SUCCESS = "success";

    private static final String ISSUCCESS = "isSuccess";

    /**
     * 注册用户
     *
     * @param userRegisterModel 用户信息
     * @return iam-response
     * @throws Exception 异常监测
     */
    public static Map<String,Object> registerUser(UserRegisterModel userRegisterModel) throws Exception {
        String invokeURL = IAMURL+DWApplicationConfigUtils.getProperty("iamUserRegisterUrl");
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("Id",userRegisterModel.getMobile());
        requestMap.put("name",userRegisterModel.getUserName());
        requestMap.put("telephone",userRegisterModel.getMobile());
        // 密码默认手机号
        requestMap.put("password",userRegisterModel.getMobile());
        String response = IamClient.requestIAM(invokeURL,requestMap, DWRequestMethod.POST);
        logger.info("1->注册用户成功，response为{}",response);
        return JSON.parseObject(response, Map.class);
    }

    /**
     * 登录用户
     *
     * @param dingdingUser 用户信息
     * @return iam-response
     * @throws Exception 异常监测
     */
    public static Map<String,Object> loginUser(FrcDingdingUser dingdingUser) throws Exception {
        String invokeURL = IAMURL+DWApplicationConfigUtils.getProperty("iamUrlLogin");
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("tenantSid",dingdingUser.getTenantsid());
        requestMap.put("tenantId",dingdingUser.getTenantid());
        requestMap.put("userId",dingdingUser.getIamId());
        // 密码默认手机号
        requestMap.put("passwordHash",dingdingUser.getPassword());
        requestMap.put("identityType","query");
        String response = IamClient.requestIAM(invokeURL,requestMap, DWRequestMethod.POST);
        logger.info("1->用户登录成功，response为{}",response);
        return JSON.parseObject(response, Map.class);
    }

    /**
     * 赋予普通用户的普通角色
     * @param iamId 用户id
     * @throws Exception 异常监测
     */
    public static void addRoleForUser(String iamId,String token) throws Exception {
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("roleId", IAMRoleEnum.GENERAL.getCode());
        String userIds [] = {iamId};
        requestMap.put("userIds",userIds);
        logger.info("赋予普通用户的普通角色，入参为{},digi-middleware-auth-user为{}",requestMap,token);
        String response = IamClient.requestIAMWithToken(IAMURL+DWApplicationConfigUtils.getProperty("associationRoleAddUsersUrl")
                ,requestMap, DWRequestMethod.POST,token,false);
        logger.info("response为{},digi-middleware-auth-user为",response);
    }

    /**
     * 管理员创建角色
     */
    public static void createRole(String token) throws Exception {
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("id",IAMRoleEnum.GENERAL.getCode());
        requestMap.put("name",IAMRoleEnum.GENERAL.getMessage());
        requestMap.put("catalogId",IAMRoleEnum.GENERAL_CATEGORY.getCode());
        logger.info("7->创建角色入参为{},digi-middleware-auth-user为{}",requestMap,token);
        IamClient.requestIAMWithToken(IAMURL+DWApplicationConfigUtils.getProperty("roleUpdateUrl")
                ,requestMap, DWRequestMethod.POST,token,true);
    }

    /**
     * 註冊租户
     * @param userRegisterModel 注册时传入的租户信息
     * @return tenantSid
     * @throws Exception 异常检测
     */
    public static Long tenantRegister(UserRegisterModel userRegisterModel,String token) throws Exception {
        String invokeURL = IAMURL+DWApplicationConfigUtils.getProperty("tenantRegisterUrl");
        Map<String,Object> tenantMessage = new HashMap<>();
        // 租户id
        tenantMessage.put("id",userRegisterModel.getTenantId());
        // 租户名称
        tenantMessage.put("name",userRegisterModel.getTenantName());
        // 租户来源
        tenantMessage.put("comeFrom",userRegisterModel.getTenantSource());
        tenantMessage.put("customerId",userRegisterModel.getCustomerId());
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("tenant",tenantMessage);
        logger.info("3->注册租户入参为{},digi-middleware-auth-user为{}",requestMap,token);
        String response = IamClient.requestIAMWithToken(invokeURL,requestMap, DWRequestMethod.POST,token,false);
        logger.info("4->注册租户成功，response为{}",response);
        HashMap<String,Object> hashMap = JSON.parseObject(response, HashMap.class);
        Boolean successFlag = (Boolean) hashMap.get(SUCCESS);
        if (!Boolean.TRUE.equals(successFlag)) {
            throw new DWRuntimeException("tenant register error ");
        }
        JSONObject dataMap = (JSONObject) hashMap.get("data");
        return (Long) dataMap.get("sid");
    }

    /**
     * 人工给租户进行应用授权
     * @param userRegisterModel
     * @param goods
     * @param newToken
     * @throws Exception
     */
    public static void authorizedTenant(UserRegisterModel userRegisterModel,JSONObject goods,String newToken) throws Exception {
        Assert.notNull(userRegisterModel.getTenantId(),"tenantId is null");
        String invokeURL = DWApplicationConfigUtils.getProperty("omcUrl")+DWApplicationConfigUtils.getProperty("perOrderUrl");
        Map<String,Object> requestMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        requestMap.put("sourceBillCode", System.currentTimeMillis()+"");
        requestMap.put("businessCode", userRegisterModel.getUserId());
        requestMap.put("tenantSid", userRegisterModel.getTenantSid());
        requestMap.put("tenantId", userRegisterModel.getTenantId());
        requestMap.put("tenantName", userRegisterModel.getTenantName());
        requestMap.put("remark", "钉钉企业应用授权");
        requestMap.put("sendEmail", false);
        requestMap.put("orderType", "0");
        JSONArray detailArr = new JSONArray();
        JSONObject detail = new JSONObject();
        JSONObject sellingStrategys = goods.getJSONArray("sellingStrategys").getJSONObject(0);
        JSONObject app = goods.getJSONObject("modules").getJSONObject("app");
        Date now = new Date();
        detail.fluentPut("goodsSid",goods.getString("id"))
                .fluentPut("goodsCode",goods.getString("code"))
                .fluentPut("goodsName",goods.getString("displayName"))
                .fluentPut("categoryId",goods.getString("catoryId"))
                .fluentPut("strategySid",sellingStrategys.getLong("id"))
                .fluentPut("strategyCode",sellingStrategys.getString("code"))
                .fluentPut("strategyName",sellingStrategys.getString("name"))
                .fluentPut("mainStrategy",true)
                .fluentPut("effectiveDateTime",sdf.format(now) + " 00:00:00")
                .fluentPut("expiredDateTime",sdf.format(addYear(now,1)) + " 23:59:59");


        JSONArray modules = new JSONArray();
        JSONArray module = goods.getJSONObject("modules").getJSONArray("modules");
        for (int i = 0; i < module.size(); i++) {
            JSONObject m = module.getJSONObject(i);
            modules.fluentAdd(new JSONObject().fluentPut("id",m.getString("id"))
                    .fluentPut("name",m.getString("name")));
        }
        detail.fluentPut("modules",modules);
        detailArr.add(detail);
        requestMap.put("details",detailArr);
        requestMap.put("orderSource","钉钉");
        String response = IamClient.requestIAMWithToken(invokeURL,requestMap, DWRequestMethod.POST,newToken,true);
        logger.info("response为{}",response);
        HashMap<String,Object> hashMap = JSON.parseObject(response, HashMap.class);
        Boolean successFlag = (Boolean) hashMap.get(SUCCESS);
        if (!Boolean.TRUE.equals(successFlag)) {
            throw new DWRuntimeException("租户的人工授权应用添加失败 ");
        }
    }

    /**
     * 某日期添加年份
     * @param date
     * @param year
     * @return
     */
    public static Date addYear(Date date,int year){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR,year);
        return calendar.getTime();
    }

    /**
     * 获取鼎捷云商品信息
     * @param appId 应用id
     * @return
     * @throws Exception
     */
    public static JSONObject getCloudgoods(String appId) throws Exception {
        String invokeURL = DWApplicationConfigUtils.getProperty("gmcUrl")
                +DWApplicationConfigUtils.getProperty("cloudgoodsUrl")+"/"+appId;
        String response = IamClient.requestIAM(invokeURL,new HashMap<>(), DWRequestMethod.GET);
        logger.info("response为{}",response);
        JSONObject result = JSON.parseObject(response, JSONObject.class);
        return result;
    }

    /**
     * 检查用户是否已加入租户
     * @param userId
     * @return
     * @throws Exception
     */
    public static void tenantCheck(String userId,String token) throws Exception {
        String invokeURL = IAMURL+DWApplicationConfigUtils.getProperty("tenantCheckUrl");
        Map<String,Object> userMessage = new HashMap<>();
        // user
        userMessage.put("user",userId);
        logger.info("5->检查用户是否已加入租户，入参{},,digi-middleware-auth-user为{}",userMessage,token);
        String response = IamClient.requestIAMWithToken(invokeURL,userMessage, DWRequestMethod.POST,token,true);
        logger.info("6->response为{}",response);
        Boolean result = JSON.parseObject(response, Boolean.class);
        Assert.isTrue(result,"response is false");
    }

    /**
     * 将当前登录用户的账号自动加入指定租户
     * @param userRegisterModel 注册信息
     * @param token 人员token
     * @throws Exception
     */
    public static void currentJoinTenant(UserRegisterModel userRegisterModel,String token) throws Exception {
        Assert.notNull(userRegisterModel.getCustomerId(),"customerId is null");
        String invokeURL = IAMURL+DWApplicationConfigUtils.getProperty("currentJoinTenantUrl");
        Map<String,Object> customerMessage = new HashMap<>();
        // user
        customerMessage.put("customerId",userRegisterModel.getCustomerId());
        logger.info("将当前登录用户的账号自动加入指定租户，入参{},,digi-middleware-auth-user为{}",customerMessage,token);
        String response = IamClient.requestIAMWithToken(invokeURL,customerMessage, DWRequestMethod.POST,token,true);
        logger.info("response为{}",response);
    }

    /**
     * 切换租户，重新刷新UserToken
     * @param tenantId 租户id
     * @param token 旧token
     * @return 新token
     * @throws Exception 异常检测
     */
    public static String tokenRefreshTenant(String tenantId,String token) throws Exception {
        String invokeURL = IAMURL+DWApplicationConfigUtils.getProperty("tokenRefreshUrl");
        Map<String,Object> refreshMap = new HashMap<>();
        refreshMap.put("tenantId",tenantId);
        logger.info("切换租户，重新刷新UserToken，入参{},digi-middleware-auth-user为{}",refreshMap,token);
        String response = IamClient.requestIAMWithToken(invokeURL,refreshMap, DWRequestMethod.POST,token,true);
        logger.info("response为{}",response);
        HashMap<String,Object> hashMap = JSON.parseObject(response, HashMap.class);
        Assert.notNull(hashMap.get("user_token"),"user_token is null");
        return (String) hashMap.get("user_token");
    }

    /**
     * 授权用户
     * @param userRegisterModel 注册信息
     * @param newToken new token
     * @throws Exception 异常检测
     */
    public static void countingUserAdd(UserRegisterModel userRegisterModel,String newToken) throws Exception {
        Assert.notNull(userRegisterModel.getTenantId(),"tenantId is null");
        String invokeURL = DWApplicationConfigUtils.getProperty("cacUrl")+DWApplicationConfigUtils.getProperty("countingUserAddUrl");
        Map<String,Object> requestMap = new HashMap<>();
        requestMap.put("countingId", Constant.APPID);
        requestMap.put("tenantId",userRegisterModel.getTenantId());
        requestMap.put("userId",userRegisterModel.getMobile());
        String response = IamClient.requestIAMWithToken(invokeURL,requestMap, DWRequestMethod.POST,newToken,true);
        logger.info("response为{}",response);
        HashMap<String,Object> hashMap = JSON.parseObject(response, HashMap.class);
        Boolean successFlag = (Boolean) hashMap.get(ISSUCCESS);
        if (!Boolean.TRUE.equals(successFlag)) {
            throw new DWRuntimeException("countingUserAdd error ");
        }
    }
}
