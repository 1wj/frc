package com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.digiwin.app.frc.service.athena.common.Const.PendingCreateConst;
import com.digiwin.app.frc.service.athena.common.Const.QuestionDistributionConst;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.DefectCodeEntity;
import com.digiwin.app.frc.service.athena.mtw.mapper.DefectCodeMapper;
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
import com.digiwin.app.frc.service.athena.qdh.domain.vo.*;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.create.QuestionIdentifyVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.QuestionTraceStrategy;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.TransferTool;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName QuestionActionIdentification
 * @Description 问题分析(迭代三说法) 问题识别(迭代一说法)逻辑处理
 * @Author author
 * @Date 2021/11/15 0:09
 * @Version 1.0
 **/
public class QuestionActionIdentification implements QuestionTraceStrategy {
    @Autowired
    DefectCodeMapper defectCodeMapper = SpringContextHolder.getBean(DefectCodeMapper.class);

    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    InitQuestionBiz initQuestionBiz =  SpringContextHolder.getBean(InitQuestionBiz.class);


    @Override
    public JSONObject updateQuestionTrace(String parameters) throws Exception {
        // string转model，将待更新的字段转为model
        IdentifyUpdateModel identifyModel = TransferTool.convertString2Model(parameters, IdentifyUpdateModel.class);
        // 参数校验
        ParamCheckUtil.checkQAParams(identifyModel);
        // 更新任务卡状态
        QuestionInfoModel questionInfoModel = identifyModel.getQuestionInfos().get(0);
        QuestionActionTraceEntity entity = questionInfoModel.convertTo();
        QuestionIdentifyDetailModel identifyDetailModel = identifyModel.getQuestionIdentifyInfo().get(0);
        // 处理分析信息
        JSONObject re = updateDetail(entity,identifyDetailModel, identifyModel.getAttachmentModels(),questionInfoModel.getOid());
        return re;
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

    @Override
    public JSONArray insertUnapprovedQuestionTrace(QuestionActionTraceEntity entity) throws Exception {
        // 1-获取当前处理步骤前一步骤 即 获取问题反馈数据
        List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTraceForIdentity(TenantTokenUtil.getTenantSid(),
                entity.getQuestionNo(),QuestionUpdateEnum.question_feedback.getCode(),
                null);
        // 2-新增待审核问题追踪
        String dataInstanceOid = IdGenUtil.uuid();
        String oid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        entity.setOid(oid);
        // 获取处理顺序
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

        // 4-表单数据流转
        DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVos.get(0).getDataContent());
        actionTraceBiz.insertActionTrace(entity,dataInstanceEntity);
        // 5-封装response数据
        JSONArray responseParam = new JSONArray();
        JSONObject responseObject = new JSONObject();
        responseObject.put(PendingCreateConst.pending_approve_question_id,oid);
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
        // 获取前一结点QF的表单数据
        JSONObject resultJsonObject = JSONObject.parseObject(dataContent);
        // 获取最外层 question_result[].get(0)
        JSONObject dataDetail = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).getJSONObject(0);
        //带入 缺陷名称和缺陷等级
        JSONObject detailInfo = dataDetail.getJSONArray(PendingCreateConst.question_detail_info).getJSONObject(0);
        String defectGrade = "";
        // 基础讯息存入缺陷等级&缺陷名称&缺陷代号(ps:详情已存，存入基础讯息只是为了方便前端逻辑处理)
        if (!StringUtils.isEmpty(detailInfo.getString(PendingCreateConst.defect_no))) {
            List<DefectCodeEntity> defectCodeEntities = defectCodeMapper.getDefectCodeInfo(TenantTokenUtil.getTenantSid(),null,null,detailInfo.getString(PendingCreateConst.defect_no),null,null,null,null);
            if (!CollectionUtils.isEmpty(defectCodeEntities)) {
                defectGrade = defectCodeEntities.get(0).getDefectGrade();
            }
        }
        // 赋值基础信息
        JSONObject basicInfo = dataDetail.getJSONArray(PendingCreateConst.question_basic_info).getJSONObject(0);
        basicInfo.put(PendingCreateConst.defect_name,detailInfo.getString(PendingCreateConst.defect_name));
        basicInfo.put(PendingCreateConst.defect_grade,defectGrade);

        // 将问题识别(迭代一) 问题分析(迭代三说法)信息加入
        JSONArray identityInfo = new JSONArray();
        identityInfo.add(JSONObject.parse(new ObjectMapper().writeValueAsString(new QuestionIdentifyVo())));
        // 加入问题识别信息-QUESTION_IDENTIFY_INFO
        dataDetail.put(QuestionDistributionConst.QUESTION_IDENTIFY_INFO,identityInfo);
        // 保留value为null的数据
        JSONObject.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        // null 转 “”
        String dataContentString = JSON.toJSONString(resultJsonObject, filter);
        // 落存问题识别 详细数据
        DataInstanceEntity entity = new DataInstanceEntity();
        entity.setOid(dataInstanceOid);
        entity.setDataContent(dataContentString);
        entity.setQuestionTraceOid(oid);
        return entity;
    }

    /**
     * null转“” 过滤
     */
    private ValueFilter filter = (obj, s, v) -> {
        if (null == v) {
            return "";
        }
        return v;
    };

    /**
     * 更新表单详情数据
     * @param questionActionTraceEntity question_action_trace 主表数据
     * @param questionIdentifyDetailModel 入参代入的待更新字段信息
     * @param attachmentModels  附件信息
     * @param oid 问题识别处理卡主键
     * @throws JsonProcessingException 异常处理
     */
    private JSONObject updateDetail(QuestionActionTraceEntity questionActionTraceEntity,QuestionIdentifyDetailModel questionIdentifyDetailModel, List<QuestionAttachmentModel> attachmentModels,String oid) throws IOException {
        // 获取需更新的表单数据，并解析结构，获取核心数据
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        // 更新风险等级等信息
        QuestionRecordEntity recordEntity = updateRecord(dataInstanceVo.getRecordOId(),questionIdentifyDetailModel);
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
        JSONArray attachmentInfos = dataDetail.getJSONArray(PendingCreateConst.attachment_info);
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleAttachmentsForModel(attachmentInfos,attachmentModels,
                dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),dataInstanceVo.getQuestionProcessStep());
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());

        // 初始实体
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // 落存数据
        return initQuestionBiz.updateQA(questionActionTraceEntity,attachmentEntities,entity,recordEntity);
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
