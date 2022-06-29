package com.digiwin.app.frc.service.impl.athena.rqi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.rqi.biz.QuestionTrackInfoBiz;
import com.digiwin.app.frc.service.athena.rqi.service.IESPReportQueryService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWEAIResult;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/12/31 13:44
 * @Version 1.0
 * @Description
 */
public class ESPReportQueryService implements IESPReportQueryService {

    @Autowired
    private QuestionTrackInfoBiz questionTrackInfoBiz;

    @Override
    public DWEAIResult getQuestionTrackProposerInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseDate(messageBody);
            List<JSONObject> result = questionTrackInfoBiz.getQuestionTrackProposerInfo(dataContent);
            dataResult.put("track_result_info", result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]", MultilingualismUtil.getLanguage("queryFail"), e.toString()));
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("querySuccess"), dataResult);
    }


    @Override
    public DWEAIResult getQuestionTrackProcessorInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseDate(messageBody);
            List<JSONObject> result = questionTrackInfoBiz.getQuestionTrackProcessorInfo(dataContent);
            dataResult.put("track_result_info", result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]", MultilingualismUtil.getLanguage("queryFail"), e.toString()));
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("querySuccess"), dataResult);
    }


    @Override
    public DWEAIResult getQuestionTrackResponsibleInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseDate(messageBody);
            List<JSONObject> result = questionTrackInfoBiz.getQuestionTrackResponsibleInfo(dataContent);
            dataResult.put("track_result_info", result);
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
     * @return JSONArray
     */
    private JSONArray parseDate(String messageBody) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray dataContent = (JSONArray) parameter.get("track_query_info");
        return dataContent;
    }


}
