package com.digiwin.app.frc.service.impl.athena.qdh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.common.Const.ParamConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.meta.rabbitmq.handler.MessageSendHandler;
import com.digiwin.app.frc.service.athena.qdh.biz.*;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionRecordEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.*;
import com.digiwin.app.frc.service.athena.qdh.domain.model.init.AttachmentModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.init.BasicModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.init.DetailModel;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.solution.BasicInfoVo;
import com.digiwin.app.frc.service.athena.qdh.service.ITestService;
import com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.QuestionActionTraceFactory;
import com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.QuestionTraceStrategy;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.TransferTool;
import com.digiwin.app.frc.service.athena.util.qdh.GeneratePendingUtil;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.ParamsUtil;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.app.service.DWServiceResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName Test
 * @Description TODO
 * @Author author
 * @Date 2021/11/11 0:05
 * @Version 1.0
 **/
public class TestService implements ITestService{

    Logger logger = LoggerFactory.getLogger(TestService.class);


    @Autowired
    ActionTraceBiz actionTraceBiz;

    @Autowired
    RecordBiz recordBiz;

    @Autowired
    IamEocBiz iamEocBiz;

    @Autowired
    InitQuestionBiz initQuestionBiz;

    @Autowired
    QuestionToKnoBaseBiz questionToKnoBaseBiz;

    @Autowired
    MessageSendHandler messageSendHandler;


    @Override
    public DWServiceResult toKMO(String messageBody) throws IOException {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONObject param = JSON.parseObject(messageBody);
            String questionNo = param.getString("question_no");
            // 清楚假数据
            param.remove("question_no");
            result.setData(questionToKnoBaseBiz.questionToBase(questionNo,param));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("actionSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult toKMOTest(String messageBody) throws IOException {
        DWServiceResult result = new DWServiceResult();
            String routingKey = DWModuleConfigUtils.getCurrentModuleProperty("frc.to.kmo");
            JSONObject parameter = new JSONObject();
            parameter.put("questionOid","027f693da7c0456e8a64f4b2bd341334");
            messageSendHandler.send(routingKey, parameter);
            result.setData(null);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("actionSuccess"));

        return result;
    }

    @Override
    public DWServiceResult initQuestion(String messageBody) throws IOException {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取json数据
            String param = ParamsUtil.getInitParamForTest(messageBody);
            // 转实体model
            BasicModel basicModel = TransferTool.convertString2Model(param,"question_basic_info",true,BasicModel.class);
            DetailModel detailModel = TransferTool.convertString2Model(param,"question_detail_info",true, DetailModel.class);
            List<AttachmentModel> attachmentModels = TransferTool.convertString2List(param,"attachment_info", AttachmentModel.class);
            // 获取表单信息
            JSONObject dataInstance = new JSONObject();
            JSONArray info = new JSONArray();
            info.add(JSONObject.parse(param));
            dataInstance.put("question_result",info);
            String dataContent = dataInstance.toJSONString();
            result.setData(initQuestionBiz.initQuestionMessage(basicModel,detailModel,attachmentModels,dataContent.replaceAll("\\\\","")));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult getFeedBackDetail(String messageBody) {
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
    public DWServiceResult getFeedBackDetailTest(String messageBody) {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            JSONObject stdData = (JSONObject) JSONObject.parseObject(messageBody).get(ParamConst.STD_DATA);
            JSONObject parameter = (JSONObject) stdData.get(ParamConst.PARAMETER);
            String basic_info = parameter.get("basic_info").toString();
            BasicInfoVo basicInfoVo = new ObjectMapper().readValue(basic_info.getBytes(), BasicInfoVo.class);
            result.setData(basicInfoVo.getQuestionClassificationName());
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("key"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult getIdentifyDetail(String messageBody) throws Exception {
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
    public DWServiceResult getDistributionDetail(String messageBody) throws Exception {
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
    public DWServiceResult getSolutionStep(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取请求参数
            String solutionOid = (String) ParamsUtil.getSolutionParams(messageBody).get("solution_id");
            result.setData(actionTraceBiz.getSolutionStep(solutionOid));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("selectSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult getCurbDistributionDetail(String messageBody) throws Exception {
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
    public DWServiceResult getCurbDetail(String messageBody) throws Exception {
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
    public DWServiceResult getCurbVerifyDetail(String messageBody) throws Exception {
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
    public DWServiceResult getCloseDetail(String messageBody) throws Exception {
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
    public DWServiceResult getAcceptanceDetail(String messageBody) throws Exception {
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
    public DWServiceResult getDeparts() throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            result.setData(iamEocBiz.getDepartments());
            result.setSuccess(true);
            result.setMessage("select success");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult getUsers() throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            result.setData(iamEocBiz.getUsers());
            result.setSuccess(true);
            result.setMessage("select success");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }


    @Override
    public DWServiceResult UpdateQuestionFeedback(String messageBody) {
        DWServiceResult result = new DWServiceResult();
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionUpdateEnum.question_feedback.getCode());
            JSONObject re = questionTraceStrategy.updateQuestionTrace(ParamsUtil.getQFParams(messageBody));
            JSONObject r = new JSONObject();
            r.put("result",re);
            result.setData(r);
            result.setSuccess(true);
            result.setMessage("update Success!");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult UpdateQuestionIdentify(String messageBody) {
        DWServiceResult result = new DWServiceResult();
        JSONObject re = new JSONObject();
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionUpdateEnum.question_identification.getCode());
            re = questionTraceStrategy.updateQuestionTrace(ParamsUtil.getUpdateParams(messageBody));
            result.setData(re);
            result.setSuccess(true);
            result.setMessage("update Success!");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult UpdateQuestionDistribution(String messageBody) {
        DWServiceResult result = new DWServiceResult();
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionSolveEnum.question_distribution.getCode());
            questionTraceStrategy.updateQuestionTrace(ParamsUtil.getUpdateParams(messageBody));
            result.setSuccess(true);
            result.setMessage("update Success!");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult UpdateQuestionCurbDistribution(String messageBody) {
        DWServiceResult result = new DWServiceResult();
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionSolveEnum.question_curb_distribution.getCode());
            questionTraceStrategy.updateQuestionTrace(ParamsUtil.getQuestionUpdateParams(messageBody));
            result.setSuccess(true);
            result.setMessage("update Success!");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult UpdateQuestionCurb(String messageBody) {
        DWServiceResult result = new DWServiceResult();
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionSolveEnum.question_curb.getCode());
            questionTraceStrategy.updateQuestionTrace(ParamsUtil.getQuestionUpdateParams(messageBody));
            result.setSuccess(true);
            result.setMessage("update Success!");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult UpdateQuestionCurbVerify(String messageBody) {
        DWServiceResult result = new DWServiceResult();
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionSolveEnum.question_verify.getCode());
            questionTraceStrategy.updateQuestionTrace(ParamsUtil.getQuestionUpdateParams(messageBody));
            result.setSuccess(true);
            result.setMessage("update Success!");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult UpdateQuestionClose(String messageBody) {
        DWServiceResult result = new DWServiceResult();
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionSolveEnum.question_close.getCode());
            questionTraceStrategy.updateQuestionTrace(ParamsUtil.getQuestionUpdateParams(messageBody));
            result.setSuccess(true);
            result.setMessage("update Success!");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult UpdateQuestionAcceptance(String messageBody) {
        DWServiceResult result = new DWServiceResult();
        try {
            // 策略模式+工厂模式，传入更新数据
            QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(QuestionUpdateEnum.question_acceptance.getCode());
            questionTraceStrategy.updateQuestionTrace(ParamsUtil.getQuestionUpdateParams(messageBody));
            result.setSuccess(true);
            result.setMessage("update Success!");
            result.setData( questionTraceStrategy.updateQuestionTrace(ParamsUtil.getQuestionUpdateParams(messageBody)));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }


    @Override
    public DWServiceResult createUnapprovedQuestionData(String messageBody) {
        DWServiceResult result = new DWServiceResult();
        JSONObject response;
        try {
            // 获取待生成数据入参
            String paramData = ParamsUtil.getDetailParams(messageBody).toJSONString();
            // string转问题自定义实体类
            UnapprovedModel unapprovedModel = new ObjectMapper().readValue(paramData.getBytes(), UnapprovedModel.class);
            QuestionActionTraceEntity actionTraceEntity = new QuestionActionTraceEntity();
            BeanUtils.copyProperties(unapprovedModel,actionTraceEntity);
            // 如果存在预计完成日期，需进行日期转换
            if (!StringUtils.isEmpty(unapprovedModel.getExpectCompleteDate())) {
                actionTraceEntity.setExpectCompleteDate(new SimpleDateFormat("yyyy-MM-dd").parse(unapprovedModel.getExpectCompleteDate()));
            }

            // 处理退回
            if (!StringUtils.isEmpty(unapprovedModel.getReturnFlagId())) {
                logger.info("进入退回逻辑");
                JSONArray responseParam = actionTraceBiz.insertReturnBackDetail(actionTraceEntity);
                response = new JSONObject();
                response.put("return_data",responseParam);
                result.setData(response);
                result.setSuccess(true);
                result.setMessage(MultilingualismUtil.getLanguage("insertUnapprovedDataSuccess"));
                return result;
            }
            logger.info("进入生成待审核任务卡逻辑");
            // 策略模式+工厂模式，进入 生成待审核数据 逻辑
            QuestionTraceStrategy questionTraceStrategy;
            if (!StringUtils.isEmpty(unapprovedModel.getQuestionSolveStep())) {
                questionTraceStrategy = QuestionActionTraceFactory.getStrategy(unapprovedModel.getQuestionSolveStep());
                ParamCheckUtil.checkApprovedQuestionCurbParams(unapprovedModel);
            }else {
                ParamCheckUtil.checkApprovedQuestionParams(unapprovedModel);
                questionTraceStrategy = QuestionActionTraceFactory.getStrategy(unapprovedModel.getQuestionProcessStep());
            }
            JSONArray responseParam = questionTraceStrategy.insertUnapprovedQuestionTrace(actionTraceEntity);
            response = new JSONObject();
            response.put("return_data",responseParam);
            result.setData(response);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("insertUnapprovedDataSuccess"));
            return result;

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult createUnapprovedQuestionCurbData(String messageBody) {
        DWServiceResult result = new DWServiceResult();
        JSONObject response;

        try {
            // 获取待生成数据入参
//            String paramData = ParamsUtil.getDetailParams(messageBody).toJSONString();

            // 获取请求参数
            JSONObject parameter = ParamsUtil.getAthenaParameter(messageBody);
            // 获取 M层 ： question_info
            JSONArray questionInfo = (JSONArray) parameter.get(ParamConst.QUESTION_INFO);
            List<QuestionActionTraceEntity> list = new ArrayList<>();
            for (Iterator iterator = questionInfo.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                // string转问题自定义实体类
                UnApprovedCurbModel unapprovedModel = new ObjectMapper().readValue(obj.toJSONString().getBytes(), UnApprovedCurbModel.class);
                QuestionActionTraceEntity actionTraceEntity = new QuestionActionTraceEntity();
                BeanUtils.copyProperties(unapprovedModel,actionTraceEntity);
                // 获取需退回到的步骤
                if (!StringUtils.isEmpty(unapprovedModel.getQuestionId())) {
                    actionTraceEntity.setOid(unapprovedModel.getQuestionId());
                }
                // 如果存在预计完成日期，需进行日期转换
                if (!StringUtils.isEmpty(unapprovedModel.getExpectCompleteDate())) {
                    actionTraceEntity.setExpectCompleteDate(new SimpleDateFormat("yyyy-MM-dd").parse(unapprovedModel.getExpectCompleteDate()));
                }
                list.add(actionTraceEntity);
            }

            //todo 若return_flag_id不为空，则进入退回逻辑
            if (StringUtils.isEmpty(list.get(0).getReturnFlagId())) {
                // 策略模式+工厂模式，进入 生成待审核数据 逻辑
                QuestionTraceStrategy questionTraceStrategy = QuestionActionTraceFactory.getStrategy(list.get(0).getQuestionSolveStep());
                JSONArray responseParam = questionTraceStrategy.insertUnapprovedQuestionTrace(list.get(0));
                response = new JSONObject();
                response.put("return_data",responseParam);
            }else {
                // 进入退回逻辑
                JSONArray responseParam = actionTraceBiz.insertUnapprovedCurbQuestionTrace(list);
                response = new JSONObject();
                response.put("return_data",responseParam);
            }
            result.setData(response);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("insertUnapprovedDataSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult updateRecord(String messageBody) {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取待生成数据入参
            String paramData = ParamsUtil.getDetailParams(messageBody).toJSONString();
            // string转问题自定义实体类
            QuestionRecordModel recordModel = new ObjectMapper().readValue(paramData.getBytes(), QuestionRecordModel.class);
            QuestionRecordEntity recordEntity = new QuestionRecordEntity();
            BeanUtils.copyProperties(recordModel,recordEntity);
            recordEntity.setTenantsid((Long) DWServiceContext.getContext().getProfile().get("tenantSid"));
            recordBiz.updateRecord(recordEntity);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("success"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult createRecord(String messageBody) {
        DWServiceResult result = new DWServiceResult();
        try {
            // 获取待生成数据入参
            String paramData = ParamsUtil.getDetailParams(messageBody).toJSONString();
            // string转问题自定义实体类
            QuestionRecordModel recordModel = new ObjectMapper().readValue(paramData.getBytes(), QuestionRecordModel.class);
            QuestionRecordEntity recordEntity = new QuestionRecordEntity();
            BeanUtils.copyProperties(recordModel,recordEntity);
            result.setData(recordBiz.insertRecord(recordEntity));
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("success"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult getRecord(String messageBody) {
        DWServiceResult result = new DWServiceResult();
        try {
            List<QuestionRecordInfoModel> recordInfoModelList = GeneratePendingUtil.string2List(messageBody, QuestionRecordInfoModel.class);
            // 参数校验
            ParamCheckUtil.checkParamsForAnnotation(Collections.singletonList(recordInfoModelList));
            List jsonArray = recordBiz.getRecordNew(recordInfoModelList);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("question_info",jsonArray);
            result.setData(jsonObject);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("success"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

}
