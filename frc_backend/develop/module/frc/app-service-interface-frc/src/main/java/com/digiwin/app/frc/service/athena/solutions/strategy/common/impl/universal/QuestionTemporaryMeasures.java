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
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.QuestionInfoUniversalModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.TemporaryMeasuresInfoModel;
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
 *???????????? ????????????
 * @author cds
 * @date 2022/4/7
 * @param
 * @return
 */

public class QuestionTemporaryMeasures implements QuestionHandlerStrategy {
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
        TemporaryMeasuresInfoModel containmentMeasureInfoModel = TransferTool.convertString2Model(parameters, TemporaryMeasuresInfoModel.class);
        //2.????????????
        ParamCheckUtil.checkTemporaryMeauser(containmentMeasureInfoModel);
        //3.?????????????????????
        QuestionInfoUniversalModel questionInfoModel = containmentMeasureInfoModel.getQuestionInfoUniversalModels();
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONObject questionConfirm = resultJsonObject.getJSONObject("question_confirm");
        JSONArray attachmentInfos =  resultJsonObject.getJSONArray("attachment_info");
        JSONArray temporaryMeasure = resultJsonObject.getJSONArray("temporary_measure");

        //4.??????????????????
        return updateDetailInfo(entity,questionConfirm,temporaryMeasure, attachmentInfos,questionInfoModel.getOid());
    }
    private JSONObject updateDetailInfo(QuestionActionTraceEntity entity, JSONObject questionConfirm,JSONArray temporaryMeasure, JSONArray attachmentModels, String oid) throws ParseException {
        // ?????????????????????????????????????????????????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());

        JSONObject dataDetail = getDataDetail(resultJsonObject);

        JSONArray planManagementInfo = dataDetail.getJSONArray("plan_arrange");

        // ???????????? ????????? ???????????????
        for (Iterator<Object> iteratorNew = temporaryMeasure.iterator(); iteratorNew.hasNext();) {
            SimpleDateFormat format=  new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            JSONObject newObj = (JSONObject)iteratorNew.next();
            String expectSolveDate = (String) newObj.get("expect_solve_date");
            newObj.put("expect_solve_date", format.format(format.parse(expectSolveDate)).substring(0,10)+" "+formatter.format(new Date()));
        }

        //????????????????????????
        dataDetail.remove("temporary_measure");
        dataDetail.put("temporary_measure",temporaryMeasure);

        dataDetail.remove("question_confirm");
        dataDetail.put("question_confirm",questionConfirm);

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
                    repeatCheckFlag = "SE003002".equals(attach.getString("attachment_belong_stage"));
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
            //????????????????????????
            for (Iterator<Object> ite = planManagementInfo.iterator(); ite.hasNext(); ) {
                JSONObject obj = (JSONObject) ite.next();
                if (QuestionUniversalSolveEnum.temporary_measures.equals(obj.get("step_no"))) {
                    if (obj.get("attachment_upload_flag").equals("Y")) {
                        if (mustUploadAttachments.size() == 0) {
                            // ???????????????
                            List<AttachmentEntity> attachmentEntities = attachmentMapper.getAttachments(TenantTokenUtil.getTenantSid(), dataInstanceVo.getOid());
                            if (attachmentEntities.size() == 0  && !repeatCheckFlag) {
                                throw new DWRuntimeException("attachment_upload_flag is Y, so attachment must be uploaded ! ");
                            }
                            break;
                        }
                    }
                }
            }
        }
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE003002");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());

        // ????????????
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,dataInstanceEntity);

        return universalQuestionBiz.handleUpdateForTemporaryMeasures(entity,attachmentEntities,dataInstanceEntity);

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
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(beforeQuestionVos.get(0).getDataContent());
        // ??????????????? question_result
        JSONObject questionResult= resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);

        //??????????????????????????????????????????
        DateUtil.assignValueForExpectCompleteTime(traceEntity,questionResult, QuestionUniversalSolveEnum.temporary_measures.getCode());


        //????????????????????????
        JSONArray temporaryMeasure = new JSONArray();
        questionResult.put("temporary_measure",temporaryMeasure);

        // ????????????????????????
        JSONObject reasonAnalysis = (JSONObject) questionResult.get("reason_analysis");

        //????????????????????????
        JSONObject verifyDetailInfo = new JSONObject();
        //?????? ???????????? -?????? ????????????+?????????+????????????
        verifyDetailInfo.put("liable_person_id",beforeQuestionVos.get(0).getLiablePersonId());
        verifyDetailInfo.put("liable_person_name",beforeQuestionVos.get(0).getLiablePersonName());
        if (null != beforeQuestionVos.get(0).getActualCompleteDate()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter.format(beforeQuestionVos.get(0).getActualCompleteDate());
            verifyDetailInfo.put("process_date",dateString);
        }else {
            verifyDetailInfo.put("process_date","");
        }
        // ????????????????????????
        JSONArray planArrange = questionResult.getJSONArray("plan_arrange");
        questionResult.remove("plan_arrange");
        String textNew = JSON.toJSONString(planArrange, SerializerFeature.DisableCircularReferenceDetect);
        JSONArray jsonArray = JSON.parseArray(textNew);
        questionResult.put("plan_arrange",jsonArray);

        verifyDetailInfo.put("plan_arrange",planArrange);
        verifyDetailInfo.put("reason_analysis_description", reasonAnalysis.get("reason_analysis_description"));
        questionResult.put("plan_arrange_info",verifyDetailInfo);

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
