package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.universal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
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
import com.digiwin.app.frc.service.athena.qdh.domain.vo.QuestionDetailVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.PermanentMeasureVerifyInfoModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.QuestionInfoUniversalModel;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: xieps
 * @Date: 2022/4/11 14:12
 * @Version 1.0
 * @Description D5-2 ??????????????????
 */
public class QuestionPermanentMeasuresExecuteVerify implements QuestionHandlerStrategy {

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);


    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    UniversalQuestionBiz universalQuestionBiz =  SpringContextHolder.getBean(UniversalQuestionBiz.class);

    @Autowired
    IamEocBiz iamEocBiz =   SpringContextHolder.getBean(IamEocBiz.class);

    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        JSONObject resultJsonObject = JSON.parseObject(parameters);

        //1.string???model??????????????????????????????model
        PermanentMeasureVerifyInfoModel measureVerifyInfoModel = TransferTool.convertString2Model(parameters, PermanentMeasureVerifyInfoModel.class);

        //2.????????????
        ParamCheckUtil.checkPermanentMeasureVerifyParams(measureVerifyInfoModel);

        //3.?????????????????????
        QuestionInfoUniversalModel questionInfoModel = measureVerifyInfoModel.getQuestionInfoUniversalModel();
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONArray permanentMeasureVerify = resultJsonObject.getJSONArray("lasting_measure_execute_verify");
        JSONArray attachmentInfos =  resultJsonObject.getJSONArray("attachment_info");
        JSONObject questionConfirm =  resultJsonObject.getJSONObject("question_confirm");

        return handleDetail(entity,permanentMeasureVerify,attachmentInfos,questionConfirm,questionInfoModel.getOid());
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // ?????????????????????????????????????????????????????????
            List<BeforeQuestionVo> permanentMeasureList = actionTraceMapper.getBeforeQuestionTraceForList1(TenantTokenUtil.getTenantSid(),
                    entity.getQuestionRecordOid(), entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                    QuestionUniversalSolveEnum.permanent_measures.getCode());
            int step = permanentMeasureList.get(0).getPrincipalStep();

            // ???????????????????????????????????????(??????????????????????????????)
            List<BeforeQuestionVo> beforeQuestionVoList = actionTraceMapper.getBeforeQuestionTraceForList(TenantTokenUtil.getTenantSid(),
                    entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                    QuestionUniversalSolveEnum.permanent_measures_execute.getCode(),step);

            // ?????????-???????????????-?????? entity
            String dataInstanceOid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            String oid = IdGenUtil.uuid();
            entity.setOid(oid);

            // ??????????????????
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
            // ??????????????????
            DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVoList,permanentMeasureList.get(0),entity);
            actionTraceBiz.insertActionTraceForCurb(entity,dataInstanceEntity);

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
            QuestionDetailVo questionDetailVo = actionTraceMapper.getQuestionTrace(entity.getOid());
            // ??????????????????
            JSONObject re = createDataInstanceForHandleBack(entity,questionDetailVo);
            responseParam.add(re);
        }
        responseObject.put("return_data",responseParam);
        return responseObject;
    }


    /**
     * ???????????????????????????????????????????????????
     * @param oid ???????????????????????????????????? ??????
     * @param dataInstanceOid ???????????????????????????????????????????????? ??????
     * @param beforeQuestionVoList ?????????????????????
     */
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid,List<BeforeQuestionVo> beforeQuestionVoList,BeforeQuestionVo containmentMeasureEntity,QuestionActionTraceEntity actionEntity){
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(containmentMeasureEntity.getDataContent());

        // ??????????????? question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);

        //??????????????????????????????????????????
        DateUtil.assignValueForExpectCompleteTime(actionEntity,dataDetail, QuestionUniversalSolveEnum.permanent_measures.getCode());


        JSONArray permanentMeasureInfo = (JSONArray) dataDetail.get("lasting_measure");

        dataDetail.remove("lasting_measure");

        JSONArray measureVerifyContent = new JSONArray();

        JSONArray attachments = new JSONArray();
        //????????????????????????
        for (Iterator<Object> iterator = permanentMeasureInfo.iterator(); iterator.hasNext();) {
            JSONObject de = (JSONObject)iterator.next();
            for (BeforeQuestionVo beforeQuestionVo:beforeQuestionVoList) {
                JSONObject curbObject = JSON.parseObject(beforeQuestionVo.getDataContent());
                // ??????????????? question_result
                JSONObject measureExecuteDetail = curbObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
                JSONArray files = measureExecuteDetail.getJSONArray("attachment_info");
                JSONArray measureExecuteInfo = (JSONArray) measureExecuteDetail.get("lasting_measure_execute");
                int flag = 0;
                for (Iterator<Object> ded = measureExecuteInfo.iterator(); ded.hasNext();) {
                    JSONObject curba = (JSONObject) ded.next();
                    //????????????????????????????????????
                    // ???????????????uuid  ????????????????????????????????????????????????????????????????????????????????????????????????
                    // ??????????????????????????????  ?????????????????????????????????
                    if (de.get("uuid").equals(curba.get("uuid"))) {
                        boolean filter = de.containsKey("is_history_data") && "Y".equals(de.get("is_history_data"));
                        if(!filter){
                            attachments.addAll(files);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            if (null != beforeQuestionVo.getActualCompleteDate()) {
                                String dateString = formatter.format(beforeQuestionVo.getActualCompleteDate());
                                curba.put("actual_finish_date", dateString);
                            } else {
                                curba.put("actual_finish_date", "");
                            }
                            curba.put("verify_date", formatter.format(new Date()));
                            curba.put("verify_illustrate", "");
                            curba.put("verify_status", "Y");
                            curba.put("question_id",beforeQuestionVo.getOid());

                            measureVerifyContent.add(curba);
                            flag = 1;
                            break;
                        }
                    }
                }
                if (flag ==1) {
                    break;
                }
            }

        }

        //?????????????????????????????????????????????????????? is_history_date???Y
        //????????????????????????????????????
        List<BeforeQuestionVo> historicalDataList = actionTraceMapper.getHistoricalData(TenantTokenUtil.getTenantSid(),actionEntity.getQuestionRecordOid(),
                actionEntity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionUniversalSolveEnum.permanent_measures_execute_verify.getCode(),actionEntity.getPrincipalStep());
        if(!historicalDataList.isEmpty()){
                JSONObject parseObject = JSON.parseObject(historicalDataList.get(0).getDataContent());
                JSONObject questionResult = parseObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
                JSONArray measureExecuteVerify = questionResult.getJSONArray("lasting_measure_execute_verify");
                for ( Iterator<Object> iterator = measureExecuteVerify.iterator();iterator.hasNext();){
                    JSONObject obj = (JSONObject) iterator.next();
                    obj.put("is_history_data","Y");
                    measureVerifyContent.add(obj);
                }
        }

        Collections.reverse(measureVerifyContent);

        dataDetail.put("lasting_measure_execute_verify",measureVerifyContent);
        dataDetail.remove("attachment_info");
        dataDetail.put("attachment_info",packageAttachments(attachments));
        // ??????value???null?????????
        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        // jsonObject???string
        String dataContentString = JSON.toJSONString(resultJsonObject);

        // ??????????????????????????????????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        entity.setOid(dataInstanceOid);
        entity.setDataContent(dataContentString);
        entity.setQuestionTraceOid(oid);
        return entity;
    }

    /**
     * ????????????,????????????
     * @param attachments ?????????????????????
     * @return JSONArray
     */
    public static JSONArray packageAttachments(JSONArray attachments){
        JSONArray tempArray = new JSONArray();
        for (int i = 0; i < attachments.size(); i++) {
            JSONObject file =attachments.getJSONObject(i);
            if (i == 0) {
                // ?????????????????????
                tempArray.add(file);
            }else {
                int flag = 1;//????????????????????? ??????
                for (int j = 0; j < tempArray.size(); j++) {
                    JSONObject tempFile =tempArray.getJSONObject(j);
                    if (file.getString("attachment_id").equals(tempFile.getString("attachment_id"))) {
                        flag = 0;
                    }
                }
                if (flag == 1) {
                    tempArray.add(file);
                }
            }
        }
        return tempArray;
    }



    /**
     * ????????????????????????
     * @param permanentMeasureVerify ?????? ?????????????????????
     * @param oid ????????????????????????
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONArray permanentMeasureVerify,JSONArray attachmentModels,JSONObject questionConfirm ,String oid) throws IOException {
        // ???????????? ???????????????
        // ???????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        // ??????????????? question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject("question_result");

        dataDetail.remove("lasting_measure_execute");
        //???????????????????????? ????????????????????????????????? ?????????????????????????????????????????????????????????
        JSONArray beforeMeasureExecuteVerify = dataDetail.getJSONArray("lasting_measure_execute_verify");
        for(Iterator<Object> iterator = beforeMeasureExecuteVerify.iterator();iterator.hasNext();){
            JSONObject objectBefore = (JSONObject) iterator.next();
            for(Iterator<Object> iterator1 = permanentMeasureVerify.iterator();iterator1.hasNext();){
                JSONObject objectNow = (JSONObject) iterator1.next();
                if(objectBefore.get("uuid").equals(objectNow.get("uuid"))){
                    objectNow.remove("expect_solve_date");
                    objectNow.put("expect_solve_date",objectBefore.getString("expect_solve_date"));
                    break;
                }
            }
        }
        dataDetail.remove("lasting_measure_execute_verify");
        dataDetail.put("lasting_measure_execute_verify",permanentMeasureVerify);
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

        // ????????????
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE003008");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // ????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // ????????????
        return universalQuestionBiz.handleUpdateForPermanentMeasureVerify(questionActionTraceEntity,attachmentEntities,entity);
    }


    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param entity
     * @param questionDetailVo
     * @return
     */
    private JSONObject createDataInstanceForHandleBack(QuestionActionTraceEntity entity, QuestionDetailVo questionDetailVo) throws Exception {
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(questionDetailVo.getDataContent());
        // ??????????????? question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);

        JSONArray lastingMeasureExecuteInfo = dataDetail.getJSONArray("lasting_measure_execute");
        String liablePersonId = null;
        String liablePersonName = null;
        for (Iterator<Object> iterator = lastingMeasureExecuteInfo.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject) iterator.next();
            if ("N".equals(obj.get("is_history_data"))) {
                obj.put("execute_status", "1");
                liablePersonId = obj.getString("liable_person_id");
                liablePersonName = obj.getString("liable_person_name");
            }
        }
        // ?????????-???????????????-?????? entity
        String dataInstanceOid = IdGenUtil.uuid();
        String oid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        entity.setOid(oid);
        entity.setLiablePersonId(liablePersonId);
        entity.setLiablePersonName(liablePersonName);
        entity.setReturnId((String) DWServiceContext.getContext().getProfile().get("userId"));
        entity.setReturnName((String) DWServiceContext.getContext().getProfile().get("userName"));
        // ??????????????????
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
        //????????????????????????
        entity.setExpectCompleteDate(questionDetailVo.getExpectCompleteDate());
        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        // jsonObject???string
        String dataContentString = JSON.toJSONString(resultJsonObject);

        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        dataInstanceEntity.setOid(dataInstanceOid);
        dataInstanceEntity.setDataContent(dataContentString);
        dataInstanceEntity.setQuestionTraceOid(oid);
        //???????????????????????????????????????????????????
        actionTraceMapper.insertActionTrace(entity);
        dataInstanceMapper.insertDataInstance(dataInstanceEntity);

        // 5-??????response??????
        PendingQuestionVo vo = new PendingQuestionVo();
        BeanUtils.copyProperties(entity, vo);
        Map<String,Object> user = iamEocBiz.getEmpUserId(liablePersonId);
        vo.setLiablePersonId((String) user.get("id"));
        vo.setLiablePersonName((String) user.get("name"));
        vo.setEmpId(liablePersonId);
        vo.setEmpName(liablePersonName);
        return JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
    }


}
