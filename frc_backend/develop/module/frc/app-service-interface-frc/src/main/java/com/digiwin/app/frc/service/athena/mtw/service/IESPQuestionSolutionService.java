package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/16 15:20
 * @Version 1.0
 * @Description 提供Athena维护作业-解决方案配置基础信息服务
 */
public interface IESPQuestionSolutionService extends DWService {

    /**
     * 添加解决方案
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.solution.info.create")
    DWEAIResult postAddQuestionSolutionInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除解决方案
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.solution.info.delete")
    DWEAIResult postDeleteQuestionSolutionInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 更新解决方案
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.solution.info.update")
    DWEAIResult postUpdateQuestionSolutionInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取解决方案
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.solution.info.get")
    DWEAIResult getQuestionSolutionInfo(Map<String, Object> headers, String messageBody) throws Exception;

}
