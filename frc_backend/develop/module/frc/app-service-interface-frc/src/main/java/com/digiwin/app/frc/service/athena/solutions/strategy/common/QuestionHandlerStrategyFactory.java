package com.digiwin.app.frc.service.athena.solutions.strategy.common;

import com.digiwin.app.frc.service.athena.common.enums.qdh.Question8DSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUniversalSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.eightD.*;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.universal.*;


import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName QuestionHandlerStrategyFactory
 * @Description 解决方案策略
 * @Author HeX
 * @Date 2022/3/8 3:11
 * @Version 1.0
 **/
public class QuestionHandlerStrategyFactory {
    private static final Map<String, QuestionHandlerStrategy> map = new HashMap<>();

    static {
        // 8D解决方案
        init8DMap(map);
        // 通用解决方案
        initUniversalMap(map);
    }

    public static QuestionHandlerStrategy getStrategy(String processType) {
        return map.get(processType);
    }

    public static void init8DMap(Map<String, QuestionHandlerStrategy> map){
        // 问题反馈相关场景处理
        map.put(Question8DSolveEnum.form_team.getCode(), new QuestionFormTeam());
        map.put(Question8DSolveEnum.containment_measure.getCode(), new QuestionContainmentMeasure());
        map.put(Question8DSolveEnum.containment_measure_execute.getCode(), new QuestionContainmentMeasureExecute());
        map.put(Question8DSolveEnum.containment_measure_verify.getCode(), new QuestionContainmentMeasureVerify());
        map.put(Question8DSolveEnum.correct_execute.getCode(), new QuestionCorrectExecute());
        map.put(Question8DSolveEnum.key_reason_correct.getCode(), new QuestionKeyReasonCorrect());
        map.put(Question8DSolveEnum.correct_verify.getCode(), new QuestionRectifyVerify());
        map.put(Question8DSolveEnum.precaution.getCode(), new QuestionPrecaution());
        map.put(Question8DSolveEnum.precaution_verify.getCode(), new QuestionPreventionMeasureExecuteVerify());
        map.put(Question8DSolveEnum.precaution_execute.getCode(), new QuestionPreventionMeasureExecute());
        map.put(Question8DSolveEnum.confirm.getCode(),new QuestionConfirm());
        map.put(Question8DSolveEnum.feedback_person_verify.getCode(),new QuestionFeedBackPersonVerify());

        map.put(QuestionUpdateEnum.question_acceptance.getCode(),new QuestionSharedAcceptance());


    }

    public static void initUniversalMap(Map<String, QuestionHandlerStrategy> map){
        // 问题反馈相关场景处理
        map.put(QuestionUniversalSolveEnum.plan_arrange.getCode(), new QuestionPlanArrange());
        map.put(QuestionUniversalSolveEnum.temporary_measures.getCode(), new QuestionTemporaryMeasures());
        map.put(QuestionUniversalSolveEnum.temporary_measures_execute.getCode(), new QuestionTemporaryMeasuresExecute());
        map.put(QuestionUniversalSolveEnum.temporary_measures_execute_verify.getCode(), new QuestionTemporaryMeasuresExecuteVerify());
        map.put(QuestionUniversalSolveEnum.short_term_closing_acceptance.getCode(), new QuestionShortTermClosingAcceptance());
        map.put(QuestionUniversalSolveEnum.permanent_measures.getCode(), new QuestionPermanentMeasures());
        map.put(QuestionUniversalSolveEnum.permanent_measures_execute.getCode(), new QuestionPermanentMeasuresExecute());
        map.put(QuestionUniversalSolveEnum.permanent_measures_execute_verify.getCode(), new QuestionPermanentMeasuresExecuteVerify());
        map.put(QuestionUniversalSolveEnum.process_confirmation.getCode(), new QuestionProcessConfirmation());



    }
}
