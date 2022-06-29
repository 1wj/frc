package com.digiwin.app.frc.service.athena.qdh.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.eai.EAIService;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;

import java.util.Map;

/**
 * @ClassName IESPService
 * @Description 对外暴露api；互联中台交互
 * @Author HeXin
 * @Date 2021/11/10 16:11
 * @Version 1.0
 **/
public interface IESPService extends DWService {
    /**
     * 侦测-athena侦测待审核反馈数据
     * @param headers 请求头参数
     * @param messageBody 入参
     * @return DWEAIResult
     * @throws Exception 异常处理
     */
    @EAIService(id = "data.change.get")
    DWEAIResult dataChange(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 问题发起
     *
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.initiate.info.create")
    DWEAIResult initQuestion(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 获取问题反馈详情信息
     *
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.feedback.detail.info.get")
    DWEAIResult getFeedBackDetail(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * 生成待处理问题信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "pending.approve.question.info.create")
    DWEAIResult postApprovedQuestion(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 生成待处理遏制信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "pending.approve.curb.info.create")
    DWEAIResult postApprovedCurbQuestion(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 新增问题记录信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.record.info.create")
    DWEAIResult postQuestionRecord(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 更新问题记录信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.record.info.update")
    DWEAIResult postQuestionRecordUpdate(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 获取问题记录信息
     * @param messageBody
     * @return
     */
    @EAIService(id = "question.record.info.get")
    DWEAIResult getRecord(String messageBody);



    /**
     * 获取问题识别详情信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.identify.info.get")
    DWEAIResult getIdentifyDetail(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 获取解决方案步骤信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "solution.step.info.get")
    DWEAIResult getSolutionStep(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 获取问题分配详情信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.distribution.info.get")
    DWEAIResult getDistributionDetail(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 获取遏制分配详情信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "curb.distribution.info.get")
    DWEAIResult getCurbDistributionDetail(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 获取遏制详情信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "curb.detail.info.get")
    DWEAIResult getCurbDetail(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 获取遏制审核详情信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "curb.verify.info.get")
    DWEAIResult getCurbVerifyDetail(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 获取问题关闭详情信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.close.info.get")
    DWEAIResult getCloseDetail(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 获取问题验收详情信息
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.acceptance.detail.info.get")
    DWEAIResult getAcceptanceDetail(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * 更新问题反馈
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.feedback.info.update")
    DWEAIResult postQuestionFeedbackUpdate(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 更新问题识别-处理
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.identify.info.update")
    DWEAIResult postQuestionIdentifyUpdate(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * 更新问题分配
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.distribution.info.update")
    DWEAIResult postQuestionDistributionUpdate(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 更新遏制分配
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "curb.distribution.info.update")
    DWEAIResult postQuestionCurbDistributionUpdate(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 更新遏制
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "curb.info.update")
    DWEAIResult postQuestionCurbUpdate(Map<String, String> headers, String messageBody) throws Exception;

    /**
     * 更新遏制审核
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "curb.verify.info.update")
    DWEAIResult postQuestionCurbVerifyUpdate(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * 更新问题关闭
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.close.info.update")
    DWEAIResult postQuestionCloseUpdate(Map<String, String> headers, String messageBody) throws Exception;


    /**
     * 更新问题验收
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @EAIService(id = "question.acceptance.info.update")
    DWEAIResult postQuestionAcceptanceUpdate(Map<String, String> headers, String messageBody) throws Exception;

}
