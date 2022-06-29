package com.digiwin.app.frc.service.athena.solutions.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/4/6 15:20
 * @Version 1.0
 * @Description  迭代五 api - 通用解决方案
 */
public interface IESPServiceForIterationUniversal extends DWService {


    /**
     * D1&D2&D3 获取通用方案计划安排信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "general.solution.plan.arrange.info.get")
    DWEAIResult getPlanArrangeInfo(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * D1&D2&D3 更新通用方案计划安排信息，
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "general.solution.plan.arrange.info.update")
    DWEAIResult postPlanArrangeInfoUpdate(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 	D4获取问题临时措施信息，
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.temporary.measure.info.get")
    DWEAIResult getTemporaryMeasureInfo(Map<String, String> headers, String messageBody) throws Exception;

    /**
     *  D4更新问题临时措施信息，
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.temporary.measure.info.update")
    DWEAIResult postTemporaryMeasureInfoUpdate(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * D4-1获取问题临时措施执行信息，
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.temporary.measure.execute.info.get")
    DWEAIResult getTemporaryMeasureexEcuteInfo(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 	D4-1更新问题临时措施执行信息吗，
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.temporary.measure.execute.info.update")
    DWEAIResult postTemporaryMeasureexEcuteInfoUpdate(Map<String, String> headers, String messageBody) throws Exception;

    /**
     *D4-2更新问题临时措施执行验证信息，
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.temporary.measure.execute.verify.info.update")
    DWEAIResult postTemporaryMeasureexEcuteVerifyInfoUpdate(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * D4-2	获取问题临时措施执行验证信息，
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.temporary.measure.execute.verify.info.get")
    DWEAIResult getTemporaryMeasureexEcuteVerifyInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D4-3	更新通用方案短期结案验证信息，
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "general.solution.short.term.close.verify.info.update")
    DWEAIResult postSolutionShortTermCloseVerifyInfoUpdate(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * D4-3	获取通用方案短期结案验证信息，
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "general.solution.short.term.close.verify.info.get")
    DWEAIResult getSolutionShortTermCloseVerifyInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D5	获取恒久措施信息，
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.lasting.measure.info.get")
    DWEAIResult getPermanentMeasureInfo(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * D5	更新恒久措施信息，
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.lasting.measure.info.update")
    DWEAIResult updatePermanentMeasureInfo(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * D5-1	获取恒久措施执行信息，
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.lasting.measure.execute.info.get")
    DWEAIResult getPermanentMeasureExecuteInfo(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * D5-1	更新恒久措施执行信息，
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.lasting.measure.execute.info.update")
    DWEAIResult updatePermanentMeasureExecuteInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D5-2	获取恒久措施执行验证信息，
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.lasting.measure.execute.verify.info.get")
    DWEAIResult getPermanentMeasureExecuteVerifyInfo(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * D5-2	更新恒久措施执行验证信息，
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.lasting.measure.execute.verify.info.update")
    DWEAIResult updatePermanentMeasureExecuteVerifyInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D6	获取通用方案确认信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "general.solution.confirm.info.get")
    DWEAIResult getSolutionConfirmInfo(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * D6	更新通用方案确认信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "general.solution.confirm.info.update")
    DWEAIResult updateSolutionConfirmInfo(Map<String, String> headers, String messageBody) throws Exception;


}
