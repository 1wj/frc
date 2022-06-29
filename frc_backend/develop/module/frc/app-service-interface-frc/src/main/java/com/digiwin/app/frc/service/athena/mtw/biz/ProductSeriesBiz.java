package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/12 16:02
 * @Version 1.0
 * @Description 产品系列处理Biz
 */
public interface ProductSeriesBiz {
    /**
     * 添加产品系列信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws DWArgumentException
     * @throws IOException
     */
    List<JSONObject> addProductSeries(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 删除产品系列信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     */
    boolean deleteProductSeries(JSONArray dataContent);

    /**
     * 更新产品系列信息
     *
     * @param dataContent 解析后数据
     * @return boolean
     * @throws DWArgumentException
     * @throws IOException
     */
    boolean updateProductSeries(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 获取产品系列信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     * @throws JsonProcessingException
     */
    List<JSONObject> getProductSeries(JSONArray dataContent) throws JsonProcessingException;
}
