package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/2/17 16:27
 * @Version 1.0
 * @Description
 */
public interface IESPQuestionAcceptanceConfigService extends DWService {

    /**
     * 添加问题验收阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.acceptance.config.info.create")
    DWEAIResult postAddQuestionAcceptanceConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除问题验收阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.acceptance.config.info.delete")
    DWEAIResult postDeleteQuestionAcceptanceConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 修改问题验收阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.acceptance.config.info.update")
    DWEAIResult postUpdateQuestionAcceptanceConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取问题验收阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.acceptance.config.info.get")
    DWEAIResult getQuestionAcceptanceConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;




}
