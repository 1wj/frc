package com.digiwin.app.frc.service.athena.mtw.service;


import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;



/**
 * @Author: xieps
 * @Date: 2021/11/16 13:30
 * @Version 1.0
 * @Description 提供Athena维护作业-工艺信息基础信息服务
 */
@DWRestfulService
public interface ICraftDataService extends DWService {

    /**
     * 添加工艺信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/op/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addCraftDataInfo(String messageBody) throws Exception;

    /**
     * 删除工艺信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/op/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteCraftDataInfo(String messageBody) throws Exception;

    /**
     * 更新工艺信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/op/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateCraftDataInfo(String messageBody) throws Exception;

    /**
     * 获取工艺信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/op/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getCraftDataInfo(String messageBody) throws Exception;



}
