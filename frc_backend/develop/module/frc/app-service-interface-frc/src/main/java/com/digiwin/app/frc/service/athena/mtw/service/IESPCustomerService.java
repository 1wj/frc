package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/28 23:27
 * @Version 1.0
 * @Description 为Athena提供维护作业-客服对接供应商信息服务
 */
public interface IESPCustomerService extends DWService {

    /**
     * 添加客服对接供应商信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "dealer.contact.person.info.create")
    DWEAIResult postAddCustomerServiceInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除客服对接供应商信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "dealer.contact.person.info.delete")
    DWEAIResult postDeleteCustomerServiceInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 更新客服对接供应商信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "dealer.contact.person.info.update")
    DWEAIResult postUpdateCustomerServiceInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取客服对接供应商信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "dealer.contact.person.info.get")
    DWEAIResult getCustomerServiceInfo(Map<String, Object> headers, String messageBody) throws Exception;



}
