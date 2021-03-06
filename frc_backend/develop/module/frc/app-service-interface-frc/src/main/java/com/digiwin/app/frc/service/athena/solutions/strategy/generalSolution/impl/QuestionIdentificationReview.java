package com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.common.Const.PendingCreateConst;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.InitQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionRecordEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QuestionAttachmentModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QA.QuestionIdentifyDetailModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QA.IdentifyUpdateModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.other.QuestionInfoModel;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.create.QuestionIdentifyVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.QuestionTraceStrategy;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.TransferTool;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @ClassName QuestionIdentificationReview
 * @Description ????????????????????????
 * @Author author
 * @Date 2021/11/19 0:04
 * @Version 1.0
 **/
public class QuestionIdentificationReview implements QuestionTraceStrategy {

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    /**
     * ????????????new?????????????????????????????????????????????
     */
    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    InitQuestionBiz initQuestionBiz =  SpringContextHolder.getBean(InitQuestionBiz.class);

    @Override
    public JSONObject updateQuestionTrace(String parameters) throws Exception {
        // ????????????????????? ?????????????????????????????????????????????????????????
        // string???model??????????????????????????????model
        IdentifyUpdateModel identifyModel = TransferTool.convertString2Model(parameters, IdentifyUpdateModel.class);
        // ????????????
        ParamCheckUtil.checkQAParams(identifyModel);
        QuestionInfoModel questionInfoModel = identifyModel.getQuestionInfos().get(0);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);
        Date updateDate = new Date();
        entity.setUpdateTime(updateDate);
        QuestionIdentifyDetailModel questionIdentifyDetailModel = identifyModel.getQuestionIdentifyInfo().get(0);
        JSONObject re = updateDetail(entity,questionIdentifyDetailModel, identifyModel.getAttachmentModels(),questionInfoModel.getOid());
        return re;
    }

    @Override
    public JSONArray insertUnapprovedQuestionTrace(QuestionActionTraceEntity entity) throws Exception {
        if ("2".equals(entity.getSkip())) {
            JSONArray responseParam = new JSONArray();
            JSONObject responseObject = new JSONObject();
            responseObject.put("pending_approve_question_id","");
            responseParam.add(responseObject);
            return responseParam;
        }
        // ???????????????????????????????????? ??? ????????????????????????
        List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTraceForIdentity((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),
                entity.getQuestionNo(),QuestionUpdateEnum.question_identification.getCode(),
                null);
        // ???????????????????????????
        String dataInstanceOid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        String oid = IdGenUtil.uuid();
        entity.setOid(oid);
        // ??????????????????
        List<QuestionActionTraceEntity> questionActionTraceEntityList = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntityList.get(0).getPrincipalStep()+1);
        // ???????????????????????????????????????
        // ??????????????????
        DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVos.get(0).getDataContent());
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
     * @param dataContent ?????????????????? ????????????
     */
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid,String dataContent) throws JsonProcessingException {
        // ?????????????????? ????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        entity.setOid(dataInstanceOid);
        entity.setDataContent(dataContent);
        entity.setQuestionTraceOid(oid);
        return entity;
    }

    /**
     * ????????????????????????
     * @param questionIdentifyDetailModel ?????? ?????????????????????
     * @param oid ????????????????????????
     * @throws JsonProcessingException
     */
    private JSONObject updateDetail(QuestionActionTraceEntity questionActionTraceEntity,QuestionIdentifyDetailModel questionIdentifyDetailModel, List<QuestionAttachmentModel> attachmentModels, String oid) throws IOException {
        // ?????????????????????????????????????????????????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        // ??????????????????????????????
        QuestionRecordEntity recordEntity = updateRecord(dataInstanceVo.getRecordOId(),questionIdentifyDetailModel);
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSONObject.parseObject(dataInstanceVo.getDataContent());
        JSONObject dataDetail = getDataDetail(resultJsonObject);

        // ?????????????????????????????? ????????????&?????????&?????????
        JSONObject basicInfo = dataDetail.getJSONArray("question_basic_info").getJSONObject(0);
        basicInfo.put("risk_level_oid",questionIdentifyDetailModel.getRiskLevelOId());
        basicInfo.put("risk_level_no",questionIdentifyDetailModel.getRiskLevelNo());
        basicInfo.put("risk_level_name",questionIdentifyDetailModel.getRiskLevelName());
        basicInfo.put("important",questionIdentifyDetailModel.getImportant());
        basicInfo.put("urgency",questionIdentifyDetailModel.getUrgency());

        // ????????????????????????
        JSONArray detailInfos = dataDetail.getJSONArray(PendingCreateConst.question_identify_info);
        String detailInfo = detailInfos.getString(0);
        QuestionIdentifyVo questionIdentifyVo = new ObjectMapper().readValue(detailInfo.getBytes(), QuestionIdentifyVo.class);
        BeanUtils.copyProperties(questionIdentifyDetailModel,questionIdentifyVo);
        String detailInfoVoJson = new ObjectMapper().writeValueAsString(questionIdentifyVo);
        JSONArray result = new JSONArray();
        result.add(JSONObject.parseObject(detailInfoVoJson));
        // ??????
        dataDetail.put(PendingCreateConst.question_identify_info,result);

        // ????????????
        JSONArray attachmentInfos = (JSONArray) dataDetail.get(PendingCreateConst.attachment_info);
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleAttachmentsForModel(attachmentInfos,attachmentModels,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),dataInstanceVo.getQuestionProcessStep());
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());

        // ????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // ????????????
        return initQuestionBiz.updateQA(questionActionTraceEntity,attachmentEntities,entity,recordEntity);
    }

    /**
     * ????????????????????????
     * @recordOId ??????????????????
     * @identifyDetailModel ??????????????????
     * @return
     */
    private QuestionRecordEntity updateRecord(String recordOId, QuestionIdentifyDetailModel identifyDetailModel){
        QuestionRecordEntity entity = new QuestionRecordEntity();
        entity.setOid(recordOId);
        entity.setRiskLevelOId(identifyDetailModel.getRiskLevelOId());
        entity.setImportant(identifyDetailModel.getImportant());
        entity.setUrgency(identifyDetailModel.getUrgency());
        return entity;
    }


    /**
     * ?????????????????????????????????????????????????????????????????????
     * @param resultJsonObject
     * @return
     */
    private JSONObject getDataDetail(JSONObject resultJsonObject) {
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = questionResult.getJSONObject(0);
        return dataDetail;
    }


}
