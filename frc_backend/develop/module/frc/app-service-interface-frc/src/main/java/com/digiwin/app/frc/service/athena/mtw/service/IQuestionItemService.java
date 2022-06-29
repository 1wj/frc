package com.digiwin.app.frc.service.athena.mtw.service;


import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/11 11:18
 * @Version 1.0
 * @Description  为Athena提供维护作业-物料信息基础信息服务
 */
@DWRestfulService
public interface IQuestionItemService extends DWService {

    /**
     * 添加物料维护信息
     *
     * @param messageBody
     * @return
     * @throws Exception
     */
    @DWRequestMapping(path = "/item/basic/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addQuestionItemInfo(String messageBody) throws Exception;

    /**
     * 删除缺物料维护信息
     *
     * @param messageBody
     * @return
     * @throws Exception
     */
    @DWRequestMapping(path = "/item/basic/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteQuestionItemInfo(String messageBody) throws Exception;


    /**
     * 修改物料维护信息
     *
     * @param messageBody
     * @return
     * @throws Exception
     */
    @DWRequestMapping(path = "/item/basic/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionItemInfo(String messageBody) throws Exception;

    /**
     * 获取物料维护信息
     *
     * @param messageBody
     * @return
     * @throws Exception
     */
    @DWRequestMapping(path = "/item/basic/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionItemInfo(String messageBody) throws Exception;



}
