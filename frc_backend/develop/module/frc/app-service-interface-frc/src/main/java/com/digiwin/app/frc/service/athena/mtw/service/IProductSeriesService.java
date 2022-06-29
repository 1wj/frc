package com.digiwin.app.frc.service.athena.mtw.service;


import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;


/**
 * @Author: xieps
 * @Date: 2021/11/12 15:56
 * @Version 1.0
 * @Description 提供Athena维护作业-产品系列基础信息服务
 */
@DWRestfulService
public interface IProductSeriesService extends DWService {

    /**
     * 添加产品系列信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/product/series/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addProductSeriesInfo(String messageBody) throws Exception;

    /**
     * 删除产品系列信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/product/series/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteProductSeriesInfo(String messageBody) throws Exception;

    /**
     * 更新产品系列信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/product/series/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateProductSeriesInfo(String messageBody) throws Exception;

    /**
     * 获取产品系列信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/product/series/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getProductSeriesInfo(String messageBody) throws Exception;



}
