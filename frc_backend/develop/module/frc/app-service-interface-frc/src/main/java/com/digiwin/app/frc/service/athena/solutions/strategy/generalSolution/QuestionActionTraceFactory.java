package com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution;

import com.digiwin.app.frc.service.athena.common.enums.qdh.Question8DSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.impl.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName QuestionTraceServiceFactory
 * @Description 解决方案策略
 * @Author HeXin
 * @Date 2021/11/11 22:44
 * @Version 1.0
 **/
@Component
public class QuestionActionTraceFactory {

    private static final Map<String, QuestionTraceStrategy> map = new HashMap<>();

    static {
        // 一般解决方案
        initCommonMap(map);
        // 8D解决方案

    }

    public static QuestionTraceStrategy getStrategy(String processType) {
        return map.get(processType);
    }

    /**
     * 初始化 一般解决方案策略
     * @param map
     */
    public static void initCommonMap(Map<String, QuestionTraceStrategy> map){
        // 问题反馈相关场景处理
        map.put(QuestionUpdateEnum.question_feedback.getCode(), new QuestionActionFeedback());
        // 问题识别处理
        map.put(QuestionUpdateEnum.question_identification.getCode(), new QuestionActionIdentification());
        // 问题识别审核
        map.put(QuestionUpdateEnum.question_identification_review.getCode(), new QuestionIdentificationReview());
        // 问题分配
        map.put(QuestionSolveEnum.question_distribution.getCode(), new QuestionDistribution());
        // 问题遏制
        map.put(QuestionSolveEnum.question_curb_distribution.getCode(), new QuestionCurbDistribution());
        // 遏制
        map.put(QuestionSolveEnum.question_curb.getCode(), new QuestionCurb());
        // 遏制审核
        map.put(QuestionSolveEnum.question_verify.getCode(), new QuestionCurbVerify());
        //问题关闭
        map.put(QuestionSolveEnum.question_close.getCode(), new QuestionClose());
        // 问题验收
        map.put(QuestionUpdateEnum.question_acceptance.getCode(), new QuestionAcceptance());
    }

    public static void init8DMap(Map<String, QuestionTraceStrategy> map){
        // 问题反馈相关场景处理
        map.put(Question8DSolveEnum.form_team.getCode(), new QuestionActionFeedback());
        map.put(QuestionUpdateEnum.question_feedback.getCode(), new QuestionActionFeedback());

    }


}
