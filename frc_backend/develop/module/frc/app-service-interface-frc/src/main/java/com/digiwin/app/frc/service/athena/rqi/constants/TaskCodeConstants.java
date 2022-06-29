package com.digiwin.app.frc.service.athena.rqi.constants;

import java.util.HashMap;
import java.util.Map;

/**
*@ClassName: TaskCodeConstants
*@Description 追踪报表任务编号常量类
*@Author Jiangyw
*@Date 2022/5/25
*@Time 17:06
*@Version 迭代6
*/
public class TaskCodeConstants {
    public static Map<String,String> TASK_CODE_MAP= new HashMap<String, String>(){{
        put("QF","questionFeedbackReview");
        put("QIA","questionIdentify");
        put("QIR","questionIdentifyReview");
        put("QA","questionAcceptance");
        //QS另做处理
        //8D解决方案
        put("SE001001","questionDescriptionTeamFormation");
        put("SE001002","questionContainmentMeasures");
        put("SE001003","questionImplementationOfContainmentMeasures");
        put("SE001004","questionValidationOfContainmentMeasures");
        put("SE001007","questionRootCauseAnalysisAndCorrective_Action");
        put("SE001008","questionCorrective_Action_Implementation");
        put("SE001009","questionValidationOfCorrectiveAction");
        put("SE001010","questionPrecaution");
        put("SE001011","questionPrecautionaryMeasuresToDealWith");
        put("SE001012","questionPrecautionsHandlingVerification");
        put("SE001013","questionProcessConfirmation");
        put("SE001014","FeedBackPersonVerify");
        //一般解决方案
        put("SE002001","questionDeliver");
        put("SE002002","questionDeliverSolve");
        put("SE002003","questionSolve");
        put("SE002004","questionSolveReview");
        put("SE002005","questionClose");
        //通用解决方案
        put("SE003001","UniversalPlanManagement");
        put("SE003002","UniversalTemporaryMeasures");
        put("SE003003","UniversalTemporaryMeasuresExecute");
        put("SE003004","UniversalTemporaryMeasuresVerify");
        put("SE003005","UniversalShortTermAcceptance");
        put("SE003006","UniversalPermanentMeasures");
        put("SE003007","UniversalPermanentMeasuresExecute");
        put("SE003008","UniversalPermanentMeasuresVerify");
        put("SE003009","UniversalProcessConfirmation");
    }};
}
