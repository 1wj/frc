package com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionDistributionConst;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.config.annotation.ParamValidationHandler;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.other.QuestionInfoModel;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.SolutionStepVo;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName QuestionDistribution
 * @Description ????????????
 * @Author author
 * @Date 2021/11/19 1:00
 * @Version 1.0
 **/
public class QuestionDistribution  implements QuestionTraceStrategy {

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
        // ??????????????????
        JSONObject resultJsonObject = JSONObject.parseObject(parameters);
        // question_info??????????????????
        QuestionInfoModel questionInfoModel = TransferTool.convertString2Model(resultJsonObject.getJSONArray("question_info").getString(0), QuestionInfoModel.class);
        // ????????????
        ParamValidationHandler.validateParams(questionInfoModel);
        JSONArray attachmentInfos = resultJsonObject.getJSONArray("attachment_info");
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONObject questionDistributeInfo = resultJsonObject.getJSONArray("question_distribute_info").getJSONObject(0);
        JSONObject re = handleDetail(entity,questionDistributeInfo,attachmentInfos,questionInfoModel.getOid());
        return re;
    }

    @Override
    public JSONArray insertUnapprovedQuestionTrace(QuestionActionTraceEntity entity) throws Exception {
        List<BeforeQuestionVo> beforeQuestionVos;
        // ??????skip
        ParamValidationHandler.validateParams(entity);
        if ("2".equals(entity.getSkip())) {
            beforeQuestionVos = actionTraceMapper.getBeforeQuestionTraceForIdentity(TenantTokenUtil.getTenantSid(),entity.getQuestionNo(),QuestionUpdateEnum.question_identification.getCode(), null);
        }else {
            // ???????????????????????????????????????(??????????????????????????????)
            beforeQuestionVos = actionTraceMapper.getBeforeQuestionTraceForIdentity(TenantTokenUtil.getTenantSid(),entity.getQuestionNo(),QuestionUpdateEnum.question_identification_review.getCode(), null);
        }
        if (CollectionUtils.isEmpty(beforeQuestionVos)) {
            throw new DWRuntimeException( MultilingualismUtil.getLanguage("beforeStepNull"));
        }
        // ?????????-???????????????-?????? entity
        String dataInstanceOid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        String oid = IdGenUtil.uuid();
        entity.setOid(oid);

        // ??????????????????
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStepForIdentity(TenantTokenUtil.getTenantSid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

        // ??????????????????
        DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVos.get(0).getDataContent(),entity);
        actionTraceBiz.insertActionTrace(entity,dataInstanceEntity);

        // ??????response
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
     * @param dataContent ?????????????????? ????????????
     */
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid,String dataContent,QuestionActionTraceEntity traceEntity) throws Exception {
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSONObject.parseObject(dataContent);
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = (JSONObject) questionResult.get(0);

        // ?????????

        // ??????????????????id
        JSONArray identifyInfos = (JSONArray) dataDetail.get("question_identify_info");
        JSONObject identify = (JSONObject) identifyInfos.get(0);
        String solutionId = (String) identify.get("solution_id");


        List<SolutionStepVo> solutionStepVos = actionTraceMapper.getSolutionStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),solutionId);


        // ???????????????????????????
        JSONArray distributions = new JSONArray();
        JSONObject distribution = new JSONObject();
        // ???????????? ??????????????????-question_distribute_request
        distribution.put(QuestionDistributionConst.QUESTION_DISTRIBUTE_REQUEST,"");
        // ???????????? ??????????????????-question_distribute_detail
        JSONArray distributeDetail = new JSONArray();
        //??????????????????????????????
        for (SolutionStepVo solutionStepVo : solutionStepVos) {
            JSONObject distribute = new JSONObject();
            distribute.put("step_id",solutionStepVo.getStepId());
            distribute.put("step_name",solutionStepVo.getStepName());
            if ("SE002001".equals(solutionStepVo.getStepId())) {
                distribute.put("process_person_id",identify.get("liable_person_id"));
                distribute.put("process_person_name",identify.get("liable_person_name"));
                //???????????????????????????????????????  yyyy-MM-dd HH:mm:ss
                traceEntity.setExpectCompleteDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(DateUtil.getExpectDateWithHourMinute(solutionStepVo.getExpectCompleteDays())));
            }else {
                distribute.put("process_person_id",solutionStepVo.getProcessPersonId());
                distribute.put("process_person_name",solutionStepVo.getProcessPersonName());
            }
            distribute.put("expect_complete_date", DateUtil.getExpectDateWithHourMinute(solutionStepVo.getExpectCompleteDays()));
            distribute.put("attachment_upload_flag","");
            distributeDetail.add(distribute);
        }
        distribution.put(QuestionDistributionConst.QUESTION_DISTRIBUTE_DETAIL,distributeDetail);
        distributions.add(distribution);
        // ????????? ??????????????????-question_distribute_info
        dataDetail.put(QuestionDistributionConst.QUESTION_DISTRIBUTE_INFO,distributions);

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
     * @param questionActionTraceEntity ???????????????????????????
     * @param distributionInfo ?????? ?????????????????????
     * @param oid ????????????????????????
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONObject distributionInfo,JSONArray attachmentModels,String oid) throws IOException {
        // ???????????? ???????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);

        JSONObject resultJsonObject = JSONObject.parseObject(dataInstanceVo.getDataContent());
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray("question_result");
        JSONObject dataDetail = questionResult.getJSONObject(0);

        JSONArray questionDistributeInfos = dataDetail.getJSONArray("question_distribute_info");
        JSONObject questionDistributeInfo = questionDistributeInfos.getJSONObject(0);
        questionDistributeInfo.remove("question_distribute_request");
        questionDistributeInfo.put("question_distribute_request",distributionInfo.get("question_distribute_request"));
        // ????????????????????????
        //??????????????????????????????
        JSONArray jsonArray=distributionInfo.getJSONArray("question_distribute_detail");
        //?????????????????????????????????
        JSONArray jsonArrayDb=questionDistributeInfo.getJSONArray("question_distribute_detail");
        for(Iterator<Object> iterator = jsonArrayDb.iterator();iterator.hasNext();){
            JSONObject objectBefore = (JSONObject) iterator.next();
            String date=objectBefore.get("expect_complete_date").toString();
            String second = date.substring(10);
            for(Iterator<Object> iteratorNew = jsonArray.iterator();iteratorNew.hasNext();){
                JSONObject objectNow = (JSONObject) iteratorNew.next();
                if (objectBefore.get("step_id").equals(objectNow.get("step_id"))){
                    String dateNew=objectNow.get("expect_complete_date").toString();
                    objectNow.remove("expect_complete_date");
                    objectNow.put("expect_complete_date",dateNew+second);
                    break;
                }
            }
        }
        questionDistributeInfo.remove("question_distribute_detail");
        questionDistributeInfo.put("question_distribute_detail",distributionInfo.get("question_distribute_detail"));
        JSONArray attachmentInfos = dataDetail.getJSONArray("attachment_info");
        // ??????????????????????????????
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleAttachments(questionActionTraceEntity,attachmentInfos,attachmentModels,
                dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE002001",true,distributionInfo.getJSONArray("question_distribute_detail"));
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // ????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // ????????????
        return actionTraceBiz.handleUpdateForDistribution(questionActionTraceEntity,attachmentEntities,entity);
    }


}
