package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/16 16:45
 * @Version 1.0
 * @Description 提供Athena维护作业-原因代码基础信息服务
 */
public interface IESPQuestionReasonService extends DWService {

    /**
     * 添加原因代码
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.reason.info.create")
    DWEAIResult postAddQuestionReasonInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除原因代码
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.reason.info.delete")
    DWEAIResult postDeleteQuestionReasonInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 更新原因代码
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.reason.info.update")
    DWEAIResult postUpdateQuestionReasonInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取原因代码
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.reason.info.get")
    DWEAIResult getQuestionReasonInfo(Map<String, Object> headers, String messageBody) throws Exception;



}
