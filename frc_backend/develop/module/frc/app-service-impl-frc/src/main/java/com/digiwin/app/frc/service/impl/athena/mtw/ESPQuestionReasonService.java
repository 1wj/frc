package com.digiwin.app.frc.service.impl.athena.mtw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionReasonBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IESPQuestionReasonService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWEAIResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/16 17:06
 * @Version 1.0
 * @Description
 */
public class ESPQuestionReasonService implements IESPQuestionReasonService {

    @Autowired
    private QuestionReasonBiz questionReasonBiz;

    /**
     * 添加原因代码信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @Override
    public DWEAIResult postAddQuestionReasonInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        //对参数进行解析并校验返回结果
        JSONArray dataContent = parseData(messageBody, true);
        List<JSONObject> result = questionReasonBiz.addQuestionReason(dataContent);
        dataResult.put("reason_info", result);
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("addSuccess"), dataResult);
    }

    /**
     * 删除原因代码信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @Override
    public DWEAIResult postDeleteQuestionReasonInfo(Map<String, Object> headers, String messageBody) throws Exception {
        boolean result;
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent = parseData(messageBody, true);
            result = questionReasonBiz.deleteQuestionReason(dataContent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return result ? new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("deleteSuccess"), null) :
                new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("deleteFail"), null);
    }

    /**
     * 更新原因代码信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @Override
    public DWEAIResult postUpdateQuestionReasonInfo(Map<String, Object> headers, String messageBody) throws Exception {
        boolean result;
        //对参数进行解析并校验返回结果
        JSONArray dataContent = parseData(messageBody, true);
        result = questionReasonBiz.updateQuestionReason(dataContent);
        return result ? new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("updateSuccess"), null) :
                new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("updateFail"), null);
    }

    /**
     * 查询原因代码信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @Override
    public DWEAIResult getQuestionReasonInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseData(messageBody, false);
            List<JSONObject> result = questionReasonBiz.getQuestionReason(dataContent);
            dataResult.put("reason_info", result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("querySuccess"), dataResult);
    }


    /**
     * 解析请求数据并是否进行校验
     *
     * @param messageBody 消息体
     * @param check 是否校验
     * @return  JSONArray
     */
    private JSONArray parseData(String messageBody,boolean check) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray solutionData = (JSONArray) parameter.get("reason_info");
        if(check){
            if (StringUtils.isEmpty(solutionData) || solutionData.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return solutionData;
    }


}
