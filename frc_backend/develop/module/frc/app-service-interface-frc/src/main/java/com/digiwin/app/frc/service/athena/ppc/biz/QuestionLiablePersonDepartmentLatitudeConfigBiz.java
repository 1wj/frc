package com.digiwin.app.frc.service.athena.ppc.biz;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
*@Author Jiangyw
*@Date 2022/3/14
*@Time 9:50
*@Version
*/
public interface QuestionLiablePersonDepartmentLatitudeConfigBiz {

    /**
     * 新增问题责任人部门纬度配置
     * @param dataContent 参数数据
     * @return JSON对象数组
     * @throws DWArgumentException
     * @throws IOException
     */
    List<JSONObject> addQuestionLiablePersonDepartmentLatitudeConfig(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     * 删除问题责任人部门纬度配置
     * @param dataContent 参数数据
     * @return 成功/失败
     */
    Boolean deleteQuestionLiablePersonDepartmentLatitudeConfig(JSONArray dataContent);

    /**
     * 更新问题责任人部门纬度配置
     * @param dataContent 参数数据
     * @return 成功/失败
     * @throws DWArgumentException
     * @throws IOException
     */
    Boolean updateQuestionLiablePersonDepartmentLatitudeConfig(JSONArray dataContent) throws DWArgumentException, IOException;

    /**
     *  获取问题责任人部门纬度配置
     * @param dataContent 参数数据
     * @return JSON对象数组
     * @throws DWArgumentException
     * @throws JsonProcessingException
     */
    List<JSONObject> getQuestionLiablePersonDepartmentLatitudeConfig(JSONArray dataContent) throws DWArgumentException, JsonProcessingException;

    /**
     * 获取问题责任人信息
     * @param attributionNo 问题归属
     * @param riskLevelOid 风险等级
     * @param feedbackDepartmentOid 部门主键
     * @param sourceOid 问题来源主键
     * @param classificationOid 问题分类主键
     * @return Map key-value  "liablePersonId"-问题责任人id "liablePersonName"-问题责任人name
     */
    Map<String, Object> getLiablePersonMessage(String attributionNo, String riskLevelOid, String feedbackDepartmentOid, String sourceOid, String classificationOid,String solutionOid);
}
