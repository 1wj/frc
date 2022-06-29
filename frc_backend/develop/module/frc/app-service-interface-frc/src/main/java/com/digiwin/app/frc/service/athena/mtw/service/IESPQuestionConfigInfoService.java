package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/1/11 11:27
 * @Version 1.0
 * @Description 为Athena提供问题配置信息服务
 */
public interface IESPQuestionConfigInfoService extends DWService {

    /**
     * 获取问题责任人配置信息
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult
     * @throws Exception
     */
    @EAIService(id = "question.liable.person.config.info.get")
    DWEAIResult getQuestionLiablePersonConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;



}
