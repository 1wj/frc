package com.digiwin.app.frc.service.impl.athena.ppc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionLiablePersonDepartmentLatitudeConfigBiz;
import com.digiwin.app.frc.service.athena.ppc.service.IQuestionLiablePersonDepartmentLatitudeConfigService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
*@Author Jiangyw
*@Date 2022/3/14
*@Time 9:39
*@Version
*/
public class QuestionLiablePersonDepartmentLatitudeConfigService implements IQuestionLiablePersonDepartmentLatitudeConfigService {
    @Autowired
    QuestionLiablePersonDepartmentLatitudeConfigBiz questionLiablePersonDepartmentLatitudeConfigBiz;

    @Override
    public DWServiceResult deleteQuestionLiablePersonDepartmentLatitudeConfigInfo(String message) throws Exception{
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseDate(message,true);
            boolean resultInfo = questionLiablePersonDepartmentLatitudeConfigBiz.deleteQuestionLiablePersonDepartmentLatitudeConfig(dataContent);
            if (resultInfo) {
                result.setSuccess(true);
                result.setMessage(MultilingualismUtil.getLanguage("deleteSuccess"));
            }else {
                result.setSuccess(false);
                result.setMessage(MultilingualismUtil.getLanguage("deleteFail"));
            }
        }catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult getQuestionLiablePersonDepartmentLatitudeConfigInfo(String message) throws Exception{
        DWServiceResult result = new DWServiceResult();
        try{
            JSONArray dataContent = parseDate(message,true);
            List<JSONObject> resultInfo = questionLiablePersonDepartmentLatitudeConfigBiz.getQuestionLiablePersonDepartmentLatitudeConfig(dataContent);
            Map<String, Object> map = new HashMap<>();
            map.put("config_info",resultInfo);
            result.setData(map);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult addQuestionLiablePersonDepartmentLatitudeConfigInfo(String message) throws Exception{
        DWServiceResult result = new DWServiceResult();
        try{
            JSONArray dataContent = parseDate(message,true);
            List<JSONObject> resultInfo = questionLiablePersonDepartmentLatitudeConfigBiz.addQuestionLiablePersonDepartmentLatitudeConfig(dataContent);
            Map<String, Object> map = new HashMap<>();
            map.put("config_info",resultInfo);
            result.setData(map);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("addSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult updateQuestionLiablePersonDepartmentLatitudeConfigInfo(String message) throws Exception{
        DWServiceResult result = new DWServiceResult();
        try{
            JSONArray dataContent = parseDate(message,true);
            boolean resultInfo = questionLiablePersonDepartmentLatitudeConfigBiz.updateQuestionLiablePersonDepartmentLatitudeConfig(dataContent);
            if (resultInfo) {
                result.setSuccess(true);
                result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
            }else {
                result.setSuccess(false);
                result.setMessage(MultilingualismUtil.getLanguage("updateFail"));
            }
        }catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
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
