package com.digiwin.app.frc.service.impl.athena.mtw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionSolutionBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IESPQuestionSolutionService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWEAIResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * @Author: xieps
 * @Date: 2021/11/16 15:26
 * @Version 1.0
 * @Description
 */
public class ESPQuestionSolutionService implements IESPQuestionSolutionService {

    @Autowired
    private QuestionSolutionBiz questionSolutionBiz;

    @Override
    public DWEAIResult postAddQuestionSolutionInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String,Object> dataResult = new HashMap<>(16);
        try {
            JSONArray solutionData = parseData(messageBody,true);
            List result = questionSolutionBiz.addQuestionSolution(solutionData);
            dataResult.put("solution_info",result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]", MultilingualismUtil.getLanguage("addFail"), e.toString()));
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("addSuccess"), dataResult);
    }



    @Override
    public DWEAIResult postDeleteQuestionSolutionInfo(Map<String, Object> headers, String messageBody) throws Exception {
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent =  parseData(messageBody, true);
            questionSolutionBiz.deleteQuestionSolution(dataContent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return  new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("deleteSuccess"), null);
    }

    @Override
    public DWEAIResult postUpdateQuestionSolutionInfo(Map<String, Object> headers, String messageBody) throws Exception {
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent = parseData(messageBody, true);
            questionSolutionBiz.updateQuestionSolution(dataContent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return  new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("updateSuccess"), null);

    }

    @Override
    public DWEAIResult getQuestionSolutionInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseData(messageBody, false);
            List<Map<String, Object>> result = questionSolutionBiz.getSolutionInfo(dataContent);
            dataResult.put("solution_info", result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]", MultilingualismUtil.getLanguage("queryFail"), e.toString()));
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
        JSONArray solutionData = (JSONArray) parameter.get("solution_info");
        if(check){
            if (StringUtils.isEmpty(solutionData) || solutionData.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return solutionData;
    }
}
