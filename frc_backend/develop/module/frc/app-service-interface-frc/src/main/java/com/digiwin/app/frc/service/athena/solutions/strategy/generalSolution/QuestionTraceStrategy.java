package com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;

/**
 * @ClassName ActionTraceStrategy
 * @Description 任务卡策略工厂-一般解决方案，仅针对于迭代一 至 迭代三，>=迭代四，进去common
 * @Author HeXin
 * @Date 2021/11/11 22:40
 * @Version 1.0
 **/
public interface QuestionTraceStrategy {
    /**
     * 更新问题处理追踪数据
     * @param parameters athena传入需更新参数
     */
    JSONObject updateQuestionTrace(String parameters) throws Exception;

    /**
     * 生成待审核问题处理追踪数据
     * @param actionTraceEntity 待审核 athena传入的转换后参数
     * @throws Exception 异常处理
     */
    JSONArray insertUnapprovedQuestionTrace(QuestionActionTraceEntity actionTraceEntity) throws Exception;

}
