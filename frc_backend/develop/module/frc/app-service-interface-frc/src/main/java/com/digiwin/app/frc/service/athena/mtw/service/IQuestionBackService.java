package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2021/11/29 9:40
 * @Version 1.0
 * @Description 提供Athena维护作业-问题退回基础信息服务
 */
@DWRestfulService
public interface IQuestionBackService extends DWService {

    /**
     * 添加问题退回信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/return/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addQuestionBackInfo(String messageBody) throws Exception;

    /**
     * 删除问题退回信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/return/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteQuestionBackInfo(String messageBody) throws Exception;


    /**
     * 修改问题退回信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/return/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionBackInfo(String messageBody) throws Exception;

    /**
     * 获取问题退回信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/return/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionBackInfo(String messageBody) throws Exception;


}
