package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.eightD;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.Question8DSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.file.biz.IPdfServiceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.EightDQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.FeedBackVerifyModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.QuestionInfo8DModel;
import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.PendingQuestionVo;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.frc.service.athena.util.rqi.EocUtils;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/5/9 13:39
 * @Version 1.0
 * @Description  D3-3 反馈者验收卡业务逻辑
 */
public class QuestionFeedBackPersonVerify implements QuestionHandlerStrategy {
    /**
     * 手动注入(工厂new出的对象)
     */
    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    EightDQuestionBiz eightDQuestionBiz =  SpringContextHolder.getBean(EightDQuestionBiz.class);

    @Autowired
    IPdfServiceBiz iPdfServiceBiz = SpringContextHolder.getBean(IPdfServiceBiz.class);



    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        JSONObject resultJsonObject = JSON.parseObject(parameters);
        //1.string转model，将待更新的字段转为model
        FeedBackVerifyModel feedBackVerifyModel = TransferTool.convertString2Model(parameters, FeedBackVerifyModel.class);
        //2.参数校验
        ParamCheckUtil.checkFeedBackVerifyParams(feedBackVerifyModel);
        //3.更新任务卡状态
        QuestionInfo8DModel questionInfoModel = feedBackVerifyModel.getQuestionInfos().get(0);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONObject shortTermVerify = resultJsonObject.getJSONObject("short_term_verify");
        JSONArray attachmentInfos =  resultJsonObject.getJSONArray("attachment_info");
        JSONObject questionConfirm = resultJsonObject.getJSONObject("question_confirm");

        return  updateDetailInfo(entity,shortTermVerify, attachmentInfos,questionInfoModel.getOid(),questionConfirm);
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 1-获取当前处理步骤前一步骤
            List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), Question8DSolveEnum.containment_measure_verify.getCode());

            // 1-获取D4D5节点数据
            List<BeforeQuestionVo> keyReasonCorrect = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), Question8DSolveEnum.key_reason_correct.getCode());

            // 2-新增待审核问题追踪
            String dataInstanceOid = IdGenUtil.uuid();
            String oid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            entity.setOid(oid);

            // 3-获取处理顺序
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

            // 4-表单数据流转
            DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVos,entity,keyReasonCorrect);
            actionTraceBiz.insertActionTrace(entity,dataInstanceEntity);

            // 5-封装response数据
            PendingQuestionVo vo = new PendingQuestionVo();
            BeanUtils.copyProperties(entity, vo);
            String userId = TenantTokenUtil.getUserId();
            vo.setEmpId(EocUtils.getEmpId(userId));
            vo.setEmpName(EocUtils.getEmpName(userId));
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            responseParam.add(jsonObject);
        }
        responseObject.put("return_data",responseParam);
        return responseObject;
    }

    @Override
    public JSONObject handleBack(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 获取退回的节点的数据
            List<BeforeQuestionVo> beforeQuestionVo = actionTraceMapper.getBeforeQuestionTraceForIdentity((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionNo(), entity.getQuestionProcessStep(),
                    entity.getQuestionSolveStep());
            //预计完成时间赋值
            String content = beforeQuestionVo.get(0).getDataContent();
            JSONObject resultJsonObject = JSON.parseObject(content);
            JSONObject questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).getJSONObject(0);
            DateUtil.assignValueForExpectCompleteTime(entity,questionResult,Question8DSolveEnum.containment_measure.getCode());

            // 新增待审核问题追踪
            String dataInstanceOid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            String oid = IdGenUtil.uuid();
            entity.setOid(oid);
            entity.setTenantsid((Long) DWServiceContext.getContext().getProfile().get("tenantSid"));
            // 获取处理顺序
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
            // 加入退回人id、name
            entity.setReturnId((String) DWServiceContext.getContext().getProfile().get("userId"));
            entity.setReturnName((String) DWServiceContext.getContext().getProfile().get("userName"));

            DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
            dataInstanceEntity.setOid(dataInstanceOid);

            dataInstanceEntity.setDataContent(beforeQuestionVo.get(0).getDataContent());
            dataInstanceEntity.setQuestionTraceOid(oid);

            actionTraceMapper.insertActionTrace(entity);
            dataInstanceMapper.insertDataInstance(dataInstanceEntity);

            JSONObject object = new JSONObject();
            object.put("pending_approve_question_id",oid);
            responseParam.add(object);

        }

        responseObject.put("return_data",responseParam);
        return responseObject;
    }



    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid, List<BeforeQuestionVo> beforeQuestionVos,QuestionActionTraceEntity traceEntity, List<BeforeQuestionVo> keyReasonCorrect ) throws Exception {
        String dataContent = beforeQuestionVos.get(0).getDataContent();
        //获取前一个节点问题分析审核的表单数据
        JSONObject resultJsonObject = JSON.parseObject(dataContent);
        //获取最外层 question_result[].get(0)
        JSONObject questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).getJSONObject(0);
        JSONObject questionBasicInfo = questionResult.getJSONArray("question_basic_info").getJSONObject(0);
        //获取D4D5审核的表单数据
        String keyReasonCorrectDataContent = keyReasonCorrect.get(0).getDataContent();
        JSONObject resultJsonObjectReasonCorrect = JSON.parseObject(keyReasonCorrectDataContent);
        questionResult.put("containment_measure", getDataDetail(resultJsonObjectReasonCorrect).getJSONObject("containment_measure"));
        //为了符合列印报告数据结构
        if (questionResult.containsKey("containment_measure_verify")) {
            JSONObject verifyDetailInfo = new JSONObject();
            //处理 更多信息 -加入 处理步骤+负责人+处理时间
            verifyDetailInfo.put(ReuseConstant.liable_person_id, beforeQuestionVos.get(0).getLiablePersonId());
            verifyDetailInfo.put(ReuseConstant.liable_person_name, beforeQuestionVos.get(0).getLiablePersonName());
            if (null != beforeQuestionVos.get(0).getActualCompleteDate()) {
                SimpleDateFormat formatter = new SimpleDateFormat(ReuseConstant.YEAR_MONTH_DAY);
                String dateString = formatter.format(beforeQuestionVos.get(0).getActualCompleteDate());
                verifyDetailInfo.put("process_date", dateString);
            } else {
                verifyDetailInfo.put("process_date", "");
            }
            // 处理循环复用问题
            JSONArray measureVerify = questionResult.getJSONArray("containment_measure_verify");
            questionResult.remove("containment_measure_verify");
            verifyDetailInfo.put("containment_measure_verify_detail", measureVerify);
            questionResult.put("containment_measure_verify", verifyDetailInfo);
        }
        //添加  short_term_verify  短期结案验收信息
        JSONObject shortTermVerify = new JSONObject();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        shortTermVerify.put("verify_illustrate","");
        shortTermVerify.put("inspector_id",questionBasicInfo.get("question_proposer_id"));
        shortTermVerify.put("inspector_name",questionBasicInfo.get("question_proposer_name"));
        shortTermVerify.put("verify_date",formatter.format(new Date()));
        //添加列印报告
        questionResult.put("short_term_verify",shortTermVerify);
        String printReportId=iPdfServiceBiz.getReportPdf(resultJsonObject);
        shortTermVerify.put("print_report_id",printReportId);

        //添加 question_confirm DTD组件需要
        JSONObject questionConfirm = new JSONObject();
        questionConfirm.put("status","");
        questionConfirm.put("return_step_no","");
        questionConfirm.put("return_reason","");
        questionResult.put("question_confirm",questionConfirm);

        //为traceEntity中的预计完成时间赋值
        DateUtil.assignValueForExpectCompleteTime(traceEntity,questionResult,Question8DSolveEnum.containment_measure.getCode());

        // null 转 “”
        String dataContentString = JSON.toJSONString(resultJsonObject, filter);
        // 落存实例表数据信息
        DataInstanceEntity entity = new DataInstanceEntity();
        entity.setOid(dataInstanceOid);
        entity.setDataContent(dataContentString);
        entity.setQuestionTraceOid(oid);
        return entity;
    }

    /**
     * null转“” 过滤
     */
    private ValueFilter filter = (obj, s, v) -> {
        if (null == v) {
            return "";
        }
        return v;
    };


    private JSONObject updateDetailInfo(QuestionActionTraceEntity entity, JSONObject shortTermVerify, JSONArray attachmentModels, String oid, JSONObject questionConfirm) {
        // 获取需更新的表单数据，并解析结构，获取核心数据
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        JSONObject dataDetail = getDataDetail(resultJsonObject);

        //处理短期结案验收 和 question_confirm 信息
        dataDetail.remove("short_term_verify");
        dataDetail.put("short_term_verify",shortTermVerify);
        dataDetail.remove("question_confirm");
        dataDetail.put("question_confirm",questionConfirm);

        // 处理附件
        JSONArray attachmentInfos = (JSONArray) dataDetail.get("attachment_info");
        JSONArray mustUploadAttachments = new JSONArray();

        for (Iterator<Object> iterator = attachmentModels.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            boolean status = true;
            for (Iterator<Object> it = attachmentInfos.iterator();it.hasNext();) {
                JSONObject attach = (JSONObject)it.next();
                if (attach.get("attachment_id").equals(obj.get("attachment_id"))) {
                    status =false;
                    break;
                }
            }
            if (status) {
                mustUploadAttachments.add(obj);
            }
        }

        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE001014");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());

        // 初始实体
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,dataInstanceEntity);

        return eightDQuestionBiz.handleUpdateForFeedBackVerify(entity,attachmentEntities,dataInstanceEntity);

    }


    /**
     * 获取需更新的表单数据，并解析结构，获取核心数据
     * @param resultJsonObject
     * @return
     */
    private JSONObject getDataDetail(JSONObject resultJsonObject) {
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = questionResult.getJSONObject(0);
        return dataDetail;
    }

}


