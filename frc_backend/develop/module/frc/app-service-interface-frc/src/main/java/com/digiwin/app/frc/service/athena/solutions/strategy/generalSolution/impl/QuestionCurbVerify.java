package com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.dao.DWDao;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.config.annotation.ParamValidationHandler;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.other.QuestionInfoModel;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.QuestionTraceStrategy;
import com.digiwin.app.frc.service.athena.util.DateUtil;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.TransferTool;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName QuestionDistribution
 * @Description ????????????
 * @Author author
 * @Date 2021/11/21 1:00
 * @Version 1.0
 **/
public class QuestionCurbVerify implements QuestionTraceStrategy {

    @Autowired
    DWDao dwDao;

    /**
     * ????????????new?????????????????????????????????????????????
     */
    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);



    @Override
    public JSONObject updateQuestionTrace(String parameters) throws Exception {
        JSONObject resultJsonObject = JSONObject.parseObject(parameters);

        // question_info??????????????????
        QuestionInfoModel questionInfoModel = TransferTool.convertString2Model(resultJsonObject.getJSONArray("question_info").getString(0), QuestionInfoModel.class);
        // ????????????
        ParamValidationHandler.validateParams(questionInfoModel);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONObject questionCurbVerifyInfo = resultJsonObject.getJSONArray("curb_verify_info").getJSONObject(0);
        JSONArray attachmentInfos = resultJsonObject.getJSONArray("attachment_info");

        JSONObject re =  handleDetail(entity,questionCurbVerifyInfo,attachmentInfos,questionInfoModel.getOid());
        return re;
    }

    @Override
    public JSONArray insertUnapprovedQuestionTrace(QuestionActionTraceEntity entity) {
        // ???????????????????????????????????????????????????
        List<BeforeQuestionVo> curbDistributionList = actionTraceMapper.getBeforeQuestionTraceForList1(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                QuestionSolveEnum.question_curb_distribution.getCode());

        if (CollectionUtils.isEmpty(curbDistributionList)) {
            throw new DWRuntimeException("get SE002002 task card error,maybe param is null");
        }
        int step = curbDistributionList.get(0).getPrincipalStep();

        // ???????????????????????????????????????(??????????????????????????????)
        List<BeforeQuestionVo> beforeQuestionVoList = actionTraceMapper.getBeforeQuestionTraceForList(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                QuestionSolveEnum.question_curb.getCode(),step);
        if (CollectionUtils.isEmpty(beforeQuestionVoList)) {
            throw new DWRuntimeException("get SE002003 task card error,maybe param is null");
        }
        // ?????????-???????????????-?????? entity
        String dataInstanceOid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        String oid = IdGenUtil.uuid();
        entity.setOid(oid);
        // ??????????????????
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

        // ??????????????????
        DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVoList,curbDistributionList.get(0),entity);
        actionTraceBiz.insertActionTraceForCurb(entity,dataInstanceEntity);
        JSONArray responseParam = new JSONArray();
        JSONObject responseObject = new JSONObject();
        responseObject.put("pending_approve_question_id",oid);
        responseParam.add(responseObject);
        return responseParam;
    }

    /**
     * ???????????????????????????????????????????????????
     * @param oid ???????????????????????????????????? ??????
     * @param dataInstanceOid ???????????????????????????????????????????????? ??????
     * @param beforeQuestionVoList ?????????????????????
     */
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid,List<BeforeQuestionVo> beforeQuestionVoList,BeforeQuestionVo curbDistributeEntity,QuestionActionTraceEntity traceEntity){
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSONObject.parseObject(curbDistributeEntity.getDataContent());
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = questionResult.getJSONObject(0);
        //????????????????????????
        DateUtil.assignValueForCommonExpectCompleteTime(traceEntity,dataDetail,QuestionSolveEnum.question_curb_distribution.getCode());

        JSONArray curb_distribute_info = dataDetail.getJSONArray("curb_distribute_info");
        JSONObject curb_distribute = curb_distribute_info.getJSONObject(0);
        String curbRequest = curb_distribute.getString("curb_request");
        JSONArray curb_distribute_details = curb_distribute.getJSONArray("curb_distribute_detail");
        dataDetail.remove("curb_distribute_info");

        JSONArray curb_verify_content = new JSONArray();

        // ??????????????????
        JSONArray attachments = new JSONArray();
        for (Iterator<Object> iterator = curb_distribute_details.iterator(); iterator.hasNext();) {
            JSONObject de = (JSONObject)iterator.next();

            for (BeforeQuestionVo beforeQuestionVo:beforeQuestionVoList) {
                JSONObject curbObject = JSONObject.parseObject(beforeQuestionVo.getDataContent());
                // ??????????????? question_result
                JSONArray curbQuestionResult = curbObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
                JSONObject curbDataDetail = curbQuestionResult.getJSONObject(0);

                JSONArray files = curbDataDetail.getJSONArray("attachment_info");
                JSONArray curbDistributeInfo = curbDataDetail.getJSONArray("curb_distribute_info");
                JSONObject curb = curbDistributeInfo.getJSONObject(0);
                // ????????????
                String curbFeedBack = curb.getString("curb_feedback");
                JSONArray curbDistributeDetail =  curb.getJSONArray("curb_distribute_detail");

                int flag = 0;

                for (Iterator<Object> ded = curbDistributeDetail.iterator(); ded.hasNext();) {
                    JSONObject curba = (JSONObject)ded.next();
                    if (StringUtils.isEmpty(curba.get("uuid"))) {
                        throw new DWRuntimeException(" SE003 curb_distribute_info uuid is null");
                    }
                    if (StringUtils.isEmpty(de.get("uuid"))) {
                        throw new DWRuntimeException(" SE002 curb_distribute_info uuid is null");
                    }
                    if (de.get("uuid").equals(curba.get("uuid"))) {
                        attachments.addAll(files);
                        curba.put("curb_feedback",curbFeedBack);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        if (null != beforeQuestionVo.getActualCompleteDate()) {
                            String dateString = formatter.format(beforeQuestionVo.getActualCompleteDate());
                            curba.put("actual_complete_date",dateString);
                        }else {
                            curba.put("actual_complete_date","");
                        }
                        curba.put("process_result","4");
                        curba.put("question_id",beforeQuestionVo.getOid());
                        curb_verify_content.add(curba);
                        flag = 1;
                        break;
                    }
                }
                if (flag ==1) {
                    break;
                }
            }

        }
        JSONArray curbVerifyInfo =new JSONArray();
        JSONObject curbVerify = new JSONObject();
        curbVerify.put("curb_request",curbRequest);
        curbVerify.put("curb_verify_detail",curb_verify_content);
        curbVerifyInfo.add(curbVerify);

        dataDetail.put("curb_verify_info",curbVerifyInfo);

        dataDetail.remove("attachment_info");
        dataDetail.put("attachment_info",packageAttachments(attachments));
        // ??????value???null?????????
        JSONObject.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        // jsonObject???string
        String dataContentString = JSON.toJSONString(resultJsonObject);
        // ?????????????????? ????????????
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
     * @param questionCurbVerifyInfo ?????? ?????????????????????
     * @param oid ????????????????????????
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONObject questionCurbVerifyInfo,JSONArray attachmentModels, String oid) throws IOException {
        // ???????????? ???????????????
        // ???????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        JSONObject resultJsonObject = JSONObject.parseObject(dataInstanceVo.getDataContent());
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray("question_result");
        JSONObject dataDetail = (JSONObject) questionResult.get(0);
        JSONArray curbVerifyInfo = (JSONArray) dataDetail.get("curb_verify_info");
        JSONObject curbVerify = (JSONObject) curbVerifyInfo.get(0);
        JSONArray jsonArray=curbVerify.getJSONArray("curb_verify_detail");
        //????????????????????????
        JSONArray front=questionCurbVerifyInfo.getJSONArray("curb_verify_detail");
        //?????????????????????????????????
        for(Iterator iterator = jsonArray.iterator();iterator.hasNext();){
            JSONObject objectBefore = (JSONObject) iterator.next();
            String date=objectBefore.get("expect_complete_date").toString();
            String second = date.substring(10);
            for(Iterator iteratorNew = front.iterator();iteratorNew.hasNext();){
                JSONObject objectNow = (JSONObject) iteratorNew.next();
                String dateNew=objectNow.get("expect_complete_date").toString();
                if(objectBefore.get("uuid").equals(objectNow.get("uuid"))){
                    objectNow.remove("expect_complete_date");
                    objectNow.put("expect_complete_date",dateNew+second);
                    break;
                }
            }
        }
        curbVerify.remove("curb_verify_detail");
        curbVerify.put("curb_verify_detail",questionCurbVerifyInfo.get("curb_verify_detail"));

        JSONArray attachmentInfos = (JSONArray) dataDetail.get("attachment_info");

        // ?????????????????????????????????
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
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE002004");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // ????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // ????????????
       return actionTraceBiz.handleUpdateForDistribution(questionActionTraceEntity,attachmentEntities,entity);
    }
}
