package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/15 17:10
 * @Version 1.0
 * @Description 问题退回处理Biz
 */
public interface QuestionBackBiz {

    /**
     * 添加问题退回
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject> 返回添加后回传前端数据类型
     * @throws IOException
     * @throws DWArgumentException
     */
    List<JSONObject> addQuestionBack(JSONArray dataContent) throws IOException, DWArgumentException;

    /**
     * 删除问题退回
     *
     * @param dataContent 解析后数据
     * @return boolean 表示是否删除成功
     */
    boolean deleteQuestionBack(JSONArray dataContent);

    /**
     * 修改问题退回
     *
     * @param dataContent 解析后数据
     * @return boolean 表示是否修改成功
     * @throws IOException
     * @throws DWArgumentException
     */
    boolean updateQuestionBack(JSONArray dataContent) throws IOException, DWArgumentException;

    /**
     * 获取问题退回信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject> 返回添加后回传前端数据类型
     * @throws JsonProcessingException
     */
    List<JSONObject> getQuestionBack(JSONArray dataContent) throws JsonProcessingException;
}
