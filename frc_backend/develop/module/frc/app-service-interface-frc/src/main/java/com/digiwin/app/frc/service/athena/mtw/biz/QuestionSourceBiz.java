package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/11 15:11
 * @Version 1.0
 * @Description 问题来源处理Biz
 */
public interface QuestionSourceBiz {

    /**
     * 添加问题来源信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws DWArgumentException
     * @throws IOException
     */
    List<JSONObject> addQuestionSource(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 删除问题来源信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     */
    boolean deleteQuestionSource(JSONArray dataContent);

    /**
     * 更新问题来源信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     * @throws DWArgumentException
     * @throws IOException
     */
    boolean updateQuestionSource(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 获取问题来源信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws JsonProcessingException
     */
    List<JSONObject> getQuestionSource(JSONArray dataContent) throws JsonProcessingException;

}
