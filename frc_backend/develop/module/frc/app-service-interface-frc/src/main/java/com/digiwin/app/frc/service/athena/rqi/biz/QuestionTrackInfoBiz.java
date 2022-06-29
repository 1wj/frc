package com.digiwin.app.frc.service.athena.rqi.biz;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.text.ParseException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/12/31 14:11
 * @Version 1.0
 * @Description 获取问题追踪信息Biz
 */
public interface QuestionTrackInfoBiz {

    /**
     * 获取任务提出者问题追踪数据
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws ParseException
     * @throws JsonProcessingException
     */
    List<JSONObject> getQuestionTrackProposerInfo(JSONArray dataContent) throws ParseException, JsonProcessingException, Exception;


    /**
     * 获取处理者问题追踪数据
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws ParseException
     * @throws JsonProcessingException
     */
    List<JSONObject> getQuestionTrackProcessorInfo(JSONArray dataContent) throws ParseException, JsonProcessingException, Exception;


    /**
     * 获取项目当责者问题追踪信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     */
    List<JSONObject> getQuestionTrackResponsibleInfo(JSONArray dataContent) throws ParseException, JsonProcessingException, Exception;
}
