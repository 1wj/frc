package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/2/10 11:25
 * @Version 1.0
 * @Description   为Athena提供风险等级维护作业
 */
public interface IESPQuestionRiskLevelService extends DWService {

    /**
     * 添加风险等级信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "risk.level.info.create")
    DWEAIResult postAddQuestionRiskLevelInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除风险等级信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "risk.level.info.delete")
    DWEAIResult postDeleteQuestionRiskLevelInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 修改风险等级信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "risk.level.info.update")
    DWEAIResult postUpdateQuestionRiskLevelInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取风险等级信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "risk.level.info.get")
    DWEAIResult getQuestionRiskLevelInfo(Map<String, Object> headers, String messageBody) throws Exception;


}
