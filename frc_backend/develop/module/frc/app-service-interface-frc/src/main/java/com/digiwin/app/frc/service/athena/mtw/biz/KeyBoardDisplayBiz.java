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
 * @Date: 2021/11/23 10:09
 * @Version 1.0
 * @Description 看板模板显示处理Biz
 */
public interface KeyBoardDisplayBiz {

    /**
     * 新增看板模板显示信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws DWArgumentException
     * @throws IOException
     */
    List<JSONObject> addKeyBoardDisplay(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 删除看板模板显示信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     */
    boolean deleteKeyBoardDisplay(JSONArray dataContent);

    /**
     * 更新看板模板显示信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     * @throws IOException
     * @throws DWArgumentException
     */
    boolean updateKeyBoardDisplay(JSONArray dataContent) throws IOException, DWArgumentException;

    /**
     * 获取看板模板显示信息
     *
     * @param dataContent 解析后数据
     * @return List<Map<String, Object>>
     * @throws JsonProcessingException
     * @throws DWArgumentException
     */
    List<Map<String, Object>> getKeyBoardDisplay(JSONArray dataContent) throws JsonProcessingException, DWArgumentException;
}
