package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2021/11/29 23:07
 * @Version 1.0
 * @Description 提供Athena维护作业-解决方案配置基础信息服务
 */
@DWRestfulService
public interface IQuestionSolutionService extends DWService {

    /**
     * 添加解决方案信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/solution/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addQuestionSolutionInfo(String messageBody) throws Exception;

    /**
     * 删除解决方案信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/solution/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteQuestionSolutionInfo(String messageBody) throws Exception;

    /**
     * 更新解决方案信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/solution/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionSolutionInfo(String messageBody) throws Exception;

    /**
     * 获取解决方案信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/solution/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionSolutionInfo(String messageBody) throws Exception;




}
