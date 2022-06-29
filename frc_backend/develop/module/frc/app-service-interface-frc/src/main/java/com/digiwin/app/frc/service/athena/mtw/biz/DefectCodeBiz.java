package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;


import java.io.IOException;
import java.util.List;


/**
 * @Author: xieps
 * @Date: 2021/11/9 17:22
 * @Version 1.0
 * @Description 缺陷代码信息处理Biz
 */
public interface DefectCodeBiz {

    /**
     * 添加缺陷代码信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws DWArgumentException
     * @throws IOException
     */
    List<JSONObject> addDefectCodeInfo(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 删除缺陷代码信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     */
    boolean deleteDefectCode(JSONArray dataContent);

    /**
     * 更新缺陷代码信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     * @throws DWArgumentException
     * @throws IOException
     */
    boolean updateDefectCode(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 获取缺陷代码信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws JsonProcessingException
     */
    List<JSONObject> getDefectCode(JSONArray dataContent) throws JsonProcessingException;
}
