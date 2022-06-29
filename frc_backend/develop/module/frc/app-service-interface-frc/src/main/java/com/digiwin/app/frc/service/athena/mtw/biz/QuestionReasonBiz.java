package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/16 17:08
 * @Version 1.0
 * @Description 处理原因代码Biz
 */
public interface QuestionReasonBiz {

    /**
     * 添加原因代码
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws IOException
     * @throws DWArgumentException
     */
    List<JSONObject> addQuestionReason(JSONArray dataContent) throws IOException, DWArgumentException;

    /**
     * 删除原因代码
     *
     * @param dataContent 解析后数据
     * @return boolean 表示是否删除成功
     */
    boolean deleteQuestionReason(JSONArray dataContent);

    /**
     * 更新原因代码
     *
     * @param dataContent 解析后数据
     * @return boolean 表示是否更新成功
     * @throws IOException
     * @throws DWArgumentException
     */
    boolean updateQuestionReason(JSONArray dataContent) throws IOException, DWArgumentException;

    /**
     * 查询原因代码
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws JsonProcessingException
     */
    List<JSONObject> getQuestionReason(JSONArray dataContent) throws JsonProcessingException;
}
