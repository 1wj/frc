package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/24 10:11
 * @Version 1.0
 * @Description 看板权限配置处理Biz
 */
public interface KeyBoardAuthorityBiz {

    /**
     * 添加看板权限配置信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws DWArgumentException
     * @throws IOException
     */
    List<JSONObject> addKeyBoardAuthority(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 删除看板权限配置信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     */
    boolean deleteKeyBoardAuthority(JSONArray dataContent);

    /**
     * 更新看板权限配置信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     * @throws IOException
     * @throws DWArgumentException
     */
    boolean updateKeyBoardAuthority(JSONArray dataContent) throws IOException, DWArgumentException;

    /**
     * 获取看板权限配置信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws JsonProcessingException
     */
    List<JSONObject> getKeyBoardAuthority(JSONArray dataContent) throws JsonProcessingException;
}
