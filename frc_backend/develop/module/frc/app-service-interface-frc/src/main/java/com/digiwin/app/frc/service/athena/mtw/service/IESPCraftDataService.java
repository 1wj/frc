package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/26 11:51
 * @Version 1.0
 * @Description 为Athena提供维护作业-工艺信息基础信息服务
 */
public interface IESPCraftDataService extends DWService {

    /**
     * 添加工艺信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "op.info.create")
    DWEAIResult postAddCraftDataInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除工艺信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "op.info.delete")
    DWEAIResult postDeleteCraftDataInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 修改工艺信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "op.info.update")
    DWEAIResult postUpdateCraftDataInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取工艺信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "op.info.get")
    DWEAIResult getCraftDataInfo(Map<String, Object> headers, String messageBody) throws Exception;


}
