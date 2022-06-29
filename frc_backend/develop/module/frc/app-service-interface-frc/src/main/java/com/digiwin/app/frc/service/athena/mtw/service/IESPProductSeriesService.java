package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/26 14:42
 * @Version 1.0
 * @Description 提供Athena维护作业-产品系列基础信息服务
 */
public interface IESPProductSeriesService extends DWService {

    /**
     * 添加产品系列信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "product.series.info.create")
    DWEAIResult postAddProductSeriesInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除产品系列信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "product.series.info.delete")
    DWEAIResult postDeleteProductSeriesInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 修改产品系列信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "product.series.info.update")
    DWEAIResult postUpdateProductSeriesInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取产品系列信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "product.series.info.get")
    DWEAIResult getProductSeriesInfo(Map<String, Object> headers, String messageBody) throws Exception;


}
