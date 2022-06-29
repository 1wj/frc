package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author:zhangzlz
 * @Date 2022/2/17   19:03
 */
public interface IESPQuestionOccurStageService extends DWService {

    /**
     * 添加解决方案
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.occur.stage.info.create")
    DWEAIResult postAddQuestionOccurStageInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除解决方案
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.occur.stage.info.delete")
    DWEAIResult postDeleteQuestionOccurStageInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 更新解决方案
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.occur.stage.info.update")
    DWEAIResult postUpdateQuestionOccurStageInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取解决方案
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "question.occur.stage.info.get")
    DWEAIResult getQuestionOccurStageInfo(Map<String, Object> headers, String messageBody) throws Exception;

}
