package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2022/2/17 14:40
 * @Version 1.0
 * @Description
 */
@DWRestfulService
public interface IQuestionAnalysisConfigService extends DWService {

    /**
     * 添加问题分析配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/analysis/config/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addQuestionConfirmConfigInfo(String messageBody) throws Exception;

    /**
     * 删除问题分析配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/analysis/config/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteQuestionAnalysisConfigInfo(String messageBody) throws Exception;

    /**
     * 更新问题分析配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/analysis/config/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionAnalysisConfigInfo(String messageBody) throws Exception;

    /**
     * 获取问题分析配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/analysis/config/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionAnalysisConfigInfo(String messageBody) throws Exception;



}
