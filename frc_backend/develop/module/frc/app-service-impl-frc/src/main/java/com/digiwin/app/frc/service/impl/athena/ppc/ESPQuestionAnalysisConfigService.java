package com.digiwin.app.frc.service.impl.athena.ppc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionAnalysisConfigBiz;
import com.digiwin.app.frc.service.athena.ppc.service.IESPQuestionAnalysisConfigService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWEAIResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/2/17 14:44
 * @Version 1.0
 * @Description
 */
public class ESPQuestionAnalysisConfigService implements IESPQuestionAnalysisConfigService {

    @Autowired
    private QuestionAnalysisConfigBiz questionAnalysisConfigBiz;

    @Override
    public DWEAIResult postAddQuestionAnalysisConfigInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        //对参数进行解析并校验返回结果
        JSONArray dataContent =  parseDate(messageBody, true);
        List<JSONObject> result = questionAnalysisConfigBiz.addQuestionAnalysisConfig(dataContent);
        dataResult.put("analysis_config_info", result);
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("addSuccess"), dataResult);
    }

    @Override
    public DWEAIResult postDeleteQuestionAnalysisConfigInfo(Map<String, Object> headers, String messageBody) throws Exception {
        boolean result;
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent =  parseDate(messageBody, true);
            result = questionAnalysisConfigBiz.deleteQuestionAnalysisConfig(dataContent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return result ? new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("deleteSuccess"), null) :
                new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("deleteFail"), null);
    }

    @Override
    public DWEAIResult postUpdateQuestionAnalysisConfigInfo(Map<String, Object> headers, String messageBody) throws Exception {
        boolean result;
        //对参数进行解析并校验返回结果
        JSONArray dataContent = parseDate(messageBody, true);
        result = questionAnalysisConfigBiz.updateQuestionAnalysisConfigInfo(dataContent);
        return result ? new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("updateSuccess"), null) :
                new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("updateFail"), null);
    }

    @Override
    public DWEAIResult getQuestionAnalysisConfigInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseDate(messageBody, false);
            List<JSONObject> result = questionAnalysisConfigBiz.getQuestionAnalysisConfig(dataContent);
            dataResult.put("analysis_config_info", result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]", MultilingualismUtil.getLanguage("queryFail"), e.toString()));
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("querySuccess"), dataResult);
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
        JSONArray dataContent = (JSONArray) parameter.get("analysis_config_info");
        if (check) {
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return dataContent;
    }
}