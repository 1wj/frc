package com.digiwin.app.frc.service.athena.solutions.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.eai.EAIService;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/3/9 16:44
 * @Version 1.0
 * @Description
 */
@DWRestfulService
public interface ITest8DService extends DWService {

    /**
     * 生成待处理问题信息(即athena推卡;生成下一关待办任务卡)
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/generate/pending/question/info/process", method = {DWRequestMethod.POST})
    DWServiceResult postPendingQuestionInfo(String messageBody) throws Exception;

    /**
     * D1&D2 组建团队问题描述更新
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/team/building/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdateTeamBuildingInfo(String messageBody) throws Exception;

    /**
     * D1&D2 组建团队问题描述查询
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/team/building/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getTeamBuildingInfo(String messageBody) throws Exception;

    /**
     * D3 围堵措施更新
     * @param messageBody 消息体
     * @return  DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/containment/measure/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdateContainmentMeasureInfo(String messageBody) throws Exception;

    /**
     * D3 围堵措施查询
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/containment/measure/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getContainmentMeasureInfo(String messageBody) throws Exception;

    /**
     * D3-1 围堵措施执行查询
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/containment/measure/execute/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getContainmentMeasureExecuteInfo(String messageBody) throws Exception;

    /**
     * D3-1 围堵措施执行更新
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/containment/measure/execute/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdateContainmentMeasureExecuteInfo(String messageBody) throws Exception;

    /**
     * D3-2 围堵措施执行验证查询
     * @param messageBody 消息体
     * @return  DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/containment/measure/execute/verify/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getContainmentMeasureVerifyInfo(String messageBody) throws Exception;

    /**
     * D3-2 围堵措施执行验证更新
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/containment/measure/execute/verify/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdateContainmentMeasureVerifyInfo(String messageBody) throws Exception;

    /**
     * D4&D5 根本原因&纠正措施查询
     * @param messageBody 消息体
     * @return  DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/key/reason/rectify/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionKeyReasonRectifyInfo(String messageBody) throws Exception;

    /**
     * D4&D5 根本原因&纠正措施更新
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/key/reason/rectify/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdateQuestionReasonRectifyInfo(String messageBody) throws Exception;

    /**
     * 人力瓶颈分析信息查询
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/personnel/pending/question/count/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getPersonPendingQuestionNumInfo(String messageBody) throws Exception;

    /**
     * D5-1 纠正措施执行信息查询
     * @param messageBody 消息体
     * @return  DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/rectify/execute/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionRectifyExecuteInfo(String messageBody) throws Exception;

    /**
     * D5-1 纠正措施执行信息更新
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/rectify/execute/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdateRectifyExecuteInfo(String messageBody) throws Exception;


    /**
     * D6纠正措施信息更新
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/rectify/verify/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdateRectifyVerifyInfo(String messageBody) throws Exception;
    /**
     * D7 预防措施信息查询
     * @param messageBody 消息体
     * @return  DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/prevention/measure/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionPreventionMeasureInfo(String messageBody) throws Exception;

    /**
     * D7 预防措施信息更新
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/prevention/measure/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdatePreventionMeasureInfo(String messageBody) throws Exception;

    /**
     * D7 预防措施信息更新
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/prevention/measure/excute/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdatePreventionMeasureExcuteInfo(String messageBody) throws Exception;
    /**
     * D7-2 预防措施验证信息查询
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @DWRequestMapping(path = "/question/prevention/measure/verify/info/get",method = {DWRequestMethod.POST})
    DWServiceResult getQuestionPreventionMeasureVerifyInfo(String messageBody) throws Exception;


    /**
     * D7-2 预防措施验证更新
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @DWRequestMapping(path = "/question/prevention/measure/verify/info/update",method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionPreventionMeasureVerifyInfo(String messageBody) throws Exception;

    /**
     * D8 预防措施
     * @param headers 请求头参数
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @DWRequestMapping(path = "/question/confirm/info/update",method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionConfirmyInfo(String messageBody) throws Exception;


    /**
     * D3-3 反馈者验收信息查询
     * @param messageBody 消息体
     * @return  DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/containment/measure/feedback/person/verify/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getFeedBackPersonVerifyInfo(String messageBody) throws Exception;

    /**
     * D3-3 反馈者验收信息更新
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/containment/measure/feedback/person/verify/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdateFeedBackPersonVerifyInfo(String messageBody) throws Exception;


}
