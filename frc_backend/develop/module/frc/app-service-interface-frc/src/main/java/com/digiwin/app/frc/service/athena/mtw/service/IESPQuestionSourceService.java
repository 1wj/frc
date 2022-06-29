package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/11 15:05
 * @Version 1.0
 * @Description 为Athena提供维护作业-问题来源信息基础信息服务
 */
public interface IESPQuestionSourceService extends DWService {


    /**
     * 添加问题来源信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.source.info.create")
    DWEAIResult postAddQuestionSourceInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除问题来源信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.source.info.delete")
    DWEAIResult postDeleteQuestionSourceInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 更新问题来源信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.source.info.update")
    DWEAIResult postUpdateQuestionSourceInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取问题来源信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.source.info.get")
    DWEAIResult getQuestionSourceInfo(Map<String, Object> headers, String messageBody) throws Exception;


}
