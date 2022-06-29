package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author:zhangzlz
 * @Date 2022/3/14   10:21
 */
public interface IESPLiablePersonConfigService extends DWService {
    /**
     * 添加问题责任人配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.liable.person.question.latitude.config.info.create")
    DWEAIResult postAddLiablePersonConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 删除问题责任人配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.liable.person.question.latitude.config.info.delete")
    DWEAIResult postDeleteLiablePersonConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;


    /**
     * 修改问题责任人配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.liable.person.question.latitude.config.info.update")
    DWEAIResult postUpdateLiablePersonConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * 获取问题责任人配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.liable.person.question.latitude.config.info.get")
    DWEAIResult getLiablePersonConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

}
