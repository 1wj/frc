package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/12 14:27
 * @Version 1.0
 * @Description 生产线/设备信息处理Biz
 */
public interface EquipmentBiz {

    /**
     * 添加生产线/设备信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws DWArgumentException
     * @throws IOException
     */
    List<JSONObject> addEquipment(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 删除生产线/设备信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     */
    boolean deleteEquipment(JSONArray dataContent);

    /**
     * 更新生产线/设备信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     * @throws DWArgumentException
     * @throws IOException
     */
    boolean updateEquipment(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 获取生产线/设备信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws JsonProcessingException
     */
    List<JSONObject> getEquipment(JSONArray dataContent) throws JsonProcessingException;
}
