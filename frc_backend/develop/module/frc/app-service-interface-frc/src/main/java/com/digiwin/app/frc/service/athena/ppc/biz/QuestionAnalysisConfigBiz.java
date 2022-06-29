package com.digiwin.app.frc.service.athena.ppc.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/17 14:45
 * @Version 1.0
 * @Description  问题分析配置处理Biz
 */
public interface QuestionAnalysisConfigBiz {

    /**
     * 添加问题分析配置信息
     *
     * @param dataContent 解析后数据
     * @return  List<JSONObject>
     * @throws IOException
     * @throws DWArgumentException
     */
    List<JSONObject> addQuestionAnalysisConfig(JSONArray dataContent) throws IOException, DWArgumentException;

    /**
     * 获取问题分析配置信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws JsonProcessingException
     */
    List<JSONObject> getQuestionAnalysisConfig(JSONArray dataContent) throws JsonProcessingException;

    /**
     * 删除问题分析配置信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     */
    boolean deleteQuestionAnalysisConfig(JSONArray dataContent);

    /**
     * 更新问题分析配置信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     * @throws IOException
     * @throws DWArgumentException
     */
    boolean updateQuestionAnalysisConfigInfo(JSONArray dataContent) throws IOException, DWArgumentException;
}
