package com.digiwin.app.frc.service.athena.ppc.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/17 10:39
 * @Version 1.0
 * @Description  问题处理阶段Biz
 */
public interface QuestionProcessConfigBiz {

    /**
     * 添加问题处理阶段信息
     *
     * @param dataContent
     * @return List<JSONObject>
     * @throws IOException
     * @throws DWArgumentException
     */
    List<JSONObject> addQuestionProcessConfig(JSONArray dataContent) throws IOException, DWArgumentException;

    /**
     * 删除问题处理阶段信息
     *
     * @param dataContent
     * @return boolean
     */
    boolean deleteQuestionProcessConfig(JSONArray dataContent);

    /**
     * 更新问题处理阶段信息
     *
     * @param dataContent
     * @return boolean
     * @throws IOException
     * @throws DWArgumentException
     */
    boolean updateQuestionProcessConfigInfo(JSONArray dataContent) throws IOException, DWArgumentException;

    /**
     * 获取问题处理阶段信息
     *
     * @param dataContent
     * @return List<JSONObject>
     * @throws JsonProcessingException
     */
    List<JSONObject> getQuestionProcessConfig(JSONArray dataContent) throws JsonProcessingException;

    JSONObject getQuestionDetail(String questionId);
}
