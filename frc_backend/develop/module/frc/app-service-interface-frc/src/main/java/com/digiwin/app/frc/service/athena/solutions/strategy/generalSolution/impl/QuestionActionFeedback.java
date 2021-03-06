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
 * @Description ????????????(???????????????)|| ????????????(???????????????)
 * @Author author
 * @Date 2021/11/11 22:55
 * @Version 1.0
 **/
public class QuestionActionFeedback implements QuestionTraceStrategy {

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
    InitQuestionBiz initQuestionBiz =  SpringContextHolder.getBean(InitQuestionBiz.class);


    @Override
    public JSONObject updateQuestionTrace(String parameters) throws Exception {
        UpdateModel updateModel = TransferTool.convertString2Model(parameters,UpdateModel.class);
        // ????????????
        ValidationHandler.doValidator(updateModel.getQuestionInfo().get(0));
        ValidationHandler.doValidator(updateModel.getQuestionBasicInfo().get(0));
        // ????????????????????????????????????
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
     * ?????????????????????
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
        // ????????????
        recordEntity.setQuestionSourceType(basicModel.getQuestionAttributionNo());
        // ?????????
        recordEntity.setImportant(basicModel.getImportant());
        // ?????????
        recordEntity.setUrgency(basicModel.getUrgency());
        // ????????????
        recordEntity.setProjectNo(detailModel.getProjectNo());
        // ????????????
        recordEntity.setRiskLevelOId(basicModel.getRiskLevelOId());
        // ??????
        recordEntity.setTenantsid(TenantTokenUtil.getTenantSid());
        recordEntity.setUpdateTime(new Date());
        recordEntity.setUpdateId(TenantTokenUtil.getUserId());
        recordEntity.setUpdateName(TenantTokenUtil.getUserName());
        return recordEntity;

    }

    /**
     * ??????????????????
     * @param oid question_id
     * @param questionBasicModel ????????????
     * @param detailModel ??????
     */
    private DataInstanceEntity handleDataContent(String oid, QuestionBasicModel questionBasicModel, DetailModel detailModel) throws IOException {
        DataInstanceEntity instanceEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),oid);
        Assert.notNull(instanceEntity,"instanceEntity is null ");
        if (StringUtils.isEmpty(instanceEntity.getDataContent())) {
            throw new RuntimeException("data_content query is null");
        }
        // ?????????????????? string???jsonObject??????
        JSONObject resultJsonObject = JSON.parseObject(instanceEntity.getDataContent());
        // ??????????????? question_result[].get(0)
        JSONObject dataDetail = resultJsonObject.getJSONArray(QuestionUpdateConst.QUESTION_RESULT).getJSONObject(0);
        // ?????? ????????????basic_info json
        dataDetail.put(QuestionUpdateConst.question_basic_info,handleBasicInfoObject(dataDetail, questionBasicModel));
        // ?????? ??????question_detail_info
        dataDetail.put(QuestionUpdateConst.question_detail_info,handDetailObject(detailModel));
        // ??????value???null?????????
        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        // null ??? ??????
        String dataContentString = JSON.toJSONString(resultJsonObject, filter);
        instanceEntity.setDataContent(dataContentString);
        return instanceEntity;
    }

    /**
     * ?????? ????????????basic_info json
     * @param dataDetail ?????????
     * @param questionBasicModel ???????????????????????????
     * @return
     */
    private JSONArray handleBasicInfoObject(JSONObject dataDetail, QuestionBasicModel questionBasicModel) throws IOException {
        JSONObject basicInfo = dataDetail.getJSONArray(QuestionUpdateConst.question_basic_info).getJSONObject(0);
        // ??? ??????
        BasicModel oldBasicModel = new ObjectMapper().readValue(basicInfo.toJSONString().getBytes(), BasicModel.class);
        // ????????????
        BeanUtils.copyProperties(questionBasicModel,oldBasicModel);
        // ?????????object ?????????[]
        JSONArray basicInfoArray = new JSONArray();
        basicInfoArray.add(JSON.parseObject(new ObjectMapper().writeValueAsString(oldBasicModel)));
        return basicInfoArray;
    }

    /**
     * ?????? ?????? question_detail_info
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
     * @return ???????????????????????????
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
        // ???????????????????????????????????? ??? ????????????????????????
        List<BeforeQuestionVo> beforeQuestionVo = actionTraceMapper.getBeforeQuestionTraceForIdentity((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),
                entity.getQuestionNo(), QuestionUpdateEnum.question_feedback.getCode(),
                null);
        // ???????????????????????????
        String dataInstanceOid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        String oid = IdGenUtil.uuid();
        entity.setOid(oid);
        entity.setTenantsid(TenantTokenUtil.getTenantSid());
        // ??????????????????
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
        //?????????????????????
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        dataInstanceEntity.setOid(dataInstanceOid);
        dataInstanceEntity.setDataContent(beforeQuestionVo.get(0).getDataContent());
        dataInstanceEntity.setQuestionTraceOid(oid);
        // ????????????
        actionTraceBiz.insertActionTrace(entity,dataInstanceEntity);
        // ?????????????????????
        JSONArray responseParam = new JSONArray();
        JSONObject responseObject = new JSONObject();
        responseObject.put(ParamConst.pending_approve_question_id,oid);
        responseParam.add(responseObject);
        return responseParam;
    }


    /**
     * ????????????????????????
     * @param questionFeedbackDetailModel ?????? ?????????????????????
     * @param oid ????????????????????????
     * @throws JsonProcessingException
     */
    private void handleDetail(QuestionFeedbackDetailModel questionFeedbackDetailModel, String oid) throws IOException {
        // ???????????? ???????????????
        DataInstanceEntity instanceEntity = dataInstanceMapper.getQuestionDetail((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),oid);
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(instanceEntity.getDataContent());
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = (JSONObject) questionResult.get(0);
        // ??????question_detail_info
        JSONArray detailInfos = (JSONArray) dataDetail.get(PendingCreateConst.question_detail_info);
        String detailInfo = detailInfos.getString(0);
        DetailInfoVo detailInfoVo = new ObjectMapper().readValue(detailInfo.getBytes(), DetailInfoVo.class);
        // ?????????,????????????????????????
        BeanUtils.copyProperties(questionFeedbackDetailModel,detailInfoVo);
        String detailInfoVoJson = new ObjectMapper().writeValueAsString(detailInfoVo);
        JSONArray result = new JSONArray();
        result.add(JSON.parseObject(detailInfoVoJson));
        // ??????
        dataDetail.put(PendingCreateConst.question_detail_info,result);
        // ??????value???null?????????
        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        // null ??? ??????
        String dataContentString = JSON.toJSONString(resultJsonObject, filter);
        instanceEntity.setDataContent(dataContentString);
        dataInstanceMapper.updateDataInstance(instanceEntity);
    }

        /**
         * null ??? ??????
         */
        private ValueFilter filter = (obj, s, v) -> {
            if (null == v) {
                return "";
            }
            return v;
        };

}
