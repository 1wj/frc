package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/1/4 14:32
 * @Version 1.0
 * @Description 为Athena提供问题识别配置信息
 */
public interface IESPQuestionIdentifyConfigService extends DWService {

    /**
     * 添加问题识别配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.identify.config.info.create")
    DWEAIResult postAddQuestionIdentifyConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除问题识别配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.identify.config.info.delete")
    DWEAIResult postDeleteQuestionIdentifyConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 修改问题识别配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.identify.config.info.update")
    DWEAIResult postUpdateQuestionIdentifyConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取问题识别配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.identify.config.info.get")
    DWEAIResult getQuestionIdentifyConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;



}
