package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2022/2/10 11:35
 * @Version 1.0
 * @Description  为Athena提供风险等级维护作业
 */
@DWRestfulService
public interface IQuestionRiskLevelService extends DWService {

    /**
     * 添加风险等级信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/risk/level/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addQuestionRiskLevelInfo(String messageBody) throws Exception;

    /**
     * 删除工艺信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/risk/level/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteQuestionRiskLevelInfo(String messageBody) throws Exception;

    /**
     * 更新工艺信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/risk/level/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionRiskLevelInfo(String messageBody) throws Exception;

    /**
     * 获取工艺信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/risk/level/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionRiskLevelInfo(String messageBody) throws Exception;



}
