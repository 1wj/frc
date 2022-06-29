package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/2/17 14:29
 * @Version 1.0
 * @Description 为Athena提供问题分析配置信息
 */
public interface IESPQuestionAnalysisConfigService extends DWService {

    /**
     * 添加问题分析阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.analysis.config.info.create")
    DWEAIResult postAddQuestionAnalysisConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除问题分析阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.analysis.config.info.delete")
    DWEAIResult postDeleteQuestionAnalysisConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 修改问题分析阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.analysis.config.info.update")
    DWEAIResult postUpdateQuestionAnalysisConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取问题分析阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.analysis.config.info.get")
    DWEAIResult getQuestionAnalysisConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;



}
