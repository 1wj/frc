package com.digiwin.app.frc.service.athena.ppc.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/10 11:43
 * @Version 1.0
 * @Description  风险等级信息处理Biz
 */
public interface QuestionRiskLevelBiz {

    /**
     * 添加问题风险等级信息
     *
     * @param dataContent
     * @throws IOException
     * @throws DWArgumentException
     * @return  List<JSONObject>
     */
    List<JSONObject> addQuestionRiskLevel(JSONArray dataContent) throws IOException, DWArgumentException;

    /**
     * 删除问题风险等级信息
     *
     * @param dataContent
     * @return  boolean
     */
    boolean deleteQuestionRiskLevel(JSONArray dataContent);

    /**
     * 更新问题风险等级信息
     *
     * @param dataContent
     * @throws DWArgumentException
     * @throws IOException
     * @return boolean
     */
    boolean updateQuestionRiskLevel(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 获取问题风险等级信息
     *
     * @param dataContent
     * @throws JsonProcessingException
     * @return  List<JSONObject>
     */
    List<JSONObject> getQuestionRiskLevel(JSONArray dataContent) throws JsonProcessingException;
}
