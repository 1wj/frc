package com.digiwin.app.frc.service.impl.athena.ppc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.ParamConst;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionProcessConfigBiz;
import com.digiwin.app.frc.service.athena.ppc.service.IESPQuestionProcessConfigService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.qdh.ParamsUtil;
import com.digiwin.app.service.DWEAIResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/2/17 10:37
 * @Version 1.0
 * @Description
 */
public class ESPQuestionProcessConfigService implements IESPQuestionProcessConfigService {

    @Autowired
    private QuestionProcessConfigBiz questionProcessConfigBiz;

    @Override
    public DWEAIResult postAddQuestionProcessConfigInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        //对参数进行解析并校验返回结果
        JSONArray dataContent =  parseDate(messageBody, true);
        List<JSONObject> result = questionProcessConfigBiz.addQuestionProcessConfig(dataContent);
        dataResult.put("process_config_info", result);
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("addSuccess"), dataResult);
    }

    @Override
    public DWEAIResult postDeleteQuestionProcessConfigInfo(Map<String, Object> headers, String messageBody) throws Exception {
        boolean result;
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent =  parseDate(messageBody, true);
            result = questionProcessConfigBiz.deleteQuestionProcessConfig(dataContent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return result ? new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("deleteSuccess"), null) :
                new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("deleteFail"), null);
    }

    @Override
    public DWEAIResult postUpdateQuestionProcessConfigInfo(Map<String, Object> headers, String messageBody) throws Exception {
        boolean result;
        //对参数进行解析并校验返回结果
        JSONArray dataContent = parseDate(messageBody, true);
        result = questionProcessConfigBiz.updateQuestionProcessConfigInfo(dataContent);
        return result ? new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("updateSuccess"), null) :
                new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("updateFail"), null);
    }

    @Override
    public DWEAIResult getQuestionProcessConfigInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseDate(messageBody, false);
            List<JSONObject> result = questionProcessConfigBiz.getQuestionProcessConfig(dataContent);
            dataResult.put("process_config_info", result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]", MultilingualismUtil.getLanguage("queryFail"), e.toString()));
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("querySuccess"), dataResult);
    }


    @Override
    public DWEAIResult getUniversalSolutionInfo(Map<String, Object> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = questionProcessConfigBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0",MultilingualismUtil.getLanguage("selectSuccess"),result);
    }


    /**
     * 解析前端参数信息
     *
     * @param messageBody 消息体
     * @param check       是否进行校验
     * @return JSONArray
     */
    private JSONArray parseDate(String messageBody, boolean check) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray dataContent = (JSONArray) parameter.get("process_config_info");
        if (check) {
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return dataContent;
    }
}
