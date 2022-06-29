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
import com.digiwin.app.frc.service.athena.solutions.service.IESPServiceForIteration8D;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategyFactory;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.ResultTool;
import com.digiwin.app.frc.service.athena.util.qdh.GeneratePendingUtil;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.ParamsUtil;
import com.digiwin.app.service.DWEAIResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName ESPServiceForIteration8D
 * @Description 迭代四 api - 8D
 * @Author HeX
 * @Date 2022/3/8 2:51
 * @Version 1.0
 **/
public class ESPServiceForIteration8D implements IESPServiceForIteration8D {

    Logger logger = LoggerFactory.getLogger(ESPServiceForIteration8D.class);


    @Autowired
    ActionTraceBiz actionTraceBiz;

    @Override
    public DWEAIResult postApprovedQuestion(Map<String, String> headers, String messageBody) throws Exception {
        List<PendingQuestionModel> pendingQuestionModels = ParamsUtil.string2List(messageBody, PendingQuestionModel.class);
        // 参数校验
        ParamCheckUtil.checkParamsForAnnotation(Collections.singletonList(pendingQuestionModels));
        // 转entity
        List<QuestionActionTraceEntity> actionTraceEntities = pendingQuestionModels.stream().map(e ->{
            QuestionActionTraceEntity entity = e.convertTo();
            return entity;
        }).collect(Collectors.toList());

        //走退回逻辑
        String returnFlagId = actionTraceEntities.get(0).getReturnFlagId();
        if(!StringUtils.isEmpty(returnFlagId) && returnFlagId.contains("R")){
            String returnChoice = returnFlagId.substring(0, returnFlagId.indexOf("R"));
            QuestionHandlerStrategy returnHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(returnChoice);
            // 走退回
            return ResultTool.success("",returnHandlerStrategy.handleBack(actionTraceEntities));
        }

        // 走不同策略 生成相应的待审核任务卡
        QuestionHandlerStrategy handlerStrategy = QuestionHandlerStrategyFactory.getStrategy(actionTraceEntities.get(0).getQuestionSolveStep());
        return ResultTool.success("",handlerStrategy.generatePendingQuestion(actionTraceEntities));
    }

    @Override
    public DWEAIResult postQuestionTeamBuildUpdate(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.form_team.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult getQuestionTeamBuildInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult getQuestionContainmentMeasureInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult updateContainmentMeasureInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.containment_measure.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }


    @Override
    public DWEAIResult getQuestionContainmentMeasureExecuteInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }


    @Override
    public DWEAIResult updateContainmentMeasureExecuteInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.containment_measure_execute.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult getQuestionContainmentMeasureExecuteVerifyInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }


    @Override
    public DWEAIResult updateQuestionContainmentMeasureExecuteVerifyInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.containment_measure_verify.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult getQuestionKeyReasonRectifyInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult updateQuestionKeyReasonRectifyInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.key_reason_correct.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Autowired
    private EightDQuestionBiz eightDQuestionBiz;

    @Override
    public DWEAIResult getPersonPendingQuestionNumInfo(Map<String, String> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            // 获取请求参数
            JSONArray dataContent = parseDate(messageBody, false);
            List<JSONObject> resultInfo = eightDQuestionBiz.getPersonPendingQuestionNum(dataContent);
            dataResult.put("return_data",resultInfo);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),dataResult);

    }


    @Override
    public DWEAIResult getQuestionRectifyExecuteInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult updateQuestionRectifyExecuteInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.correct_execute.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult updateQuestionRectifyVerifyInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.correct_verify.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }


    @Override
    public DWEAIResult getQuestionRectifyVerifyInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }


    @Override
    public DWEAIResult getQuestionPreventionMeasureInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult updateQuestionPreventionMeasureInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.precaution.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult getQuestionPreventionMeasureExecuteInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult updateQuestionPreventionMeasureExecuteInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.precaution_execute.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult getQuestionPreventionMeasureVerifyInfo(Map<String, String> headers, String messageBody) throws Exception {
        DWEAIResult dweaiResult;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            JSONObject result = actionTraceBiz.getQuestionDetail(questionId);
            dweaiResult = ResultTool.success(MultilingualismUtil.getLanguage("selectSuccess"),result);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return dweaiResult;
    }

    @Override
    public DWEAIResult updateQuestionPreventionMeasureVerifyInfo(Map<String, String> headers, String messageBody) throws Exception {
        DWEAIResult dweaiResult;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.precaution_verify.getCode());
            JSONObject result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
            dweaiResult = ResultTool.success("update Success!",result);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return dweaiResult;
    }

    @Override
    public DWEAIResult getQuestionConfirmInfo(Map<String, String> headers, String messageBody) throws Exception {
        DWEAIResult dweaiResult;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            JSONObject result = actionTraceBiz.getQuestionDetail(questionId);
            dweaiResult = ResultTool.success(MultilingualismUtil.getLanguage("selectSuccess"),result);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return dweaiResult;
    }

    @Override
    public DWEAIResult updateQuestionConfirmInfo(Map<String, String> headers, String messageBody){
        DWEAIResult dweaiResult;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.confirm.getCode());
            JSONObject result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
            dweaiResult = ResultTool.success("update Success!",result);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return dweaiResult;
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
    public DWEAIResult getFeedBackPersonVerifyInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 获取请求参数
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("selectSuccess"),result);
    }


    @Override
    public DWEAIResult updateFeedBackPersonVerifyInfo(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionHandlerStrategy questionHandlerStrategy = QuestionHandlerStrategyFactory.getStrategy(Question8DSolveEnum.feedback_person_verify.getCode());
            result = questionHandlerStrategy.updateQuestion(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }



}
