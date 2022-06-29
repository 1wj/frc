package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/12 11:25
 * @Version 1.0
 * @Description 客服对接经销商信息处理Biz
 */
public interface CustomerServiceBiz {

    /**
     * 添加客服对接经销商信息
     *
     * @param dataContent 解析后的数据
     * @return List<JSONObject>
     * @throws DWArgumentException
     * @throws IOException
     */
    List<JSONObject> addCustomerService(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 删除客服对接经销商信息
     *
     * @param dataContent 解析后的数据
     * @return boolean
     */
    boolean deleteCustomerService(JSONArray dataContent);

    /**
     * 修改客服对接经销商信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     * @throws DWArgumentException
     * @throws IOException
     */
    boolean updateCustomerService(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 获取客服对接经销商信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws JsonProcessingException
     */
    List<JSONObject> getCustomerService(JSONArray dataContent) throws JsonProcessingException;
}
