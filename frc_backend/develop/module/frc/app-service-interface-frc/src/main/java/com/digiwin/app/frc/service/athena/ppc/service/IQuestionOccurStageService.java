package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author:zhangzlz
 * @Date 2022/2/11   10:07
 * @Description  发生问题阶段维护
 */
@DWRestfulService
public interface IQuestionOccurStageService extends DWService {

    /**
     * 添加问题发生阶段信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/occur/stage/info/create",method = {DWRequestMethod.POST})
    DWServiceResult addQuestionOccurStageInfo(String messageBody) throws Exception;

    /**
     * 删除问题发生阶段信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/occur/stage/info/delete",method = {DWRequestMethod.POST})
    DWServiceResult deleteQuestionOccurStageInfo(String messageBody) throws Exception;

    /**
     * 修改问题发生阶段信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/occur/stage/info/update",method = {DWRequestMethod.POST})
    DWServiceResult updateQuestionOccurStageInfo(String messageBody) throws Exception;

    /**
     * 查询问题发生阶段信息
     *
     * @param messageBody 消息体
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/question/occur/stage/info/get",method = {DWRequestMethod.POST})
    DWServiceResult getQuestionOccurStageInfo(String messageBody) throws Exception;
}
