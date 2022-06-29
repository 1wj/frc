package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/26 14:11
 * @Version 1.0
 * @Description 为Athena提供维护作业-物料基础信息服务
 */
public interface IESPQuestionItemService extends DWService {

    /**
     * 添加物料信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "item.basic.info.create")
    DWEAIResult postAddQuestionItemInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除物料信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "item.basic.info.delete")
    DWEAIResult postDeleteQuestionItemInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 修改物料信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "item.basic.info.update")
    DWEAIResult postUpdateQuestionItemInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取物料信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "item.basic.info.get")
    DWEAIResult getQuestionItemInfo(Map<String, Object> headers, String messageBody) throws Exception;


}
