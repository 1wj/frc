package com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWRuntimeException;
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
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName QuestionClose
 * @Description ????????????
 * @Author author
 * @Date 2021/11/22 21:54
 * @Version 1.0
 **/
public class QuestionClose implements QuestionTraceStrategy {

    /**
     * ????????????new?????????????????????????????????????????????
     */
    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    AttachmentMapper attachmentMapper = SpringContextHolder.getBean(AttachmentMapper.class);

    @Override
    public JSONObject updateQuestionTrace(String parameters) throws Exception {
        JSONObject resultJsonObject = JSONObject.parseObject(parameters);

        // question_info??????????????????
        QuestionInfoModel questionInfoModel = TransferTool.convertString2Model(resultJsonObject.getJSONArray("question_info").getString(0), QuestionInfoModel.class);
        // ????????????
        ParamValidationHandler.validateParams(questionInfoModel);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONObject questionCurbCloseInfo = resultJsonObject.getJSONObject("question_closure");
        JSONArray attachmentInfos =  resultJsonObject.getJSONArray("attachment_info");
        JSONObject re =handleDetail(entity,questionCurbCloseInfo,attachmentInfos,questionInfoModel.getOid());
        return re;
    }

    @Override
    public JSONArray insertUnapprovedQuestionTrace(QuestionActionTraceEntity entity) throws Exception {
        // ???????????????????????????????????????(??????????????????????????????)
        List<BeforeQuestionVo> beforeQuestionVo = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                QuestionSolveEnum.question_verify.getCode());
        List<BeforeQuestionVo> distribution = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                QuestionSolveEnum.question_distribution.getCode());

        // ?????????-???????????????-?????? entity
        String dataInstanceOid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        String oid = IdGenUtil.uuid();
        entity.setOid(oid);
        // ??????????????????
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

        // ??????????????????
        DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVo.get(0),distribution.get(0),entity);
        actionTraceBiz.insertActionTrace(entity,dataInstanceEntity);

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
     * @param beforeQuestionVo ?????????????????????
     */
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid,BeforeQuestionVo beforeQuestionVo,BeforeQuestionVo distribution,QuestionActionTraceEntity traceEntity){
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSONObject.parseObject(beforeQuestionVo.getDataContent());
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = (JSONObject) questionResult.get(0);
        DateUtil.assignValueForCommonExpectCompleteTime(traceEntity,dataDetail,QuestionSolveEnum.question_close.getCode());

        // ???????????????????????????
        JSONArray curbCloseInfo = new JSONArray();
        JSONObject curbClose = new JSONObject();
        curbClose.put("question_summary","");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if (null != distribution.getExpectCompleteDate()) {
            String dateString = formatter.format(distribution.getExpectCompleteDate());
            curbClose.put("expect_complete_date",dateString);
            curbClose.put("actual_complete_date","");
        }else {
            curbClose.put("expect_complete_date","");
            curbClose.put("actual_complete_date","");
        }
        curbCloseInfo.add(curbClose);
        // ????????? ??????????????????-question_distribute_info
        dataDetail.put("question_closure",curbCloseInfo);

        // ?????? ????????????
        JSONArray questionVerifyInfos = (JSONArray) dataDetail.get("curb_verify_info");
        JSONObject questionVerifyInfo = (JSONObject) questionVerifyInfos.get(0);

        JSONArray curbVerifyInfo = new JSONArray();
        JSONObject curbVerify = new JSONObject();
        //?????? ???????????? -?????? ????????????+?????????+????????????
        curbVerify.put("curb_request",questionVerifyInfo.get("curb_request"));
        curbVerify.put("curb_verify_id",beforeQuestionVo.getQuestionSolveStep());
        curbVerify.put("curb_verify_name", MultilingualismUtil.getLanguage(beforeQuestionVo.getQuestionSolveStep()));
        curbVerify.put("process_person_id",beforeQuestionVo.getLiablePersonId());
        curbVerify.put("process_person_name",beforeQuestionVo.getLiablePersonName());

        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        if (null != beforeQuestionVo.getActualCompleteDate()) {
            String dateString = formatter1.format(beforeQuestionVo.getActualCompleteDate());
            curbVerify.put("process_date",dateString);
        }else {
            curbVerify.put("process_date","");
        }
        curbVerify.put("curb_verify_detail",questionVerifyInfo.get("curb_verify_detail"));
        curbVerifyInfo.add(curbVerify);
        dataDetail.remove("curb_verify_info");
        dataDetail.put("curb_verify_info",curbVerifyInfo);


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
     * ????????????????????????
     * @param questionCurbCloseInfo ?????? ?????????????????????
     * @param oid ????????????????????????
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONObject questionCurbCloseInfo,JSONArray attachmentModels, String oid) throws IOException {
        // ???????????? ???????????????
        // ???????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        JSONObject resultJsonObject = JSONObject.parseObject(dataInstanceVo.getDataContent());
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray("question_result");
        JSONObject dataDetail = (JSONObject) questionResult.get(0);

        // ????????????????????????
        JSONArray processInfos= (JSONArray) dataDetail.get("question_process_info");
        JSONObject processInfo = (JSONObject) processInfos.get(0);
        JSONArray distributeDetails= (JSONArray)processInfo.get("question_distribute_detail");


        // ???curb_verify_info????????????
        JSONArray questionCurbCloseInfos = (JSONArray) dataDetail.get("question_closure");
        JSONObject curbCloseInfo = (JSONObject) questionCurbCloseInfos.get(0);
        curbCloseInfo.put("question_summary",questionCurbCloseInfo.get("question_summary"));

        curbCloseInfo.put("expect_complete_date",questionCurbCloseInfo.get("expect_complete_date"));
        curbCloseInfo.put("actual_complete_date",questionCurbCloseInfo.get("actual_complete_date"));

        JSONArray attachmentInfos = (JSONArray) dataDetail.get("attachment_info");

        //todo ?????????????????????????????????
        JSONArray mustUploadAttachments = new JSONArray();

        for (Iterator iterator = attachmentModels.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            boolean status = true;
            for (Iterator it = attachmentInfos.iterator();it.hasNext();) {
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

        if (questionActionTraceEntity.getQuestionProcessStatus() != 6 && questionActionTraceEntity.getQuestionProcessResult() != 5) {
            for (Iterator ite = distributeDetails.iterator(); ite.hasNext();) {
                JSONObject obj = (JSONObject)ite.next();
                if ("SE002005".equals(obj.get("step_id"))) {
                    if (obj.get("attachment_upload_flag").equals("Y")) {
                        if (Collections.isEmpty(mustUploadAttachments)) {
                            // ???????????????
                            List<AttachmentEntity> attachmentEntities = attachmentMapper.getAttachments((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),dataInstanceVo.getOid());
                            if (Collections.isEmpty(attachmentEntities)) {
                                throw new DWRuntimeException("attachment must upload");
                            }
                            break;
                        }
                    }
                }
            }
        }


        // ????????????
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE002005");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // ????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // ????????????
        return actionTraceBiz.handleUpdateForDistribution(questionActionTraceEntity,attachmentEntities,entity);
    }

}
