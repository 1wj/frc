package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/2/15 13:28
 * @Version 1.0
 * @Description 为Athena提供问题确认阶段配置信息维护作业
 */
public interface IESPQuestionConfirmConfigService extends DWService {

    /**
     * 添加问题确认阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.confirm.config.info.create")
    DWEAIResult postAddQuestionConfirmConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除问题确认阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.confirm.config.info.delete")
    DWEAIResult postDeleteQuestionConfirmConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 修改问题确认阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.confirm.config.info.update")
    DWEAIResult postUpdateQuestionConfirmConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取问题确认阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.confirm.config.info.get")
    DWEAIResult getQuestionConfirmConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;




}
