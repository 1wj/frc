package com.digiwin.app.frc.service.impl.athena.mtw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionSourceBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IQuestionSourceService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;


/**
 * @Author: xieps
 * @Date: 2021/11/11 15:10
 * @Version 1.0
 * @Description
 */
public class QuestionSourceService implements IQuestionSourceService {

    @Autowired
    private QuestionSourceBiz questionSourceBiz;

    @Override
    public DWServiceResult addQuestionSourceInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseDate(messageBody, true);
            List<JSONObject> resultInfo = questionSourceBiz.addQuestionSource(dataContent);
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
    public DWServiceResult deleteQuestionSourceInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseDate(messageBody, true);
            boolean resultInfo = questionSourceBiz.deleteQuestionSource(dataContent);
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
    public DWServiceResult updateQuestionSourceInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //??????????????????????????????????????????
            JSONArray dataContent = parseDate(messageBody, true);
            boolean resultInfo = questionSourceBiz.updateQuestionSource(dataContent);
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
    public DWServiceResult getQuestionSourceInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //?????????????????????????????????
            JSONObject jsonObject = JSONObject.parseObject(messageBody);
            JSONObject stdData = (JSONObject) jsonObject.get("std_data");
            JSONObject parameter = (JSONObject) stdData.get("parameter");
            JSONArray dataContent = (JSONArray) parameter.get("source_info");
            //??????source_info ????????????null ??????????????????JSONArray
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                dataContent = new JSONArray();
            }
            List<JSONObject> resultInfo = questionSourceBiz.getQuestionSource(dataContent);
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
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
