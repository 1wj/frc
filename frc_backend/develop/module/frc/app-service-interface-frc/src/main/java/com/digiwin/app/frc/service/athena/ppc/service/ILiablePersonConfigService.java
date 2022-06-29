package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author:zhangzlz
 * @Date 2022/3/11   9:59
 */
@DWRestfulService
public interface ILiablePersonConfigService extends DWService {


    /**
     * 添加问题责任人配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/liable/person/question/latitude/config/info/create", method = {DWRequestMethod.POST})
    DWServiceResult addLiablePersonConfigInfo(String messageBody) throws Exception;

    /**
     * 删除问题责任人配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/liable/person/question/latitude/config/info/delete", method = {DWRequestMethod.POST})
    DWServiceResult deleteLiablePersonConfigInfo(String messageBody) throws Exception;

    /**
     * 修改问题责任人配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/liable/person/question/latitude/config/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateLiablePersonConfigInfo(String messageBody) throws Exception;

    /**
     * 查询问题分析配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/liable/person/question/latitude/config/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getLiablePersonConfigInfo(String messageBody) throws Exception;

}
