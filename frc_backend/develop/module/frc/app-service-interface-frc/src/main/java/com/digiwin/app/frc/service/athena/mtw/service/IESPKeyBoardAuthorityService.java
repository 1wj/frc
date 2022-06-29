package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/12/1 19:56
 * @Version 1.0
 * @Description  为Athena提供维护作业-看板权限配置基础信息服务
 */
public interface IESPKeyBoardAuthorityService extends DWService {

    /**
     * 添加看板权限配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "kanban.permissions.info.create")
    DWEAIResult postAddKeyBoardAuthorityInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除看板权限配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "kanban.permissions.info.delete")
    DWEAIResult postDeleteKeyBoardAuthorityInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 更新看板权限配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "kanban.permissions.info.update")
    DWEAIResult postUpdateKeyBoardAuthorityInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取看板权限配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "kanban.permissions.info.get")
    DWEAIResult getKeyBoardAuthorityInfo(Map<String, Object> headers, String messageBody) throws Exception;



}
