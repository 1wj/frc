package com.digiwin.app.frc.service.athena.app.common;

import com.aliyun.dingtalkcontact_1_0.models.*;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponse;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.frc.service.athena.app.cache.TokenCache;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.taobao.api.ApiException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DingDingHttpUtils {
    /**
     *获取用户id信息
     * @param code 免登code码
     * @param accessToken
     * @return
     */
    public static OapiV2UserGetuserinfoResponse getUserId(String code,String accessToken) throws DWException{
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/getuserinfo");
        OapiV2UserGetuserinfoRequest req = new OapiV2UserGetuserinfoRequest();
        req.setCode(code);
        OapiV2UserGetuserinfoResponse rsp = null;
        try {
            rsp = client.execute(req, accessToken);
            if(!rsp.isSuccess()) {
                throw new DWException("调用钉钉用户userId接口失败："+rsp.getMessage());
            }
        } catch (ApiException e) {
            e.printStackTrace();
            throw new DWException("调用钉钉用户userId接口失败："+e.getMessage());
        }
        return rsp;
    }

    /**
     * 获取用户详情信息
     * @param accessToken
     * @param userId
     * @param language
     * @return
     * @throws DWException
     */
    public static OapiV2UserGetResponse getUserInfo(String accessToken, String userId, String language) throws DWException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/user/get");
        OapiV2UserGetRequest req = new OapiV2UserGetRequest();
        req.setLanguage(language);
        req.setUserid(userId);
        OapiV2UserGetResponse rsp = null;
        try {
             rsp = client.execute(req, accessToken);
            if(!rsp.isSuccess()) {
                throw new DWException("调用钉钉用户详细信息接口失败："+rsp.getMessage());
            }
        } catch (ApiException e) {
            e.printStackTrace();
            throw new DWException("调用钉钉用户详细信息接口失败："+e.getMessage());
        }
        return rsp;
    }

    /**
     * 使用 Token 初始化账号Client
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dingtalkcontact_1_0.Client createClient() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalkcontact_1_0.Client(config);
    }

    /**
     * 使用 Token 初始化账号Client
     * @return Client
     * @throws Exception
     */
    public static com.aliyun.dingtalkoauth2_1_0.Client createClient2() throws Exception {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return new com.aliyun.dingtalkoauth2_1_0.Client(config);
    }

    /**
     * 根据unionId获取用户信息
     * @param unionId
     * @param userAccessToken
     */
    public static GetUserResponseBody getUsersByUnionId(String unionId,String userAccessToken) throws DWException {
        com.aliyun.dingtalkcontact_1_0.Client client = null;
        try {
            client = createClient();
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWException("获取用户信息初始化账号Client错误");
        }
        GetUserHeaders getUserHeaders = new GetUserHeaders();
        getUserHeaders.xAcsDingtalkAccessToken = userAccessToken;
        try {
            GetUserResponse userWithOptions = client.getUserWithOptions(unionId, getUserHeaders, new RuntimeOptions());
            return userWithOptions.getBody();
        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }
            throw new DWException("获取用户信息错误："+err.message);
        }
    }

    /**
     * 获取用户token
     * @param authCode
     * @param refreshToken
     * @throws DWException
     */
    public static GetUserTokenResponseBody getUserToken(String authCode, String refreshToken) throws DWException {
        com.aliyun.dingtalkoauth2_1_0.Client client = null;
        try {
            client = createClient2();
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWException("获取用户token初始化账号Client错误");
        }
        String suiteKey = DWApplicationConfigUtils.getProperty("suiteKey");
        String suiteSecret = DWApplicationConfigUtils.getProperty("suiteSecret");
        GetUserTokenRequest getUserTokenRequest = new GetUserTokenRequest()
                .setClientId(suiteKey)
                .setClientSecret(suiteSecret)
                .setCode(authCode)
                .setRefreshToken(refreshToken)
                .setGrantType(StringUtils.isEmpty(refreshToken) ? "authorization_code" : "refresh_token");
        try {
            GetUserTokenResponse userToken = client.getUserToken(getUserTokenRequest);
            return userToken.getBody();
        }  catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }
            throw new DWException("获取用户token错误："+err.message);
        }
    }

    /**
     * 根据unionId获取用户id
     * @param unionId
     * @param accessToken 企业应用token
     * @return
     * @throws DWException
     */
    public static String getUserIdByUnionId(String unionId,String accessToken) throws DWException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/user/getbyunionid");
        OapiUserGetbyunionidRequest req = new OapiUserGetbyunionidRequest();
        req.setUnionid(unionId);
        OapiUserGetbyunionidResponse rsp = null;
        try {
            rsp = client.execute(req, accessToken);
            return rsp.getResult().getUserid();
        } catch (ApiException e) {
            e.printStackTrace();
            throw new DWException("根据unionId获取UserId出错");
        }
    }

    /**
     * 获取企业信息
     * @param corpId
     * @param accessToken
     * @return
     * @throws Exception
     */
    public static GetOrgAuthInfoResponseBody getCorpInfo(String corpId,String accessToken) throws Exception {
        com.aliyun.dingtalkcontact_1_0.Client client = createClient();
        GetOrgAuthInfoHeaders getOrgAuthInfoHeaders = new GetOrgAuthInfoHeaders();
        getOrgAuthInfoHeaders.xAcsDingtalkAccessToken = accessToken;
        GetOrgAuthInfoRequest getOrgAuthInfoRequest = new GetOrgAuthInfoRequest()
                .setTargetCorpId(corpId);
        try {
            GetOrgAuthInfoResponse orgAuthInfoWithOptions = client.getOrgAuthInfoWithOptions(getOrgAuthInfoRequest, getOrgAuthInfoHeaders, new RuntimeOptions());
            return orgAuthInfoWithOptions.getBody();
        } catch (TeaException err) {
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }
            throw new DWException("获取企业信息错误："+err.getMessage());

        } catch (Exception _err) {
            TeaException err = new TeaException(_err.getMessage(), _err);
            if (!com.aliyun.teautil.Common.empty(err.code) && !com.aliyun.teautil.Common.empty(err.message)) {
                // err 中含有 code 和 message 属性，可帮助开发定位问题
            }
            throw new DWException("获取企业信息错误："+err.getMessage());
        }
    }

    /**
     * 根据部门id获取用户id集合
     * @param deptId
     * @param accessToken
     * @return
     * @throws DWException
     */
    public static List<String> getUserIdByDept(Long deptId, String accessToken) throws DWException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/user/listid");
        OapiUserListidRequest req = new OapiUserListidRequest();
        req.setDeptId(deptId);
        OapiUserListidResponse rsp = null;
        try {
            rsp = client.execute(req, accessToken);
            return rsp.getResult().getUseridList();
        } catch (ApiException e) {
            e.printStackTrace();
            throw new DWException("获取部门下用户id集合错误："+e.getMessage());
        }
    }

    /**
     * 获取部门下子部门id集合
     * @param deptId
     * @param accessToken
     * @return
     * @throws DWException
     */
    public static List<Long> getDeptList(Long deptId, String accessToken) throws DWException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/v2/department/listsubid");
        OapiV2DepartmentListsubidRequest req = new OapiV2DepartmentListsubidRequest();
        req.setDeptId(deptId);
        OapiV2DepartmentListsubidResponse rsp = null;
        try {
            rsp = client.execute(req, accessToken);
            return rsp.getResult().getDeptIdList();
        } catch (ApiException e) {
            e.printStackTrace();
            throw new DWException("获取部门下子部门id集合错误："+e.getMessage());
        }
    }
}
