package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/11 11:26
 * @Version 1.0
 * @Description 物料信息处理Biz
 */
public interface QuestionItemBiz {

    /**
     * 添加物料信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws DWArgumentException
     * @throws IOException
     */
    List<JSONObject> addQuestionItem(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 删除物料信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     */
    boolean deleteQuestionItem(JSONArray dataContent);

    /**
     * 更新物料信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     * @throws DWArgumentException
     * @throws IOException
     */
    boolean updateQuestionItem(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 获取物料信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws JsonProcessingException
     */
    List<JSONObject> getQuestionItem(JSONArray dataContent) throws JsonProcessingException;
}
