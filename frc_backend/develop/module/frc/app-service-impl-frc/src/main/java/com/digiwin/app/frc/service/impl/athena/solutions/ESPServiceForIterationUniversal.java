package com.digiwin.app.frc.service.impl.athena.solutions;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.ParamConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.Question8DSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUniversalSolveEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.UniversalQuestionBiz;
import com.digiwin.app.frc.service.athena.solutions.service.IESPServiceForIterationUniversal;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategyFactory;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.qdh.ParamsUtil;
import com.digiwin.app.service.DWEAIResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/4/6 15:20
 * @Version 1.0
 * @Description  迭代五 api - 通用解决方案
 */
public class ESPServiceForIterationUniversal implements IESPServiceForIterationUniversal {


    @Autowired
    UniversalQuestionBiz universalQuestionBiz;


    @Override
    public DWEAIResult getPlanArrangeInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result = universalQuestionBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult postPlanArrangeInfoUpdate(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.plan_arrange.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParamsForUniversal(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult getTemporaryMeasureInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result = universalQuestionBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult postTemporaryMeasureInfoUpdate(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.temporary_measures.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateObjectParams(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult getTemporaryMeasureexEcuteInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result = universalQuestionBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult postTemporaryMeasureexEcuteInfoUpdate(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.temporary_measures_execute.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateObjectParams(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult postTemporaryMeasureexEcuteVerifyInfoUpdate(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.temporary_measures_execute_verify.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateObjectParams(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult getTemporaryMeasureexEcuteVerifyInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result = universalQuestionBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult postSolutionShortTermCloseVerifyInfoUpdate(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.short_term_closing_acceptance.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateObjectParams(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult getSolutionShortTermCloseVerifyInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result = universalQuestionBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult getPermanentMeasureInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result = universalQuestionBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult updatePermanentMeasureInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.permanent_measures.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParamsForUniversal(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }


    @Override
    public DWEAIResult getPermanentMeasureExecuteInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result = universalQuestionBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult updatePermanentMeasureExecuteInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.permanent_measures_execute.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParamsForUniversal(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult getPermanentMeasureExecuteVerifyInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result = universalQuestionBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult updatePermanentMeasureExecuteVerifyInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.permanent_measures_execute_verify.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParamsForUniversal(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }


    @Override
    public DWEAIResult getSolutionConfirmInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result = universalQuestionBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult updateSolutionConfirmInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.process_confirmation.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParamsForUniversal(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }



}
