package com.digiwin.app.frc.service.impl.athena.mtw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionSourceBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IESPQuestionSourceService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWEAIResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/11 15:10
 * @Version 1.0
 * @Description
 */
public class ESPQuestionSourceService implements IESPQuestionSourceService {

    @Autowired
    private QuestionSourceBiz questionSourceBiz;

    @Override
    public DWEAIResult postAddQuestionSourceInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        //对参数进行解析并校验返回结果
        JSONArray dataContent = parseDate(messageBody, true);
        List<JSONObject> result = questionSourceBiz.addQuestionSource(dataContent);
        dataResult.put("source_info", result);
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("addSuccess"), dataResult);
    }


    @Override
    public DWEAIResult postDeleteQuestionSourceInfo(Map<String, Object> headers, String messageBody) throws Exception {
        boolean result;
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent = parseDate(messageBody, true);
            result = questionSourceBiz.deleteQuestionSource(dataContent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return result ? new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("deleteSuccess"), null) :
                new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("deleteFail"), null);
    }


    @Override
    public DWEAIResult postUpdateQuestionSourceInfo(Map<String, Object> headers, String messageBody) throws Exception {
        boolean result;
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent = parseDate(messageBody, true);
            result = questionSourceBiz.updateQuestionSource(dataContent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return result ? new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("updateSuccess"), null) :
                new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("updateFail"), null);
    }

    @Override
    public DWEAIResult getQuestionSourceInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //对参数进行解析并校验返回结果
            JSONObject jsonObject = JSONObject.parseObject(messageBody);
            JSONObject stdData = (JSONObject) jsonObject.get("std_data");
            JSONObject parameter = (JSONObject) stdData.get("parameter");
            JSONArray dataContent = (JSONArray) parameter.get("source_info");
            //如果source_info 取出来为null 赋值一个空的JSONArray
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                dataContent = new JSONArray();
            }
            List<JSONObject> result = questionSourceBiz.getQuestionSource(dataContent);
            dataResult.put("source_info", result);
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
        JSONArray dataContent = (JSONArray) parameter.get("source_info");
        if (check) {
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return dataContent;
    }


}
