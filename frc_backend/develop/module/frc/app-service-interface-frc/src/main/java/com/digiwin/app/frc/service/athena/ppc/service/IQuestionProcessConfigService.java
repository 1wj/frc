package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2022/2/17 10:32
 * @Version 1.0
 * @Description  为Athena提供问题处理配置信息
 */
@DWRestfulService
public interface IQuestionProcessConfigService extends DWService {

    /**
     * 添加问题处理配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/process/config/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addQuestionProcessConfigInfo(String messageBody) throws Exception;

    /**
     * 删除问题处理配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/process/config/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteQuestionProcessConfigInfo(String messageBody) throws Exception;

    /**
     * 更新问题处理配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/process/config/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionProcessConfigInfo(String messageBody) throws Exception;

    /**
     * 获取问题处理配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    //@DWRequestMapping(path = "/question/process/config/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionProcessConfigInfo(String messageBody) throws Exception;



    @DWRequestMapping(path = "/question/process/config/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getUniversalSolutionInfo(String messageBody) throws Exception;


}
