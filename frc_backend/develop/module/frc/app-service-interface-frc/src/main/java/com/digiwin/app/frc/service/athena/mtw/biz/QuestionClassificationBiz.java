package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/5 14:56
 * @Version 1.0
 * @Description 问题分类实体服务处理Biz
 */
public interface QuestionClassificationBiz {

    /**
     * 添加问题分类信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>  返回添加后回传前端数据类型
     * @throws DWArgumentException
     * @throws IOException
     */
    List<JSONObject> addQuestionClassification(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 删除问题分类信息
     *
     * @param dataContent 解析后数据
     * @return boolean 表判断是否删除成功
     * @throws DWArgumentException
     */
    boolean deleteQuestionClassification(JSONArray dataContent) throws DWArgumentException;

    /**
     * 更新问题分类信息
     *
     * @param dataContent 解析后数据
     * @return boolean 表示判断是否更新成功
     * @throws DWArgumentException
     * @throws IOException
     */
    boolean updateQuestionClassification(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 获取问题分类信息
     *
     * @param dataContent 解析后数据
     * @return  List<JSONObject> 返回查询结果类型
     * @throws JsonProcessingException
     */
    List<JSONObject> getQuestionClassification(JSONArray dataContent) throws JsonProcessingException;
}
