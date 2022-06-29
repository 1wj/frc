package com.digiwin.app.frc.service.athena.rqi.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2022/1/25 9:22
 * @Version 1.0
 * @Description 看板信息展示
 */
@DWRestfulService
public interface IKanbanInfoDisplayService extends DWService {

    /**
     * 获取看板搜索栏位信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/kanban/search/field/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getKanbanSearchFieldInfo(String messageBody) throws Exception;


    /**
     * 获取问题看板信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/kanban/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getKanbanInfo(String messageBody) throws Exception;



    /**
     * 获取议题矩阵管理总览信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/issue/management/matrix/overview/get", method = {DWRequestMethod.POST})
    DWServiceResult getIssueManagementMatrixOverviewInfo(String messageBody) throws Exception;


    /**
     * 获取议题矩阵管理信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/issue/management/matrix/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getIssueManagementMatrixInfo(String messageBody) throws Exception;


}
