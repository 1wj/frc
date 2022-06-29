package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/15 17:06
 * @Version 1.0
 * @Description 提供Athena维护作业-问题退回基础信息服务
 */
public interface IESPQuestionBackService extends DWService {

    /**
     * 添加问题退回信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.return.info.create")
    DWEAIResult postAddQuestionBackInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除问题退回信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.return.info.delete")
    DWEAIResult postDeleteQuestionBackInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 更新问题退回信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.return.info.update")
    DWEAIResult postUpdateQuestionBackInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取问题退回信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.return.info.get")
    DWEAIResult getQuestionBackInfo(Map<String, Object> headers, String messageBody) throws Exception;





}
