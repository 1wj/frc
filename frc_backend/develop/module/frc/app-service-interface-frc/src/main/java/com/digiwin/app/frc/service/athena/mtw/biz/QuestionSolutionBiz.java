package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.util.OperationException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/16 15:26
 * @Version 1.0
 * @Description 解决方案处理Biz
 */
public interface QuestionSolutionBiz {

    /**
     * 添加解决方案信息
     *
     * @param solutionData 解析后数据
     * @return List<Map < String, Object>>
     * @throws IOException
     * @throws DWArgumentException
     */
    List<Map<String, Object>> addQuestionSolution(JSONArray solutionData) throws IOException, DWArgumentException, OperationException;

    /**
     * 删除解决方案信息
     *
     * @param dataContent 解析后数据
     */
    void deleteQuestionSolution(JSONArray dataContent);

    /**
     * 更新解决方案信息
     *
     * @param solutionData 解析后数据
     * @throws IOException
     * @throws DWArgumentException
     */
    void updateQuestionSolution(JSONArray solutionData) throws IOException, DWArgumentException, OperationException;

    /**
     * 获取解决方案信息
     *
     * @param solutionData 解析后数据
     * @return List<Map < String, Object>>
     * @throws JsonProcessingException
     */
    List<Map<String, Object>> getSolutionInfo(JSONArray solutionData) throws JsonProcessingException;
}
