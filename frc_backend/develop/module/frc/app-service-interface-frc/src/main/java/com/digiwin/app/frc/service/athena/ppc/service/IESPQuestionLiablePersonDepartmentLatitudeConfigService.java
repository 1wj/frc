package com.digiwin.app.frc.service.athena.ppc.service;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.eai.EAIService;

import java.io.IOException;
import java.util.Map;

/**
*@ClassName: IESPQuestionLiablePersonDepartmentLatitudeConfigService
*@Description 问题责任人部门维度配置
*@Author Jiangyw
*@Date 2022/3/11
*@Time 9:25
*@Version 1.0
*/

public interface IESPQuestionLiablePersonDepartmentLatitudeConfigService extends DWService {

    /**
     * @Description 新增问题责任人部门维度配置
     * @param headers 请求头
     * @param messageBody 消息体
     * @return com.digiwin.app.service.DWEAIResult 返回值类型
     * @author Jiangyw
     * @Date 2022/3/11
     */
    @EAIService(id="question.liable.person.department.latitude.config.info.create")
    DWEAIResult postAddQuestionLiablePersonDepartmentLatitudeConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * @Description 删除问题责任人部门维度配置 
     * @param headers 请求头
     * @param messageBody 消息体
     * @return com.digiwin.app.service.DWEAIResult 返回值类型
     * @author Jiangyw
     * @Date 2022/3/11
     */
    @EAIService(id = "question.liable.person.department.latitude.config.info.delete")
    DWEAIResult postDeleteQuestionLiablePersonDepartmentLatitudeConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * @Description 更新问题责任人部门维度配置 
     * @param headers 请求头
     * @param messageBody 消息体
     * @return com.digiwin.app.service.DWEAIResult 返回值类型
     * @author Jiangyw
     * @Date 2022/3/11
     */
    @EAIService(id = "question.liable.person.department.latitude.config.info.update")
    DWEAIResult postUpdateQuestionLiablePersonDepartmentLatitudeConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

    /**
     * @Description 获取问题责任人部门维度配置
     * @param headers 请求头
     * @param messageBody 消息体
     * @return com.digiwin.app.service.DWEAIResult 返回值类型
     * @author Jiangyw
     * @Date 2022/3/11
     */
    @EAIService(id = "question.liable.person.department.latitude.config.info.get")
    DWEAIResult postGetQuestionLiablePersonDepartmentLatitudeConfigInfo(Map<String, Object> headers, String messageBody) throws Exception;

}
