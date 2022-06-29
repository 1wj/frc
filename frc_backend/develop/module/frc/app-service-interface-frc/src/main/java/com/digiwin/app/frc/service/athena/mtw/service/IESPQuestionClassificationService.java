package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/26 14:34
 * @Version 1.0
 * @Description 为Athena提供维护作业-问题分类基础信息服务
 */
public interface IESPQuestionClassificationService extends DWService {

    /**
     * 添加问题分类信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.classification.info.create")
    DWEAIResult postAddQuestionClassificationInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除问题分类信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.classification.info.delete")
    DWEAIResult postDeleteQuestionClassificationInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 修改问题分类信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.classification.info.update")
    DWEAIResult postUpdateQuestionClassificationInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取问题分类信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.classification.info.get")
    DWEAIResult getQuestionClassificationInfo(Map<String, Object> headers, String messageBody) throws Exception;


}
