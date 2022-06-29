package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/1/4 11:43
 * @Version 1.0
 * @Description 问题识别配置处理Biz
 */
public interface QuestionIdentifyConfigBiz {

    /**
     * 添加问题识别配置信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws DWArgumentException
     * @throws IOException
     */
    List<JSONObject> addQuestionIdentifyConfig(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 删除问题识别配置信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     */
    boolean deleteQuestionIdentifyConfig(JSONArray dataContent);

    /**
     * 更新问题识别配置信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     * @throws DWArgumentException
     * @throws IOException
     */
    boolean updateQuestionIdentifyConfig(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 查询问题识别配置信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws JsonProcessingException
     */
    List<JSONObject> getQuestionIdentifyConfig(JSONObject dataContent) throws JsonProcessingException;


}
