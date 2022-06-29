package com.digiwin.app.frc.service.athena.ppc.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/15 14:52
 * @Version 1.0
 * @Description 问题确认配置处理Biz
 */
public interface QuestionConfirmConfigBiz {

    /**
     * 添加问题确认配置信息
     *
     * @param dataContent 解析后数据
     * @throws IOException
     * @throws DWArgumentException
     * @return  List<JSONObject>
     */
    List<JSONObject> addQuestionConfirmConfig(JSONArray dataContent) throws IOException, DWArgumentException;

    /**
     * 获取问题确认配置信息
     *
     * @param dataContent 解析后数据
     * @throws JsonProcessingException
     * @return List<JSONObject>
     */
    List<JSONObject> getQuestionConfirmConfig(JSONArray dataContent) throws JsonProcessingException;

    /**
     * 删除问题确认配置信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     */
    boolean deleteQuestionConfirmConfig(JSONArray dataContent);

    /**
     * 更新问题确认配置信息
     *
     * @param dataContent  解析后数据
     * @return boolean
     * @throws IOException
     * @throws DWArgumentException
     */
    boolean updateQuestionConfirmConfigInfo(JSONArray dataContent) throws IOException, DWArgumentException;
}
