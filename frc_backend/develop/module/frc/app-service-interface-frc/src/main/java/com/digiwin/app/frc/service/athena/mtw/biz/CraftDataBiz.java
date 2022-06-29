package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/16 13:54
 * @Version 1.0
 * @Description 工艺信息处理Biz
 */
public interface CraftDataBiz {
    /**
     * 添加工艺信息
     *
     * @param dataContent 解析后的数据
     * @return List<JSONObject>
     * @throws IOException
     * @throws DWArgumentException
     */
    List<JSONObject> addCraftData(JSONArray dataContent) throws IOException, DWArgumentException;

    /**
     * 删除工艺信息
     *
     * @param dataContent 解析后的数据
     * @return boolean 表示是否删除成功
     */
    boolean deleteCraftData(JSONArray dataContent);

    /**
     * 更新工艺信息
     *
     * @param dataContent 解析后数据
     * @return boolean 表示是否删除成功
     * @throws IOException
     * @throws DWArgumentException
     */
    boolean updateCraftData(JSONArray dataContent) throws IOException, DWArgumentException;

    /**
     * 获取工艺信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws JsonProcessingException
     */
    List<JSONObject> getCraftData(JSONArray dataContent) throws JsonProcessingException;
}
