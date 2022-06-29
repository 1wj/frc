package com.digiwin.app.frc.service.athena.app.dingding;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.frc.service.athena.app.cache.TokenCache;
import com.digiwin.app.frc.service.athena.app.common.CacheCommonUtils;
import com.digiwin.app.frc.service.athena.app.common.DingDingHttpUtils;
import com.digiwin.app.frc.service.athena.app.entity.FrcDingdingUser;
import com.digiwin.app.frc.service.athena.app.entity.OpenSyncBizData;
import com.digiwin.app.frc.service.athena.app.job.CreateNoticeHandler;
import com.digiwin.app.frc.service.athena.app.mapper.FrcDingdingUserMapper;
import com.digiwin.app.frc.service.athena.app.mapper.OpenSyncBizDataMapper;
import com.digiwin.app.frc.service.athena.util.RequestClient;
import com.digiwin.app.service.DWServiceContext;
import com.dingtalk.oapi.lib.aes.DingTalkEncryptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CallbackService implements ICallbackService{
    /**
     * 创建应用，验证回调URL创建有效事件（第一次保存回调URL之前）
     */
    private static final String EVENT_CHECK_CREATE_SUITE_URL = "check_create_suite_url";

    /**
     * 创建应用，验证回调URL变更有效事件（第一次保存回调URL之后）
     */
    private static final String EVENT_CHECK_UPADTE_SUITE_URL = "check_update_suite_url";

    /**
     * suite_ticket推送事件
     */
    private static final String EVENT_SUITE_TICKET = "suite_ticket";

    /**
     * 企业授权开通应用事件
     */
    private static final String EVENT_TMP_AUTH_CODE = "tmp_auth_code";
    /**
     * 优先级高数据
     */
    private static final String SYNC_HTTP_PUSH_HIGH = "SYNC_HTTP_PUSH_HIGH";

    @Autowired
    private CreateNoticeHandler createNoticeHandler;

    @Autowired
    private OpenSyncBizDataMapper openSyncBizDataMapper;

    @Autowired
    private FrcDingdingUserMapper frcDingdingUserMapper;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    private CacheCommonUtils cacheCommonUtils;

    @Override
    public Object dingCallback(String signature,Long timestamp,String nonce, JSONObject body) {
        String params = "signature:" + signature + " timestamp:" + timestamp + " nonce:" + nonce + " body:" + body;
        try {
            log.info("begin callback:" + params);
            String callBackToken = DWApplicationConfigUtils.getProperty("callBackToken");
            String aesKey = DWApplicationConfigUtils.getProperty("aesKey");
            String suiteKey = DWApplicationConfigUtils.getProperty("suiteKey");
            log.info("callBackToken:"+callBackToken+";aesKey:"+";suiteKey:"+suiteKey);
            DingTalkEncryptor dingTalkEncryptor = new DingTalkEncryptor(callBackToken,aesKey, suiteKey);

            // 从post请求的body中获取回调信息的加密数据进行解密处理
            String encrypt = body.getString("encrypt");
            String plainText = dingTalkEncryptor.getDecryptMsg(signature, timestamp.toString(), nonce, encrypt);
//            plainText = "{\"EventType\":\"SYNC_HTTP_PUSH_HIGH\",\"bizData\":[{\"gmt_create\":1653965595000,\"biz_type\":4,\"open_cursor\":0,\"subscribe_id\":\"25662002_0\",\"id\":20846,\"gmt_modified\":1653965595000,\"biz_id\":\"25662002\",\"biz_data\":\"{\\\"syncAction\\\":\\\"suite_ticket\\\",\\\"suiteTicket\\\":\\\"VjlpV50uRCpyTf6YkRtZGfowJArfrF95rSZdDJwLkUKBARuhizWmDcC4czL9rLb69DxKIZedOZ3jptYFnu59wh\\\",\\\"syncSeq\\\":\\\"79BE5A12E5DE8FDEE8CA8D33A9\\\"}\",\"corp_id\":\"ding7ab87c867d9170e535c2f4657eb6378f\",\"status\":0}]}";
            JSONObject callBackContent = JSON.parseObject(plainText);
            log.info("plainText:"+plainText);
            // 根据回调事件类型做不同的业务处理
            String eventType = callBackContent.getString("EventType");
            if (EVENT_CHECK_CREATE_SUITE_URL.equals(eventType)) {
                log.info("验证新创建的回调URL有效性: " + plainText);
            } else if (EVENT_CHECK_UPADTE_SUITE_URL.equals(eventType)) {
                log.info("验证更新回调URL有效性: " + plainText);
            } else if (EVENT_SUITE_TICKET.equals(eventType)) {
                // suite_ticket用于用签名形式生成accessToken(访问钉钉服务端的凭证)，需要保存到应用的db。
                // 钉钉会定期向本callback url推送suite_ticket新值用以提升安全性。
                // 应用在获取到新的时值时，保存db成功后，返回给钉钉success加密串（如本demo的return）
                log.info("应用suite_ticket数据推送: " + plainText);
            } else if (EVENT_TMP_AUTH_CODE.equals(eventType)) {
                // 本事件应用应该异步进行授权开通企业的初始化，目的是尽最大努力快速返回给钉钉服务端。用以提升企业管理员开通应用体验
                // 即使本接口没有收到数据或者收到事件后处理初始化失败都可以后续再用户试用应用时从前端获取到corpId并拉取授权企业信息，进而初始化开通及企业。
                log.info("企业授权开通应用事件: " + plainText);
            } else if(SYNC_HTTP_PUSH_HIGH.equals(eventType)){
                // 高优先级数据
                OpenSyncBizData openSyncBizData = JSON.parseObject(
                        callBackContent.getJSONArray("bizData").getJSONObject(0).toString(),
                        OpenSyncBizData.class);
                openSyncBizDataMapper.insertSelective(openSyncBizData);
                if(4 == openSyncBizData.getBiz_type()){
                    //企业授权变更，获取授权用户信息
                    JSONObject bizData = JSON.parseObject(openSyncBizData.getBiz_data());
                    addUser(bizData);
                }else if(17 == openSyncBizData.getBiz_type()){
                    //获取订单信息，保存购买人信息为该企业的管理员
                    JSONObject bizData = JSON.parseObject(openSyncBizData.getBiz_data());
                    addAdmin(bizData);
                }
                new Thread(() -> {
                    log.info("异步线程 =====> 同步钉钉数据开始 =====> " + System.currentTimeMillis());
                    try {
                        RequestClient.appRequest(DWApplicationConfigUtils.getProperty("syncDingDataUrl"), null,
                                JSON.toJSONString(openSyncBizData), JSONObject.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.info("同步钉钉数据接口调用失败");
                    }
                    log.info("异步线程 =====> 同步钉钉数据结束 =====> " + System.currentTimeMillis());
                }).start();
            }else{
                // 其他类型事件处理
            }
            Map<String, String> success = dingTalkEncryptor.getEncryptedMap("success", timestamp, nonce);
            //处理返回不被封装
            DWServiceContext.getContext().setStandardResult(false);
//            String evne = dingTalkEncryptor.getDecryptMsg(success.get("msg_signature"), success.get("timeStamp"), success.get("nonce"), success.get("encrypt"));
//            System.err.println("encrypt:"+success.get("encrypt"));
//            System.out.println("evne:"+evne);
            // 返回success的加密信息表示回调处理成功
            return success;
        } catch (Exception e) {
            //失败的情况，应用的开发者应该通过告警感知，并干预修复
            log.error("process callback fail." + params, e);
            return null;
        }
    }

    /**
     * 批量插入普通用户
     * @param bizData
     */
    public void addUser(JSONObject bizData) throws DWException {
        JSONObject corpInfo = bizData.getJSONObject("auth_corp_info");
        if(corpInfo == null){
            return;
        }
        String corpId = corpInfo.getString("corpid");
        String corpName = corpInfo.getString("corp_name");
        JSONObject authScope = bizData.getJSONObject("auth_scope");
        if(authScope == null){
            return;
        }
        JSONObject orgScopes = authScope.getJSONObject("auth_org_scopes");
        if(orgScopes == null){
            return;
        }
        List<String> userList = (List<String>)orgScopes.get("authed_user");
        List<Long> deptList = (List<Long>)orgScopes.get("authed_dept");
        Set<String> uList = new HashSet<>();
        //获取部门下所有用户
        String accessToken = cacheCommonUtils.getAccessToken(corpId);
        List<String> userListByDept = getUserListByDept(deptList,accessToken);
        uList.addAll(userList);
        uList.addAll(userListByDept);
        
        List<String> users = frcDingdingUserMapper.selectUserByCorpId(corpId,null);
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        FrcDingdingUserMapper mapper = sqlSession.getMapper(FrcDingdingUserMapper.class);
        AtomicInteger atomicInteger = new AtomicInteger();
        userList = new ArrayList<>(uList);
        for (int i = 0; i < userList.size(); i++) {
            String userId = userList.get(i);
            if(users.contains(userId)){
                continue;
            }
            FrcDingdingUser dingUser = FrcDingdingUser.builder().cropId(corpId).id(userId).cropName(corpName)
                    .type(FrcDingdingUser.TYPE_USER).createTime(new Date()).build();
            mapper.insertSelective(dingUser);
            if(i % 500 == 0 || i == userList.size() - 1){
                //每1000条数据，手动提交一次，提交后的数据无法回滚
                sqlSession.commit();
                //积累数据量太多容易导致内存溢出，所以每次提交后清理缓存，防止溢出
                sqlSession.clearCache();
                atomicInteger.incrementAndGet();
                log.info("当前提交批次为："+atomicInteger.get());
            }
        }
    }

    /**
     * 获取部门下所有用户id
     * @param depts
     * @param accessToken
     * @return
     * @throws DWException
     */
    public List<String> getUserListByDept(List<Long> depts,String accessToken) throws DWException {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < depts.size(); i++) {
            Object val = depts.get(i);
            Long deptId;
            if(val instanceof Integer){
                deptId = ((Integer) val).longValue();
            }else{
                deptId = depts.get(i);
            }
            List<String> userIdList = DingDingHttpUtils.getUserIdByDept(deptId, accessToken);
            result.addAll(userIdList);
            List<Long> deptList = DingDingHttpUtils.getDeptList(deptId, accessToken);
            List<String> userListByDept = getUserListByDept(deptList,accessToken);
            result.addAll(userListByDept);
        }
        return result;
    }

    /**
     * 添加管理员用户
     * @param bizData
     * @throws DWException
     */
    public void addAdmin(JSONObject bizData) throws Exception {
        String corpId = bizData.getString("corpId");
        String unionId = bizData.getString("unionId");
        String accessToken = cacheCommonUtils.getAccessToken(corpId);
        JSONObject authorization = createNoticeHandler.getAuthorization(corpId);
        String corpName = authorization.getJSONObject("auth_corp_info").getString("corp_name");
        String userId = DingDingHttpUtils.getUserIdByUnionId(unionId, accessToken);
        FrcDingdingUser dingUser = FrcDingdingUser.builder().cropId(corpId).id(userId).cropName(corpName)
                .type(FrcDingdingUser.TYPE_ADMIN).createTime(new Date()).build();
        frcDingdingUserMapper.insertSelective(dingUser);
    }

    @Override
    public Map<String, String> test(String signature, Long timestamp, String nonce, JSONObject body) {
        DWServiceContext.getContext().setStandardResult(false);
        Map<String, String> test = new HashMap<>();
        test.put("test","test");
        return test;
    }

    @Override
    public JSONObject syncData(JSONObject data) {
        try {
            OpenSyncBizData openSyncBizData = JSON.parseObject(data.toString(), OpenSyncBizData.class);
            openSyncBizDataMapper.insertSelective(openSyncBizData);
            if(4 == openSyncBizData.getBiz_type()){
                //企业授权变更，获取授权用户信息
                JSONObject bizData = JSON.parseObject(openSyncBizData.getBiz_data());
                addUser(bizData);
            }else if(17 == openSyncBizData.getBiz_type()){
                //获取订单信息，保存购买人信息为该企业的管理员
                JSONObject bizData = JSON.parseObject(openSyncBizData.getBiz_data());

                    addAdmin(bizData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("数据同步失败原因："+e.getMessage());
            return new JSONObject().fluentPut("message","同步数据失败");
        }
        return new JSONObject().fluentPut("message","同步数据成功");
    }


}
