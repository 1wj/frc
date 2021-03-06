package com.digiwin.app.frc.service.impl.athena.qdh;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.ParamConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.DataChangeBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.InitQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.RecordBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionRecordEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.QuestionRecordInfoModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.QuestionRecordModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.UnApprovedCurbModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.UnapprovedModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.init.AttachmentModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.init.BasicModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.init.DetailModel;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.service.IESPService;
import com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.QuestionActionTraceFactory;
import com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.QuestionTraceStrategy;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.ResultTool;
import com.digiwin.app.frc.service.athena.util.TransferTool;
import com.digiwin.app.frc.service.athena.util.qdh.GeneratePendingUtil;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.ParamsUtil;
import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName ESPService
 * @Description ESPService
 * @Author author
 * @Date 2021/11/10 16:13
 * @Version 1.0
 **/
public class ESPService implements IESPService {

    Logger logger = LoggerFactory.getLogger(ESPService.class);

    @Autowired
    ActionTraceBiz actionTraceBiz;

    @Autowired
    DataChangeBiz dataChange;

    @Autowired
    RecordBiz recordBiz;

    @Autowired
    ActionTraceMapper actionTraceMapper;

    @Autowired
    InitQuestionBiz initQuestionBiz;

    @Override
    public DWEAIResult dataChange(Map<String, String> headers, String messageBody){
        logger.info("-----------data.change.get---------");
        Map<String, Object> map = JSONObject.parseObject(messageBody);
        logger.info("?????????{}", map);
        Map<String, Object> result;
        try {
            // ?????????????????????
            result = dataChange.dataChange(headers, map);
        } catch (Exception e) {
            throw new DWRuntimeException(e.getMessage());
        }
        logger.info("?????????{}", result);
        //????????????
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("storeSuccess"), result);
    }

    @Override
    public DWEAIResult initQuestion(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // ??????????????????
            String param = ParamsUtil.getInitParam(messageBody);
            // ?????????model
            BasicModel basicModel = TransferTool.convertString2Model(param,"question_basic_info",true,BasicModel.class);
            DetailModel detailModel = TransferTool.convertString2Model(param,"question_detail_info",true, DetailModel.class);
            List<AttachmentModel> attachmentModels = TransferTool.convertString2List(param,"attachment_info", AttachmentModel.class);
            JSONObject dataInstance = new JSONObject();
            JSONArray info = new JSONArray();
            info.add(JSONObject.parse(param));
            dataInstance.put("question_result",info);
            String dataContent = dataInstance.toJSONString();
            result = initQuestionBiz.initQuestionMessage(basicModel,detailModel,attachmentModels,dataContent.replaceAll("\\\\",""));
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0",MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult getFeedBackDetail(Map<String, String> headers, String messageBody){
        JSONObject result;
        try {
            // ??????????????????
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0",MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult postApprovedQuestion(Map<String, String> headers, String messageBody){
        JSONObject response;
        try {
            // string???????????????????????????
            UnapprovedModel unapprovedModel = TransferTool.convertString2Model(ParamsUtil.getDetailParams(messageBody).toJSONString(),UnapprovedModel.class);
            QuestionActionTraceEntity actionTraceEntity = new QuestionActionTraceEntity();
            BeanUtils.copyProperties(unapprovedModel,actionTraceEntity);
            // ??????????????????????????????????????????????????????
            if (!StringUtils.isEmpty(unapprovedModel.getExpectCompleteDate())) {
                actionTraceEntity.setExpectCompleteDate(new SimpleDateFormat("yyyy-MM-dd").parse(unapprovedModel.getExpectCompleteDate()));
            }
            // ????????????
            if (!StringUtils.isEmpty(unapprovedModel.getReturnFlagId())) {
                JSONArray responseParam = actionTraceBiz.insertReturnBackDetail(actionTraceEntity);
                response = new JSONObject();
                response.put("return_data",responseParam);
                return ResultTool.success(MultilingualismUtil.getLanguage("actionSuccess"),response);
            }
            // ?????????????????????
            QuestionTraceStrategy questionTraceStrategy;
            if (!StringUtils.isEmpty(unapprovedModel.getQuestionSolveStep())) {
                // ???????????????
                questionTraceStrategy = QuestionActionTraceFactory.getStrategy(unapprovedModel.getQuestionSolveStep());
                ParamCheckUtil.checkApprovedQuestionCurbParams(unapprovedModel);
            }else {
                ParamCheckUtil.checkApprovedQuestionParams(unapprovedModel);
                questionTraceStrategy = QuestionActionTraceFactory.getStrategy(unapprovedModel.getQuestionProcessStep());
            }
            JSONArray responseParam = questionTraceStrategy.insertUnapprovedQuestionTrace(actionTraceEntity);
            response = new JSONObject();
            response.put("return_data",responseParam);
            return ResultTool.success(MultilingualismUtil.getLanguage("actionSuccess"),response);
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
    }

    @Override
    public DWEAIResult postApprovedCurbQuestion(Map<String, String> headers, String messageBody) throws Exception {
        DWEAIResult dweaiResult = new DWEAIResult();
        JSONObject response;
        try {
            // ??????????????????
            JSONObject parameter = ParamsUtil.getAthenaParameter(messageBody);
            // ?????? M??? ??? question_info
            JSONArray questionInfo = parameter.getJSONArray(ParamConst.QUESTION_INFO);
            List<QuestionActionTraceEntity> list = new ArrayList<>();
            for (Iterator iterator = questionInfo.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                // string???????????????????????????
                UnApprovedCurbModel unapprovedModel = TransferTool.convertString2Model(obj.toJSONString(),UnApprovedCurbModel.class);
                QuestionActionTraceEntity actionTraceEntity = new QuestionActionTraceEntity();
                BeanUtils.copyProperties(unapprovedModel,actionTraceEntity);
                // ???????????????????????????
                actionTraceEntity.setOid(unapprovedModel.getQuestionId());
                // ??????????????????????????????????????????????????????
                if (!StringUtils.isEmpty(unapprovedModel.getExpectCompleteDate())) {
                    actionTraceEntity.setExpectCompleteDate(new SimpleDateFormat("yyyy-MM-dd").parse(unapprovedModel.getExpectCompleteDate()));
                }
                list.add(actionTraceEntity);
            }

            // ???return_flag_id?????????????????????????????????
            if (StringUtils.isEmpty(list.get(0).getReturnFlagId())) {
                // ????????????+????????????????????? ????????????????????? ??????
                QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(list.get(0).getQuestionSolveStep());
                JSONArray responseParam = questionTraceStrategy.insertUnapprovedQuestionTrace(list.get(0));
                response = new JSONObject();
                response.put("return_data",responseParam);
            }else {
                // ??????????????????
                JSONArray responseParam = actionTraceBiz.insertUnapprovedCurbQuestionTrace(list);
                response = new JSONObject();
                response.put("return_data",responseParam);
            }
            dweaiResult = new DWEAIResult("0", "0",MultilingualismUtil.getLanguage("actionSuccess"),response);
        }catch (Exception e){
            e.printStackTrace();
            Map map = new HashMap();
            map.put("error",e.toString());
            dweaiResult = new DWEAIResult("-1", "0",e.getMessage(),map);
        }finally {
            return dweaiResult;
        }

    }

    @Override
    public DWEAIResult postQuestionRecord(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject jsonObject = new JSONObject();
        try {
           // ???????????????????????????
           String paramData = ParamsUtil.getDetailParams(messageBody).toJSONString();
           // string???????????????????????????
           QuestionRecordModel recordModel = new ObjectMapper().readValue(paramData.getBytes(), QuestionRecordModel.class);
           ParamCheckUtil.checkQuestionRecordCreateParams(recordModel);
           QuestionRecordEntity recordEntity = new QuestionRecordEntity();
           BeanUtils.copyProperties(recordModel,recordEntity);
           jsonObject = recordBiz.insertRecord(recordEntity);
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0",MultilingualismUtil.getLanguage("actionSuccess"),jsonObject);
    }

    @Override
    public DWEAIResult postQuestionRecordUpdate(Map<String, String> headers, String messageBody) throws Exception {
        try {
            // ???????????????????????????
            String paramData = ParamsUtil.getDetailParams(messageBody).toJSONString();
            // string???????????????????????????
            QuestionRecordModel recordModel = new ObjectMapper().readValue(paramData.getBytes(), QuestionRecordModel.class);
            QuestionRecordEntity recordEntity = new QuestionRecordEntity();
            BeanUtils.copyProperties(recordModel,recordEntity);
            recordEntity.setTenantsid((Long) DWServiceContext.getContext().getProfile().get("tenantSid"));
            recordBiz.updateRecord(recordEntity);
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0",MultilingualismUtil.getLanguage("actionSuccess"),null);
    }

    @Override
    public DWEAIResult getRecord(String messageBody) {
        JSONObject jsonObject = new JSONObject();
        try {
            List<QuestionRecordInfoModel> recordInfoModelList = GeneratePendingUtil.string2List(messageBody, QuestionRecordInfoModel.class);
            // ????????????
            ParamCheckUtil.checkParamsForAnnotation(Collections.singletonList(recordInfoModelList));
            List jsonArray = recordBiz.getRecordNew(recordInfoModelList);
            jsonObject.put("question_info",jsonArray);
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0",MultilingualismUtil.getLanguage("actionSuccess"),jsonObject);
    }


    @Override
    public DWEAIResult getIdentifyDetail(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // ??????????????????
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0",MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult getSolutionStep(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // ??????????????????
            String solutionOid = (String) ParamsUtil.getSolutionParams(messageBody).get("solution_id");
            result = actionTraceBiz.getSolutionStep(solutionOid);
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0",MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult getDistributionDetail(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // ??????????????????
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0",MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult getCurbDistributionDetail(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // ??????????????????
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0",MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult getCurbDetail(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // ??????????????????
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0",MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult getCurbVerifyDetail(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // ??????????????????
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0",MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult getCloseDetail(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // ??????????????????
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0",MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult getAcceptanceDetail(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // ??????????????????
            String questionId = ParamsUtil.getDetailParams(messageBody).getString(ParamConst.QUESTION_ID);
            result = actionTraceBiz.getQuestionDetail(questionId);
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0",MultilingualismUtil.getLanguage("selectSuccess"),result);
    }

    @Override
    public DWEAIResult postQuestionFeedbackUpdate(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result = new JSONObject();
        try {
            // ????????????+?????????????????????????????????
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionUpdateEnum.question_feedback.getCode());
            result = questionTraceStrategy.updateQuestionTrace(ParamsUtil.getQFParams(messageBody));
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult postQuestionIdentifyUpdate(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result = new JSONObject();
        try {
            // ????????????+?????????????????????????????????
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionUpdateEnum.question_identification.getCode());
            result = questionTraceStrategy.updateQuestionTrace(ParamsUtil.getQFParams(messageBody));

        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult postQuestionDistributionUpdate(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try {
            // ????????????+?????????????????????????????????
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionSolveEnum.question_distribution.getCode());
            result = questionTraceStrategy.updateQuestionTrace(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult postQuestionCurbDistributionUpdate(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result = new JSONObject();
        try{
            // ????????????+?????????????????????????????????
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionSolveEnum.question_curb_distribution.getCode());
            result =questionTraceStrategy.updateQuestionTrace(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult postQuestionCurbUpdate(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try{
            // ????????????+?????????????????????????????????
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionSolveEnum.question_curb.getCode());
            result = questionTraceStrategy.updateQuestionTrace(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult postQuestionCurbVerifyUpdate(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try{
            // ????????????+?????????????????????????????????
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionSolveEnum.question_verify.getCode());
            result = questionTraceStrategy.updateQuestionTrace(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult postQuestionCloseUpdate(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try{
            // ????????????+?????????????????????????????????
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionSolveEnum.question_close.getCode());
            result = questionTraceStrategy.updateQuestionTrace(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }

    @Override
    public DWEAIResult postQuestionAcceptanceUpdate(Map<String, String> headers, String messageBody) throws Exception {
        JSONObject result;
        try{
            // ????????????+?????????????????????????????????
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionUpdateEnum.question_acceptance.getCode());
            result = questionTraceStrategy.updateQuestionTrace(ParamsUtil.getUpdateParams(messageBody));
        }catch (Exception e){
            throw new DWRuntimeException(e.getMessage());
        }
        return new DWEAIResult("0", "0","update Success!",result);
    }
}
