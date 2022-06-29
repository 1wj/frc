package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2022/1/4 11:38
 * @Version 1.0
 * @Description 问题识别配置信息
 */
@DWRestfulService
public interface IQuestionIdentifyConfigService extends DWService {

    /**
     * 添加问题识别配置
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/identify/config/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addQuestionIdentifyConfigInfo(String messageBody) throws Exception;

    /**
     * 删除问题识别配置
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/identify/config/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteQuestionIdentifyConfigInfo(String messageBody) throws Exception;

    /**
     * 更新问题识别配置
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/identify/config/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionIdentifyConfigInfo(String messageBody) throws Exception;

    /**
     * 获取问题识别配置
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/identify/config/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionIdentifyConfigInfo(String messageBody) throws Exception;



}
