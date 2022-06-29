package com.digiwin.app.frc.service.athena.mtw.service;


import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;


/**
 * @Author: xieps
 * @Date: 2021/11/12 14:20
 * @Version 1.0
 * @Description 为Athena提供维护作业-生产线、设备基础信息服务
 */
@DWRestfulService
public interface IEquipmentService extends DWService {

    /**
     * 添加设备信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/workstation/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addEquipmentInfo(String messageBody) throws Exception;

    /**
     * 删除设备信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/workstation/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteEquipmentInfo(String messageBody) throws Exception;

    /**
     * 更新设备信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/workstation/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateEquipmentInfo(String messageBody) throws Exception;

    /**
     * 获取设备信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/workstation/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getEquipmentInfo(String messageBody) throws Exception;




}
