package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2021/12/1 13:50
 * @Version 1.0
 * @Description 为Athena提供维护作业-看板模板显示基础信息服务
 */
@DWRestfulService
public interface IKeyBoardDisplayService extends DWService {

    /**
     * 新增看板模板显示信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/kanban/template/display/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addKeyBoardDisplayInfo(String messageBody) throws Exception;

    /**
     * 删除看板模板显示信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/kanban/template/display/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteKeyBoardDisplayInfo(String messageBody) throws Exception;


    /**
     * 更新看板模板显示信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/kanban/template/display/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateKeyBoardDisplayInfo(String messageBody) throws Exception;

    /**
     * 获取看板模板显示信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/kanban/template/display/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getKeyBoardDisplayInfo(String messageBody) throws Exception;


}
