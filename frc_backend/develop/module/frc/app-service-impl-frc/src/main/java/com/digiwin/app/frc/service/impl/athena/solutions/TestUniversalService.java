package com.digiwin.app.frc.service.impl.athena.solutions;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.common.Const.ParamConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUniversalSolveEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.UniversalQuestionBiz;
import com.digiwin.app.frc.service.athena.solutions.service.ITestUniversalService;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategyFactory;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.qdh.ParamsUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: xieps
 * @Date: 2022/4/6 15:21
 * @Version 1.0
 * @Description  迭代五 api - 通用解决方案
 */
public class TestUniversalService implements ITestUniversalService {

    @Autowired
    UniversalQuestionBiz universalQuestionBiz;

    @Override
    public DWServiceResult getPlanArrangeInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(universalQuestionBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult postUpdatePlanArrangeInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.plan_arrange.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParamsForUniversal(messageBody));
            result.setData(result1);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
        }catch (Exception e){
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult getTemporaryMeasureInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(universalQuestionBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult postUpdateTemporaryMeasureInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.temporary_measures.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParamsForUniversal(messageBody));
            result.setData(result1);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
        }catch (Exception e){
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;

    }

    @Override
    public DWServiceResult getTemporaryMeasureExecuteInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(universalQuestionBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult postUpdateTemporaryMeasureExecuteInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.temporary_measures_execute.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParamsForUniversal(messageBody));
            result.setData(result1);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
        }catch (Exception e){
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;

    }


    @Override
    public DWServiceResult getPermanentMeasureInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(universalQuestionBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult postUpdatePermanentMeasureInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.permanent_measures.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParamsForUniversal(messageBody));
            result.setData(result1);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
        }catch (Exception e){
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult getPermanentMeasureExecuteInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(universalQuestionBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult postUpdatePermanentMeasureExecuteInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.permanent_measures_execute.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParamsForUniversal(messageBody));
            result.setData(result1);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
        }catch (Exception e){
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }


    @Override
    public DWServiceResult getPermanentMeasureExecuteVerifyInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(universalQuestionBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult postUpdatePermanentMeasureExecuteVerifyInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.permanent_measures_execute_verify.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParamsForUniversal(messageBody));
            result.setData(result1);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
        }catch (Exception e){
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult getSolutionConfirmInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParamsForUniversal(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(universalQuestionBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult postUpdateSolutionConfirmInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(QuestionUniversalSolveEnum.process_confirmation.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParamsForUniversal(messageBody));
            result.setData(result1);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
        }catch (Exception e){
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

}
