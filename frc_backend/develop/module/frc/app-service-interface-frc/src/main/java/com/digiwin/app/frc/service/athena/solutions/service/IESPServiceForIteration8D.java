package com.digiwin.app.frc.service.athena.solutions.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @ClassName IESPServiceForIteration8D
 * @Description 迭代四 api
 * @Author HeX
 * @Date 2022/3/8 2:32
 * @Version 1.0
 **/
public interface IESPServiceForIteration8D extends DWService {

    /**
     * 生成待处理问题信息(即athena推卡;生成下一关待办任务卡)
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "generate.pending.question.info.process")
    DWEAIResult postApprovedQuestion(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D1&D2 更新组建团队&问题描述信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.team.building.info.update")
    DWEAIResult postQuestionTeamBuildUpdate(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D1&D2 获取组建团队&问题描述信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.team.building.info.get")
    DWEAIResult getQuestionTeamBuildInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D3 获取围堵措施信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.containment.measure.info.get")
    DWEAIResult getQuestionContainmentMeasureInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D3 更新围堵措施信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.containment.measure.info.update")
    DWEAIResult updateContainmentMeasureInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D3-1 获取围堵措施执行信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.containment.measure.execute.info.get")
    DWEAIResult getQuestionContainmentMeasureExecuteInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D3-1 更新围堵措施执行信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.containment.measure.execute.info.update")
    DWEAIResult updateContainmentMeasureExecuteInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D3-2 获取围堵措施执行验证信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.containment.measure.execute.verify.info.get")
    DWEAIResult getQuestionContainmentMeasureExecuteVerifyInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D3-2 更新围堵措施执行验证信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.containment.measure.execute.verify.info.update")
    DWEAIResult updateQuestionContainmentMeasureExecuteVerifyInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D4&D5 获取根本原因和纠正措施信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.key.reason.rectify.info.get")
    DWEAIResult getQuestionKeyReasonRectifyInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D4&D5 更新根本原因和纠正措施信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.key.reason.rectify.info.update")
    DWEAIResult updateQuestionKeyReasonRectifyInfo(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 查询人员待处理问题数信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "personnel.pending.question.count.info.get")
    DWEAIResult getPersonPendingQuestionNumInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D5-1 查询纠正措施执行信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.rectify.execute.info.get")
    DWEAIResult getQuestionRectifyExecuteInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D5-1 更新纠正措施执行信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.rectify.execute.info.update")
    DWEAIResult updateQuestionRectifyExecuteInfo(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * D6 问题纠正验证资讯更新
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.rectify.verify.info.update")
    DWEAIResult updateQuestionRectifyVerifyInfo(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * D6 问题纠正验证资讯查询
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.rectify.verify.info.get")
    DWEAIResult getQuestionRectifyVerifyInfo(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * D7 预防措施信息查询
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.prevention.measure.info.get")
    DWEAIResult getQuestionPreventionMeasureInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D7 预防措施信息更新
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.prevention.measure.info.update")
    DWEAIResult updateQuestionPreventionMeasureInfo(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * D7-1 预防措施执行信息查询
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.prevention.measure.execute.info.get")
    DWEAIResult getQuestionPreventionMeasureExecuteInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D7-1 预防措施信执行息更新
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.prevention.measure.execute.info.update")
    DWEAIResult updateQuestionPreventionMeasureExecuteInfo(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * D7-2 预防措施验证信息查询
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.prevention.measure.verify.info.get")
    DWEAIResult getQuestionPreventionMeasureVerifyInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D7-1 预防措施验证更新
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.prevention.measure.verify.info.update")
    DWEAIResult updateQuestionPreventionMeasureVerifyInfo(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * D8 问题确认get
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.confirm.info.get")
    DWEAIResult getQuestionConfirmInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D8 问题确认更新
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.confirm.info.update")
    DWEAIResult updateQuestionConfirmInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D3-3 获取反馈者验收信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "containment.measure.feedback.person.verify.info.get")
    DWEAIResult getFeedBackPersonVerifyInfo(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * D3-3 更新反馈者验收信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "containment.measure.feedback.person.verify.info.update")
    DWEAIResult updateFeedBackPersonVerifyInfo(Map<String, String> headers, String messageBody) throws Exception;



}
