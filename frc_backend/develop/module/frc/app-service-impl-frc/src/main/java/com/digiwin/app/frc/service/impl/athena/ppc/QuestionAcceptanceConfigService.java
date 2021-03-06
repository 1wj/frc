package com.digiwin.app.frc.service.impl.athena.ppc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionAcceptanceConfigBiz;
import com.digiwin.app.frc.service.athena.ppc.service.IQuestionAcceptanceConfigService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/17 16:29
 * @Version 1.0
 * @Description
 */
public class QuestionAcceptanceConfigService implements IQuestionAcceptanceConfigService {

    @Autowired
    private QuestionAcceptanceConfigBiz questionAcceptanceConfigBiz;

    @Override
    public DWServiceResult addQuestionAcceptanceConfigInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseDate(messageBody, true);
            List<JSONObject> resultInfo = questionAcceptanceConfigBiz.addQuestionAcceptanceConfig(dataContent);
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
    public DWServiceResult deleteQuestionAcceptanceConfigInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseDate(messageBody, true);
            boolean resultInfo = questionAcceptanceConfigBiz.deleteQuestionAcceptanceConfig(dataContent);
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
    public DWServiceResult updateQuestionAcceptanceConfigInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //??????????????????????????????????????????
            JSONArray dataContent = parseDate(messageBody, true);
            boolean resultInfo = questionAcceptanceConfigBiz.updateQuestionAcceptanceConfigInfo(dataContent);
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
    public DWServiceResult getQuestionAcceptanceConfigInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //?????????????????????????????????
            JSONArray dataContent = parseDate(messageBody, false);
            List<JSONObject> resultInfo = questionAcceptanceConfigBiz.getQuestionAcceptanceConfig(dataContent);
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            //result.setMessage(MultilingualismUtil.getLanguage("queryFail"));
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
        JSONArray dataContent = (JSONArray) parameter.get("acceptance_config_info");
        if (check) {
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return dataContent;
    }
}
