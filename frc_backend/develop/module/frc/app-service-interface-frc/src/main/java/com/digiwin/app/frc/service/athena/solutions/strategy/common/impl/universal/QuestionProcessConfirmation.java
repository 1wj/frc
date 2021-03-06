package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.universal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.Const.solutions.eightD.ResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUniversalSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.IamEocBiz;
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
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.UniversalConfirmInfoModel;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: xieps
 * @Date: 2022/4/12 14:30
 * @Version 1.0
 * @Description D6????????????????????????
 */
public class QuestionProcessConfirmation implements QuestionHandlerStrategy {

    @Autowired
    AttachmentMapper attachmentMapper  =  SpringContextHolder.getBean(AttachmentMapper.class);

    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    UniversalQuestionBiz universalQuestionBiz =  SpringContextHolder.getBean(UniversalQuestionBiz.class);

    @Autowired
    IamEocBiz iamEocBiz =  SpringContextHolder.getBean(IamEocBiz.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);


    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        //1.string???model??????????????????????????????model
        UniversalConfirmInfoModel confirmInfoModel = TransferTool.convertString2Model(parameters, UniversalConfirmInfoModel.class);
        //2. ????????????
        ParamCheckUtil.checkUniversalConfirmParams(confirmInfoModel);
        //3.?????????????????????
        QuestionInfoUniversalModel questionInfoModel = confirmInfoModel.getQuestionInfoUniversalModel();
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);
        // ????????????????????????
        entity.setActualCompleteDate(new Date());
        JSONObject resultJsonObject = JSON.parseObject(parameters);
        JSONObject confirmVerify = resultJsonObject.getJSONObject("process_confirm_verify");
        JSONObject questionConfirm = resultJsonObject.getJSONObject("question_confirm");
        JSONArray attachmentInfos = resultJsonObject.getJSONArray("attachment_info");
        return handleDetail(entity,confirmVerify,attachmentInfos,questionConfirm);
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        if (CollectionUtils.isEmpty(actionTraceEntityList)) {
            throw new DWRuntimeException("list index is 0 ");
        }
        QuestionActionTraceEntity entity = actionTraceEntityList.get(0);
        // ??????[????????????4-3 ??????????????????]??????
        List<BeforeQuestionVo> shortTermCloseAcceptanceInfo = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionUniversalSolveEnum.short_term_closing_acceptance.getCode());
        if (!"4".equals(shortTermCloseAcceptanceInfo.get(0).getProcessStatus()) || !"2".equals(shortTermCloseAcceptanceInfo.get(0).getProcessResult())) {
            throw new DWRuntimeException(" wait short_term_closing_acceptance to finish");
        }

        // ??????[????????????????????????]??????
        List<BeforeQuestionVo> permanentMeasureVerifyInfo = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionUniversalSolveEnum.permanent_measures_execute_verify.getCode());
        if (CollectionUtils.isEmpty(permanentMeasureVerifyInfo)) {
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("beforeStepNull"));
        }

        return new JSONObject().fluentPut("return_data",createDataInstance(entity, shortTermCloseAcceptanceInfo.get(0),permanentMeasureVerifyInfo.get(0)));
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
            entity.setTenantsid(TenantTokenUtil.getTenantSid());
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
            JSONArray lastingMeasure = questionResult.getJSONArray("lasting_measure");
            for(Iterator<Object> iterator = lastingMeasure.iterator();iterator.hasNext();){
                JSONObject obj = (JSONObject) iterator.next();
                obj.remove("is_history_data");
                obj.put("is_history_data","Y");
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


    private JSONArray createDataInstance(QuestionActionTraceEntity entity, BeforeQuestionVo shortTermCloseAcceptanceInfo, BeforeQuestionVo permanentMeasureVerifyInfo){
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(permanentMeasureVerifyInfo.getDataContent());
        // ??????????????? question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);


        //??????????????????????????????????????????
        DateUtil.assignValueForExpectCompleteTime(entity,dataDetail, QuestionUniversalSolveEnum.process_confirmation.getCode());


        // ????????? ??????????????????table
        JSONArray permanentMeasureExecuteVerify =dataDetail.getJSONArray("lasting_measure_execute_verify");

        // ????????? ??????????????????table
        JSONArray temporaryMeasureExecuteVerify = getTemporaryMeasureVerifyTable(shortTermCloseAcceptanceInfo);

        // ?????? ???????????? ?????????????????? - ????????????
        processData(dataDetail,shortTermCloseAcceptanceInfo,permanentMeasureVerifyInfo,permanentMeasureExecuteVerify,temporaryMeasureExecuteVerify,entity);
        // ?????? ???????????? - ????????????table
        processConfirmData(dataDetail);
        // ??????value???null?????????
        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);

        // ?????????-???????????????-?????? entity
        QuestionActionTraceEntity initTraceEntity = initQuestionActionEntity(entity);
        // ?????????-dataInstance
        DataInstanceEntity dataInstanceEntity = initDataInstance(resultJsonObject,initTraceEntity,shortTermCloseAcceptanceInfo);
        // dao
        actionTraceBiz.insertActionTraceForCurb(entity,dataInstanceEntity);
        // init -??????
        JSONArray responseParam = new JSONArray();

        responseParam.add(initResponse(initTraceEntity));
        return responseParam;
    }

    /**
     * ??????[????????????4-3 ??????????????????]table
     * @param shortTermCloseAcceptanceInfo ??????????????????
     * @return
     */
    private JSONArray getTemporaryMeasureVerifyTable(BeforeQuestionVo shortTermCloseAcceptanceInfo){
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(shortTermCloseAcceptanceInfo.getDataContent());
        // ??????????????? question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
        // ???????????????????????? table
        JSONObject temporaryMeasureExecuteVerify = dataDetail.getJSONObject("temporary_measure_execute_verify");
        return temporaryMeasureExecuteVerify.getJSONArray("temporary_measure_execute_verify_detail");
    }


    /**
     * D6 -- ????????????????????????
     *
     * @param dataDetail dataDetail
     * @param shortTermCloseAcceptanceInfo D4???????????????????????????
     * @param permanentMeasureVerifyInfo   D5????????????????????????
     * @param permanentMeasureExecuteVerify ????????????????????????table
     * @param temporaryMeasureExecuteVerify ????????????????????????table
     */
    private void processData(JSONObject dataDetail,BeforeQuestionVo shortTermCloseAcceptanceInfo,BeforeQuestionVo permanentMeasureVerifyInfo,JSONArray permanentMeasureExecuteVerify,JSONArray temporaryMeasureExecuteVerify,QuestionActionTraceEntity entity){

        // D6-??????????????????????????????????????????????????????????????????table,???remove
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dataDetail.remove("lasting_measure_execute_verify");
        dataDetail.put("lasting_measure_execute_verify",new JSONObject().fluentPut("liable_person_id",permanentMeasureVerifyInfo.getLiablePersonId())
                .fluentPut("liable_person_name",permanentMeasureVerifyInfo.getLiablePersonName())
                .fluentPut("process_date",dateFormat.format(permanentMeasureVerifyInfo.getActualCompleteDate()))
                .fluentPut("lasting_measure_execute_verify_detail",permanentMeasureExecuteVerify));
        dataDetail.put("temporary_measure_execute_verify",new JSONObject().fluentPut("liable_person_id",shortTermCloseAcceptanceInfo.getLiablePersonId())
                .fluentPut("liable_person_name",shortTermCloseAcceptanceInfo.getLiablePersonName())
                .fluentPut("process_date",dateFormat.format(shortTermCloseAcceptanceInfo.getActualCompleteDate()))
                .fluentPut("temporary_measure_execute_verify_detail",temporaryMeasureExecuteVerify));
        //D4orD5????????? ???????????????????????????????????????????????????????????????
        List<BeforeQuestionVo> planArrangeInfos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionUniversalSolveEnum.plan_arrange.getCode());
        JSONObject planArrangeResultObject = JSON.parseObject(planArrangeInfos.get(0).getDataContent());
        JSONObject questionResult = planArrangeResultObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
        JSONObject reasonAnalysis = questionResult.getJSONObject("reason_analysis");
        JSONObject planArrangeInfo = dataDetail.getJSONObject("plan_arrange_info");
        planArrangeInfo.remove("plan_arrange");
        planArrangeInfo.put("plan_arrange",questionResult.getJSONArray("plan_arrange"));
        planArrangeInfo.remove("reason_analysis_description");
        planArrangeInfo.put("reason_analysis_description",reasonAnalysis.getString("reason_analysis_description"));
    }


    /**
     * ?????? D6- ????????????table
     * @param newDataDetail D6 data
     * @return
     */
    private void processConfirmData(JSONObject newDataDetail){
        newDataDetail.fluentPut("process_confirm_verify",new JSONObject().fluentPut("verify_illustrate","")
                .fluentPut("verify_person_id","")
                .fluentPut("verify_person_name","")
                .fluentPut("verify_date",""));
    }


    /**
     * ???????????????????????????-question_action_trace???
     * @param entity ????????????
     * @return ??????????????????
     */
    private QuestionActionTraceEntity initQuestionActionEntity(QuestionActionTraceEntity entity){
        String dataInstanceOid = IdGenUtil.uuid();
        String oid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        entity.setOid(oid);
        // ??????????????????
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
        return entity;
    }

    /**
     * ????????????????????????
     * @param resultJsonObject data_content
     * @param entity action_trace?????????
     * @return DataInstanceEntity
     */
    private DataInstanceEntity initDataInstance(JSONObject resultJsonObject,QuestionActionTraceEntity entity,BeforeQuestionVo shortTermCloseAcceptanceInfo){
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        dataInstanceEntity.setOid(entity.getDataInstanceOid());
        //?????????????????????????????????????????????????????????????????????
        JSONObject shortCloseMeasureJsonObject = JSON.parseObject(shortTermCloseAcceptanceInfo.getDataContent());
        JSONObject dataShortCloseMeasureDetail = shortCloseMeasureJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
        JSONObject permanentDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
        JSONArray permanentAttachmentInfo = permanentDetail.getJSONArray("attachment_info");
        JSONArray shortCloseMeasureAttachmentInfo = dataShortCloseMeasureDetail.getJSONArray("attachment_info");
        List<Map<String,Object>> unionList = ListUtils.union(permanentAttachmentInfo, shortCloseMeasureAttachmentInfo);
        //??????attachment_id????????????
        List<Map<String, Object>> restructuredAttachment = unionList.stream().filter(DeduplicationUtil.distinctByKey(item -> item.get("attachment_id"))).collect(Collectors.toList());
        permanentDetail.remove("attachment_info");
        permanentDetail.put("attachment_info",restructuredAttachment);
        dataInstanceEntity.setDataContent(JSON.toJSONString(resultJsonObject));
        dataInstanceEntity.setQuestionTraceOid(entity.getOid());
        return dataInstanceEntity;
    }


    /**
     * init ??????
     * @param entity question_action_trace??????
     * @return ??????-???DTD??????
     */
    private JSONObject initResponse(QuestionActionTraceEntity entity){
        JSONObject responseObject = new JSONObject();
        responseObject.put(ResponseConst.pending_approve_question_id,entity.getOid());
        responseObject.put(ResponseConst.question_no,entity.getQuestionNo());
        responseObject.put(ResponseConst.question_description,entity.getQuestionDescription());
        responseObject.put(ResponseConst.return_flag_id,entity.getReturnFlagId());
        responseObject.put(ResponseConst.return_flag_name,entity.getReturnFlagName());
        responseObject.put(ResponseConst.expect_solve_date,entity.getExpectCompleteDate());
        return responseObject;
    }

    /**
     * ????????????????????????
     * @param confirmVerify ?????? ?????????????????????
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONObject confirmVerify,JSONArray attachmentModels,JSONObject questionConfirm) throws IOException {
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(questionActionTraceEntity.getOid());
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        // ??????????????? question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject("question_result");

        JSONArray planManagementInfo = dataDetail.getJSONArray("plan_arrange");

        dataDetail.remove("process_confirm_verify");
        dataDetail.put("process_confirm_verify",confirmVerify);

        dataDetail.remove("question_confirm");
        dataDetail.put("question_confirm",questionConfirm);

        JSONArray attachmentInfos = dataDetail.getJSONArray("attachment_info");

        JSONArray mustUploadAttachments = new JSONArray();
        //??????????????????????????????????????????
        boolean repeatCheckFlag = false;
        for (Iterator<Object> iterator = attachmentModels.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            boolean status = true;
            for (Iterator<Object> it = attachmentInfos.iterator();it.hasNext();) {
                JSONObject attach = (JSONObject)it.next();
                if(!repeatCheckFlag){
                    repeatCheckFlag = "SE003009".equals(attach.getString("attachment_belong_stage"));
                }
                if (attach.get("attachment_id").equals(obj.get("attachment_id"))) {
                    status =false;
                    break;
                }
            }
            if (status) {
                mustUploadAttachments.add(obj);
            }
        }
        //?????????????????????????????????
        if(questionActionTraceEntity.getQuestionProcessStatus() != 6 && questionActionTraceEntity.getQuestionProcessResult() != 5) {
            //????????????????????????
            for (Iterator<Object> ite = planManagementInfo.iterator(); ite.hasNext(); ) {
                JSONObject obj = (JSONObject) ite.next();
                if ("SE003009".equals(obj.get("step_no"))) {
                    if (obj.get("attachment_upload_flag").equals("Y")) {
                        if (mustUploadAttachments.size() == 0) {
                            // ???????????????
                            List<AttachmentEntity> attachmentEntities = attachmentMapper.getAttachments((Long) DWServiceContext.getContext().getProfile().get("tenantSid"), dataInstanceVo.getOid());
                            if (attachmentEntities.size() == 0 && !repeatCheckFlag) {
                                throw new DWRuntimeException("attachment_upload_flag is Y, so attachment must be uploaded ! ");
                            }
                            break;
                        }
                    }
                }
            }
        }
        // ????????????
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),
                dataInstanceVo.getOid(),"SE003009");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // ????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // ????????????
        return universalQuestionBiz.handleUpdateForProcessConfirm(questionActionTraceEntity,attachmentEntities,entity);
    }

}
