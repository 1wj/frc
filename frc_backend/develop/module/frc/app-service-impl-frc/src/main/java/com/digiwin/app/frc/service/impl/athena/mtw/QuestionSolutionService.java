package com.digiwin.app.frc.service.impl.athena.mtw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionSolutionBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IQuestionSolutionService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/29 23:09
 * @Version 1.0
 * @Description
 */
public class QuestionSolutionService implements IQuestionSolutionService {

    @Autowired
    private QuestionSolutionBiz questionSolutionBiz;

    @Override
    public DWServiceResult addQuestionSolutionInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseDate(messageBody, true);
            List resultInfo = questionSolutionBiz.addQuestionSolution(dataContent);
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
    public DWServiceResult deleteQuestionSolutionInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseDate(messageBody, true);
            questionSolutionBiz.deleteQuestionSolution(dataContent);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("deleteSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult updateQuestionSolutionInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent = parseDate(messageBody, true);
            questionSolutionBiz.updateQuestionSolution(dataContent);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult getQuestionSolutionInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseDate(messageBody, false);
            List<Map<String, Object>> resultInfo = questionSolutionBiz.getSolutionInfo(dataContent);
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
        JSONArray dataContent = (JSONArray) parameter.get("solution_info");
        if (check) {
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return dataContent;
    }
}
