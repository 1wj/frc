package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2022/2/15 13:31
 * @Version 1.0
 * @Description
 */
@DWRestfulService
public interface IQuestionConfirmConfigService extends DWService {

    /**
     * 添加问题确认配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/confirm/config/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addQuestionConfirmConfigInfo(String messageBody) throws Exception;

    /**
     * 删除问题确认配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/confirm/config/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteQuestionConfirmConfigInfo(String messageBody) throws Exception;

    /**
     * 更新问题确认配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/confirm/config/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionConfirmConfigInfo(String messageBody) throws Exception;

    /**
     * 获取问题确认配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/confirm/config/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionConfirmConfigInfo(String messageBody) throws Exception;



}
