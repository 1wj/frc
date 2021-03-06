package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.universal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUniversalSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.file.biz.IPdfServiceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.UniversalQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.QuestionInfoUniversalModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.ShortTermCloseVerifyInfoModel;
import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.PendingQuestionVo;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.DateUtil;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.TransferTool;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.frc.service.athena.util.rqi.EocUtils;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *???????????? D4-3??????????????????
 * @author cds
 * @date 2022/4/7
 * @param
 * @return
 */

public class QuestionShortTermClosingAcceptance implements QuestionHandlerStrategy {
    @Autowired
    AttachmentMapper attachmentMapper  =  SpringContextHolder.getBean(AttachmentMapper.class);

    /**
     * ????????????(??????new????????????)
     */
    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    IPdfServiceBiz iPdfServiceBiz = SpringContextHolder.getBean(IPdfServiceBiz.class);

    @Autowired
    UniversalQuestionBiz universalQuestionBiz =  SpringContextHolder.getBean(UniversalQuestionBiz.class);


    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        JSONObject resultJsonObject = JSON.parseObject(parameters);
        //1.string???model??????????????????????????????model
        ShortTermCloseVerifyInfoModel containmentMeasureInfoModel = TransferTool.convertString2Model(parameters, ShortTermCloseVerifyInfoModel.class);
        //2.????????????
        ParamCheckUtil.checkTemporaryShortTermCloseVerifyInfoModel(containmentMeasureInfoModel);
        //3.?????????????????????
        QuestionInfoUniversalModel questionInfoModel = containmentMeasureInfoModel.getQuestionInfoUniversalModels();
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONObject questionConfirm = resultJsonObject.getJSONObject("question_confirm");
        JSONArray attachmentInfos =  resultJsonObject.getJSONArray("attachment_info");
        JSONObject shortTermVerify = resultJsonObject.getJSONObject("short_term_verify");

        //4.??????????????????
        return updateDetailInfo(entity,questionConfirm,shortTermVerify, attachmentInfos,questionInfoModel.getOid());
    }
    private JSONObject updateDetailInfo(QuestionActionTraceEntity entity, JSONObject questionConfirm ,JSONObject shortTermVerify, JSONArray attachmentModels, String oid) throws ParseException {
        // ?????????????????????????????????????????????????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());

        JSONObject dataDetail = getDataDetail(resultJsonObject);

        JSONObject questionBasicInfo = dataDetail.getJSONObject("question_basic_info");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        shortTermVerify.put("inspector_id",questionBasicInfo.get("question_proposer_id"));
        shortTermVerify.put("inspector_name",questionBasicInfo.get("question_proposer_name"));
        shortTermVerify.put("verify_illustrate",shortTermVerify.getString("verify_illustrate"));
        shortTermVerify.put("verify_date",formatter.format(new Date()));

        //????????????????????????
        dataDetail.remove("short_term_verify");
        dataDetail.put("short_term_verify",shortTermVerify);

        dataDetail.remove("question_confirm");
        dataDetail.put("question_confirm",questionConfirm);

        // ????????????
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



        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE003005");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());

        // ????????????
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,dataInstanceEntity);

        return universalQuestionBiz.handleUpdateForQuestionShortTermClosingAcceptance(entity,attachmentEntities,dataInstanceEntity);

    }



    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 1-????????????????????????????????????
            List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionUniversalSolveEnum.temporary_measures_execute_verify.getCode());

            // 2-???????????????????????????
            String dataInstanceOid = IdGenUtil.uuid();
            String oid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            entity.setOid(oid);

            // 3-??????????????????
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

            // 4-??????????????????
            DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVos,entity);
            actionTraceBiz.insertActionTrace(entity,dataInstanceEntity);

            // 5-??????response??????
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
            // ??????????????????????????????
            List<BeforeQuestionVo> beforeQuestionVo = actionTraceMapper.getBeforeQuestionTraceForIdentity(TenantTokenUtil.getTenantSid(),entity.getQuestionNo(), entity.getQuestionProcessStep(),
                    entity.getQuestionSolveStep());
            // ???????????????????????????
            String dataInstanceOid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            String oid = IdGenUtil.uuid();
            entity.setOid(oid);
            entity.setTenantsid((Long) DWServiceContext.getContext().getProfile().get("tenantSid"));
            // ??????????????????
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
            // ???????????????id???name
            entity.setReturnId((String) DWServiceContext.getContext().getProfile().get("userId"));
            entity.setReturnName((String) DWServiceContext.getContext().getProfile().get("userName"));
            //????????????????????????
            entity.setExpectCompleteDate(beforeQuestionVo.get(0).getExpectCompleteDate());
            DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
            dataInstanceEntity.setOid(dataInstanceOid);
            //????????????????????? ????????????????????????????????? is_history_data ??????????????????????????????Y
            String dataContent = beforeQuestionVo.get(0).getDataContent();
            JSONObject resultJsonObject = JSON.parseObject(dataContent);
            JSONObject questionResult = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
            JSONArray lastingMeasure = questionResult.getJSONArray("temporary_measure");
            for(Iterator<Object> iterator = lastingMeasure.iterator();iterator.hasNext();){
                JSONObject object = (JSONObject) iterator.next();
                object.remove("is_history_data");
                object.put("is_history_data","Y");
            }
            String dataContentString = JSON.toJSONString(resultJsonObject);
            dataInstanceEntity.setDataContent(dataContentString);

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


    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid, List<BeforeQuestionVo> beforeQuestionVos, QuestionActionTraceEntity traceEntity) throws Exception {
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(beforeQuestionVos.get(0).getDataContent());
        // ??????????????? question_result
        JSONObject questionResult= resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);

        //??????????????????????????????????????????
        DateUtil.assignValueForExpectCompleteTime(traceEntity,questionResult, QuestionUniversalSolveEnum.temporary_measures.getCode());

        //????????????
        JSONObject questionBasicInfo = questionResult.getJSONObject("question_basic_info");
        //????????????????????????
        JSONObject shortVerify= new JSONObject();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        shortVerify.put("verify_illustrate","");
        shortVerify.put("inspector_id",questionBasicInfo.get("question_proposer_id"));
        shortVerify.put("inspector_name",questionBasicInfo.get("question_proposer_name"));
        shortVerify.put("verify_date",formatter.format(new Date()));
        //questionResult.put("short_term_verify",shortVerify);
        // ????????????????????????
        JSONObject verifyDetailInfo = new JSONObject();
        //?????? ???????????? -?????? ????????????+?????????+????????????
        verifyDetailInfo.put("liable_person_id",beforeQuestionVos.get(0).getLiablePersonId());
        verifyDetailInfo.put("liable_person_name",beforeQuestionVos.get(0).getLiablePersonName());
        if (null != beforeQuestionVos.get(0).getActualCompleteDate()) {
            String dateString = formatter.format(beforeQuestionVos.get(0).getActualCompleteDate());
            verifyDetailInfo.put("process_date",dateString);
        }else {
            verifyDetailInfo.put("process_date","");
        }
        // ????????????????????????
        JSONArray temporaryMeasureExecuteVerify = questionResult.getJSONArray("temporary_measure_execute_verify");
        questionResult.remove("temporary_measure_execute_verify");
        String textNew = JSON.toJSONString(temporaryMeasureExecuteVerify, SerializerFeature.DisableCircularReferenceDetect);
        JSONArray jsonArray = JSON.parseArray(textNew);
        questionResult.put("temporary_measure_execute_verify",jsonArray);

        verifyDetailInfo.put("temporary_measure_execute_verify_detail",temporaryMeasureExecuteVerify);
        questionResult.put("temporary_measure_execute_verify",verifyDetailInfo);
        questionResult.put("short_term_verify",shortVerify);
        String printReportId=iPdfServiceBiz.getReportPdf(resultJsonObject);
        shortVerify.put("print_report_id",printReportId);

        // null ??? ??????
        String dataContentString = JSON.toJSONString(resultJsonObject, filter);
        // ????????????????????????????????????????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        entity.setOid(dataInstanceOid);
        entity.setDataContent(dataContentString);
        entity.setQuestionTraceOid(oid);
        return entity;
    }


    /**
     * null????????? ??????
     */
    private ValueFilter filter = (obj, s, v) -> {
        if (null == v) {
            return "";
        }
        return v;
    };



    /**
     * ?????????????????????????????????????????????????????????????????????
     * @param resultJsonObject
     * @return
     */
    private JSONObject getDataDetail(JSONObject resultJsonObject) {
        // ??????????????? question_result
        return resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
    }
}
