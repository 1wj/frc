package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/1/10 17:10
 * @Version 1.0
 * @Description 看板防呆校验查询
 */
public interface IESPKanbanFoolproofService extends DWService {

    /**
     * 看板防呆校验
     *
     * @param headers     请求头
     * @param messageBody 消息体
     * @return DWEAIResult 返回值类型
     * @throws Exception
     */
    @EAIService(id = "wait.delete.kanban.solution.info.get")
    DWEAIResult getKanbanFoolproofInfo(Map<String, Object> headers, String messageBody) throws Exception;


}
