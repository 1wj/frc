package com.digiwin.app.frc.service.impl.athena.solutions;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.ParamConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.Question8DSolveEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.EightDQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.PendingQuestionModel;
import com.digiwin.app.frc.service.athena.solutions.service.ITest8DService;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategyFactory;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.ResultTool;
import com.digiwin.app.frc.service.athena.util.qdh.GeneratePendingUtil;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.ParamsUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: xieps
 * @Date: 2022/3/9 16:48
 * @Version 1.0
 * @Description
 */
public class Test8DService implements ITest8DService {



    @Autowired
    ActionTraceBiz actionTraceBiz;

    @Override
    public DWServiceResult postPendingQuestionInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        List<PendingQuestionModel> pendingQuestionModels = ParamsUtil.string2List(messageBody, PendingQuestionModel.class);
        // 参数校验
        ParamCheckUtil.checkParamsForAnnotation(Collections.singletonList(pendingQuestionModels));
        // 转entity
        List<QuestionActionTraceEntity> actionTraceEntities = pendingQuestionModels.stream().map(e ->{
            return  e.convertTo();
        }).collect(Collectors.toList());
        // 走不同策略
        QuestionHandlerStrategy handlerStrategy = QuestionHandlerStrategyFactory.getStrategy(actionTraceEntities.get(0).getQuestionSolveStep());
        if (StringUtils.isEmpty(actionTraceEntities.get(0).getReturnFlagId())) {
            // todo 多语系
            JSONObject object = handlerStrategy.generatePendingQuestion(actionTraceEntities);
            result.setData(object);
        }
        String returnFlagId = actionTraceEntities.get(0).getReturnFlagId();
        if(!StringUtils.isEmpty(returnFlagId) && returnFlagId.contains("R")){
            String returnChoice = returnFlagId.substring(0, returnFlagId.indexOf("R"));
            QuestionHandlerStrategy returnHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(returnChoice);
            // 走退回
            JSONObject object = returnHandlerStrategy.handleBack(actionTraceEntities);
            result.setData(object);
        }
        return result;

    }


    @Override
    public DWServiceResult postUpdateTeamBuildingInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.form_team.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
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
    public DWServiceResult getTeamBuildingInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(actionTraceBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult postUpdateContainmentMeasureInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.containment_measure.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
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
    public DWServiceResult getContainmentMeasureInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(actionTraceBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }



    @Override
    public DWServiceResult getContainmentMeasureExecuteInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(actionTraceBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult postUpdateContainmentMeasureExecuteInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.containment_measure_execute.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
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
    public DWServiceResult getContainmentMeasureVerifyInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(actionTraceBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult postUpdateContainmentMeasureVerifyInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.containment_measure_verify.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
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
    public DWServiceResult getQuestionKeyReasonRectifyInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(actionTraceBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult postUpdateQuestionReasonRectifyInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.key_reason_correct.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
            result.setData(result1);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
        }catch (Exception e){
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Autowired
    private EightDQuestionBiz eightDQuestionBiz;

    @Override
    public DWServiceResult getPersonPendingQuestionNumInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseDate(messageBody, false);
            List<JSONObject> resultInfo = eightDQuestionBiz.getPersonPendingQuestionNum(dataContent);
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(MultilingualismUtil.getLanguage("queryFail"));
        }
        return result;
    }


    @Override
    public DWServiceResult getQuestionRectifyExecuteInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(actionTraceBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult postUpdateRectifyExecuteInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.correct_execute.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
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
    public DWServiceResult postUpdateRectifyVerifyInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.correct_verify.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
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
    public DWServiceResult getQuestionPreventionMeasureInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(actionTraceBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult postUpdatePreventionMeasureInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.precaution.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
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
    public DWServiceResult postUpdatePreventionMeasureExcuteInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.precaution_execute.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
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
    public DWServiceResult getQuestionPreventionMeasureVerifyInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(actionTraceBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult updateQuestionPreventionMeasureVerifyInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.precaution_verify.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
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
    public DWServiceResult updateQuestionConfirmyInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.confirm.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
            result.setData(result1);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
        }catch (Exception e){
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 解析前端参数信息
     *
     * @param messageBody 消息体
     * @param check       是否进行校验
     * @return JSONArray
     */
    private JSONArray parseDate(String messageBody, boolean check) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray dataContent = (JSONArray) parameter.get("query_info");
        if (check) {
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return dataContent;
    }

    @Override
    public DWServiceResult getFeedBackPersonVerifyInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result.setData(actionTraceBiz.getQuestionDetail(questionId));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult postUpdateFeedBackPersonVerifyInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        JSONObject result1;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.feedback_person_verify.getCode());
            result1 = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
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

