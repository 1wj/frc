package com.digiwin.app.frc.service.athena.mtw.service;


import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;


/**
 * @Author: xieps
 * @Date: 2021/11/12 11:14
 * @Version 1.0
 * @Description 为Athena提供维护作业-客服对接供应商信息服务
 */
@DWRestfulService
public interface ICustomerService extends DWService {

    /**
     * 添加客服对接供应商信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/dealer/contact/person/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addCustomerServiceInfo(String messageBody) throws Exception;

    /**
     * 删除客服对接供应商信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/dealer/contact/person/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteCustomerServiceInfo(String messageBody) throws Exception;

    /**
     * 更新客服对接供应商信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/dealer/contact/person/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateCustomerServiceInfo(String messageBody) throws Exception;

    /**
     * 获取客服对接供应商信息
     *
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/dealer/contact/person/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getCustomerServiceInfo(String messageBody) throws Exception;



}
