package com.digiwin.app.frc.service.athena.mtw.service;


import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;

import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;



/**
 * @Author: xieps
 * @Date: 2021/11/5 14:05
 * @Version 1.0
 * @Description 提供Athena维护作业-问题分类基础信息服务
 */
@DWRestfulService
public interface IQuestionClassificationService extends DWService {

    /**
     * 添加问题分类信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/classification/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addQuestionClassificationInfo(String messageBody) throws Exception;

    /**
     * 删除问题分类信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/classification/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteQuestionClassificationInfo(String messageBody) throws Exception;

    /**
     * 更新问题分类信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/classification/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionClassificationInfo(String messageBody) throws Exception;

    /**
     * 获取问题分类信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/classification/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionClassificationInfo(String messageBody) throws Exception;

}
