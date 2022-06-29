package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2022/1/11 11:00
 * @Version 1.0
 * @Description  为Athena提供配置信息服务
 */
@DWRestfulService
public interface IQuestionConfigInfoService extends DWService {

    /**
     * 获取问题责任人配置信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/liable/person/config/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getQuestionLiablePersonConfigInfo(String messageBody) throws Exception;



}
