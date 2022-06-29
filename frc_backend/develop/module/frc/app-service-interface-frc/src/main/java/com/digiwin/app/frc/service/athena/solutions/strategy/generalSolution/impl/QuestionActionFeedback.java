package com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.ParamConst;
import com.digiwin.app.frc.service.athena.common.Const.PendingCreateConst;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.Const.qdh.update.QuestionUpdateConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.InitQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionRecordEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.init.BasicModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.init.DetailModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QF.QuestionBasicModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QF.QuestionInfoModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QuestionFeedbackDetailModel;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.solution.DetailInfoVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.QuestionTraceStrategy;
import com.digiwin.app.frc.service.athena.config.annotation.ValidationHandler;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.TransferTool;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QF.UpdateModel;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @ClassName QFUpdate
 * @Description 问题确认(迭代三叫法)|| 问题反馈(迭代一叫法)
 * @Author author
 * @Date 2021/11/11 22:55
 * @Version 1.0
 **/
public class QuestionActionFeedback implements QuestionTraceStrategy {

    /**
     * 因从工厂new出的对象，所以这里采用手动注入
     */
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
        UpdateModel updateModel = TransferTool.convertString2Model(parameters,UpdateModel.class);
        // 参数检验
        ValidationHandler.doValidator(updateModel.getQuestionInfo().get(0));
        ValidationHandler.doValidator(updateModel.getQuestionBasicInfo().get(0));
        // 只更新问题确认卡的负责人
        if (null == updateModel.getQuestionBasicInfo() && null ==updateModel.getQuestionDetailInfo()) {
            QuestionActionTraceEntity actionTraceEntity = updateActionTrace(updateModel.getQuestionInfo().get(0));
            return initQuestionBiz.updateQF(null,actionTraceEntity,null);
        }
        QuestionRecordEntity recordEntity = handRecord(updateModel.getQuestionInfo().get(0).getQuestionRecordOid(),updateModel.getQuestionBasicInfo().get(0),updateModel.getQuestionDetailInfo().get(0));
        QuestionActionTraceEntity actionTraceEntity = updateActionTrace(updateModel.getQuestionInfo().get(0));
        DataInstanceEntity dataInstanceEntity = handleDataContent(actionTraceEntity.getOid(),updateModel.getQuestionBasicInfo().get(0),updateModel.getQuestionDetailInfo().get(0));
        return initQuestionBiz.updateQF(recordEntity,actionTraceEntity,dataInstanceEntity);
    }

    /**
     * 更新问题记录表
     * @param recordOId
     * @param basicModel
     * @param detailModel
     * @return
     */
    private QuestionRecordEntity handRecord(String recordOId,QuestionBasicModel basicModel,DetailModel detailModel){
        QuestionRecordEntity recordEntity = new QuestionRecordEntity();
        recordEntity.setOid(recordOId);
        recordEntity.setQuestionDescription(basicModel.getQuestionDescription());
        recordEntity.setQuestionSourceNo(basicModel.getQuestionSourceNo());
        recordEntity.setQuestionSourceOId(basicModel.getQuestionSourceOId());
        recordEntity.setQuestionClassificationOId(basicModel.getQuestionClassificationOId());
        // 归属类型
        recordEntity.setQuestionSourceType(basicModel.getQuestionAttributionNo());
        // 重要性
        recordEntity.setImportant(basicModel.getImportant());
        // 紧急度
        recordEntity.setUrgency(basicModel.getUrgency());
        // 项目代号
        recordEntity.setProjectNo(detailModel.getProjectNo());
        // 风险等级
        recordEntity.setRiskLevelOId(basicModel.getRiskLevelOId());
        // 切面
        recordEntity.setTenantsid(TenantTokenUtil.getTenantSid());
        recordEntity.setUpdateTime(new Date());
        recordEntity.setUpdateId(TenantTokenUtil.getUserId());
        recordEntity.setUpdateName(TenantTokenUtil.getUserName());
        return recordEntity;

    }

    /**
     * 处理数据实例
     * @param oid question_id
     * @param questionBasicModel 基础信息
     * @param detailModel 详情
     */
    private DataInstanceEntity handleDataContent(String oid, QuestionBasicModel questionBasicModel, DetailModel detailModel) throws IOException {
        DataInstanceEntity instanceEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),oid);
        Assert.notNull(instanceEntity,"instanceEntity is null ");
        if (StringUtils.isEmpty(instanceEntity.getDataContent())) {
            throw new RuntimeException("data_content query is null");
        }
        // 获取表单数据 string转jsonObject处理
        JSONObject resultJsonObject = JSON.parseObject(instanceEntity.getDataContent());
        // 获取最外层 question_result[].get(0)
        JSONObject dataDetail = resultJsonObject.getJSONArray(QuestionUpdateConst.QUESTION_RESULT).getJSONObject(0);
        // 重组 基础信息basic_info json
        dataDetail.put(QuestionUpdateConst.question_basic_info,handleBasicInfoObject(dataDetail, questionBasicModel));
        // 重组 详情question_detail_info
        dataDetail.put(QuestionUpdateConst.question_detail_info,handDetailObject(detailModel));
        // 保留value为null的数据
        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        // null 转 “”
        String dataContentString = JSON.toJSONString(resultJsonObject, filter);
        instanceEntity.setDataContent(dataContentString);
        return instanceEntity;
    }

    /**
     * 重组 基础信息basic_info json
     * @param dataDetail 原数据
     * @param questionBasicModel 更新的基础信息数据
     * @return
     */
    private JSONArray handleBasicInfoObject(JSONObject dataDetail, QuestionBasicModel questionBasicModel) throws IOException {
        JSONObject basicInfo = dataDetail.getJSONArray(QuestionUpdateConst.question_basic_info).getJSONObject(0);
        // 转 对象
        BasicModel oldBasicModel = new ObjectMapper().readValue(basicInfo.toJSONString().getBytes(), BasicModel.class);
        // 数据重组
        BeanUtils.copyProperties(questionBasicModel,oldBasicModel);
        // 对象转object 封装成[]
        JSONArray basicInfoArray = new JSONArray();
        basicInfoArray.add(JSON.parseObject(new ObjectMapper().writeValueAsString(oldBasicModel)));
        return basicInfoArray;
    }

    /**
     * 重组 详情 question_detail_info
     * @param detailModel
     * @return
     * @throws JsonProcessingException
     */
    private JSONArray handDetailObject(DetailModel detailModel) throws JsonProcessingException {
        JSONArray detailInfo = new JSONArray();
        detailInfo.add(JSON.parseObject(new ObjectMapper().writeValueAsString(detailModel)));
        return detailInfo;
    }


    /**
     *
     * @param questionInfoModel
     * @return 需更新的任务卡数据
     */
    private QuestionActionTraceEntity updateActionTrace(QuestionInfoModel questionInfoModel){
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);
        if (StringUtils.isEmpty(entity.getOid())) {
            throw new DWRuntimeException(" question_id is null !");
        }
        return entity;
    }

    @Override
    public JSONArray insertUnapprovedQuestionTrace(QuestionActionTraceEntity entity) throws Exception {
        // 获取当前处理步骤前一步骤 即 获取问题反馈数据
        List<BeforeQuestionVo> beforeQuestionVo = actionTraceMapper.getBeforeQuestionTraceForIdentity((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),
                entity.getQuestionNo(), QuestionUpdateEnum.question_feedback.getCode(),
                null);
        // 新增待审核问题追踪
        String dataInstanceOid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        String oid = IdGenUtil.uuid();
        entity.setOid(oid);
        entity.setTenantsid(TenantTokenUtil.getTenantSid());
        // 获取处理顺序
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
        //初始化数据实例
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        dataInstanceEntity.setOid(dataInstanceOid);
        dataInstanceEntity.setDataContent(beforeQuestionVo.get(0).getDataContent());
        dataInstanceEntity.setQuestionTraceOid(oid);
        // 保存数据
        actionTraceBiz.insertActionTrace(entity,dataInstanceEntity);
        // 封装格式，返回
        JSONArray responseParam = new JSONArray();
        JSONObject responseObject = new JSONObject();
        responseObject.put(ParamConst.pending_approve_question_id,oid);
        responseParam.add(responseObject);
        return responseParam;
    }


    /**
     * 更新表单详情数据
     * @param questionFeedbackDetailModel 入参 需更新字段信息
     * @param oid 问题处理追踪主键
     * @throws JsonProcessingException
     */
    private void handleDetail(QuestionFeedbackDetailModel questionFeedbackDetailModel, String oid) throws IOException {
        // 获取入参 需更新表单
        DataInstanceEntity instanceEntity = dataInstanceMapper.getQuestionDetail((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),oid);
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(instanceEntity.getDataContent());
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = (JSONObject) questionResult.get(0);
        // 获取question_detail_info
        JSONArray detailInfos = (JSONArray) dataDetail.get(PendingCreateConst.question_detail_info);
        String detailInfo = detailInfos.getString(0);
        DetailInfoVo detailInfoVo = new ObjectMapper().readValue(detailInfo.getBytes(), DetailInfoVo.class);
        // 转实体,并将表单字段更新
        BeanUtils.copyProperties(questionFeedbackDetailModel,detailInfoVo);
        String detailInfoVoJson = new ObjectMapper().writeValueAsString(detailInfoVo);
        JSONArray result = new JSONArray();
        result.add(JSON.parseObject(detailInfoVoJson));
        // 更新
        dataDetail.put(PendingCreateConst.question_detail_info,result);
        // 保留value为null的数据
        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        // null 转 “”
        String dataContentString = JSON.toJSONString(resultJsonObject, filter);
        instanceEntity.setDataContent(dataContentString);
        dataInstanceMapper.updateDataInstance(instanceEntity);
    }

        /**
         * null 转 “”
         */
        private ValueFilter filter = (obj, s, v) -> {
            if (null == v) {
                return "";
            }
            return v;
        };

}
