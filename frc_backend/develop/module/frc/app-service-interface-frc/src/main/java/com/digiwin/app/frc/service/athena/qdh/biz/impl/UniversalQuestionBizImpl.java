package com.digiwin.app.frc.service.athena.qdh.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.Const.qdh.update.QuestionUpdateConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionSolveEnum;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionBackEntity;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionBackMapper;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionProcessConfigBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.IamEocBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.UniversalQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.QuestionDetailVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/4/6 19:40
 * @Version 1.0
 * @Description  针对通用解决方案 添加处理Biz-impl
 */
@Service
public class UniversalQuestionBizImpl implements UniversalQuestionBiz {


    @Autowired
    DataInstanceMapper dataInstanceMapper;

    @Autowired
    ActionTraceMapper actionTraceMapper;

    @Autowired
    IamEocBiz iamEocBiz;

    Logger logger = LoggerFactory.getLogger(ActionTraceBiz.class);

    @Autowired
    QuestionProcessConfigBiz questionProcessConfigBiz;

    @Override
    public JSONObject getQuestionDetail(String questionId) throws ParseException {
        // 根据questionId查询问题详情
        QuestionDetailVo questionDetailVo =
                actionTraceMapper.getQuestionTrace(questionId);
        if (null == questionDetailVo) {
            // 查询为空，返回空对象
            return null;
        }
        // 解析最外层结构
        JSONObject jsonObject = JSONObject.parseObject(questionDetailVo.getDataContent());
        // detail为即将返回数据格式
        JSONObject detail = jsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);

        //迭代五  修改预计完成时间回传前端的格式为yyyy-MM-dd
        processExpectSolveDate(detail);

        // 附件所属阶段加入多语系
        JSONArray attachmentInfos = detail.getJSONArray("attachment_info");
        for (Iterator iterator = attachmentInfos.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            String step = (String) obj.get("attachment_belong_stage");
            obj.put("attachment_belong_stage", MultilingualismUtil.getLanguage(step));
        }
        // 封装question_info
        detail.put(QuestionResponseConst.QUESTION_INFO,packageQuestionInfo(questionId,questionDetailVo));
        // 封装最外层，用于任务卡展示
        packageDetail(detail,questionDetailVo);

        return jsonObject;
    }

    private void processExpectSolveDate(JSONObject detail) throws ParseException {
        //迭代五 planArrange中预计完成时间回传前端yyyy-MM-dd
        JSONArray planArrange = detail.getJSONArray("plan_arrange");
        for (Iterator iterator = planArrange.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            String expectSolveDate = (String) obj.get("expect_solve_date");
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
            obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
        }

        //迭代五 D4中预计完成时间回传前端yyyy-MM-dd
        if (detail.containsKey("temporary_measure") && detail.get("temporary_measure") instanceof JSONArray){
            JSONArray temporaryMeasure = detail.getJSONArray("temporary_measure");
            for (Iterator iterator = temporaryMeasure.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }

        //迭代五 D4-1中预计完成时间回传前端yyyy-MM-dd
        if (detail.containsKey("temporary_measure_execute") && detail.get("temporary_measure_execute") instanceof JSONArray){
            JSONArray temporaryMeasure = detail.getJSONArray("temporary_measure_execute");
            for (Iterator iterator = temporaryMeasure.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }
        //迭代五 D4-2中预计完成时间回传前端yyyy-MM-dd
        if (detail.containsKey("temporary_measure_execute_verify") && detail.get("temporary_measure_execute_verify") instanceof JSONArray){
            JSONArray temporaryMeasure = detail.getJSONArray("temporary_measure_execute_verify");
            for (Iterator iterator = temporaryMeasure.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }

        //迭代五 D5中预计完成时间回传前端yyyy-MM-dd
        if (detail.containsKey("lasting_measure") && detail.get("lasting_measure") instanceof JSONArray){
            JSONArray lastingMeasure = detail.getJSONArray("lasting_measure");
            for (Iterator iterator = lastingMeasure.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }
        if (detail.containsKey("plan_arrange_info")  && detail.get("plan_arrange_info") instanceof JSONObject){
            JSONObject planArrangeInfo = detail.getJSONObject("plan_arrange_info");
            JSONArray planArrange1 = planArrangeInfo.getJSONArray("plan_arrange");
            for (Iterator iterator = planArrange1.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }

        //迭代五 D5-1中预计完成时间回传前端yyyy-MM-dd
        if (detail.containsKey("lasting_measure_execute")  && detail.get("lasting_measure_execute") instanceof JSONArray){
            JSONArray lastingMeasureExecute = detail.getJSONArray("lasting_measure_execute");
            for (Iterator iterator = lastingMeasureExecute.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }

        //迭代五 D5-2中预计完成时间回传前端yyyy-MM-dd
        if (detail.containsKey("lasting_measure_execute_verify") && detail.get("lasting_measure_execute_verify") instanceof JSONArray){
            JSONArray lastingMeasureExecuteVerify = detail.getJSONArray("lasting_measure_execute_verify");
            for (Iterator iterator = lastingMeasureExecuteVerify.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }

        //迭代五 D6中处理详情 预计完成时间回传前端yyyy-MM-dd
        if (detail.containsKey("temporary_measure_execute_verify") && detail.get("temporary_measure_execute_verify") instanceof JSONObject){
            JSONObject temporaryMeasureExecuteVerify = detail.getJSONObject("temporary_measure_execute_verify");
            JSONArray temporaryMeasureExecuteVerifyDetail = temporaryMeasureExecuteVerify.getJSONArray("temporary_measure_execute_verify_detail");
            for (Iterator iterator = temporaryMeasureExecuteVerifyDetail.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }

        if (detail.containsKey("lasting_measure_execute_verify") && detail.get("lasting_measure_execute_verify") instanceof JSONObject){
            JSONObject lastingMeasureExecuteVerify = detail.getJSONObject("lasting_measure_execute_verify");
            JSONArray lastingMeasureExecuteVerifyDetail = lastingMeasureExecuteVerify.getJSONArray("lasting_measure_execute_verify_detail");
            for (Iterator iterator = lastingMeasureExecuteVerifyDetail.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }
    }


    /**
     * 封装question_info层
     * @param questionId 问题号
     * @param questionDetailVo 查询出的问题数据
     * @return JSONArray
     */
    private Object packageQuestionInfo(String questionId, QuestionDetailVo questionDetailVo){
        JSONObject object = new JSONObject();
        object.put(QuestionResponseConst.QUESTION_ID,questionId);
        object.put(QuestionResponseConst.QUESTION_PROCESS_STATUS,questionDetailVo.getQuestionProcessStatus());
        object.put(QuestionResponseConst.QUESTION_PROCESS_RESULT,questionDetailVo.getQuestionProcessResult());
        object.put(QuestionResponseConst.QUESTION_NO,questionDetailVo.getQuestionNo());
        object.put(QuestionResponseConst.QUESTION_RECORD_ID,questionDetailVo.getQuestionRecordId());
        object.put(QuestionResponseConst.QUESTION_FLAG_ID,questionDetailVo.getReturnFlagId());
        object.put(QuestionResponseConst.QUESTION_FLAG_NAME,questionDetailVo.getReturnFlagName());
        object.put(QuestionResponseConst.QUESTION_DESCRIPTION,questionDetailVo.getQuestionDescription());
        object.put("liable_person_id",questionDetailVo.getLiablePersonId());
        object.put("liable_person_name",questionDetailVo.getLiablePersonName());
        object.put("liable_person_position_id",questionDetailVo.getLiablePersonPositionId());
        object.put("liable_person_position_name",questionDetailVo.getLiablePersonPositionName());
        object.put("return_reason_no",questionDetailVo.getReturnNo());
        object.put("close_reason",questionDetailVo.getCloseReason());
        object.put("create_date",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(questionDetailVo.getCreateDate()));
        /**
         * 冲刺六新增字段
         */
        object.put("return_reason",questionDetailVo.getReturnReason());
        object.put("return_step_no",questionDetailVo.getReturnStepNo());
        // null 转 “”
        String dataContentString = JSON.toJSONString(object, filter);
        // 按规格封装object  question_info层组成集合形式
        Object parse = JSONObject.parse(dataContentString);
        return parse;
    }

    private ValueFilter filter = (obj, s, v) -> {
        if (null == v) {
            return "";
        }
        return v;
    };

    /**
     * 封装最外层，用于任务卡展示，只是为了配合athena页面展示，一定要放在最外层，所以有很多冗余字段返回
     * @param detail data_content内容
     * @param questionDetailVo 根据主键查询出的问题详细信息
     */
    private void packageDetail(JSONObject detail, QuestionDetailVo questionDetailVo) {
        // 加入退回原因编号
        detail.put(QuestionResponseConst.RETURN_REASON_NO,"");
        // 退回原因
        detail.put(QuestionResponseConst.RETURN_REASON,StringUtils.isEmpty(questionDetailVo.getReturnReason())?"":questionDetailVo.getReturnReason());
        // 退回人姓名
        detail.put("return_name","");
        // 退回人id
        detail.put("return_id","");
        // 退回标识：由哪个关卡退回
        detail.put("return_flag_name","");

        if (!StringUtils.isEmpty(questionDetailVo.getReturnFlagId())) {
            detail.put("return_step_no",StringUtils.isEmpty(questionDetailVo.getReturnStepNo())?"":questionDetailVo.getReturnStepNo());
            // 加入问题退回人
            detail.put("return_id",questionDetailVo.getReturnId());
            detail.put("return_name",questionDetailVo.getReturnName());
        }
        // 问题号
        detail.put("question_no",questionDetailVo.getQuestionNo());
        // 问题描述
        detail.put("question_description",questionDetailVo.getQuestionDescription());
        if (!StringUtils.isEmpty(questionDetailVo.getReturnFlagName())) {
            detail.put("return_flag_name",questionDetailVo.getReturnFlagName());
        }
        // 加入问题提出人
        JSONObject basicInfo = detail.getJSONObject("question_basic_info");
        detail.put("question_proposer_id",basicInfo.get("question_proposer_id"));
        detail.put("question_proposer_name",basicInfo.get("question_proposer_name"));
        // 迭代三内容
        if ("QF".equals(questionDetailVo.getQuestionProcessStep())) {
            detail.put("risk_level","");
            detail.put("urgency","");
        }else {
            JSONObject identify = detail.getJSONObject("question_identify_info");
            //JSONObject identify = questionIdentifyInfo.getJSONObject(0);
            detail.put("risk_level",identify.get("risk_level_name"));
            detail.put("urgency",identify.get("urgency"));
        }
        // 最外层返回负责人信息
        detail.put("liable_person_id",questionDetailVo.getLiablePersonId());
        detail.put("liable_person_name",questionDetailVo.getLiablePersonName());
        detail.put("liable_person_position_id",questionDetailVo.getLiablePersonPositionId());
        detail.put("liable_person_position_name",questionDetailVo.getLiablePersonPositionName());

        // 获取问题分析任务卡的负责人
        try {
            if (QuestionSolveEnum.question_distribution.getCode().equals(questionDetailVo.getQuestionSolveStep()) ||
                    QuestionSolveEnum.question_curb_distribution.getCode().equals(questionDetailVo.getQuestionSolveStep())){
                //todo 加入处理人信息
                List<Map<String,Object>> list = iamEocBiz.getUsers();
                JSONArray processPersonInfos = new JSONArray();
                for (Map map : list) {
                    JSONObject personInfo = new JSONObject();
                    personInfo.put("process_person_id",map.get("id"));
                    personInfo.put("process_person_name",map.get("name"));
                    processPersonInfos.add(personInfo);
                }

                detail.put("process_person_info",processPersonInfos);
            }
            if("QIA".equals(questionDetailVo.getQuestionProcessStep()) || "QIR".equals(questionDetailVo.getQuestionProcessStep())) {
                packageQIPerson(detail);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取问题分析负责人
     * @param detail data_content
     */
    private void packageQIPerson(JSONObject detail) throws JsonProcessingException {
        // 从question_basic_info里获取 risk_level_oid、question_source_oid、question_attribution_no、question_attribution_no
        JSONObject basicInfo = detail.getJSONObject("question_basic_info");
        // 封装请求入参
        JSONArray processConfigs = new JSONArray();
        JSONObject config = new JSONObject();
        config.put("risk_level_id",basicInfo.getString("risk_level_oid"));
        config.put("source_id",basicInfo.getString("question_source_oid"));
        config.put("attribution_no",basicInfo.getString("question_attribution_no"));
        config.put("classification_id",basicInfo.getString("question_classification_oid"));
        logger.info("基础信息risk_level_id是"+basicInfo.getString("risk_level_oid"));
        logger.info("基础信息source_id是"+basicInfo.getString("question_source_oid"));
        logger.info("基础信息attribution_no是"+basicInfo.getString("question_attribution_no"));
        logger.info("基础信息classification_id是"+basicInfo.getString("question_classification_oid"));
        processConfigs.add(config);
        // 4配1获取负责人，若为null，返回 “”
        List<JSONObject> resultList = questionProcessConfigBiz.getQuestionProcessConfig(processConfigs);
        if (!CollectionUtils.isEmpty(resultList)) {
            // 获取问题分析 question_identify_info结构，将负责人信息放入
            JSONObject identifyInfo = detail.getJSONObject("question_identify_info");
            logger.info("查询出的负责人是"+resultList.get(0).getString("liable_person_id"));
            logger.info("查询出的负责人name是"+resultList.get(0).getString("liable_person_name"));

            identifyInfo.put("liable_person_id",resultList.get(0).getString("liable_person_id"));
            identifyInfo.put("liable_person_name",resultList.get(0).getString("liable_person_name"));
        }
    }

    @Autowired
    AttachmentMapper attachmentMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject handleUpdateForPlanArrange(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
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
        result.put("return_step_no", StringUtils.isEmpty(actionEntity.getReturnStepNo()) ? "" : actionEntity.getReturnStepNo());
        JSONArray re = new JSONArray();

        //加上原因分析、问题确认、计划安排信息
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONObject object = (JSONObject) JSON.parseObject(content).get("question_result");
            JSONObject reasonAnalysis = object.getJSONObject("reason_analysis");
            JSONObject questionConfirm = object.getJSONObject("question_confirm");
            JSONArray planManagement = object.getJSONArray("plan_arrange");
            result.put("reason_analysis",reasonAnalysis);
            result.put("question_confirm",questionConfirm);
            result.put("plan_arrange",planManagement);
        }

        re.add(result);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",re);
        return jsonObject;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject handleUpdateForTemporaryMeasures(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
        int actionTraceResponse = actionTraceMapper.updateActionTrace(questionActionTraceEntity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse = dataInstanceMapper.updateDataInstance(entity);
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
        result.put("question_id", actionEntity.getOid());
        result.put("question_process_status", actionEntity.getQuestionProcessStatus());
        result.put("question_process_result", actionEntity.getQuestionProcessResult());
        result.put("question_no", actionEntity.getQuestionNo());
        result.put("question_record_id", actionEntity.getQuestionRecordOid());
        result.put("question_description", actionEntity.getQuestionDescription());
        result.put("return_flag_id", StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "" : actionEntity.getReturnFlagId());
        result.put("return_flag_name", StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "" : actionEntity.getReturnFlagName());
        result.put("return_no", StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        SimpleDateFormat forma = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        result.put(QuestionUpdateConst.update_date, forma.format(questionActionTraceEntity.getUpdateTime()));
        result.put("return_step_no", StringUtils.isEmpty(actionEntity.getReturnStepNo()) ? "" : actionEntity.getReturnStepNo());
        JSONArray newJson = new JSONArray();

        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(), questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONObject object = (JSONObject) JSON.parseObject(content).get("question_result");
            //JSONObject temporaryMeasure = object.getJSONObject("temporary_measure");
            JSONArray temporaryMeasure = object.getJSONArray("temporary_measure");
            //迭代五 D4中预计完成时间回传前端yyyy-MM-dd
//                for (Iterator iterator = temporaryMeasure.iterator(); iterator.hasNext();) {
//                    JSONObject obj = (JSONObject)iterator.next();
//                    String expectSolveDate = (String) obj.get("expect_solve_date");
//                    Date date = null;
//                    try {
//                        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
//                }
            JSONObject questionConfirm = object.getJSONObject("question_confirm");
            result.put("temporary_measure", temporaryMeasure);
            result.put("question_confirm", questionConfirm);
        }

        newJson.add(result);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info", newJson);
        return jsonObject;
    }


    @Override
    public JSONObject handleUpdateForTemporaryMeasuresExecute(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
        int actionTraceResponse = actionTraceMapper.updateActionTrace(questionActionTraceEntity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse = dataInstanceMapper.updateDataInstance(entity);
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
        result.put("question_id", actionEntity.getOid());
        result.put("question_process_status", actionEntity.getQuestionProcessStatus());
        result.put("question_process_result", actionEntity.getQuestionProcessResult());
        result.put("question_no", actionEntity.getQuestionNo());
        result.put("question_record_id", actionEntity.getQuestionRecordOid());
        result.put("question_description", actionEntity.getQuestionDescription());
        result.put("return_flag_id", StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "" : actionEntity.getReturnFlagId());
        result.put("return_flag_name", StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "" : actionEntity.getReturnFlagName());
        result.put("return_no", StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        SimpleDateFormat forma = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        result.put(QuestionUpdateConst.update_date, forma.format(questionActionTraceEntity.getUpdateTime()));
        result.put("return_step_no", StringUtils.isEmpty(actionEntity.getReturnStepNo()) ? "" : actionEntity.getReturnStepNo());
        JSONArray newJson = new JSONArray();

        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(), questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONObject object = (JSONObject) JSON.parseObject(content).get("question_result");
            //JSONObject temporaryMeasureExecute = object.getJSONObject("temporary_measure_execute");
            JSONArray temporaryMeasureExecute = object.getJSONArray("temporary_measure_execute");
            JSONObject questionConfirm = object.getJSONObject("question_confirm");
            //迭代五 D4-1中预计完成时间回传前端yyyy-MM-dd
//                for (Iterator iterator = temporaryMeasureExecute.iterator(); iterator.hasNext(); ) {
//                    JSONObject obj = (JSONObject) iterator.next();
//                    String expectSolveDate = (String) obj.get("expect_solve_date");
//                    Date date = null;
//                    try {
//                        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
//                }

            result.put("temporary_measure_execute", temporaryMeasureExecute);
            result.put("question_confirm", questionConfirm);
        }
        newJson.add(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info", newJson);
        return jsonObject;
    }

    @Override
    public JSONObject handleUpdateForTemporaryMeasuresExecuteVerify(QuestionActionTraceEntity entity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity) {
        int actionTraceResponse = actionTraceMapper.updateActionTrace(entity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse = dataInstanceMapper.updateDataInstance(dataInstanceEntity);
        if (updateResponse < 0) {
            throw new DWRuntimeException("update table dataInstance or attachment fail ! ");
        }
        // 根据主键查询返回信息
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(entity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        QuestionActionTraceEntity actionEntity = list.get(0);
        result.put("question_id", actionEntity.getOid());
        result.put("question_process_status", actionEntity.getQuestionProcessStatus());
        result.put("question_process_result", actionEntity.getQuestionProcessResult());
        result.put("question_no", actionEntity.getQuestionNo());
        result.put("question_record_id", actionEntity.getQuestionRecordOid());
        result.put("question_description", actionEntity.getQuestionDescription());
        result.put("return_flag_id", StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "" : actionEntity.getReturnFlagId());
        result.put("return_flag_name", StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "" : actionEntity.getReturnFlagName());
        result.put("return_no", StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        SimpleDateFormat forma = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        result.put(QuestionUpdateConst.update_date, forma.format(entity.getUpdateTime()));
        result.put("return_step_no", StringUtils.isEmpty(actionEntity.getReturnStepNo()) ? "" : actionEntity.getReturnStepNo());
        JSONArray newJson = new JSONArray();

        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(), entity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONObject object = (JSONObject) JSON.parseObject(content).get("question_result");
            JSONArray temporaryMeasureExecuteVerify = object.getJSONArray("temporary_measure_execute_verify");
            JSONObject questionConfirm = object.getJSONObject("question_confirm");
            //迭代五 D4-2中预计完成时间回传前端yyyy-MM-dd
//                for (Iterator iterator = temporaryMeasureExecuteVerify.iterator(); iterator.hasNext();) {
//                    JSONObject obj = (JSONObject)iterator.next();
//                    String expectSolveDate = (String) obj.get("expect_solve_date");
//                    Date date = null;
//                    try {
//                        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
//                }

            result.put("temporary_measure_execute_verify", temporaryMeasureExecuteVerify);
            result.put("question_confirm", questionConfirm);
        }

        newJson.add(result);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info", newJson);
        return jsonObject;
    }

    @Override
    public JSONObject handleUpdateForQuestionShortTermClosingAcceptance(QuestionActionTraceEntity entity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity) {
        int actionTraceResponse = actionTraceMapper.updateActionTrace(entity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse = dataInstanceMapper.updateDataInstance(dataInstanceEntity);
        if (updateResponse < 0) {
            throw new DWRuntimeException("update table dataInstance or attachment fail ! ");
        }
        // 根据主键查询返回信息
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(entity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        QuestionActionTraceEntity actionEntity = list.get(0);
        result.put("question_id", actionEntity.getOid());
        result.put("question_process_status", actionEntity.getQuestionProcessStatus());
        result.put("question_process_result", actionEntity.getQuestionProcessResult());
        result.put("question_no", actionEntity.getQuestionNo());
        result.put("question_record_id", actionEntity.getQuestionRecordOid());
        result.put("question_description", actionEntity.getQuestionDescription());
        result.put("return_flag_id", StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "" : actionEntity.getReturnFlagId());
        result.put("return_flag_name", StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "" : actionEntity.getReturnFlagName());
        result.put("return_no", StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        SimpleDateFormat forma = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        result.put(QuestionUpdateConst.update_date, forma.format(entity.getUpdateTime()));
        result.put("return_step_no", StringUtils.isEmpty(actionEntity.getReturnStepNo()) ? "" : actionEntity.getReturnStepNo());
        JSONArray newJson = new JSONArray();

        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(), entity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONObject object = (JSONObject) JSON.parseObject(content).get("question_result");
            JSONObject shortTermVerify = object.getJSONObject("short_term_verify");
            JSONObject questionConfirm = object.getJSONObject("question_confirm");
            result.put("short_term_verify", shortTermVerify);
            result.put("question_confirm", questionConfirm);
        }

        newJson.add(result);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info", newJson);
        return jsonObject;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject handleUpdateForPermanentMeasure(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
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
        result.put("return_step_no", actionEntity.getReturnStepNo());
        JSONArray re = new JSONArray();

        //加上原因分析、问题确认、计划安排信息
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONObject object = (JSONObject) JSON.parseObject(content).get("question_result");
            JSONObject questionConfirm = object.getJSONObject("question_confirm");
            JSONArray lastingMeasure = object.getJSONArray("lasting_measure");
            result.put("question_confirm",questionConfirm);
            result.put("lasting_measure",lastingMeasure);
        }

        re.add(result);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",re);
        return jsonObject;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject handleUpdateForPermanentMeasureExecute(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
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
        result.put("return_step_no", actionEntity.getReturnStepNo());
        JSONArray re = new JSONArray();

        //加上问题确认、恒久措施执行
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONObject object = (JSONObject) JSON.parseObject(content).get("question_result");
            JSONObject questionConfirm = object.getJSONObject("question_confirm");
            JSONArray lastingMeasureExecute = object.getJSONArray("lasting_measure_execute");
            result.put("question_confirm",questionConfirm);
            result.put("lasting_measure_execute",lastingMeasureExecute);
        }

        re.add(result);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",re);
        return jsonObject;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject handleUpdateForPermanentMeasureVerify(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
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
        result.put("return_step_no", actionEntity.getReturnStepNo());
        JSONArray re = new JSONArray();

        //加上问题确认、恒久措施执行验证信息
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONObject object = (JSONObject) JSON.parseObject(content).get("question_result");
            JSONObject questionConfirm = object.getJSONObject("question_confirm");
            JSONArray lastingMeasureExecuteVerify = object.getJSONArray("lasting_measure_execute_verify");
            result.put("question_confirm",questionConfirm);
            result.put("lasting_measure_execute_verify",lastingMeasureExecuteVerify);
        }

        re.add(result);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",re);
        return jsonObject;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject handleUpdateForProcessConfirm(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
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
        result.put("return_step_no", actionEntity.getReturnStepNo());
        JSONArray re = new JSONArray();

        //加上问题确认、处理确认验收信息
        //查询出更新后的对应实例表 取最新值
        DataInstanceEntity queryEntity = dataInstanceMapper.getQuestionDetail(TenantTokenUtil.getTenantSid(),questionActionTraceEntity.getOid());
        if (!StringUtils.isEmpty(queryEntity)) {
            String content = queryEntity.getDataContent();
            JSONObject object = (JSONObject) JSON.parseObject(content).get("question_result");
            JSONObject questionConfirm = object.getJSONObject("question_confirm");
            JSONObject processConfirmVerify = object.getJSONObject("process_confirm_verify");
            result.put("question_confirm",questionConfirm);
            result.put("process_confirm_verify",processConfirmVerify);
        }

        re.add(result);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",re);
        return jsonObject;
    }

    @Override
    public JSONObject getQuestionDetailConvertArray(String questionId) throws ParseException {
        // 根据questionId查询问题详情
        QuestionDetailVo questionDetailVo =
                actionTraceMapper.getQuestionTrace(questionId);
        if (null == questionDetailVo) {
            // 查询为空，返回空对象
            return null;
        }
        // 解析最外层结构
        JSONObject jsonObject = JSONObject.parseObject(questionDetailVo.getDataContent());
        // detail为即将返回数据格式
        JSONObject detail = jsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
        //处理预计完成时间格式问题
        processExpectSolveDate(detail);

        // 附件所属阶段加入多语系
        JSONArray attachmentInfos = detail.getJSONArray("attachment_info");
        for (Iterator iterator = attachmentInfos.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            String step = (String) obj.get("attachment_belong_stage");
            obj.put("attachment_belong_stage", MultilingualismUtil.getLanguage(step));
        }
        // 封装question_info
        detail.put(QuestionResponseConst.QUESTION_INFO,packageQuestionInfoConvertArray(questionId,questionDetailVo));
        // 封装最外层，用于任务卡展示
        packageDetail(detail,questionDetailVo);

        processFormatArray(detail);
        jsonObject.remove(QuestionResponseConst.QUESTION_RESULT);
        String detailStr = JSON.toJSONString(detail, SerializerFeature.DisableCircularReferenceDetect);
        JSONObject detailNew = JSON.parseObject(detailStr);
        jsonObject.put(QuestionResponseConst.QUESTION_RESULT,new JSONArray().fluentAdd(detailNew));
        System.out.println("detail信息打印："+detail.toJSONString());
        return jsonObject;

    }

    private void processFormatArray(JSONObject detail) {
        JSONObject questionIdentifyInfo = detail.getJSONObject("question_identify_info");
        JSONObject questionBasicInfo = detail.getJSONObject("question_basic_info");
        JSONObject questionDetailInfo = detail.getJSONObject("question_detail_info");
        detail.remove("question_identify_info");
        detail.remove("question_basic_info");
        detail.remove("question_detail_info");

        String questionIdentifyInfoStr = JSON.toJSONString(questionIdentifyInfo, SerializerFeature.DisableCircularReferenceDetect);
        JSONObject questionIdentifyInfoNew = JSON.parseObject(questionIdentifyInfoStr);
        detail.put("question_identify_info",new JSONArray().fluentAdd(questionIdentifyInfoNew));

        String questionBasicInfoStr = JSON.toJSONString(questionBasicInfo, SerializerFeature.DisableCircularReferenceDetect);
        JSONObject questionBasicInfoNew = JSON.parseObject(questionBasicInfoStr);
        detail.put("question_basic_info",new JSONArray().fluentAdd(questionBasicInfoNew));

        String questionDetailInfoStr = JSON.toJSONString(questionDetailInfo, SerializerFeature.DisableCircularReferenceDetect);
        JSONObject questionDetailInfoNew = JSON.parseObject(questionDetailInfoStr);
        detail.put("question_detail_info",new JSONArray().fluentAdd(questionDetailInfoNew));
    }

    /**
     * 封装question_info层
     * @param questionId 问题号
     * @param questionDetailVo 查询出的问题数据
     * @return JSONArray
     */
    private JSONArray packageQuestionInfoConvertArray(String questionId,QuestionDetailVo questionDetailVo){
        JSONObject object = new JSONObject();
        object.put(QuestionResponseConst.QUESTION_ID,questionId);
        object.put(QuestionResponseConst.QUESTION_PROCESS_STATUS,questionDetailVo.getQuestionProcessStatus());
        object.put(QuestionResponseConst.QUESTION_PROCESS_RESULT,questionDetailVo.getQuestionProcessResult());
        object.put(QuestionResponseConst.QUESTION_NO,questionDetailVo.getQuestionNo());
        object.put(QuestionResponseConst.QUESTION_RECORD_ID,questionDetailVo.getQuestionRecordId());
        object.put(QuestionResponseConst.QUESTION_FLAG_ID,questionDetailVo.getReturnFlagId());
        object.put(QuestionResponseConst.QUESTION_FLAG_NAME,questionDetailVo.getReturnFlagName());
        object.put(QuestionResponseConst.QUESTION_DESCRIPTION,questionDetailVo.getQuestionDescription());
        object.put("liable_person_id",questionDetailVo.getLiablePersonId());
        object.put("liable_person_name",questionDetailVo.getLiablePersonName());
        object.put("liable_person_position_id",questionDetailVo.getLiablePersonPositionId());
        object.put("liable_person_position_name",questionDetailVo.getLiablePersonPositionName());
        object.put("return_reason_no",questionDetailVo.getReturnNo());
        object.put("close_reason",questionDetailVo.getCloseReason());
        /**
         * 冲刺六新增字段
         */
        object.put("return_reason",questionDetailVo.getReturnReason());
        object.put("return_step_no",questionDetailVo.getReturnStepNo());
        // null 转 “”
        String dataContentString = JSON.toJSONString(object, filter);
        // 按规格封装数组 question_info层组成集合形式
        JSONArray result = new JSONArray();
        result.add(JSONObject.parse(dataContentString));
        return result;
    }

}
