package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2021/12/1 16:08
 * @Version 1.0
 * @Description 为Athena提供维护作业-看板权限配置基础信息服务
 */
@DWRestfulService
public interface IKeyBoardAuthorityService extends DWService {

    /**
     * 添加看板权限配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/kanban/permissions/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addKeyBoardAuthorityInfo(String messageBody) throws Exception;

    /**
     * 删除看板权限配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/kanban/permissions/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteKeyBoardAuthorityInfo(String messageBody) throws Exception;


    /**
     * 更新看板权限配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/kanban/permissions/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateKeyBoardAuthorityInfo(String messageBody) throws Exception;

    /**
     * 获取看板权限配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/kanban/permissions/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getKeyBoardAuthorityInfo(String messageBody) throws Exception;


}
