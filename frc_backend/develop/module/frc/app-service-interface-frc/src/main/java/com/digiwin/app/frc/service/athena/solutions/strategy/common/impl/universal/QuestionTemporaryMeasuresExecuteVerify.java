package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.universal;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUniversalSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.EightDQuestionBiz;
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
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.QuestionInfoUniversalModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.TemporaryMeasuresExecuteVerifyInfoModel;
import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.PendingQuestionVo;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.frc.service.athena.util.rqi.EocUtils;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *???????????? ????????????????????????
 * @author cds
 * @date 2022/4/7
 * @param
 * @return
 */

public class QuestionTemporaryMeasuresExecuteVerify implements QuestionHandlerStrategy {

    /**
     * ????????????(??????new????????????)
     */
    @Autowired
    ActionTraceMapper actionTraceMapper = SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    UniversalQuestionBiz universalQuestionBiz = SpringContextHolder.getBean(UniversalQuestionBiz.class);
    @Autowired
    DataInstanceMapper dataInstanceMapper = SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz = SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    EightDQuestionBiz eightDQuestionBiz = SpringContextHolder.getBean(EightDQuestionBiz.class);

    @Autowired
    IamEocBiz iamEocBiz = SpringContextHolder.getBean(IamEocBiz.class);

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        if (CollUtil.isEmpty(actionTraceEntityList)) {
            throw new OperationException("?????????????????????????????? ");
        }
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // ?????????????????????????????????????????????????????????
            List<BeforeQuestionVo> temporaryMeasures = actionTraceMapper.getBeforeQuestionTraceForList1(TenantTokenUtil.getTenantSid(),
                    entity.getQuestionRecordOid(), entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                    QuestionUniversalSolveEnum.temporary_measures.getCode());
            int step = temporaryMeasures.get(0).getPrincipalStep();

            // ???????????????????????????????????????(??????????????????????????????)
            List<BeforeQuestionVo> beforeQuestionVoList = actionTraceMapper.getBeforeQuestionTraceForList(TenantTokenUtil.getTenantSid(),
                    entity.getQuestionRecordOid(), entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                    QuestionUniversalSolveEnum.temporary_measures_execute.getCode(), step);

            // ?????????-???????????????-?????? entity
            String dataInstanceOid = IdGenUtil.uuid();

            entity.setDataInstanceOid(dataInstanceOid);
            String oid = IdGenUtil.uuid();
            entity.setOid(oid);

            // ??????????????????
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(), entity.getQuestionRecordOid(), entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep() + 1);
            // ??????????????????
            DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid, oid, beforeQuestionVoList, temporaryMeasures.get(0),entity);
            actionTraceBiz.insertActionTraceForCurb(entity, dataInstanceEntity);

            // 5-??????response??????
            PendingQuestionVo vo = new PendingQuestionVo();
            BeanUtils.copyProperties(entity, vo);
            String userId = TenantTokenUtil.getUserId();
            vo.setEmpId(EocUtils.getEmpId(userId));
            vo.setEmpName(EocUtils.getEmpName(userId));
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            responseParam.add(jsonObject);


        }
        responseObject.put("return_data", responseParam);
        return responseObject;
    }

    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid, List<BeforeQuestionVo> beforeQuestionVoList, BeforeQuestionVo temporaryMeasures,QuestionActionTraceEntity actionEntity) {
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(temporaryMeasures.getDataContent());

        // ??????????????? question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);

        //??????????????????????????????????????????
        DateUtil.assignValueForExpectCompleteTime(actionEntity,dataDetail, QuestionUniversalSolveEnum.temporary_measures.getCode());

        JSONArray temporaryMeasure = (JSONArray) dataDetail.get("temporary_measure");

        JSONArray measureVerifyContent = new JSONArray();

        JSONArray attachments = new JSONArray();

        JSONArray planArrange = (JSONArray) dataDetail.get("plan_arrange");

        //???????????????id???name,?????????????????????
        String personId="";
        String personName="";
        for (Iterator<Object> plan = planArrange.iterator(); plan.hasNext();) {
            JSONObject planExecute = (JSONObject) plan.next();
            if (planExecute.get("step_no").equals("SE003002")){
                personId=planExecute.get("liable_person_id").toString();
                personName=planExecute.get("liable_person_name").toString();
            }
        }
        //????????????????????????
        for (Iterator<Object> iterator = temporaryMeasure.iterator(); iterator.hasNext(); ) {
            JSONObject de = (JSONObject) iterator.next();
            for (BeforeQuestionVo beforeQuestionVo : beforeQuestionVoList) {
                JSONObject curbObject = JSON.parseObject(beforeQuestionVo.getDataContent());
                // ??????????????? question_result
                JSONObject measureExecuteDetail = curbObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
                JSONArray files = measureExecuteDetail.getJSONArray("attachment_info");
                JSONArray temporaryMeasureExecute = (JSONArray) measureExecuteDetail.get("temporary_measure_execute");

                int flag = 0;
                for (Iterator<Object> ded = temporaryMeasureExecute.iterator(); ded.hasNext(); ) {
                    JSONObject execute = (JSONObject) ded.next();
                    //????????????????????????????????????
                    // ???????????????uuid  ????????????????????????????????????????????????????????????????????????????????????????????????
                    // ??????????????????????????????  ?????????????????????????????????
                    if (de.get("uuid").equals(execute.get("uuid"))) {
                        boolean filter = de.containsKey("is_history_data") && "Y".equals(de.get("is_history_data"));
                        if(!filter) {
                            attachments.addAll(files);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            if (null != beforeQuestionVo.getActualCompleteDate()) {
                                String dateString = formatter.format(beforeQuestionVo.getActualCompleteDate());
                                execute.put("actual_finish_date", dateString);
                            } else {
                                execute.put("actual_finish_date", "");
                            }
                            execute.put("verify_person_id", personId);
                            execute.put("verify_person_name", personName);
                            execute.put("verify_illustrate", "");
                            execute.put("verify_date", "");
                            //???????????????????????????
                            execute.put("verify_status", "Y");
                            execute.put("question_id", beforeQuestionVo.getOid());
                            measureVerifyContent.add(execute);
                            flag = 1;
                            break;
                        }
                    }
                }
                if (flag == 1) {
                    break;
                }
            }

        }

        //???????????????????????????????????????????????? is_history_date???Y
        List<BeforeQuestionVo> historicalDataList = actionTraceMapper.getHistoricalData(TenantTokenUtil.getTenantSid(),actionEntity.getQuestionRecordOid(),
                actionEntity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionUniversalSolveEnum.temporary_measures_execute_verify.getCode(),actionEntity.getPrincipalStep());
        if(!historicalDataList.isEmpty()){
                JSONObject parseObject = JSON.parseObject(historicalDataList.get(0).getDataContent());
                JSONObject questionResult = parseObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
                JSONArray measureExecuteVerify = questionResult.getJSONArray("temporary_measure_execute_verify");
                for ( Iterator<Object> iterator = measureExecuteVerify.iterator();iterator.hasNext();){
                    JSONObject obj = (JSONObject) iterator.next();
                    obj.put("is_history_data","Y");
                    measureVerifyContent.add(obj);
                }
        }

        Collections.reverse(measureVerifyContent);

        dataDetail.put("temporary_measure_execute_verify", measureVerifyContent);
        dataDetail.remove("attachment_info");
        dataDetail.put("attachment_info", packageAttachments(attachments));
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
     *
     * @param attachments ?????????????????????
     * @return JSONArray
     */
    public static JSONArray packageAttachments(JSONArray attachments) {
        JSONArray tempArray = new JSONArray();
        for (int i = 0; i < attachments.size(); i++) {
            JSONObject file = attachments.getJSONObject(i);
            if (i == 0) {
                // ?????????????????????
                tempArray.add(file);
            } else {
                int flag = 1;//????????????????????? ??????
                for (int j = 0; j < tempArray.size(); j++) {
                    JSONObject tempFile = tempArray.getJSONObject(j);
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

    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {

        JSONObject resultJsonObject = JSON.parseObject(parameters);

        //1.string???model??????????????????????????????model
        TemporaryMeasuresExecuteVerifyInfoModel measureVerifyInfoModel = TransferTool.convertString2Model(parameters, TemporaryMeasuresExecuteVerifyInfoModel.class);

        //2.????????????
        ParamCheckUtil.checkTemporaryMeauserExcuteVerify(measureVerifyInfoModel);

        //3.?????????????????????
        QuestionInfoUniversalModel questionInfoModel = measureVerifyInfoModel.getQuestionInfoUniversalModels();
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel, entity);

        JSONObject questionConfirm = resultJsonObject.getJSONObject("question_confirm");
        JSONArray attachmentInfos = resultJsonObject.getJSONArray("attachment_info");
        JSONArray temporaryMeasureExecuteVerify = resultJsonObject.getJSONArray("temporary_measure_execute_verify");

        return updateDetailInfo(entity, questionConfirm, temporaryMeasureExecuteVerify, attachmentInfos, questionInfoModel.getOid());
    }

    /**
     * ????????????????????????
     *
     * @param questionActionTraceEntity ?????? ?????????????????????
     * @param oid                          ????????????????????????
     * @throws JsonProcessingException
     */
    private JSONObject updateDetailInfo(QuestionActionTraceEntity questionActionTraceEntity, JSONObject questionConfirm, JSONArray temporaryMeasureExecuteVerify, JSONArray attachmentModels, String oid) {
        // ???????????? ???????????????
        // ???????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        // ??????????????? question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject("question_result");

        dataDetail.remove("question_confirm");
        dataDetail.put("question_confirm", questionConfirm);
        JSONArray temporaryVerifyDao = dataDetail.getJSONArray("temporary_measure_execute_verify");
        //???????????????????????????????????????????????????????????????
        for (Iterator<Object> iteratorNew = temporaryMeasureExecuteVerify.iterator(); iteratorNew.hasNext(); ) {
            //??????????????????????????????
            JSONObject newObj = (JSONObject) iteratorNew.next();
            for (Iterator<Object> iteratorDao = temporaryVerifyDao.iterator(); iteratorDao.hasNext(); ) {
                //???????????????????????????????????????
                JSONObject dao = (JSONObject) iteratorDao.next();
                if (dao.get("uuid").equals(newObj.get("uuid"))) {
                    //????????????????????????????????????
                    String processWorkHours = (String) newObj.get("process_work_hours");
                    if (NumberUtil.isNumericByRegEx(processWorkHours)){
                        double num=Double.parseDouble(processWorkHours);
                        if (num<0){
                            throw new DWRuntimeException("??????????????????????????????");
                        }
                    }else {
                        throw new DWRuntimeException("??????????????????????????????");
                    }
                    newObj.put("expect_solve_date", dao.get("expect_solve_date"));
                    break;
                }
            }
        }
        dataDetail.remove("temporary_measure_execute_verify");
        dataDetail.put("temporary_measure_execute_verify", temporaryMeasureExecuteVerify);
        JSONArray attachmentInfos = (JSONArray) dataDetail.get("attachment_info");

        //?????????????????????????????????
        JSONArray mustUploadAttachments = new JSONArray();

        for (Iterator<Object> iterator = attachmentModels.iterator(); iterator.hasNext(); ) {
            JSONObject obj = (JSONObject) iterator.next();
            boolean status = true;
            for (Iterator<Object> it = attachmentInfos.iterator(); it.hasNext(); ) {
                JSONObject attach = (JSONObject) it.next();
                if (attach.get("attachment_id").equals(obj.get("attachment_id"))) {
                    status = false;
                    break;
                }
            }
            if (status) {
                mustUploadAttachments.add(obj);
            }
        }

        // ????????????
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos, mustUploadAttachments, dataInstanceVo.getQuestionNo(), dataInstanceVo.getOid(), "SE003004");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // ????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo, entity);
        // ????????????
        return universalQuestionBiz.handleUpdateForTemporaryMeasuresExecuteVerify(questionActionTraceEntity, attachmentEntities, entity);
    }


    @Override
    public JSONObject handleBack(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            QuestionDetailVo questionDetailVo = actionTraceMapper.getQuestionTrace(entity.getOid());
            // ??????????????????
            JSONObject re = createForHandleBack(entity, questionDetailVo);
            responseParam.add(re);
        }
        responseObject.put("return_data", responseParam);
        return responseObject;
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param entity
     * @param questionDetailVo
     * @return
     */
    private JSONObject createForHandleBack(QuestionActionTraceEntity entity, QuestionDetailVo questionDetailVo) throws Exception {
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(questionDetailVo.getDataContent());
        // ??????????????? question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);

        JSONArray lastingMeasureExecuteInfo = dataDetail.getJSONArray("temporary_measure_execute");
        String liablePersonId = "";
        String liablePersonName = "";
        for (Iterator<Object> iterator = lastingMeasureExecuteInfo.iterator(); iterator.hasNext();) {
            JSONObject object = (JSONObject)iterator.next();
            if ("N".equals(object.get("is_history_data"))){
                object.put("execute_status","1");
                liablePersonId= object.getString("liable_person_id");
                liablePersonName= object.getString("liable_person_name");
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
        //????????????????????????
        entity.setExpectCompleteDate(questionDetailVo.getExpectCompleteDate());
        // ??????????????????
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

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