package com.digiwin.app.frc.service.athena.mtw.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2022/1/10 13:01
 * @Version 1.0
 * @Description 看板防呆校验查询
 */
@DWRestfulService
public interface IKanbanFoolproofService extends DWService {


    /**
     * 看板防呆校验
     *
     * @param messageBody 消息体
     * @return DWServiceResult
     * @throws Exception
     */
    @DWRequestMapping(path = "/wait/delete/kanban/solution/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getKanbanFoolproofInfo(String messageBody) throws Exception;



}
