package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2021/11/26 11:21
 * @Version 1.0
 * @Description
 */
@DWRestfulService
public interface IDefectCodeService extends DWService {

    /**
     * 添加缺陷代码信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/defect/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addDefectCodeInfo(String messageBody) throws Exception;

    /**
     * 删除缺陷代码信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/defect/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteDefectCodeInfo(String messageBody) throws Exception;


    /**
     * 更新缺陷代码信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/defect/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateDefectCodeInfo(String messageBody) throws Exception;


    /**
     * 获取缺陷代码信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/defect/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getDefectCodeInfo(String messageBody) throws Exception;


}
