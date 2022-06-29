package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2022/2/17 16:27
 * @Version 1.0
 * @Description
 */
@DWRestfulService
public interface IQuestionAcceptanceConfigService extends DWService {

    /**
     * 添加问题验收配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/acceptance/config/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addQuestionAcceptanceConfigInfo(String messageBody) throws Exception;

    /**
     * 删除问题验收配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/acceptance/config/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteQuestionAcceptanceConfigInfo(String messageBody) throws Exception;

    /**
     * 更新问题验收配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/acceptance/config/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionAcceptanceConfigInfo(String messageBody) throws Exception;

    /**
     * 获取问题验收配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/acceptance/config/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionAcceptanceConfigInfo(String messageBody) throws Exception;



}
