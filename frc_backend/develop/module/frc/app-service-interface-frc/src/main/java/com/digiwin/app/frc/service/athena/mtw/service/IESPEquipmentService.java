package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/29 11:24
 * @Version 1.0
 * @Description 为Athena提供维护作业-生产线、设备基础信息服务
 */
public interface IESPEquipmentService extends DWService {

    /**
     * 添加生产线、设备信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "workstation.info.create")
    DWEAIResult postAddEquipmentInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除生产线、设备信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "workstation.info.delete")
    DWEAIResult postDeleteEquipmentInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 修改生产线、设备信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "workstation.info.update")
    DWEAIResult postUpdateEquipmentInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取生产线、设备信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "workstation.info.get")
    DWEAIResult getEquipmentInfo(Map<String, Object> headers, String messageBody) throws Exception;


}
