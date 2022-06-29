package com.digiwin.app.frc.service.impl.athena.mtw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionIdentifyConfigBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IQuestionIdentifyConfigService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/1/4 11:41
 * @Version 1.0
 * @Description
 */
public class QuestionIdentifyConfigService implements IQuestionIdentifyConfigService {

    @Autowired
    private QuestionIdentifyConfigBiz questionIdentifyConfigBiz;

    @Override
    public DWServiceResult addQuestionIdentifyConfigInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseData(messageBody, true);
            List<JSONObject> resultInfo = questionIdentifyConfigBiz.addQuestionIdentifyConfig(dataContent);
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
    public DWServiceResult deleteQuestionIdentifyConfigInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseData(messageBody, true);
            boolean resultInfo = questionIdentifyConfigBiz.deleteQuestionIdentifyConfig(dataContent);
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
    public DWServiceResult updateQuestionIdentifyConfigInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent = parseData(messageBody, true);
            boolean resultInfo = questionIdentifyConfigBiz.updateQuestionIdentifyConfig(dataContent);
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
    public DWServiceResult getQuestionIdentifyConfigInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //对参数进行解析返回结果
            JSONObject jsonObject = JSONObject.parseObject(messageBody);
            JSONObject stdData = (JSONObject) jsonObject.get("std_data");
            JSONObject parameter = (JSONObject) stdData.get("parameter");
            JSONObject dataContent = (JSONObject) parameter.get("identify_config_info");
            List<JSONObject> resultInfo = questionIdentifyConfigBiz.getQuestionIdentifyConfig(dataContent);
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
     * 解析前端参数信息
     *
     * @param messageBody 消息体
     * @param check       是否进行校验
     * @return JSONArray
     */
    private JSONArray parseData(String messageBody, boolean check) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray dataContent = (JSONArray) parameter.get("identify_config_info");
        if (check) {
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return dataContent;
    }
}
