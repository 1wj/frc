package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/22 10:23
 * @Version 1.0
 * @Description 看板模板处理Biz
 */
public interface KeyBoardTemplateBiz {

    /**
     * 添加看板模板信息
     *
     * @param dataContent 解析后数据
     * @return List<Map < String, Object>>
     * @throws DWArgumentException
     * @throws IOException
     */
    List addKeyBoardTemplate(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 删除看板模板信息
     *
     * @param dataContent 解析后数据
     */
    void deleteKeyBoardTemplate(JSONArray dataContent);

    /**
     * 修改看板模板信息
     *
     * @param keyboardModelData 解析后数据
     * @throws DWArgumentException
     * @throws IOException
     */
    void updateKeyBoardTemplate(JSONArray keyboardModelData) throws DWArgumentException, IOException;

    /**
     * 获取看板模板信息
     *
     * @param keyboardModelData 解析后数据
     * @return List<Map < String, Object>>
     * @throws JsonProcessingException
     */
    List<Map<String, Object>> getKeyBoardTemplate(JSONArray keyboardModelData) throws JsonProcessingException;
}
