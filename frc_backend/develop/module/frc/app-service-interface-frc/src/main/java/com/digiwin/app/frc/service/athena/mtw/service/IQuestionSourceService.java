package com.digiwin.app.frc.service.athena.mtw.service;


import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;



/**
 * @Author: xieps
 * @Date: 2021/11/11 15:05
 * @Version 1.0
 * @Description  为Athena提供维护作业-问题来源信息基础信息服务
 */
@DWRestfulService
public interface IQuestionSourceService extends DWService {


    /**
     * 添加问题来源信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/source/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addQuestionSourceInfo(String messageBody) throws Exception;

    /**
     * 删除问题来源信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/source/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteQuestionSourceInfo(String messageBody) throws Exception;

    /**
     * 更新问题来源信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/source/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionSourceInfo(String messageBody) throws Exception;

    /**
     * 获取问题来源信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/source/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionSourceInfo(String messageBody) throws Exception;



}
