package com.digiwin.app.frc.service.impl.athena.ppc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionLiablePersonDepartmentLatitudeConfigBiz;
import com.digiwin.app.frc.service.athena.ppc.service.IESPQuestionLiablePersonDepartmentLatitudeConfigService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWEAIResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
*@Author Jiangyw
*@Date 2022/3/11
*@Time 9:30
*@Version 1.0
*/
public class ESPQuestionLiablePersonDepartmentLatitudeConfigService implements IESPQuestionLiablePersonDepartmentLatitudeConfigService {

    @Autowired
    QuestionLiablePersonDepartmentLatitudeConfigBiz questionLiablePersonDepartmentLatitudeConfigBiz;

    @Override
    public DWEAIResult postDeleteQuestionLiablePersonDepartmentLatitudeConfigInfo(Map<String, Object> headers,String messageBody) throws Exception{
        boolean result;
        try{
            //对参数进行解析并校验返回结果
            JSONArray dataContent = parseDate(messageBody,true);
            result = questionLiablePersonDepartmentLatitudeConfigBiz.deleteQuestionLiablePersonDepartmentLatitudeConfig(dataContent);
        }catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return result ? new DWEAIResult("0","0",MultilingualismUtil.getLanguage("deleteSuccess"),null)
                : new DWEAIResult("0","0",MultilingualismUtil.getLanguage("deleteFail"),null);

    }

    @Override
    public DWEAIResult postGetQuestionLiablePersonDepartmentLatitudeConfigInfo(Map<String, Object> headers,String messageBody) throws Exception{
        Map<String,Object> dataResult = new HashMap<>();
        try {
            JSONArray dataContent = parseDate(messageBody,true);
            List<JSONObject> result = questionLiablePersonDepartmentLatitudeConfigBiz.getQuestionLiablePersonDepartmentLatitudeConfig(dataContent);
            dataResult.put("config_info",result);
        }catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]",MultilingualismUtil.getLanguage("queryFail"), e));
        }
        //对参数进行校验并解析返回结果
        return new DWEAIResult("0","0",MultilingualismUtil.getLanguage("querySuccess"),dataResult);
    }

    @Override
    public DWEAIResult postAddQuestionLiablePersonDepartmentLatitudeConfigInfo(Map<String, Object> headers,String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        //对参数进行解析并校验返回结果
        try {
            JSONArray dataContent = parseDate(messageBody, true);
            List<JSONObject> result = questionLiablePersonDepartmentLatitudeConfigBiz.addQuestionLiablePersonDepartmentLatitudeConfig(dataContent);
            dataResult.put("config_info", result);
        }catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("addSuccess"), dataResult);
    }

    @Override
    public DWEAIResult postUpdateQuestionLiablePersonDepartmentLatitudeConfigInfo(Map<String, Object> headers,String messageBody) throws Exception{
        boolean result;
        try {
            JSONArray dataContent = parseDate(messageBody, true);
            result = questionLiablePersonDepartmentLatitudeConfigBiz.updateQuestionLiablePersonDepartmentLatitudeConfig(dataContent);
        }catch (Exception e) {
            e.printStackTrace();
            throw  new DWRuntimeException(e.getMessage());
        }
        return result ? new DWEAIResult("0","0",MultilingualismUtil.getLanguage("updateSuccess"),null)
            : new DWEAIResult("0","0",MultilingualismUtil.getLanguage("updateFail"),null);
    }

    public JSONArray parseDate(String messageBody, boolean check) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray dataContent = (JSONArray) parameter.get("config_info");
        if (check) {
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return dataContent;
    }
}
