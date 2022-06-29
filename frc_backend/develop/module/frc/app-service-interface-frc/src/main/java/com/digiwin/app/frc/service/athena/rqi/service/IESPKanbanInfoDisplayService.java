package com.digiwin.app.frc.service.athena.rqi.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/1/25 9:22
 * @Version 1.0
 * @Description 看板信息展示
 */
public interface IESPKanbanInfoDisplayService extends DWService {


    /**
     * 获取看板搜索栏位信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "kanban.search.field.info.get")
    DWEAIResult getKanbanSearchFieldInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 获取问题看板信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "kanban.info.get")
    DWEAIResult getKanbanInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 获取议题矩阵管理总览信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "issue.management.matrix.overview.get")
    DWEAIResult getIssueManagementMatrixOverviewInfo(Map<String, Object> headers, String messageBody) throws Exception;



    /**
     * 获取议题矩阵管理总览信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "issue.management.matrix.get")
    DWEAIResult getIssueManagementMatrixInfo(Map<String, Object> headers, String messageBody) throws Exception;



}
