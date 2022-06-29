package com.digiwin.app.frc.service.athena.rqi.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/12/31 13:39
 * @Version 1.0
 * @Description 为Athena提供报表信息-问题追踪进度信息
 */
public interface IESPReportQueryService extends DWService {


    /**
     * 获取任务提出者问题追踪数据
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "proposer.question.track.info.get")
    DWEAIResult getQuestionTrackProposerInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 获取任务处理者问题追踪数据
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "processor.question.track.info.get")
    DWEAIResult getQuestionTrackProcessorInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 获取项目当责者问题追踪数据
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "responsible.question.track.info.get")
    DWEAIResult getQuestionTrackResponsibleInfo(Map<String, Object> headers, String messageBody) throws Exception;


}
