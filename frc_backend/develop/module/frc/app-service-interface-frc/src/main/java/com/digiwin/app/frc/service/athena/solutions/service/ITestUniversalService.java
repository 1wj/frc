package com.digiwin.app.frc.service.athena.solutions.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2022/4/6 15:21
 * @Version 1.0
 * @Description  迭代五 api - 通用解决方案
 */
@DWRestfulService
public interface ITestUniversalService extends DWService {

    /**
     *  原因分析&计划安排信息查询
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/general/solution/plan/arrange/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getPlanArrangeInfo(String messageBody) throws Exception;


    /**
     * 原因分析&计划安排信息更新
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/general/solution/plan/arrange/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdatePlanArrangeInfo(String messageBody) throws Exception;

    /**
     * D4 临时措施信息查询
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/temporary/measure/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getTemporaryMeasureInfo(String messageBody) throws Exception;


    /**
     * D4 临时措施信息更新
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/temporary/measure/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdateTemporaryMeasureInfo(String messageBody) throws Exception;


    /**
     * D4-1 临时措施执行
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/temporary/measure/execute/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getTemporaryMeasureExecuteInfo(String messageBody) throws Exception;


    /**
     * D4-1 临时措施执行
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/temporary/measure/execute/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdateTemporaryMeasureExecuteInfo(String messageBody) throws Exception;


    /**
     * D5 恒久措施信息查询
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/lasting/measure/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getPermanentMeasureInfo(String messageBody) throws Exception;


    /**
     * D5 恒久措施信息更新
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/lasting/measure/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdatePermanentMeasureInfo(String messageBody) throws Exception;


    /**
     * D5-1 恒久措施信息执行信息查询
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/lasting/measure/execute/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getPermanentMeasureExecuteInfo(String messageBody) throws Exception;


    /**
     * D5-1 恒久措施执行信息更新
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/lasting/measure/execute/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdatePermanentMeasureExecuteInfo(String messageBody) throws Exception;


    /**
     * D5-2 恒久措施信息执行验证信息查询
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/lasting/measure/execute/verify/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getPermanentMeasureExecuteVerifyInfo(String messageBody) throws Exception;


    /**
     * D5-2 恒久措施执行验证信息更新
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/lasting/measure/execute/verify/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdatePermanentMeasureExecuteVerifyInfo(String messageBody) throws Exception;


    /**
     * D6 通用方案确认信息查询
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/general/solution/confirm/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getSolutionConfirmInfo(String messageBody) throws Exception;


    /**
     * D6 通用方案确认信息更新
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/general/solution/confirm/info/update", method = {DWRequestMethod.POST})
    DWServiceResult postUpdateSolutionConfirmInfo(String messageBody) throws Exception;



}
