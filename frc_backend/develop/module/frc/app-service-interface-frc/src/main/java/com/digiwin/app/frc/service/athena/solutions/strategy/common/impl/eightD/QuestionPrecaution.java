package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.eightD;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.Question8DSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
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
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.PreventionMeasureInfoModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.QuestionInfo8DSecondModel;
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
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/3/17 0:53
 * @Version 1.0
 * @Description ???????????? ????????????
 */
public class QuestionPrecaution implements QuestionHandlerStrategy {

    @Autowired
    AttachmentMapper attachmentMapper  =  SpringContextHolder.getBean(AttachmentMapper.class);

    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    EightDQuestionBiz eightDQuestionBiz =  SpringContextHolder.getBean(EightDQuestionBiz.class);




    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        JSONObject resultJsonObject = JSON.parseObject(parameters);
        //1.string???model??????????????????????????????model
        PreventionMeasureInfoModel preventionMeasureInfoModel = TransferTool.convertString2Model(parameters, PreventionMeasureInfoModel.class);
        //2.????????????
        try {
            ParamCheckUtil.checkPreventionMeasureParams(preventionMeasureInfoModel);
        } catch (DWArgumentException e) {
            e.printStackTrace();
        }
        //3.?????????????????????
        QuestionInfo8DSecondModel questionInfoModel = preventionMeasureInfoModel.getQuestionInfos().get(0);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONArray preventionMeasure = resultJsonObject.getJSONArray("prevention_measure");
        JSONArray attachmentInfos =  resultJsonObject.getJSONArray("attachment_info");
        //4.??????????????????
        JSONObject resultObject = updateDetailInfo(entity,preventionMeasure, attachmentInfos,questionInfoModel.getOid());

        return resultObject;
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 1-????????????????????????????????????
            List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), Question8DSolveEnum.correct_verify.getCode());

            // 2-???????????????????????????
            String dataInstanceOid = IdGenUtil.uuid();
            String oid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            entity.setOid(oid);

            // 3-??????????????????
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
            //D3-2
            List<BeforeQuestionVo> DThree = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),Question8DSolveEnum.containment_measure_verify.getCode());


            // 4-??????????????????
            DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVos,DThree,entity);
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
        return null;
    }


    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid, List<BeforeQuestionVo> beforeQuestionVos,List<BeforeQuestionVo> DThree,QuestionActionTraceEntity traceEntity) throws Exception {
        String dataContent = beforeQuestionVos.get(0).getDataContent();

        //??????????????????????????????????????????????????????
        JSONObject resultJsonObject = JSON.parseObject(dataContent);

        //??????????????? question_result[].get(0)
        JSONObject questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).getJSONObject(0);
        //+ ??????????????????
        DateUtil.assignValueForExpectCompleteTime(traceEntity,questionResult,Question8DSolveEnum.precaution.getCode());

        if (!Collections.isEmpty(DThree)) {
            //D3-2????????????
            JSONObject DthreeDetail = JSON.parseObject(DThree.get(0).getDataContent());
            JSONArray dthree = DthreeDetail.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
            JSONObject three = dthree.getJSONObject(0);
            //???????????????????????? ?????????????????????????????????
            if (three.containsKey("containment_measure_verify")) {
                JSONObject verifyDetailInfo = new JSONObject();
                //?????? ???????????? -?????? ????????????+?????????+????????????
                verifyDetailInfo.put("liable_person_id", DThree.get(0).getLiablePersonId());
                verifyDetailInfo.put("liable_person_name", DThree.get(0).getLiablePersonName());
                if (null != DThree.get(0).getActualCompleteDate()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String dateString = formatter.format(DThree.get(0).getActualCompleteDate());
                    verifyDetailInfo.put("process_date", dateString);
                } else {
                    verifyDetailInfo.put("process_date", "");
                }
                // ????????????????????????
                JSONArray measureVerify = three.getJSONArray("containment_measure_verify");
                questionResult.remove("containment_measure_verify");
                verifyDetailInfo.put("containment_measure_verify_detail", measureVerify);
                questionResult.put("containment_measure_verify", verifyDetailInfo);
            }
        }


        //??????  prevention_measure  ??????????????????
        JSONArray preventionMeasure = new JSONArray();
        questionResult.put("prevention_measure",preventionMeasure);
        //?????????????????? ????????????????????????
        JSONArray correctiveMeasureVerify = questionResult.getJSONArray("corrective_measure_verify");
        questionResult.remove("corrective_measure_verify");
        JSONObject measureVerify = new JSONObject();
        measureVerify.put("liable_person_id",beforeQuestionVos.get(0).getLiablePersonId());
        measureVerify.put("liable_person_name",beforeQuestionVos.get(0).getLiablePersonName());
        if (null != beforeQuestionVos.get(0).getActualCompleteDate()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter.format(beforeQuestionVos.get(0).getActualCompleteDate());
            measureVerify.put("process_date",dateString);
        }else {
            measureVerify.put("process_date","");
        }
        measureVerify.put("corrective_measure_verify_detail",correctiveMeasureVerify);
        questionResult.put("corrective_measure_verify",measureVerify);

        // null ??? ??????
        String dataContentString = JSON.toJSONString(resultJsonObject, filter);
        // ??????????????????
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


    private JSONObject updateDetailInfo(QuestionActionTraceEntity entity, JSONArray preventionMeasure, JSONArray attachmentModels, String oid) {
        // ?????????????????????????????????????????????????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        JSONObject dataDetail = getDataDetail(resultJsonObject);

        JSONArray planManagementInfo = dataDetail.getJSONArray("plan_arrange");
        //?????????????????????????????????
        DateUtil.measures(preventionMeasure);
        //????????????????????????
        dataDetail.remove("prevention_measure");
        dataDetail.put("prevention_measure",preventionMeasure);
        //??????????????????????????????????????????
        boolean repeatCheckFlag = false;

        // ????????????
        JSONArray attachmentInfos = (JSONArray) dataDetail.get("attachment_info");
        JSONArray mustUploadAttachments = new JSONArray();

        for (Iterator<Object> iterator = attachmentModels.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            boolean status = true;
            for (Iterator<Object> it = attachmentInfos.iterator();it.hasNext();) {
                JSONObject attach = (JSONObject)it.next();
                if(!repeatCheckFlag){
                    repeatCheckFlag = "SE001010".equals(attach.getString("attachment_belong_stage"));
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

        for (Iterator<Object> ite = planManagementInfo.iterator(); ite.hasNext();) {
            JSONObject obj = (JSONObject)ite.next();
            if ("SE001010".equals(obj.get("step_no"))) {
                if (obj.get("attachment_upload_flag").equals("Y")) {
                    if (Collections.isEmpty(mustUploadAttachments)) {
                        // ???????????????
                        List<AttachmentEntity> attachmentEntities = attachmentMapper.getAttachments((Long) DWServiceContext.getContext().getProfile().get("tenantSid"), dataInstanceVo.getOid());
                        if (Collections.isEmpty(attachmentEntities) && !repeatCheckFlag) {
                            throw new DWRuntimeException("attachment_upload_flag is Y, so attachment must be uploaded ! ");
                        }
                        break;
                    }
                }
            }
        }

        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),Question8DSolveEnum.precaution.getCode());
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());

        // ????????????
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,dataInstanceEntity);

        return eightDQuestionBiz.handleUpdateForPreventionMeasure(entity,attachmentEntities,dataInstanceEntity);

    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     * @param resultJsonObject
     * @return
     */
    private JSONObject getDataDetail(JSONObject resultJsonObject) {
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        return questionResult.getJSONObject(0);
    }

}
