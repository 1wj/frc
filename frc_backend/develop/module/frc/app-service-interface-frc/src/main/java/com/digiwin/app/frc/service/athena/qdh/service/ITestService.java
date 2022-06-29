package com.digiwin.app.frc.service.athena.qdh.service;


import com.digiwin.app.service.AllowAnonymous;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

/**
 * @ClassName ITestService
 * @Description 问题快反-DTD交互相关暴露服务
 * @Author author
 * @Date 2021/11/10 17:41
 * @Version 1.0
 **/
@DWRestfulService
public interface ITestService extends DWService {

    /**
     * 入库-测试
     * @param messageBody
     * @return
     * @throws IOException
     */
    @DWRequestMapping(path = "/question/to/kmo", method = {DWRequestMethod.POST})
    DWServiceResult toKMO(String messageBody) throws IOException;

    /**
     * 入库-测试
     * @param messageBody
     * @return
     * @throws IOException
     */
    @DWRequestMapping(path = "/question/to/kmo/test", method = {DWRequestMethod.POST})
    @AllowAnonymous
    DWServiceResult toKMOTest(String messageBody) throws IOException;

    /**
     * 问题发起测试
     * @param messageBody
     * @return
     * @throws IOException
     */
    @DWRequestMapping(path = "/question/init/create", method = {DWRequestMethod.POST})
    DWServiceResult initQuestion(String messageBody) throws IOException;

    /**
     * 获取问题反馈详情信息
     * @param messageBody 请求参数(问题处理追踪主键)
     * @return DWServiceResult 平台统一response
     */
    @DWRequestMapping(path = "/feedback/detail/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getFeedBackDetail(String messageBody);

    /**
     * 更新问题反馈信息
     * @param messageBody 需更新的问题追踪数据 问题关键信息+问题表单详情
     * @return DWServiceResult 平台统一response
     */
    @DWRequestMapping(path = "/question/feedback/info/update", method = {DWRequestMethod.POST})
    DWServiceResult UpdateQuestionFeedback(String messageBody);

    /**
     * 更新问题识别信息
     * @param messageBody 需更新的问题追踪数据 问题关键信息+问题表单详情
     * @return DWServiceResult 平台统一response
     */
    @DWRequestMapping(path = "/question/identify/info/update", method = {DWRequestMethod.POST})
    DWServiceResult UpdateQuestionIdentify(String messageBody);

    /**
     * 更新问题分配信息
     * @param messageBody 需更新的问题追踪数据 问题关键信息+问题表单详情
     * @return DWServiceResult 平台统一response
     */
    @DWRequestMapping(path = "/question/distribution/info/update", method = {DWRequestMethod.POST})
    DWServiceResult UpdateQuestionDistribution(String messageBody);

    /**
     * 更新问题遏制信息-遏制分配
     * @param messageBody 需更新的问题追踪数据 问题关键信息+问题表单详情
     * @return DWServiceResult 平台统一response
     */
    @DWRequestMapping(path = "/question/curb/distribution/info/update", method = {DWRequestMethod.POST})
    DWServiceResult UpdateQuestionCurbDistribution(String messageBody);

    /**
     * 更新问题遏制
     * @param messageBody 需更新的问题追踪数据 问题关键信息+问题表单详情
     * @return DWServiceResult 平台统一response
     */
    @DWRequestMapping(path = "/question/curb/info/update", method = {DWRequestMethod.POST})
    DWServiceResult UpdateQuestionCurb(String messageBody);

    /**
     * 更新问题遏制审核
     * @param messageBody 需更新的问题追踪数据 问题关键信息+问题表单详情
     * @return DWServiceResult 平台统一response
     */
    @DWRequestMapping(path = "/question/curb/verify/info/update", method = {DWRequestMethod.POST})
    DWServiceResult UpdateQuestionCurbVerify(String messageBody);

    /**
     * 更新问题关闭
     * @param messageBody 需更新的问题追踪数据 问题关键信息+问题表单详情
     * @return DWServiceResult 平台统一response
     */
    @DWRequestMapping(path = "/question/close/info/update", method = {DWRequestMethod.POST})
    DWServiceResult UpdateQuestionClose(String messageBody);

    /**
     * 更新问题验收
     * @param messageBody 需更新的问题追踪数据 问题关键信息+问题表单详情
     * @return DWServiceResult 平台统一response
     */
    @DWRequestMapping(path = "/question/acceptance/info/update", method = {DWRequestMethod.POST})
    DWServiceResult UpdateQuestionAcceptance(String messageBody);

    /**
     * 新增待审核问题信息
     * @param messageBody  需新增的问题追踪数据 问题关键信息 问题记录主键、问题处理步骤、问题解决步骤、负责人信息..
     * @return DWServiceResult 平台统一response
     */
    @DWRequestMapping(path = "/pending/approve/question/info/create", method = {DWRequestMethod.POST})
    DWServiceResult createUnapprovedQuestionData(String messageBody);

    /**
     * 新增待审核问题信息
     * @param messageBody  需新增的问题追踪数据 问题关键信息 问题记录主键、问题处理步骤、问题解决步骤、负责人信息..
     * @return DWServiceResult 平台统一response
     */
    @DWRequestMapping(path = "/pending/approve/curb/info/create", method = {DWRequestMethod.POST})
    DWServiceResult createUnapprovedQuestionCurbData(String messageBody);




    /**
     * 更新问题记录信息
     * @param messageBody 需传入的更新问题记录信息
     * @return DWServiceResult 平台统一response
     */
    @DWRequestMapping(path = "/question/record/info/update", method = {DWRequestMethod.POST})
    DWServiceResult updateRecord(String messageBody);

    /**
     * 新增问题记录信息
     * @param messageBody
     * @return
     */
    @DWRequestMapping(path = "/question/record/info/create", method = {DWRequestMethod.POST})
    DWServiceResult createRecord(String messageBody);

    /**
     * 获取问题记录信息
     * @param messageBody
     * @return
     */
    @DWRequestMapping(path = "/question/record/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getRecord(String messageBody);




    @DWRequestMapping(path = "/feedback/detail/info/get/test", method = {DWRequestMethod.POST})
    DWServiceResult getFeedBackDetailTest(String messageBody);

    /**
     * 获取问题识别详情信息
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @DWRequestMapping(path = "/question/identify/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getIdentifyDetail(String messageBody) throws Exception;

    /**
     * 获取问题分配详情信息
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @DWRequestMapping(path = "/question/distribution/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getDistributionDetail(String messageBody) throws Exception;

    /**
     * 获取问题解决方案
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @DWRequestMapping(path = "/solution/step/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getSolutionStep(String messageBody) throws Exception;

    /**
     * 获取遏制分配详情信息
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @DWRequestMapping(path = "/curb/distribution/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getCurbDistributionDetail(String messageBody) throws Exception;

    /**
     * 获取遏制详情信息
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @DWRequestMapping(path = "/curb/detail/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getCurbDetail( String messageBody) throws Exception;

    /**
     * 获取遏制审核详情信息
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @DWRequestMapping(path = "/curb/verify/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getCurbVerifyDetail(String messageBody) throws Exception;

    /**
     * 获取问题关闭详情信息
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @DWRequestMapping(path = "/question/close/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getCloseDetail(String messageBody) throws Exception;

    /**
     * 获取问题验收详情信息
     * @param messageBody athena传入标准入参格式
     * @return DWEAIResult EAI标准response
     * @throws Exception 异常处理
     */
    @DWRequestMapping(path = "/question/acceptance/detail/info/get", method = {DWRequestMethod.POST})
    DWServiceResult getAcceptanceDetail(String messageBody) throws Exception;


    /**
     * 获取部门
     * @return
     * @throws Exception
     */
    @DWRequestMapping(path = "/eoc/departments", method = {DWRequestMethod.GET})
    DWServiceResult getDeparts() throws Exception;

    /**
     * 获取人员
     * @return
     * @throws Exception
     */
    @DWRequestMapping(path = "/eoc/users", method = {DWRequestMethod.GET})
    DWServiceResult getUsers() throws Exception;





}
