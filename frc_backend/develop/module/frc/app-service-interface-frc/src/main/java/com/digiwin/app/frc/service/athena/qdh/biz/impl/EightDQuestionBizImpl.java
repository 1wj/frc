package com.digiwin.app.frc.service.athena.qdh.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.Const.qdh.update.QuestionUpdateConst;
import com.digiwin.app.frc.service.athena.qdh.biz.EightDQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.EightDQuestionMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.PersonPendingNumVo;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/3/9 1:11
 * @Version 1.0
 * @Description 8D解决方案处理Biz-impl
 */
@Service
public class EightDQuestionBizImpl implements EightDQuestionBiz {

    @Autowired
    DataInstanceMapper dataInstanceMapper;

    @Autowired
    ActionTraceMapper actionTraceMapper;

    @Autowired
    AttachmentMapper attachmentMapper;




    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject handleUpdateForTeamBuilder(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
        int actionTraceResponse = actionTraceMapper.updateActionTrace(questionActionTraceEntity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse =  dataInstanceMapper.updateDataInstance(entity);
        if (updateResponse < 0) {
            throw new DWRuntimeException("update table dataInstance or attachment fail ! ");
        }
        // 根据主键查询返回信息
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(questionActionTraceEntity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        QuestionActionTraceEntity actionEntity = list.get(0);
        result.put("question_id",actionEntity.getOid());
        result.put("question_process_status",actionEntity.getQuestionProcessStatus());
        result.put("question_process_result",actionEntity.getQuestionProcessResult());
        result.put("question_no",actionEntity.getQuestionNo());
        result.put("question_record_id",actionEntity.getQuestionRecordOid());
        result.put("question_description",actionEntity.getQuestionDescription());
        result.put("return_flag_id",StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "":actionEntity.getReturnFlagId());
        result.put("return_flag_name",StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "":actionEntity.getReturnFlagName());
        result.put("return_no", StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        result.put(QuestionUpdateConst.update_date,questionActionTraceEntity.getUpdateTime());
        result.put(QuestionUpdateConst.return_reason,StringUtils.isEmpty(actionEntity.getReturnReason())?"":actionEntity.getReturnReason());
        result.put(QuestionUpdateConst.return_step_no,StringUtils.isEmpty(actionEntity.getReturnStepNo())?"":actionEntity.getReturnStepNo());


        JSONArray re = new JSONArray();

        //加上团队成员信息 和 计划安排信息
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONArray questionResult = (JSONArray) JSON.parseObject(content).get("question_result");
            JSONObject object = (JSONObject) questionResult.get(0);
            JSONArray teamMember = object.getJSONArray("team_member_info");
            JSONArray planManagement = object.getJSONArray("plan_arrange");
            result.put("team_member_info",teamMember);
            result.put("plan_arrange",planManagement);
        }

        re.add(result);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",re);
        return jsonObject;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject handleUpdateForContainmentMeasure(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
        int actionTraceResponse = actionTraceMapper.updateActionTrace(questionActionTraceEntity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse =  dataInstanceMapper.updateDataInstance(entity);
        if (updateResponse < 0) {
            throw new DWRuntimeException("update table dataInstance or attachment fail ! ");
        }
        // 根据主键查询返回信息
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(questionActionTraceEntity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        QuestionActionTraceEntity actionEntity = list.get(0);
        result.put("question_id",actionEntity.getOid());
        result.put("question_process_status",actionEntity.getQuestionProcessStatus());
        result.put("question_process_result",actionEntity.getQuestionProcessResult());
        result.put("question_no",actionEntity.getQuestionNo());
        result.put("question_record_id",actionEntity.getQuestionRecordOid());
        result.put("question_description",actionEntity.getQuestionDescription());
        result.put("close_reason",StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "":actionEntity.getReturnFlagId());
        result.put("return_flag_name",StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "":actionEntity.getReturnFlagName());
        result.put("return_no", StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        result.put(QuestionUpdateConst.update_date,questionActionTraceEntity.getUpdateTime());
        JSONArray re = new JSONArray();

        //加上围堵措施信息
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONArray questionResult = (JSONArray) JSON.parseObject(content).get("question_result");
            JSONObject object = (JSONObject) questionResult.get(0);
            JSONArray containmentMeasures = object.getJSONArray("containment_measure");
            result.put("containment_measure",containmentMeasures);
        }

        re.add(result);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",re);
        return jsonObject;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject handleUpdateForContainmentMeasureExecute(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
        int actionTraceResponse = actionTraceMapper.updateActionTrace(questionActionTraceEntity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse =  dataInstanceMapper.updateDataInstance(entity);
        if (updateResponse < 0) {
            throw new DWRuntimeException("update table dataInstance or attachment fail ! ");
        }
        // 根据主键查询返回信息
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(questionActionTraceEntity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        QuestionActionTraceEntity actionEntity = list.get(0);
        result.put("question_id",actionEntity.getOid());
        result.put("question_process_status",actionEntity.getQuestionProcessStatus());
        result.put("question_process_result",actionEntity.getQuestionProcessResult());
        result.put("question_no",actionEntity.getQuestionNo());
        result.put("question_record_id",actionEntity.getQuestionRecordOid());
        result.put("question_description",actionEntity.getQuestionDescription());
        result.put("return_flag_id",StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "":actionEntity.getReturnFlagId());
        result.put("return_flag_name",StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "":actionEntity.getReturnFlagName());
        result.put("return_no",StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        result.put(QuestionUpdateConst.update_date,questionActionTraceEntity.getUpdateTime());
        JSONArray re = new JSONArray();

        //加上围堵措施执行信息
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONArray questionResult = (JSONArray) JSON.parseObject(content).get("question_result");
            JSONObject object = (JSONObject) questionResult.get(0);
            JSONArray containmentMeasuresExecute = object.getJSONArray("containment_measure_execute");
            result.put("containment_measure_execute",containmentMeasuresExecute);
        }

        re.add(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",re);
        return jsonObject;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject handleUpdateForContainmentMeasureVerify(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
        int actionTraceResponse = actionTraceMapper.updateActionTrace(questionActionTraceEntity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse =  dataInstanceMapper.updateDataInstance(entity);
        if (updateResponse < 0) {
            throw new DWRuntimeException("update table dataInstance or attachment fail ! ");
        }
        // 根据主键查询返回信息
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(questionActionTraceEntity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        QuestionActionTraceEntity actionEntity = list.get(0);
        result.put("question_id",actionEntity.getOid());
        result.put("question_process_status",actionEntity.getQuestionProcessStatus());
        result.put("question_process_result",actionEntity.getQuestionProcessResult());
        result.put("question_no",actionEntity.getQuestionNo());
        result.put("question_record_id",actionEntity.getQuestionRecordOid());
        result.put("question_description",actionEntity.getQuestionDescription());
        result.put("return_flag_id",StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "":actionEntity.getReturnFlagId());
        result.put("return_flag_name",StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "":actionEntity.getReturnFlagName());
        result.put("return_no",StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        result.put(QuestionUpdateConst.update_date,questionActionTraceEntity.getUpdateTime());
        JSONArray re = new JSONArray();

        //加上围堵措施验证信息
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONArray questionResult = (JSONArray) JSON.parseObject(content).get("question_result");
            JSONObject object = (JSONObject) questionResult.get(0);
            JSONArray containmentMeasureVerify = object.getJSONArray("containment_measure_verify");
            result.put("containment_measure_verify",containmentMeasureVerify);
        }


        re.add(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",re);
        return jsonObject;
    }



    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject keyReasonCorrectBuilder(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity) {
        //获取返回的参数
        int actionTraceResponse = actionTraceMapper.updateActionTrace(questionActionTraceEntity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse =  dataInstanceMapper.updateDataInstance(dataInstanceEntity);
        if (updateResponse < 0) {
            throw new DWRuntimeException("update table dataInstance or attachment fail ! ");
        }
        // 根据主键查询返回信息
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(questionActionTraceEntity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        //对结果进行赋值
        QuestionActionTraceEntity actionEntity = list.get(0);
        result.put("question_id",actionEntity.getOid());
        result.put("question_process_status",actionEntity.getQuestionProcessStatus());
        result.put("question_process_result",actionEntity.getQuestionProcessResult());
        result.put("question_no",actionEntity.getQuestionNo());
        result.put("question_record_id",actionEntity.getQuestionRecordOid());
        result.put("question_description",actionEntity.getQuestionDescription());
        result.put("return_flag_id",StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "":actionEntity.getReturnFlagId());
        result.put("return_flag_name",StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "":actionEntity.getReturnFlagName());
        result.put("return_no", StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        result.put(QuestionUpdateConst.update_date,questionActionTraceEntity.getUpdateTime());
        JSONArray resultJsonArray = new JSONArray();
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONArray questionResult = (JSONArray) JSON.parseObject(content).get("question_result");
            JSONObject object = (JSONObject) questionResult.get(0);
            JSONArray keyReasonAnalysis = object.getJSONArray("key_reason_analysis");
            JSONArray correctiveAction= object.getJSONArray("corrective_measure");
            result.put("key_reason_analysis",keyReasonAnalysis);
            result.put("corrective_measure",correctiveAction);
        }
        resultJsonArray.add(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",resultJsonArray);
        return jsonObject;
    }



    @Override
    public JSONObject correctExecuteBuilder(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity) {
        //获取返回的参数
        int actionTraceResponse = actionTraceMapper.updateActionTrace(questionActionTraceEntity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse =  dataInstanceMapper.updateDataInstance(dataInstanceEntity);
        if (updateResponse < 0) {
            throw new DWRuntimeException("update table dataInstance or attachment fail ! ");
        }
        // 根据主键查询返回信息
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(questionActionTraceEntity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        //对结果进行赋值
        QuestionActionTraceEntity actionEntity = list.get(0);
        result.put("question_id",actionEntity.getOid());
        result.put("question_process_status",actionEntity.getQuestionProcessStatus());
        result.put("question_process_result",actionEntity.getQuestionProcessResult());
        result.put("question_no",actionEntity.getQuestionNo());
        result.put("question_record_id",actionEntity.getQuestionRecordOid());
        result.put("question_description",actionEntity.getQuestionDescription());
        result.put("return_flag_id",StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "":actionEntity.getReturnFlagId());
        result.put("return_flag_name",StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "":actionEntity.getReturnFlagName());
        result.put("return_no", StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        result.put(QuestionUpdateConst.update_date,questionActionTraceEntity.getUpdateTime());
        JSONArray resultJsonArray = new JSONArray();
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONArray questionResult = (JSONArray) JSON.parseObject(content).get("question_result");
            JSONObject object = (JSONObject) questionResult.get(0);
            //JSONArray keyReasonAnalysis = object.getJSONArray("key_reason_analysis");
            JSONArray correctiveAction= object.getJSONArray("corrective_measure_execute");
            //result.put("key_reason_analysis",keyReasonAnalysis);
            result.put("corrective_measure_execute",correctiveAction);
        }
        resultJsonArray.add(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",resultJsonArray);
        return jsonObject;
    }

    @Override
    public JSONObject rectifyVerifyBuilder(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity) {
        //获取返回的参数
        int actionTraceResponse = actionTraceMapper.updateActionTrace(questionActionTraceEntity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse =  dataInstanceMapper.updateDataInstance(dataInstanceEntity);
        if (updateResponse < 0) {
            throw new DWRuntimeException("update table dataInstance or attachment fail ! ");
        }
        // 根据主键查询返回信息
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(questionActionTraceEntity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        //对结果进行赋值
        QuestionActionTraceEntity actionEntity = list.get(0);
        result.put("question_id",actionEntity.getOid());
        result.put("question_process_status",actionEntity.getQuestionProcessStatus());
        result.put("question_process_result",actionEntity.getQuestionProcessResult());
        result.put("question_no",actionEntity.getQuestionNo());
        result.put("question_record_id",actionEntity.getQuestionRecordOid());
        //规格变动
        result.put("return_reason",StringUtils.isEmpty(actionEntity.getReturnReason()) ? "" : actionEntity.getReturnReason());
        result.put("return_step_no",StringUtils.isEmpty(actionEntity.getReturnStepNo()) ? "" : actionEntity.getReturnStepNo());
        result.put("question_description",actionEntity.getQuestionDescription());
        result.put("return_flag_id",StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "":actionEntity.getReturnFlagId());
        result.put("return_flag_name",StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "":actionEntity.getReturnFlagName());
        result.put("return_no", StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        result.put(QuestionUpdateConst.update_date,questionActionTraceEntity.getUpdateTime());
        JSONArray resultJsonArray = new JSONArray();
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONArray questionResult = (JSONArray) JSON.parseObject(content).get("question_result");
            JSONObject object = (JSONObject) questionResult.get(0);
            JSONArray correctiveAction= object.getJSONArray("corrective_measure_verify");
            //result.put("key_reason_analysis",keyReasonAnalysis);
            result.put("corrective_measure_verify",correctiveAction);
        }
        resultJsonArray.add(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",resultJsonArray);
        return jsonObject;
    }


    @Autowired
    private EightDQuestionMapper eightDQuestionMapper;

    @Override
    public List<JSONObject> getPersonPendingQuestionNum(JSONArray dataContent) throws JsonProcessingException {
        //获取人力瓶颈分析信息
        if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
            return new ArrayList<>();
        }
        List<PersonPendingNumVo> vos = new ArrayList<>();
        for(Iterator iterator = dataContent.iterator(); iterator.hasNext();){
            JSONObject object = (JSONObject) iterator.next();
            String userId = object.getString("user_id");
            if(!StringUtils.isEmpty(userId)){
                PersonPendingNumVo vo = eightDQuestionMapper.queryPersonPendingQuestionNum(userId, TenantTokenUtil.getTenantSid());
                vos.add(vo);
            }
        }
        return convertData(vos);
    }
    /**
     * 将实体类集合转成前端要求的格式集合
     *
     * @param vos 人力瓶颈分析vo集合
     * @return
     */
    private List<JSONObject> convertData(List<PersonPendingNumVo> vos) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (PersonPendingNumVo vo : vos) {
            JSONObject jsonObject = JSONObject.parseObject(new ObjectMapper().writeValueAsString(vo));
            mapList.add(jsonObject);
        }
        return mapList;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject handleUpdateForPreventionMeasure(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
        int actionTraceResponse = actionTraceMapper.updateActionTrace(questionActionTraceEntity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse =  dataInstanceMapper.updateDataInstance(entity);
        if (updateResponse < 0) {
            throw new DWRuntimeException("update table dataInstance or attachment fail ! ");
        }
        // 根据主键查询返回信息
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(questionActionTraceEntity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        QuestionActionTraceEntity actionEntity = list.get(0);
        result.put("question_id",actionEntity.getOid());
        result.put("question_process_status",actionEntity.getQuestionProcessStatus());
        result.put("question_process_result",actionEntity.getQuestionProcessResult());
        result.put("question_no",actionEntity.getQuestionNo());
        result.put("question_record_id",actionEntity.getQuestionRecordOid());
        result.put("question_description",actionEntity.getQuestionDescription());
        result.put("return_flag_id",StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "":actionEntity.getReturnFlagId());
        result.put("return_flag_name",StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "":actionEntity.getReturnFlagName());
        result.put("return_no",StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        result.put(QuestionUpdateConst.update_date,questionActionTraceEntity.getUpdateTime());
        JSONArray re = new JSONArray();

        //加上问题预防措施信息
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONArray questionResult = (JSONArray) JSON.parseObject(content).get("question_result");
            JSONObject object = (JSONObject) questionResult.get(0);
            JSONArray preventionMeasure = object.getJSONArray("prevention_measure");
            result.put("prevention_measure",preventionMeasure);
        }


        re.add(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",re);
        return jsonObject;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject handleUpdateForFeedBackVerify(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
        int actionTraceResponse = actionTraceMapper.updateActionTrace(questionActionTraceEntity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse =  dataInstanceMapper.updateDataInstance(entity);
        if (updateResponse < 0) {
            throw new DWRuntimeException("update table dataInstance or attachment fail ! ");
        }
        // 根据主键查询返回信息
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(questionActionTraceEntity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        QuestionActionTraceEntity actionEntity = list.get(0);
        result.put("question_id",actionEntity.getOid());
        result.put("question_process_status",actionEntity.getQuestionProcessStatus());
        result.put("question_process_result",actionEntity.getQuestionProcessResult());
        result.put("question_no",actionEntity.getQuestionNo());
        result.put("question_record_id",actionEntity.getQuestionRecordOid());
        result.put("question_description",actionEntity.getQuestionDescription());
        result.put("return_flag_id",StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "":actionEntity.getReturnFlagId());
        result.put("return_flag_name",StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "":actionEntity.getReturnFlagName());
        result.put(QuestionUpdateConst.update_date,questionActionTraceEntity.getUpdateTime());

        result.put("return_step_no",questionActionTraceEntity.getReturnStepNo());
        result.put("return_reason",questionActionTraceEntity.getReturnReason());
        JSONArray re = new JSONArray();

        //加上 反馈者验收信息 和 question_confirm 信息
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONArray questionResult = (JSONArray) JSON.parseObject(content).get("question_result");
            JSONObject object = (JSONObject) questionResult.get(0);
            JSONObject shortTermVerify = object.getJSONObject("short_term_verify");
            result.put("short_term_verify",shortTermVerify);
            JSONObject questionConfirm = object.getJSONObject("question_confirm");
            result.put("question_confirm",questionConfirm);
        }


        re.add(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",re);
        return jsonObject;
    }

    @Override
    public JSONObject handleUpdateForPreventionMeasureExecute(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
        int actionTraceResponse = actionTraceMapper.updateActionTrace(questionActionTraceEntity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse =  dataInstanceMapper.updateDataInstance(entity);
        if (updateResponse < 0) {
            throw new DWRuntimeException("update table dataInstance or attachment fail ! ");
        }
        // 根据主键查询返回信息
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(questionActionTraceEntity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        QuestionActionTraceEntity actionEntity = list.get(0);
        result.put("question_id",actionEntity.getOid());
        result.put("question_process_status",actionEntity.getQuestionProcessStatus());
        result.put("question_process_result",actionEntity.getQuestionProcessResult());
        result.put("question_no",actionEntity.getQuestionNo());
        result.put("question_record_id",actionEntity.getQuestionRecordOid());
        result.put("question_description",actionEntity.getQuestionDescription());
        result.put("return_flag_id",StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "":actionEntity.getReturnFlagId());
        result.put("return_flag_name",StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "":actionEntity.getReturnFlagName());
        result.put("return_no",StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        result.put(QuestionUpdateConst.update_date,questionActionTraceEntity.getUpdateTime());
        JSONArray re = new JSONArray();

        //加上问题预防措施信息
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONArray questionResult = (JSONArray) JSON.parseObject(content).get("question_result");
            JSONObject object = (JSONObject) questionResult.get(0);
            JSONArray preventionMeasureExecute = object.getJSONArray("prevention_measure_execute");
            result.put("prevention_measure_execute",preventionMeasureExecute);
        }


        re.add(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",re);
        return jsonObject;
    }

    @Override
    public JSONObject handleUpdateForPreventionMeasureExecuteVerify(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
        JSONObject resultJsonObject = JSON.parseObject(entity.getDataContent());
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = questionResult.getJSONObject(0);
        //获取封装数据
        JSONObject processConfirm = (JSONObject) dataDetail.get("process_confirm");
        int actionTraceResponse = actionTraceMapper.updateActionTrace(questionActionTraceEntity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse =  dataInstanceMapper.updateDataInstance(entity);
        if (updateResponse < 0) {
            throw new DWRuntimeException("update table dataInstance or attachment fail ! ");
        }
        // 根据主键查询返回信息
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(questionActionTraceEntity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        QuestionActionTraceEntity actionEntity = list.get(0);
        result.put("question_id",actionEntity.getOid());
        result.put("question_process_status",actionEntity.getQuestionProcessStatus());
        result.put("question_process_result",actionEntity.getQuestionProcessResult());
        result.put("question_no",actionEntity.getQuestionNo());
        result.put("question_record_id",actionEntity.getQuestionRecordOid());
        result.put("question_description",actionEntity.getQuestionDescription());
        result.put("return_flag_id",StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "":actionEntity.getReturnFlagId());
        result.put("return_flag_name",StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "":actionEntity.getReturnFlagName());
        result.put("return_no",StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        result.put("return_step_no",StringUtils.isEmpty(actionEntity.getReturnStepNo()) ? "" : actionEntity.getReturnStepNo());
        result.put("return_reason",StringUtils.isEmpty(actionEntity.getReturnReason()) ? "" : actionEntity.getReturnReason());
        result.put(QuestionUpdateConst.update_date,questionActionTraceEntity.getUpdateTime());
        result.put("processConfirm",processConfirm);
        JSONArray re = new JSONArray();

        //加上问题预防措施信息
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONArray questionResultNew = (JSONArray) JSON.parseObject(content).get("question_result");
            JSONObject object = (JSONObject) questionResultNew.get(0);
            JSONArray preventionMeasureExecuteVerify= object.getJSONArray("prevention_measure_execute_verify");
            result.put("prevention_measure_execute_verify",preventionMeasureExecuteVerify);
        }
        re.add(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",re);
        return jsonObject;
    }
}
