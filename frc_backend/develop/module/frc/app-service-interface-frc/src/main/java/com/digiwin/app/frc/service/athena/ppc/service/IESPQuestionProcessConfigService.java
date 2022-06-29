package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/2/17 10:35
 * @Version 1.0
 * @Description
 */
public interface IESPQuestionProcessConfigService extends DWService {

    /**
     * 添加问题处理阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.process.config.info.create")
    DWEAIResult postAddQuestionProcessConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除问题处理阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.process.config.info.delete")
    DWEAIResult postDeleteQuestionProcessConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 修改问题处理阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.process.config.info.update")
    DWEAIResult postUpdateQuestionProcessConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取问题处理阶段配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    //@EAIService(id = "question.process.config.info.get")
    DWEAIResult getQuestionProcessConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 测试迭代五测试格式数据
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.process.config.info.get")
    DWEAIResult getUniversalSolutionInfo(Map<String, Object> headers, String messageBody) throws Exception;


}
