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
 * @Description 问题分析审核处理
 * @Author author
 * @Date 2021/11/19 0:04
 * @Version 1.0
 **/
public class QuestionIdentificationReview implements QuestionTraceStrategy {

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    /**
     * 因从工厂new出的对象，所以这里采用手动注入
     */
    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    InitQuestionBiz initQuestionBiz =  SpringContextHolder.getBean(InitQuestionBiz.class);

    @Override
    public JSONObject updateQuestionTrace(String parameters) throws Exception {
        // 更新关键信息： 问题处理状态、问题处理结果、负责人信息
        // string转model，将待更新的字段转为model
        IdentifyUpdateModel identifyModel = TransferTool.convertString2Model(parameters, IdentifyUpdateModel.class);
        // 参数校验
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
        // 获取当前处理步骤前一步骤 即 获取问题识别数据
        List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTraceForIdentity((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),
                entity.getQuestionNo(),QuestionUpdateEnum.question_identification.getCode(),
                null);
        // 新增待审核问题追踪
        String dataInstanceOid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        String oid = IdGenUtil.uuid();
        entity.setOid(oid);
        // 获取处理顺序
        List<QuestionActionTraceEntity> questionActionTraceEntityList = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntityList.get(0).getPrincipalStep()+1);
        // 更新前一节点状态和完成时间
        // 表单数据流转
        DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVos.get(0).getDataContent());
        actionTraceBiz.insertActionTrace(entity,dataInstanceEntity);
        JSONArray responseParam = new JSONArray();
        JSONObject responseObject = new JSONObject();
        responseObject.put("pending_approve_question_id",oid);
        responseParam.add(responseObject);
        return responseParam;
    }

    /**
     * 表单数据流转，生成待审核的表单内容
     * @param oid 当前节点的问题处理追踪表 主键
     * @param dataInstanceOid 待生成的当前处理步骤的问题实例表 主键
     * @param dataContent 问题识别审核 表单数据
     */
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid,String dataContent) throws JsonProcessingException {
        // 落存问题识别 详细数据
        DataInstanceEntity entity = new DataInstanceEntity();
        entity.setOid(dataInstanceOid);
        entity.setDataContent(dataContent);
        entity.setQuestionTraceOid(oid);
        return entity;
    }

    /**
     * 更新表单详情数据
     * @param questionIdentifyDetailModel 入参 需更新字段信息
     * @param oid 问题处理追踪主键
     * @throws JsonProcessingException
     */
    private JSONObject updateDetail(QuestionActionTraceEntity questionActionTraceEntity,QuestionIdentifyDetailModel questionIdentifyDetailModel, List<QuestionAttachmentModel> attachmentModels, String oid) throws IOException {
        // 获取需更新的表单数据，并解析结构，获取核心数据
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        // 需更新风险等级等信息
        QuestionRecordEntity recordEntity = updateRecord(dataInstanceVo.getRecordOId(),questionIdentifyDetailModel);
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSONObject.parseObject(dataInstanceVo.getDataContent());
        JSONObject dataDetail = getDataDetail(resultJsonObject);

        // 更新基础信息字段里的 风险等级&紧急度&重要性
        JSONObject basicInfo = dataDetail.getJSONArray("question_basic_info").getJSONObject(0);
        basicInfo.put("risk_level_oid",questionIdentifyDetailModel.getRiskLevelOId());
        basicInfo.put("risk_level_no",questionIdentifyDetailModel.getRiskLevelNo());
        basicInfo.put("risk_level_name",questionIdentifyDetailModel.getRiskLevelName());
        basicInfo.put("important",questionIdentifyDetailModel.getImportant());
        basicInfo.put("urgency",questionIdentifyDetailModel.getUrgency());

        // 更新问题识别数据
        JSONArray detailInfos = dataDetail.getJSONArray(PendingCreateConst.question_identify_info);
        String detailInfo = detailInfos.getString(0);
        QuestionIdentifyVo questionIdentifyVo = new ObjectMapper().readValue(detailInfo.getBytes(), QuestionIdentifyVo.class);
        BeanUtils.copyProperties(questionIdentifyDetailModel,questionIdentifyVo);
        String detailInfoVoJson = new ObjectMapper().writeValueAsString(questionIdentifyVo);
        JSONArray result = new JSONArray();
        result.add(JSONObject.parseObject(detailInfoVoJson));
        // 更新
        dataDetail.put(PendingCreateConst.question_identify_info,result);

        // 处理附件
        JSONArray attachmentInfos = (JSONArray) dataDetail.get(PendingCreateConst.attachment_info);
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleAttachmentsForModel(attachmentInfos,attachmentModels,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),dataInstanceVo.getQuestionProcessStep());
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());

        // 初始实体
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // 落存数据
        return initQuestionBiz.updateQA(questionActionTraceEntity,attachmentEntities,entity,recordEntity);
    }

    /**
     * 更新风险等级信息
     * @recordOId 风险等级主键
     * @identifyDetailModel 识别详细信息
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
     * 获取需更新的表单数据，并解析结构，获取核心数据
     * @param resultJsonObject
     * @return
     */
    private JSONObject getDataDetail(JSONObject resultJsonObject) {
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = questionResult.getJSONObject(0);
        return dataDetail;
    }


}
