package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2021/11/30 11:34
 * @Version 1.0
 * @Description 为Athena提供维护作业-看板模板基础信息服务
 */
@DWRestfulService
public interface IKeyBoardTemplateService extends DWService {

    /**
     * 添加看板模板信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/kanban/template/field/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addKeyBoardTemplateInfo(String messageBody) throws Exception;

    /**
     * 删除看板模板信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/kanban/template/field/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteKeyBoardTemplateInfo(String messageBody) throws Exception;

    /**
     * 更新看板模板信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/kanban/template/field/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateKeyBoardTemplateInfo(String messageBody) throws Exception;

    /**
     * 获取看板模板信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/kanban/template/field/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getKeyBoardTemplateInfo(String messageBody) throws Exception;


}
