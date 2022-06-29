package com.digiwin.app.frc.service.athena.rqi.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2022/1/5 11:36
 * @Version 1.0
 * @Description
 */
@DWRestfulService
public interface IReportQueryService extends DWService {

    /**
     * 获取任务提出者问题追踪数据
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/proposer/question/track/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionTrackProposerInfo(String messageBody) throws Exception;


    /**
     * 获取处理者问题追踪信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/processor/question/track/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionTrackProcessorInfo(String messageBody) throws Exception;


    /**
     * 获取项目当责者问题追踪信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/responsible/question/track/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionTrackResponsibleInfo(String messageBody) throws Exception;


}
