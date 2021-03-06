package com.digiwin.app.frc.service.athena.app.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.frc.service.athena.app.cache.TokenCache;
import com.digiwin.app.frc.service.athena.app.entity.FrcDingdingUser;
import com.digiwin.app.frc.service.athena.app.entity.OpenSyncBizData;
import com.digiwin.app.frc.service.athena.app.mapper.FrcDingdingUserMapper;
import com.digiwin.app.frc.service.athena.app.mapper.OpenSyncBizDataMapper;
import com.digiwin.app.frc.service.athena.app.mapper.QuestionCardMapper;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.file.util.ReportCommons;
import com.digiwin.app.frc.service.athena.rqi.constants.TaskCodeConstants;
import com.digiwin.app.frc.service.athena.util.RequestClient;
import com.digiwin.app.service.DWOnLoad;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiMessageCorpconversationSendbytemplateRequest;
import com.dingtalk.api.request.OapiServiceGetAuthInfoRequest;
import com.dingtalk.api.request.OapiServiceGetCorpTokenRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiMessageCorpconversationSendbytemplateResponse;
import com.dingtalk.api.response.OapiServiceGetAuthInfoResponse;
import com.dingtalk.api.response.OapiServiceGetCorpTokenResponse;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
@Slf4j
@Component("createNoticeHandler")
public class CreateNoticeHandler {
    @Autowired
    private QuestionCardMapper questionCardMapper;

    @Autowired
    private OpenSyncBizDataMapper openSyncBizDataMapper;

    @Autowired
    private FrcDingdingUserMapper frcDingdingUserMapper;

    @Autowired
    private ReportCommons reportCommons;
    //????????????????????????SuiteKey
    private static String suiteKey;
    //????????????????????????SuiteSecret
    private static String suiteSecret;


    static {
        suiteKey = DWApplicationConfigUtils.getProperty("suiteKey");
        //????????????????????????SuiteSecret
        suiteSecret = DWApplicationConfigUtils.getProperty("suiteSecret");
    }

    public void run(){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String processStep = DWApplicationConfigUtils.getProperty("showNewsProcessStep");
            String solveStep = DWApplicationConfigUtils.getProperty("showNewsSolvStep");
            Date nowDate = new Date();
            //type 0?????????????????????????????????
            String startDate = questionCardMapper.getStartDate(0);
            JSONObject param = new JSONObject().fluentPut("startDate", startDate).fluentPut("endDate", sdf.format(nowDate))
                    .fluentPut("process_step", processStep.split(",")).fluentPut("solve_step", solveStep.split(","));
            List<JSONObject> list = questionCardMapper.queryQuestionNewsList(param);
            //??????accessToken
            TokenCache tokenCache = TokenCache.getInstance();

            for ( JSONObject data: list) {
                String iamId = data.getString("liable_person_id");
                Long tenantsid = data.getLong("tenantsid");
                //TODO ????????????dao?????????????????????????????????
//                FrcDingdingUser frcDingdingUser = frcDingdingUserMapper.selectCorpId(iamId, tenantsid);
//                if(frcDingdingUser == null){
//                    continue;
//                }
//                String corpId = frcDingdingUser.getCropId();
                String corpId = "ding7ab87c867d9170e535c2f4657eb6378f";
                if(StringUtils.isEmpty(tokenCache.getTokenMap().get(corpId)) || getInterval(tokenCache.getUpdateTimeMap().get(corpId),nowDate)){
                    //token??????????????????????????????1??????50?????????????????????token
                    tokenCache.getTokenMap().put(corpId,getAccessToken(corpId));
                    tokenCache.getUpdateTimeMap().put(corpId,nowDate);
                }
                String accessToken = tokenCache.getTokenMap().get(corpId);
                if(tokenCache.getAgentMap().get(corpId) == null){
                    JSONObject authorization = getAuthorization(corpId);
                    JSONObject agent = authorization.getJSONObject("auth_info").getJSONArray("agent").getJSONObject(0);
                    Long agentId = agent.getLong("agentid");
                    tokenCache.getAgentMap().put(corpId,agentId);
                }
                //????????????
//            pushUser(accessToken,"19074453631085172","test","????????????");
//            pushUserActionCard(accessToken,"19074453631085172","test","????????????");
                String taskCode = TaskCodeConstants.TASK_CODE_MAP.get(data.getString("question_process_step"));
                if(StringUtils.isEmpty(taskCode)){
                    taskCode = TaskCodeConstants.TASK_CODE_MAP.get(data.getString("question_solve_step"));
                }
//                pushUserForm(accessToken,frcDingdingUser.getId(),frcDingdingUser.getCropId(),data,data.getString("oid"),taskCode,tokenCache.getAgentMap().get(corpId));
                pushUserForm(accessToken,"19074453631085172",corpId,data,data.getString("oid"),taskCode,tokenCache.getAgentMap().get(corpId));

            }
            questionCardMapper.updateStartDate(sdf.format(nowDate),0);
        }catch (DWException e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    /**
     * ??????????????????
     * @return
     * @throws DWException
     */
    public JSONObject getAuthorization(String corpId) throws DWException {

        List<Integer> types = new ArrayList<>();
        types.add(2);
        types.add(4);
        List<OpenSyncBizData> listData = openSyncBizDataMapper.selectTypeByCorpId(types,corpId);
        String authCorpid = "";
        String suiteTicket = "";
        //????????????????????????????????????suite_ticket
        for(OpenSyncBizData data : listData){
            if(data.getBiz_type() == 2){
                //suite_ticket
                suiteTicket = JSON.parseObject(data.getBiz_data()).getString("suiteTicket");
                if(StringUtils.isEmpty(authCorpid)){
                    authCorpid = data.getCorp_id();
                }
            }else if(data.getBiz_type() == 4){
                //Corpid
                authCorpid = data.getCorp_id();
            }
        }

        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/service/get_auth_info");
        OapiServiceGetAuthInfoRequest req = new OapiServiceGetAuthInfoRequest();
        req.setSuiteKey(suiteKey);
        req.setAuthCorpid(authCorpid);
        // ????????????????????????????????????SuiteKey???SuiteSecret???
        // ???????????????????????????CustomKey???CustomSecret???
        OapiServiceGetAuthInfoResponse rsp = null;
        try {
            rsp = client.execute(req, suiteKey ,suiteSecret ,suiteTicket);
            if(!rsp.isSuccess()) {
                log.error("?????????????????????????????????????????????"+rsp.getMessage());
                throw new DWException("?????????????????????????????????????????????"+rsp.getMessage());
            }
            return JSON.parseObject(rsp.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
            log.error("?????????????????????????????????????????????"+rsp.getMessage());
            throw new DWException("?????????????????????????????????????????????"+e.getMessage());
        }
    }

    /**
     * ??????accessToken
     * @return
     */
    public String getAccessToken(String corpId) throws DWException {
//        ???????????????????????????biz?????????2=suiteTicket???????????????token???5???????????????????????? 4=corpId ??????id??????????????????????????????
        List<Integer> types = new ArrayList<>();
        types.add(2);
        types.add(4);
        List<OpenSyncBizData> listData = openSyncBizDataMapper.selectTypeByCorpId(types,corpId);
        String authCorpid = "";
        String suiteTicket = "";
        //????????????????????????????????????suite_ticket
        for(OpenSyncBizData data : listData){
            if(data.getBiz_type() == 2){
                //suite_ticket
                suiteTicket = JSON.parseObject(data.getBiz_data()).getString("suiteTicket");
                if(StringUtils.isEmpty(authCorpid)){
                    authCorpid = data.getCorp_id();
                }
            }else if(data.getBiz_type() == 4){
                //Corpid
                authCorpid = data.getCorp_id();
            }
        }
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/service/get_corp_token");
        OapiServiceGetCorpTokenRequest req = new OapiServiceGetCorpTokenRequest();
        try {
            req.setAuthCorpid(authCorpid);
            OapiServiceGetCorpTokenResponse rsp = client.execute(req, suiteKey, suiteSecret, suiteTicket);
            if(!rsp.isSuccess()) {
                log.error("??????????????????token???????????????"+rsp.getMessage());
                throw new DWException("??????????????????token???????????????"+rsp.getMessage());
            }
            return rsp.getAccessToken();
        } catch (ApiException | DWException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new DWException("??????????????????token???????????????"+e.getMessage());
        }

    }


    /**
     * ???????????????????????????????????????
     * @param accessToken
     * @param userIds
     * @param content
     * @return
     * @throws DWException
     */
    public JSONObject pushUser(String accessToken,String userIds,String content,String title, Long agentId) throws DWException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/asyncsend_v2");
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        request.setAgentId(agentId);
        request.setUseridList(userIds);
        request.setToAllUser(false);
        OapiMessageCorpconversationAsyncsendV2Request.Msg msg = new OapiMessageCorpconversationAsyncsendV2Request.Msg();
        msg.setMsgtype("link");
        msg.setLink(new OapiMessageCorpconversationAsyncsendV2Request.Link());
        msg.getLink().setTitle(title);
        msg.getLink().setText(content);
        msg.getLink().setMessageUrl("dingtalk://dingtalkclient/page/link?url=http%3A%2F%2Fwww.dingtalk.com&pc_slide=true");
        msg.getLink().setPicUrl("https://dmc-test.digiwincloud.com.cn/api/dmc/v2/file/teamwork/preview/14b88355-0b2f-40f1-93a9-a69f40e66570");
        request.setMsg(msg);
        OapiMessageCorpconversationAsyncsendV2Response rsp = null;
        try {
            rsp = client.execute(request, accessToken);
            if(!rsp.isSuccess()) {
                log.error("???????????????????????????????????????"+rsp.getMessage());
                throw new DWException("???????????????????????????????????????"+rsp.getMessage());
            }
            return JSON.parseObject(rsp.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new DWException("???????????????????????????????????????"+e.getMessage());
        }


    }
    /**
     * ????????????????????????ActionCard?????????
     * @param accessToken
     * @param userIds
     * @param content
     * @return
     * @throws DWException
     */
    public JSONObject pushUserActionCard(String accessToken,String userIds,String content,String title, Long agentId) throws DWException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/sendbytemplate");
        OapiMessageCorpconversationSendbytemplateRequest req = new OapiMessageCorpconversationSendbytemplateRequest();
        req.setAgentId(agentId);
        req.setUseridList(userIds);
        req.setTemplateId("51590e86f9684336af1a848e2b969694");
        req.setData("{\"questionDescription\":\"questionDescription\"}");
        OapiMessageCorpconversationSendbytemplateResponse rsp = null;
        try {
            rsp = client.execute(req, accessToken);
            if(!rsp.isSuccess()) {
                log.error("???????????????????????????????????????"+rsp.getMessage());
                throw new DWException("???????????????????????????????????????"+rsp.getMessage());
            }
            return JSON.parseObject(rsp.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new DWException("???????????????????????????????????????"+e.getMessage());
        }
    }
    /**
     * ????????????????????????form?????????
     * @param accessToken
     * @param userIds
     * @param data ??????????????????
     * @return
     * @throws DWException
     */
    public JSONObject pushUserForm(String accessToken,String userIds,String corpId,JSONObject data,String oid,String taskCode, Long agentId) throws DWException {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/topapi/message/corpconversation/sendbytemplate");
        OapiMessageCorpconversationSendbytemplateRequest req = new OapiMessageCorpconversationSendbytemplateRequest();
        JSONObject basic = reportCommons.getKeyJson(
                reportCommons.getKeyJson(JSON.parseObject(data.getString("content")),
                        QuestionResponseConst.QUESTION_RESULT),
                QuestionResponseConst.QUESTION_BASIC_INFO);
        req.setAgentId(agentId);
        req.setUseridList(userIds);
        req.setTemplateId("50f6461688dd41a789e5af5f77b6ab6b");
        JSONObject param = new JSONObject().fluentPut("top","????????????--").fluentPut("pcNewsUrl",DWApplicationConfigUtils.getProperty("pcNewsUrl"))
                .fluentPut("color",DWApplicationConfigUtils.getProperty("messageTitleColor"))
                .fluentPut("questionDescription",data.getString("question_description"))
                .fluentPut("question_proposer_name",basic.getString("question_proposer_name"))
                .fluentPut("happen_date",basic.getString("happen_date"))
                .fluentPut("appNewsUrl",DWApplicationConfigUtils.getProperty("appNewsUrl")+"?id="+oid+"&taskCode="+taskCode+"&corpId="+corpId);
        req.setData(param.toString());
        OapiMessageCorpconversationSendbytemplateResponse rsp = null;
        try {
            rsp = client.execute(req, accessToken);
            if(!rsp.isSuccess()) {
                log.error("???????????????????????????????????????"+rsp.getMessage());
                throw new DWException("???????????????????????????????????????"+rsp.getMessage());
            }
            return JSON.parseObject(rsp.getBody());
        } catch (ApiException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new DWException("???????????????????????????????????????"+e.getMessage());
        }




    }

    /**
     * ????????????????????????????????????
     * @param timestamp
     * @param suiteTicket
     * @param suiteSecret
     * @return
     */
    public String getSignature(Long timestamp,String suiteTicket,String suiteSecret) {
        try {
            String stringToSign = timestamp+"\n"+suiteTicket;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(suiteSecret.getBytes("UTF-8"), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes("UTF-8"));
            String encode = new BASE64Encoder().encode(signData);
            return urlEncode(encode,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // encoding????????????utf-8
    public static String urlEncode(String value, String encoding) {
        if (value == null) {
            return "";
        }
        try {
            String encoded = URLEncoder.encode(value, encoding);
            return encoded.replace("+", "%20").replace("*", "%2A")
                    .replace("~", "%7E").replace("/", "%2F");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("FailedToEncodeUri", e);
        }
    }

    /**
     * ??????????????????????????????1???50s
     * @param date1
     * @param date2
     * @return
     */
    public boolean getInterval(Date date1,Date date2){
        if(date1 == null){
            return true;
        }
        return date2.getTime()-date1.getTime() > (1000 * 60 * 110);
    }


}
