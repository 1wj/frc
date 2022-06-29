package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/22 10:16
 * @Version 1.0
 * @Description 为Athena提供维护作业-看板模板基础信息服务
 */
public interface IESPKeyBoardTemplateService extends DWService {

    /**
     * 添加看板模板信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "kanban.template.field.info.create")
    DWEAIResult postAddKeyBoardModelInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除看板模板信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "kanban.template.field.info.delete")
    DWEAIResult postDeleteKeyBoardModeInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 更新看板模板信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "kanban.template.field.info.update")
    DWEAIResult postUpdateKeyBoardModeInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取看板模板信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "kanban.template.field.info.get")
    DWEAIResult getKeyBoardModeInfo(Map<String, Object> headers, String messageBody) throws Exception;




}
