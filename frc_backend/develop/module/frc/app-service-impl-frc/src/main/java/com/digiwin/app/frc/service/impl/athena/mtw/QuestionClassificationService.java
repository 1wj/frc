package com.digiwin.app.frc.service.impl.athena.mtw;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionClassificationBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IQuestionClassificationService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/5 14:15
 * @Version 1.0
 * @Description
 */
public class QuestionClassificationService implements IQuestionClassificationService {

    @Autowired
    private QuestionClassificationBiz questionClassificationBiz;


    @Override
    public DWServiceResult addQuestionClassificationInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseData(messageBody, true);
            List<JSONObject> resultInfo = questionClassificationBiz.addQuestionClassification(dataContent);
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("addSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult deleteQuestionClassificationInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseData(messageBody, true);
            boolean resultInfo = questionClassificationBiz.deleteQuestionClassification(dataContent);
            if (resultInfo) {
                result.setSuccess(true);
                result.setMessage(MultilingualismUtil.getLanguage("deleteSuccess"));
            } else {
                result.setSuccess(false);
                result.setMessage(MultilingualismUtil.getLanguage("deleteFail"));
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult updateQuestionClassificationInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //??????????????????????????????????????????
            JSONArray dataContent = parseData(messageBody, true);
            boolean resultInfo = questionClassificationBiz.updateQuestionClassification(dataContent);
            if (resultInfo) {
                result.setSuccess(true);
                result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
            } else {
                result.setSuccess(false);
                result.setMessage(MultilingualismUtil.getLanguage("updateFail"));
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult getQuestionClassificationInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //?????????????????????????????????
            JSONObject jsonObject = JSONObject.parseObject(messageBody);
            JSONObject stdData = (JSONObject) jsonObject.get("std_data");
            JSONObject parameter = (JSONObject) stdData.get("parameter");
            JSONArray dataContent = (JSONArray) parameter.get("classification_info");
            //??????classification_info ????????????null ??????????????????JSONArray
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                dataContent = new JSONArray();
            }
            List<JSONObject> resultInfo = questionClassificationBiz.getQuestionClassification(dataContent);
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(MultilingualismUtil.getLanguage("queryFail"));
        }
        return result;
    }

    /**
     * ????????????????????????
     *
     * @param messageBody ?????????
     * @param check       ??????????????????
     * @return JSONArray
     */
    private JSONArray parseData(String messageBody, boolean check) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray dataContent = (JSONArray) parameter.get("classification_info");
        if (check) {
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return dataContent;
    }

}
