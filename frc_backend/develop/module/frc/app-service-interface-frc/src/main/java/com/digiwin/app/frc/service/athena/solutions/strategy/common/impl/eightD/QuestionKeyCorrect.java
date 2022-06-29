package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.eightD;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;

import java.util.List;

/**
 * @ClassName QuestionKeyCorrect
 * @Description 根本原因&纠正措施 逻辑处理
 * @Author HeX
 * @Date 2022/3/8 11:10
 * @Version 1.0
 **/
public class QuestionKeyCorrect  implements QuestionHandlerStrategy {
    @Override
    public JSONObject updateQuestion(String parameters) {
        return null;
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) {
        return null;
    }

    @Override
    public JSONObject handleBack(List<QuestionActionTraceEntity> actionTraceEntityList)  {
        return null;
    }
}
