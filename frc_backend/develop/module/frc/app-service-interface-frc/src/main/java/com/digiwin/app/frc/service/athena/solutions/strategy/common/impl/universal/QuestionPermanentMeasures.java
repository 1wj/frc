package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.universal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUniversalSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
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
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.PermanentMeasureModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.QuestionInfoUniversalModel;
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
 * @Date: 2022/4/6 15:16
 * @Version 1.0
 * @Description  ?????????????????? D5-????????????????????????
 */
public class QuestionPermanentMeasures implements QuestionHandlerStrategy {

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
    UniversalQuestionBiz universalQuestionBiz =  SpringContextHolder.getBean(UniversalQuestionBiz.class);


    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        JSONObject resultJsonObject = JSON.parseObject(parameters);
        //1.string???model??????????????????????????????model
        PermanentMeasureModel permanentMeasureModel = TransferTool.convertString2Model(parameters, PermanentMeasureModel.class);
        //2.????????????
        ParamCheckUtil.checkPermanmentMeasureParams(permanentMeasureModel);
        //3.?????????????????????
        QuestionInfoUniversalModel questionInfoModel = permanentMeasureModel.getQuestionInfos();
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONObject questionConfirm = resultJsonObject.getJSONObject("question_confirm");
        JSONArray lastingMeasures = resultJsonObject.getJSONArray("lasting_measure");
        JSONArray attachmentInfos =  resultJsonObject.getJSONArray("attachment_info");

        //4.??????????????????
        return updateDetailInfo(entity,lastingMeasures, attachmentInfos,questionConfirm,questionInfoModel.getOid());
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 1-????????????????????????????????????
            List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionUniversalSolveEnum.plan_arrange.getCode());

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
            entity.setTenantsid(TenantTokenUtil.getTenantSid());
            // ??????????????????
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
            //????????????????????????
            entity.setExpectCompleteDate(beforeQuestionVo.get(0).getExpectCompleteDate());
            // ???????????????id???name
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


    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid, List<BeforeQuestionVo> beforeQuestionVos, QuestionActionTraceEntity traceEntity) throws Exception {
        String dataContent = beforeQuestionVos.get(0).getDataContent();

        //??????????????????????????????????????????????????????
        JSONObject resultJsonObject = JSON.parseObject(dataContent);

        //??????????????? question_result
        JSONObject questionResult = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);

        //??????  lasting_measure  ??????????????????
        JSONArray lastingMeasure = new JSONArray();
        questionResult.put("lasting_measure",lastingMeasure);

        //????????????????????????
        JSONObject processDetailInfo = new JSONObject();
        //?????? ???????????? -?????? ????????????+?????????+???????????? +???DTD?????????????????????????????????????????????
        processDetailInfo.put("solution", MultilingualismUtil.getLanguage("general_solution_name"));
        processDetailInfo.put("step_name",MultilingualismUtil.getLanguage("general_solution_step"));
        processDetailInfo.put("liable_person_id",beforeQuestionVos.get(0).getLiablePersonId());
        processDetailInfo.put("liable_person_name",beforeQuestionVos.get(0).getLiablePersonName());
        if (null != beforeQuestionVos.get(0).getActualCompleteDate()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter.format(beforeQuestionVos.get(0).getActualCompleteDate());
            processDetailInfo.put("process_date",dateString);
        }else {
            processDetailInfo.put("process_date","");
        }

        //+ ??????????????????
        DateUtil.assignValueForExpectCompleteTime(traceEntity,questionResult, QuestionUniversalSolveEnum.permanent_measures.getCode());

        // plan_arrange ????????????????????????
        JSONArray planArrange = questionResult.getJSONArray("plan_arrange");
        questionResult.remove("plan_arrange");
        String textNew = JSON.toJSONString(planArrange, SerializerFeature.DisableCircularReferenceDetect);
        JSONArray jsonArray = JSON.parseArray(textNew);
        questionResult.put("plan_arrange",jsonArray);

        //reason_analysis_description  ??????????????????
        String reasonAnalysisDescription = questionResult.getJSONObject("reason_analysis").getString("reason_analysis_description");
        processDetailInfo.put("reason_analysis_description",reasonAnalysisDescription);
        processDetailInfo.put("plan_arrange",planArrange);
        questionResult.put("plan_arrange_info",processDetailInfo);

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



    private JSONObject updateDetailInfo(QuestionActionTraceEntity entity, JSONArray lastingMeasures, JSONArray attachmentModels,  JSONObject questionConfirm, String oid) {
        // ?????????????????????????????????????????????????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        JSONObject dataDetail = getDataDetail(resultJsonObject);

        //????????????????????????
        dataDetail.remove("question_confirm");
        dataDetail.put("question_confirm",questionConfirm);

        //????????????????????????
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String hourMinuteSecond = currentDate.substring(10);
        for(Iterator<Object> iterator = lastingMeasures.iterator(); iterator.hasNext();){
            JSONObject object = (JSONObject) iterator.next();
            String expectSolveDate = object.getString("expect_solve_date");
            expectSolveDate+=hourMinuteSecond;
            object.remove("expect_solve_date");
            object.put("expect_solve_date",expectSolveDate);

        }
        dataDetail.remove("lasting_measure");
        dataDetail.put("lasting_measure",lastingMeasures);


        // ????????????
        JSONArray attachmentInfos = (JSONArray) dataDetail.get("attachment_info");
        JSONArray mustUploadAttachments = new JSONArray();
        //??????????????????????????????????????????
        boolean repeatCheckFlag = false;
        for (Iterator<Object> iterator = attachmentModels.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            boolean status = true;
            for (Iterator<Object> it = attachmentInfos.iterator();it.hasNext();) {
                JSONObject attach = (JSONObject)it.next();
                if(!repeatCheckFlag){
                    repeatCheckFlag = "SE003006".equals(attach.getString("attachment_belong_stage"));
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
        if(entity.getQuestionProcessStatus() != 6 && entity.getQuestionProcessResult() != 5) {
            JSONArray planArrange = dataDetail.getJSONArray("plan_arrange");
            //????????????????????????
            for (Iterator<Object> ite = planArrange.iterator(); ite.hasNext(); ) {
                JSONObject obj = (JSONObject) ite.next();
                if ("SE003006".equals(obj.get("step_no"))) {
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
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE003006");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());

        // ????????????
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,dataInstanceEntity);

        return universalQuestionBiz.handleUpdateForPermanentMeasure(entity,attachmentEntities,dataInstanceEntity);

    }


    /**
     * ?????????????????????????????????????????????????????????????????????
     * @param resultJsonObject
     * @return
     */
    private JSONObject getDataDetail(JSONObject resultJsonObject) {
        // ??????????????? question_result
        JSONObject questionResult = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
        return questionResult;
    }
}
