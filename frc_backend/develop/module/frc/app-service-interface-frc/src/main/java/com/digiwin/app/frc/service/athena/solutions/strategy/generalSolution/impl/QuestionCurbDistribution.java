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
import com.digiwin.app.frc.service.athena.qdh.domain.vo.QuestionDetailVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.QuestionTraceStrategy;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.service.DWServiceContext;
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
public class QuestionCurbDistribution implements QuestionTraceStrategy {


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
        JSONObject resultJsonObject = JSON.parseObject(parameters);

        // question_info??????????????????
        QuestionInfoModel questionInfoModel = TransferTool.convertString2Model(resultJsonObject.getJSONArray("question_info").getString(0), QuestionInfoModel.class);
        // ????????????
        ParamValidationHandler.validateParams(questionInfoModel);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONObject questionCurbDistributeInfo = resultJsonObject.getJSONArray("curb_distribute_info").getJSONObject(0);
        JSONArray attachmentInfos = (JSONArray) resultJsonObject.get("attachment_info");
        //  ???????????????????????? ??????question_id?????? ????????????record_oid?????????????????????????????????
        QuestionDetailVo questionDetailVo = actionTraceMapper.getQuestionTrace(questionInfoModel.getOid());
        if (!StringUtils.isEmpty(questionDetailVo.getQuestionNo())) {
            List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(), questionDetailVo.getQuestionRecordId(),
                    questionDetailVo.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionSolveEnum.question_distribution.getCode());
            // ????????????????????????????????? string???json
            JSONObject re = JSON.parseObject(beforeQuestionVos.get(0).getDataContent());
            // ??????????????? question_result
            JSONArray questionResult = re.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
            JSONObject dataDetail = (JSONObject) questionResult.get(0);
            JSONArray questionDistributeInfos = (JSONArray) dataDetail.get("question_distribute_info");
            JSONObject questionDistributeInfo = (JSONObject) questionDistributeInfos.get(0);


        }
        JSONObject response = handleDetail(entity,questionCurbDistributeInfo,attachmentInfos,questionInfoModel.getOid());
        return response;
    }

    @Override
    public JSONArray insertUnapprovedQuestionTrace(QuestionActionTraceEntity entity) throws Exception {
        // ???????????????????????????????????????(??????????????????????????????)
        List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(), entity.getQuestionRecordOid(),
                entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionSolveEnum.question_distribution.getCode());
        if (CollectionUtils.isEmpty(beforeQuestionVos)) {
            throw new DWRuntimeException( MultilingualismUtil.getLanguage("beforeStepNull"));
        }
        // ?????????-???????????????-?????? entity
        String dataInstanceOid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        String oid = IdGenUtil.uuid();
        entity.setOid(oid);
        // ??????????????????
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
        // ??????????????????
        DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVos.get(0),entity);
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
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid,BeforeQuestionVo beforeQuestionVo,QuestionActionTraceEntity traceEntity) throws Exception {
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(beforeQuestionVo.getDataContent());
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = (JSONObject) questionResult.get(0);

        // ???????????????????????????
        JSONArray curbDistributeInfo = new JSONArray();
        JSONObject curbDistribute = new JSONObject();
        // ???????????? ??????????????????-curb_request
        curbDistribute.put("curb_request","");
        // ???????????? ??????????????????-question_distribute_detail
        JSONArray distributeDetail = new JSONArray();
        curbDistribute.put("curb_distribute_detail",distributeDetail);
        curbDistributeInfo.add(curbDistribute);
        // ????????? ??????????????????-question_distribute_info
        dataDetail.put("curb_distribute_info",curbDistributeInfo);


        // ?????? ????????????
        JSONArray questionDistributeInfos = (JSONArray) dataDetail.get("question_distribute_info");
        JSONObject questionDistributeInfo = (JSONObject) questionDistributeInfos.get(0);

        JSONArray questionProcessInfo = new JSONArray();
        JSONObject processInfo = new JSONObject();
        //?????? ???????????? -?????? ????????????+?????????+????????????
        //12.16 ??????
        processInfo.put("question_distribute_request",questionDistributeInfo.get("question_distribute_request"));
        processInfo.put("question_distribute_no",beforeQuestionVo.getQuestionSolveStep());
        //?????????
        processInfo.put("question_distribute_name", MultilingualismUtil.getLanguage(beforeQuestionVo.getQuestionSolveStep()));
        processInfo.put("process_person_id",beforeQuestionVo.getLiablePersonId());
        processInfo.put("process_person_name",beforeQuestionVo.getLiablePersonName());
        if (null != beforeQuestionVo.getActualCompleteDate()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter.format(beforeQuestionVo.getActualCompleteDate());
            processInfo.put("process_date",dateString);
        }else {
            processInfo.put("process_date","");
        }
        processInfo.put("question_distribute_detail",questionDistributeInfo.get("question_distribute_detail"));
        questionProcessInfo.add(processInfo);
        dataDetail.put("question_process_info",questionProcessInfo);
        //????????????????????????
        DateUtil.assignValueForCommonExpectCompleteTime(traceEntity,dataDetail,QuestionSolveEnum.question_curb_distribution.getCode());

        JSONArray DistributeInfos = (JSONArray) dataDetail.get("question_distribute_info");

        dataDetail.remove("question_distribute_info");
        String textNew = JSON.toJSONString(DistributeInfos, SerializerFeature.DisableCircularReferenceDetect);
        JSONArray jsonArray = JSONArray.parseArray(textNew);
        dataDetail.put("question_distribute_info",jsonArray);
        // ??????value???null?????????
        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
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
     * @param questionActionTraceEntity ???????????????
     * @param questionCurbVerifyInfo ?????? ?????????????????????
     * @param oid ????????????????????????
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONObject questionCurbVerifyInfo,JSONArray attachmentModels, String oid) throws IOException {
        // ???????????? ???????????????
        // ???????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray("question_result");
        JSONObject dataDetail = questionResult.getJSONObject(0);

        //  ??????????????????
        //????????????????????????
        JSONArray processInfos= dataDetail.getJSONArray("question_process_info");
        JSONObject processInfo =  processInfos.getJSONObject(0);
        JSONArray distributeDetails= processInfo.getJSONArray("question_distribute_detail");

        // ???curb_verify_info????????????
        JSONArray questionCurbDistributeInfos = dataDetail.getJSONArray("curb_distribute_info");
        JSONObject curbDistributeInfo = questionCurbDistributeInfos.getJSONObject(0);
        curbDistributeInfo.remove("curb_request");
        curbDistributeInfo.put("curb_request",questionCurbVerifyInfo.get("curb_request"));
        //????????????
        JSONArray jsonArray =questionCurbVerifyInfo.getJSONArray("curb_distribute_detail");
        //?????????????????????????????????
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

            for(Iterator iteratorNew = jsonArray.iterator();iteratorNew.hasNext();){
                JSONObject objectNow = (JSONObject) iteratorNew.next();
                String dateNew=objectNow.get("expect_complete_date").toString();
                    objectNow.remove("expect_complete_date");
                    objectNow.put("expect_complete_date",dateNew+" "+formatter.format(new Date()));

            }

        curbDistributeInfo.remove("curb_distribute_detail");
        curbDistributeInfo.put("curb_distribute_detail",questionCurbVerifyInfo.get("curb_distribute_detail"));
        JSONArray attachmentInfos = (JSONArray) dataDetail.get("attachment_info");

        // ?????????????????????????????????
        JSONArray mustUploadAttachments = new JSONArray();
        //??????????????????????????????????????????
        boolean repeatCheckFlag = false;
        for (Iterator<Object> iterator = attachmentModels.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            boolean status = true;
            for (Iterator<Object> it = attachmentInfos.iterator();it.hasNext();) {
                JSONObject attach = (JSONObject)it.next();
                if(!repeatCheckFlag){
                    repeatCheckFlag = "SE002002".equals(attach.getString("attachment_belong_stage"));
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
        for (Iterator<Object> ite = distributeDetails.iterator(); ite.hasNext();) {
            JSONObject obj = (JSONObject)ite.next();
            if ("SE002002".equals(obj.get("step_id"))) {
                if (obj.get("attachment_upload_flag").equals("Y")) {
                    if (mustUploadAttachments.size()==0) {
                        // ???????????????
                        List<AttachmentEntity> attachmentEntities = attachmentMapper.getAttachments((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),dataInstanceVo.getOid());
                        if (attachmentEntities.size() == 0 && !repeatCheckFlag) {
                            throw new DWRuntimeException("attachment must upload");
                        }
                        break;
                    }

                }
            }
        }
        // ????????????
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE002002");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // ????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // ????????????
        return actionTraceBiz.handleUpdateForDistribution(questionActionTraceEntity,attachmentEntities,entity);
    }
}
