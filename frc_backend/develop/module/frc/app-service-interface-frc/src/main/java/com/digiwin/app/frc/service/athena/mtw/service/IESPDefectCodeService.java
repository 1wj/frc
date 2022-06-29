package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/9 16:57
 * @Version 1.0
 * @Description 为Athena提供维护作业-缺陷代码基础信息服务
 */
public interface IESPDefectCodeService extends DWService {

    /**
     * 添加缺陷代码信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "defect.info.create")
    DWEAIResult postAddDefectCodeInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除缺陷代码信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "defect.info.delete")
    DWEAIResult postDeleteDefectCodeInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 修改缺陷代码信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "defect.info.update")
    DWEAIResult postUpdateDefectCodeInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取缺陷代码信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "defect.info.get")
    DWEAIResult getDefectCodeInfo(Map<String, Object> headers, String messageBody) throws Exception;

}
