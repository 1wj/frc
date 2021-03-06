package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.eightD;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.Const.qdh.update.QuestionUpdateConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.Question8DSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.EightDQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.IamEocBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.QuestionDetailVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.QuestionInfo8DSecondModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.RectifyVerifyModel;
import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.PendingQuestionVo;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.frc.service.athena.util.rqi.EocUtils;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName QuestionRectifyVerify
 * @Description ??????????????????
 * @Author HeX
 * @Date 2022/3/8 11:09
 * @Version 1.0
 **/
public class QuestionRectifyVerify implements QuestionHandlerStrategy {
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
    EightDQuestionBiz eightDQuestionBiz =  SpringContextHolder.getBean(EightDQuestionBiz.class);

    @Autowired
    IamEocBiz iamEocBiz =  SpringContextHolder.getBean(IamEocBiz.class);



    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        JSONObject resultJsonObject = JSON.parseObject(parameters);

        //1.string???model??????????????????????????????model
        RectifyVerifyModel keyReasonCorrectModel = TransferTool.convertString2Model(parameters, RectifyVerifyModel.class);

        //2.????????????
        try {
            ParamCheckUtil.checkkeyQuestionRectifyVerify(keyReasonCorrectModel);
        } catch (DWArgumentException e) {
            e.printStackTrace();
        }

        //3.?????????????????????
        QuestionInfo8DSecondModel questionInfoModel = keyReasonCorrectModel.getQuestionInfos().get(0);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);
        JSONArray correctiveMeasureVerify = resultJsonObject.getJSONArray("corrective_measure_verify");
        JSONArray attachmentInfo=resultJsonObject.getJSONArray("attachment_info");
        //4.??????????????????
        return updateDetailInfo(entity,correctiveMeasureVerify,attachmentInfo,questionInfoModel.getOid());
    }
    private JSONObject updateDetailInfo(QuestionActionTraceEntity entity, JSONArray correctiveMeasureVerify, JSONArray attachmentModels, String oid) throws OperationException{
        // ?????????????????????????????????????????????????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        if (dataInstanceVo == null) {
          throw new OperationException("???????????????id??????????????????????????????id");
        }
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        JSONObject dataDetail = getDataDetail(resultJsonObject);

        //????????????????????????????????????????????????????????????
        JSONArray  questionBasicInfo=dataDetail.getJSONArray(QuestionUpdateConst.question_basic_info);

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(ReuseConstant.YEAR_MONTH_DAY);
        for (Iterator<Object> iterator =questionBasicInfo.iterator();iterator.hasNext();){
            JSONObject qObject = (JSONObject)iterator.next();
            for (Iterator<Object> ite =correctiveMeasureVerify.iterator();ite.hasNext();){
                JSONObject correctObject = (JSONObject)ite.next();
                if(correctObject.containsKey(ReuseConstant.expect_solve_date)){
                    try {
                        String correctDate =correctObject.get(ReuseConstant.expect_solve_date).toString();
                        String qDate=qObject.get(ReuseConstant.expect_solve_date).toString();
                        long timestamp = simpleDateFormat.parse(qDate).getTime();
                        long correctTimestamp = simpleDateFormat.parse(correctDate).getTime();
                        if (correctTimestamp>timestamp){
                            throw new OperationException("??????????????????: "+correctDate+"?????????????????????????????????: "+qDate);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //????????????????????????????????????????????????????????????
        JSONArray  correctiveMeasureVerifyBefore=dataDetail.getJSONArray("corrective_measure_verify");
        //?????????????????????????????????
        DateUtil.measuresExecuteVerify(correctiveMeasureVerify,correctiveMeasureVerifyBefore);
        //????????????????????????
        dataDetail.remove("corrective_measure_verify");
        dataDetail.put("corrective_measure_verify",correctiveMeasureVerify);
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
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(), Question8DSolveEnum.correct_verify.getCode());
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());

        // ????????????
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,dataInstanceEntity);

        return eightDQuestionBiz.rectifyVerifyBuilder(entity,attachmentEntities,dataInstanceEntity);
    }

    private JSONObject getDataDetail(JSONObject resultJsonObject) {
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        return questionResult.getJSONObject(0);
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception  {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // ?????????????????????????????????????????????????????????
            List<BeforeQuestionVo> keyReasonCorrectList = actionTraceMapper.getBeforeQuestionTraceForList1(TenantTokenUtil.getTenantSid(),
                    entity.getQuestionRecordOid(), entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                    Question8DSolveEnum.key_reason_correct.getCode());
            int step = keyReasonCorrectList.get(0).getPrincipalStep();

            // ???????????????????????????????????????(??????????????????????????????)
            List<BeforeQuestionVo> beforeQuestionVoList = actionTraceMapper.getBeforeQuestionTraceForList(TenantTokenUtil.getTenantSid(),
                    entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                    Question8DSolveEnum.correct_execute.getCode(),step);

            //D3-2
            List<BeforeQuestionVo> DThree = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),Question8DSolveEnum.containment_measure_verify.getCode());

            // ?????????-???????????????-?????? entity
            String dataInstanceOid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            String oid = IdGenUtil.uuid();
            entity.setOid(oid);

            // ??????????????????
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
            // ??????????????????
            DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVoList,keyReasonCorrectList.get(0),DThree,entity);
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

    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid,List<BeforeQuestionVo> beforeQuestionVoList,BeforeQuestionVo keyReasonEntity,List<BeforeQuestionVo> DThree,QuestionActionTraceEntity traceEntity){
        // ??????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(keyReasonEntity.getDataContent());

        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = (JSONObject) questionResult.get(0);
        //+ ??????????????????
        DateUtil.assignValueForExpectCompleteTime(traceEntity,dataDetail,Question8DSolveEnum.key_reason.getCode());
        JSONArray containmentMeasureInfo = (JSONArray) dataDetail.get("corrective_measure");
        dataDetail.remove("corrective_measure");

        JSONArray measureVerifyContent = new JSONArray();

        JSONArray attachments = new JSONArray();

        JSONObject newObject = JSON.parseObject(beforeQuestionVoList.get(0).getDataContent());
        // ??????????????? question_result
        JSONArray newExecuteResult = newObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject newMeasureExecuteDetail = (JSONObject) newExecuteResult.get(0);

        if (!Collections.isEmpty(DThree)) {
            //D3-2????????????
            JSONObject DthreeDetail = JSON.parseObject(DThree.get(0).getDataContent());
            JSONArray dthree = DthreeDetail.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
            JSONObject three = dthree.getJSONObject(0);
            if (three.containsKey("containment_measure_verify")) {
                JSONObject verifyDetailInfo = new JSONObject();
                //?????? ???????????? -?????? ????????????+?????????+????????????
                verifyDetailInfo.put(ReuseConstant.liable_person_id, DThree.get(0).getLiablePersonId());
                verifyDetailInfo.put(ReuseConstant.liable_person_name, DThree.get(0).getLiablePersonName());
                if (null != DThree.get(0).getActualCompleteDate()) {
                    SimpleDateFormat formatter = new SimpleDateFormat(ReuseConstant.YEAR_MONTH_DAY);
                    String dateString = formatter.format(DThree.get(0).getActualCompleteDate());
                    verifyDetailInfo.put("process_date", dateString);
                } else {
                    verifyDetailInfo.put("process_date", "");
                }
                // ????????????????????????
                JSONArray measureVerify = three.getJSONArray("containment_measure_verify");
                newMeasureExecuteDetail.remove("containment_measure_verify");
                verifyDetailInfo.put("containment_measure_verify_detail", measureVerify);
                newMeasureExecuteDetail.put("containment_measure_verify", verifyDetailInfo);
            }
        }
        //????????????????????????
        for (Iterator<Object> iterator = containmentMeasureInfo.iterator(); iterator.hasNext();) {
            JSONObject de = (JSONObject)iterator.next();
            for (BeforeQuestionVo beforeQuestionVo:beforeQuestionVoList) {
                JSONObject curbObject = JSON.parseObject(beforeQuestionVo.getDataContent());
                // ??????????????? question_result
                JSONArray containmentMeasureExecuteResult = curbObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
                JSONObject measureExecuteDetail = (JSONObject) containmentMeasureExecuteResult.get(0);
                JSONArray files = measureExecuteDetail.getJSONArray("attachment_info");
                JSONArray measureExecuteInfo = (JSONArray) measureExecuteDetail.get("corrective_measure_execute");
                int flag = 0;
                for (Iterator<Object> ded = measureExecuteInfo.iterator(); ded.hasNext();) {
                    JSONObject curba = (JSONObject) ded.next();
                    JSONObject mapObject =new JSONObject();
                    //????????????????????????????????????
                    // ???????????????uuid  ????????????????????????????????????????????????????????????????????????????????????????????????
                    // ??????????????????????????????  ?????????????????????????????????
                    if (de.get("uuid").equals(curba.get("uuid"))) {
                        attachments.addAll(files);
                        SimpleDateFormat formatter = new SimpleDateFormat(ReuseConstant.YEAR_MONTH_DAY);
                        mapObject.put("corrective_content", curba.get("corrective_content"));
                        mapObject.put("corrective_execute_illustrate", curba.get("corrective_execute_illustrate"));
                        mapObject.put("corrective_person_id", curba.get("corrective_person_id"));
                        mapObject.put("corrective_person_name", curba.get("corrective_person_name"));
                        mapObject.put("corrective_status",curba.get("complete_status"));
                        mapObject.put("process_work_hours",curba.get("process_work_hours"));
                        mapObject.put("process_work_date",curba.get("corrective_date"));
                        mapObject.put("verify_date", formatter.format(new Date()));
                        mapObject.put("expect_solve_date", curba.get("expect_solve_date"));
                        mapObject.put("verify_illustrate", "");
                        mapObject.put("verify_status", "Y");
                        mapObject.put("uuid",curba.get("uuid"));
                        mapObject.put("question_id",beforeQuestionVo.getOid());
                        measureVerifyContent.add(mapObject);
                        flag = 1;
                        break;
                    }
                }
                if (flag ==1) {
                    break;
                }
            }
        }
        newMeasureExecuteDetail.put("corrective_measure_verify",measureVerifyContent);
        newMeasureExecuteDetail.remove("attachment_info");
        newMeasureExecuteDetail.put("attachment_info",packageAttachments(attachments));
        // ??????value???null?????????

        JSON.toJSONString(newMeasureExecuteDetail, SerializerFeature.WriteMapNullValue);
        // jsonObject???string
        String dataContentString = JSON.toJSONString(newObject);

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
        JSONObject dataDetail = (JSONObject) resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).get(0);
        //????????????????????????
        DateUtil.assignValueForExpectCompleteTime(entity,dataDetail,Question8DSolveEnum.key_reason.getCode());
        JSONArray lastingMeasureExecuteInfo = dataDetail.getJSONArray("corrective_measure_execute");
        String liablePersonId = "";
        String liablePersonName = "";
        for (Iterator<Object> iterator = lastingMeasureExecuteInfo.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            obj.put("complete_status","1");
            liablePersonId= obj.getString("corrective_person_id");
            liablePersonName= obj.getString("corrective_person_name");
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
        return  JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
    }

}
