package com.digiwin.app.frc.service.athena.solutions.strategy.common;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;

import java.util.List;

/**
 * @ClassName QuestionHandlerStrategy
 * @Description 任务卡策略工厂-一般解决方案，仅针对>=迭代四，进去common，迭代一 至 迭代三 至generalSolution文件夹
 * @Author HeX
 * @Date 2022/3/8 3:07
 * @Version 1.0
 **/
public interface QuestionHandlerStrategy {
    /**
     * 更新问题处理追踪数据
     * @param parameters athena传入需更新参数
     * @return response返参
     */
    JSONObject updateQuestion(String parameters) throws Exception;

    /**
     * 生成待处理问题信息(即athena推卡;生成下一关待办任务卡)
     * @param actionTraceEntityList 待审核 athena传入的转换后的参数
     * @return response返参
     * @throws Exception 异常处理
     */
    JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception;

    /**
     * 处理退回数据
     * @param actionTraceEntityList athena传入的转换后的参数
     * @return response返参
     * @throws Exception 异常处理
     */
    JSONObject handleBack(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception;


}
