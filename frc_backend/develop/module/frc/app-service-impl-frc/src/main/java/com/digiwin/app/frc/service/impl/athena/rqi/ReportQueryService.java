package com.digiwin.app.frc.service.impl.athena.rqi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.rqi.biz.QuestionTrackInfoBiz;
import com.digiwin.app.frc.service.athena.rqi.service.IReportQueryService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/1/5 11:38
 * @Version 1.0
 * @Description
 */
public class ReportQueryService implements IReportQueryService {


    @Autowired
    private QuestionTrackInfoBiz questionTrackInfoBiz;

    @Override
    public DWServiceResult getQuestionTrackProposerInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseDate(messageBody);
            List<JSONObject> resultInfo = questionTrackInfoBiz.getQuestionTrackProposerInfo(dataContent);
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }


    @Override
    public DWServiceResult getQuestionTrackProcessorInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseDate(messageBody);
            List<JSONObject> resultInfo = questionTrackInfoBiz.getQuestionTrackProcessorInfo(dataContent);
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }


    @Override
    public DWServiceResult getQuestionTrackResponsibleInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseDate(messageBody);
            List<JSONObject> resultInfo = questionTrackInfoBiz.getQuestionTrackResponsibleInfo(dataContent);
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
     * @return JSONArray
     */
    private JSONArray parseDate(String messageBody) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray dataContent = (JSONArray) parameter.get("track_query_info");
        return dataContent;
    }
}
