package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2021/11/30 9:47
 * @Version 1.0
 * @Description 提供Athena维护作业-原因代码基础信息服务
 */
@DWRestfulService
public interface IQuestionReasonService extends DWService {

    /**
     * 添加原因代码
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/reason/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addQuestionReasonInfo(String messageBody) throws Exception;

    /**
     * 删除原因代码
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/reason/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteQuestionReasonInfo(String messageBody) throws Exception;

    /**
     * 更新原因代码
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/reason/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionReasonInfo(String messageBody) throws Exception;

    /**
     * 获取原因代码
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/reason/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionReasonInfo(String messageBody) throws Exception;


}
