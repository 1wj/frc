package com.digiwin.app.frc.service.athena.app.service;

import com.digiwin.app.service.DWFile;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: jiangzhou
 * @Date: 2022/05/24
 * @Version 1.0
 * @Description 为App提供任务卡相关接口
 */
@DWRestfulService
public interface IQuestionTaskService extends DWService {
    /**
     * 任务卡列表
     * @param messageBody
     * @return
     * @throws Exception
     */
    @DWRequestMapping(path = "/app/question/card/list", method = {DWRequestMethod.POST})
    DWServiceResult questionCardList(String messageBody) throws Exception;
    /**
     * 任务卡详情
     * @param messageBody
     * @return
     * @throws Exception
     */
    @DWRequestMapping(path = "/app/question/card/detail/info", method = {DWRequestMethod.POST})
    DWServiceResult questionCardDetailInfo(String messageBody) throws Exception;
}
