package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.universal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.Const.solutions.eightD.ResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUniversalSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.IamEocBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.UniversalQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.QuestionInfoUniversalModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.UniversalConfirmInfoModel;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: xieps
 * @Date: 2022/4/12 14:30
 * @Version 1.0
 * @Description D6处理确认业务逻辑
 */
public class QuestionProcessConfirmation implements QuestionHandlerStrategy {

    @Autowired
    AttachmentMapper attachmentMapper  =  SpringContextHolder.getBean(AttachmentMapper.class);

    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    UniversalQuestionBiz universalQuestionBiz =  SpringContextHolder.getBean(UniversalQuestionBiz.class);

    @Autowired
    IamEocBiz iamEocBiz =  SpringContextHolder.getBean(IamEocBiz.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);


    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        //1.string转model，将待更新的字段转为model
        UniversalConfirmInfoModel confirmInfoModel = TransferTool.convertString2Model(parameters, UniversalConfirmInfoModel.class);
        //2. 参数校验
        ParamCheckUtil.checkUniversalConfirmParams(confirmInfoModel);
        //3.更新任务卡状态
        QuestionInfoUniversalModel questionInfoModel = confirmInfoModel.getQuestionInfoUniversalModel();
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);
        // 更新实际完成时间
        entity.setActualCompleteDate(new Date());
        JSONObject resultJsonObject = JSON.parseObject(parameters);
        JSONObject confirmVerify = resultJsonObject.getJSONObject("process_confirm_verify");
        JSONObject questionConfirm = resultJsonObject.getJSONObject("question_confirm");
        JSONArray attachmentInfos = resultJsonObject.getJSONArray("attachment_info");
        return handleDetail(entity,confirmVerify,attachmentInfos,questionConfirm);
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        if (CollectionUtils.isEmpty(actionTraceEntityList)) {
            throw new DWRuntimeException("list index is 0 ");
        }
        QuestionActionTraceEntity entity = actionTraceEntityList.get(0);
        // 获取[临时措施4-3 短期结案验收]数据
        List<BeforeQuestionVo> shortTermCloseAcceptanceInfo = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionUniversalSolveEnum.short_term_closing_acceptance.getCode());
        if (!"4".equals(shortTermCloseAcceptanceInfo.get(0).getProcessStatus()) || !"2".equals(shortTermCloseAcceptanceInfo.get(0).getProcessResult())) {
            throw new DWRuntimeException(" wait short_term_closing_acceptance to finish");
        }

        // 获取[恒久措施验证信息]数据
        List<BeforeQuestionVo> permanentMeasureVerifyInfo = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionUniversalSolveEnum.permanent_measures_execute_verify.getCode());
        if (CollectionUtils.isEmpty(permanentMeasureVerifyInfo)) {
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("beforeStepNull"));
        }

        return new JSONObject().fluentPut("return_data",createDataInstance(entity, shortTermCloseAcceptanceInfo.get(0),permanentMeasureVerifyInfo.get(0)));
    }

    @Override
    public JSONObject handleBack(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 获取退回的节点的数据
            List<BeforeQuestionVo> beforeQuestionVo = actionTraceMapper.getBeforeQuestionTraceForIdentity(TenantTokenUtil.getTenantSid(),entity.getQuestionNo(), entity.getQuestionProcessStep(),
                    entity.getQuestionSolveStep());
            // 新增待审核问题追踪
            String dataInstanceOid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            String oid = IdGenUtil.uuid();
            entity.setOid(oid);
            entity.setTenantsid(TenantTokenUtil.getTenantSid());
            // 获取处理顺序
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
            // 加入退回人id、name
            entity.setReturnId((String) DWServiceContext.getContext().getProfile().get("userId"));
            entity.setReturnName((String) DWServiceContext.getContext().getProfile().get("userName"));
            //加入预计完成时间
            entity.setExpectCompleteDate(beforeQuestionVo.get(0).getExpectCompleteDate());
            DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
            dataInstanceEntity.setOid(dataInstanceOid);
            //因为是退回操作 生成待审核卡的数据加入 is_history_data 是否是历史数据修改为Y
            String dataContent = beforeQuestionVo.get(0).getDataContent();
            JSONObject resultJsonObject = JSON.parseObject(dataContent);
            JSONObject questionResult = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
            JSONArray lastingMeasure = questionResult.getJSONArray("lasting_measure");
            for(Iterator<Object> iterator = lastingMeasure.iterator();iterator.hasNext();){
                JSONObject obj = (JSONObject) iterator.next();
                obj.remove("is_history_data");
                obj.put("is_history_data","Y");
            }
            String dataContentString = JSON.toJSONString(resultJsonObject);
            dataInstanceEntity.setDataContent(dataContentString);

            dataInstanceEntity.setQuestionTraceOid(oid);

            actionTraceMapper.insertActionTrace(entity);
            dataInstanceMapper.insertDataInstance(dataInstanceEntity);

            JSONObject object = new JSONObject();
            object.put("pending_approve_question_id",oid);
            responseParam.add(object);


        }
        responseObject.put("return_data",responseParam);
        return responseObject;
    }


    private JSONArray createDataInstance(QuestionActionTraceEntity entity, BeforeQuestionVo shortTermCloseAcceptanceInfo, BeforeQuestionVo permanentMeasureVerifyInfo){
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(permanentMeasureVerifyInfo.getDataContent());
        // 获取最外层 question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);


        //追踪表实体类设置预期完成时间
        DateUtil.assignValueForExpectCompleteTime(entity,dataDetail, QuestionUniversalSolveEnum.process_confirmation.getCode());


        // 待封装 恒久措施验证table
        JSONArray permanentMeasureExecuteVerify =dataDetail.getJSONArray("lasting_measure_execute_verify");

        // 待封装 临时措施验证table
        JSONArray temporaryMeasureExecuteVerify = getTemporaryMeasureVerifyTable(shortTermCloseAcceptanceInfo);

        // 处理 通用方案 处理确认阶段 - 处理详情
        processData(dataDetail,shortTermCloseAcceptanceInfo,permanentMeasureVerifyInfo,permanentMeasureExecuteVerify,temporaryMeasureExecuteVerify,entity);
        // 处理 通用方案 - 处理确认table
        processConfirmData(dataDetail);
        // 保留value为null的数据
        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);

        // 初始化-待审核问题-数据 entity
        QuestionActionTraceEntity initTraceEntity = initQuestionActionEntity(entity);
        // 初始化-dataInstance
        DataInstanceEntity dataInstanceEntity = initDataInstance(resultJsonObject,initTraceEntity,shortTermCloseAcceptanceInfo);
        // dao
        actionTraceBiz.insertActionTraceForCurb(entity,dataInstanceEntity);
        // init -回参
        JSONArray responseParam = new JSONArray();

        responseParam.add(initResponse(initTraceEntity));
        return responseParam;
    }

    /**
     * 获取[临时措施4-3 短期结案验收]table
     * @param shortTermCloseAcceptanceInfo 临时措施验证
     * @return
     */
    private JSONArray getTemporaryMeasureVerifyTable(BeforeQuestionVo shortTermCloseAcceptanceInfo){
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(shortTermCloseAcceptanceInfo.getDataContent());
        // 获取最外层 question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
        // 获取临时措施验证 table
        JSONObject temporaryMeasureExecuteVerify = dataDetail.getJSONObject("temporary_measure_execute_verify");
        return temporaryMeasureExecuteVerify.getJSONArray("temporary_measure_execute_verify_detail");
    }


    /**
     * D6 -- 处理详情数组组装
     *
     * @param dataDetail dataDetail
     * @param shortTermCloseAcceptanceInfo D4的短期结案验收数据
     * @param permanentMeasureVerifyInfo   D5恒久措施验证信息
     * @param permanentMeasureExecuteVerify 恒久措施执行验证table
     * @param temporaryMeasureExecuteVerify 临时措施执行验证table
     */
    private void processData(JSONObject dataDetail,BeforeQuestionVo shortTermCloseAcceptanceInfo,BeforeQuestionVo permanentMeasureVerifyInfo,JSONArray permanentMeasureExecuteVerify,JSONArray temporaryMeasureExecuteVerify,QuestionActionTraceEntity entity){

        // D6-处理确认需重新封装恒久措施验证（经过更新后）table,故remove
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dataDetail.remove("lasting_measure_execute_verify");
        dataDetail.put("lasting_measure_execute_verify",new JSONObject().fluentPut("liable_person_id",permanentMeasureVerifyInfo.getLiablePersonId())
                .fluentPut("liable_person_name",permanentMeasureVerifyInfo.getLiablePersonName())
                .fluentPut("process_date",dateFormat.format(permanentMeasureVerifyInfo.getActualCompleteDate()))
                .fluentPut("lasting_measure_execute_verify_detail",permanentMeasureExecuteVerify));
        dataDetail.put("temporary_measure_execute_verify",new JSONObject().fluentPut("liable_person_id",shortTermCloseAcceptanceInfo.getLiablePersonId())
                .fluentPut("liable_person_name",shortTermCloseAcceptanceInfo.getLiablePersonName())
                .fluentPut("process_date",dateFormat.format(shortTermCloseAcceptanceInfo.getActualCompleteDate()))
                .fluentPut("temporary_measure_execute_verify_detail",temporaryMeasureExecuteVerify));
        //D4orD5的退回 处理详情中原因分析和计划安排取最新一笔数据
        List<BeforeQuestionVo> planArrangeInfos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionUniversalSolveEnum.plan_arrange.getCode());
        JSONObject planArrangeResultObject = JSON.parseObject(planArrangeInfos.get(0).getDataContent());
        JSONObject questionResult = planArrangeResultObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
        JSONObject reasonAnalysis = questionResult.getJSONObject("reason_analysis");
        JSONObject planArrangeInfo = dataDetail.getJSONObject("plan_arrange_info");
        planArrangeInfo.remove("plan_arrange");
        planArrangeInfo.put("plan_arrange",questionResult.getJSONArray("plan_arrange"));
        planArrangeInfo.remove("reason_analysis_description");
        planArrangeInfo.put("reason_analysis_description",reasonAnalysis.getString("reason_analysis_description"));
    }


    /**
     * 处理 D6- 处理确认table
     * @param newDataDetail D6 data
     * @return
     */
    private void processConfirmData(JSONObject newDataDetail){
        newDataDetail.fluentPut("process_confirm_verify",new JSONObject().fluentPut("verify_illustrate","")
                .fluentPut("verify_person_id","")
                .fluentPut("verify_person_name","")
                .fluentPut("verify_date",""));
    }


    /**
     * 初始化预防措施执行-question_action_trace表
     * @param entity 请求入参
     * @return 待落存表数据
     */
    private QuestionActionTraceEntity initQuestionActionEntity(QuestionActionTraceEntity entity){
        String dataInstanceOid = IdGenUtil.uuid();
        String oid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        entity.setOid(oid);
        // 获取执行顺序
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
        return entity;
    }

    /**
     * 初始化数据实例表
     * @param resultJsonObject data_content
     * @param entity action_trace表数据
     * @return DataInstanceEntity
     */
    private DataInstanceEntity initDataInstance(JSONObject resultJsonObject,QuestionActionTraceEntity entity,BeforeQuestionVo shortTermCloseAcceptanceInfo){
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        dataInstanceEntity.setOid(entity.getDataInstanceOid());
        //整合恒久措施施执行验证和临时措施结案的附件信息
        JSONObject shortCloseMeasureJsonObject = JSON.parseObject(shortTermCloseAcceptanceInfo.getDataContent());
        JSONObject dataShortCloseMeasureDetail = shortCloseMeasureJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
        JSONObject permanentDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
        JSONArray permanentAttachmentInfo = permanentDetail.getJSONArray("attachment_info");
        JSONArray shortCloseMeasureAttachmentInfo = dataShortCloseMeasureDetail.getJSONArray("attachment_info");
        List<Map<String,Object>> unionList = ListUtils.union(permanentAttachmentInfo, shortCloseMeasureAttachmentInfo);
        //根据attachment_id进行去重
        List<Map<String, Object>> restructuredAttachment = unionList.stream().filter(DeduplicationUtil.distinctByKey(item -> item.get("attachment_id"))).collect(Collectors.toList());
        permanentDetail.remove("attachment_info");
        permanentDetail.put("attachment_info",restructuredAttachment);
        dataInstanceEntity.setDataContent(JSON.toJSONString(resultJsonObject));
        dataInstanceEntity.setQuestionTraceOid(entity.getOid());
        return dataInstanceEntity;
    }


    /**
     * init 回参
     * @param entity question_action_trace数据
     * @return 回参-供DTD使用
     */
    private JSONObject initResponse(QuestionActionTraceEntity entity){
        JSONObject responseObject = new JSONObject();
        responseObject.put(ResponseConst.pending_approve_question_id,entity.getOid());
        responseObject.put(ResponseConst.question_no,entity.getQuestionNo());
        responseObject.put(ResponseConst.question_description,entity.getQuestionDescription());
        responseObject.put(ResponseConst.return_flag_id,entity.getReturnFlagId());
        responseObject.put(ResponseConst.return_flag_name,entity.getReturnFlagName());
        responseObject.put(ResponseConst.expect_solve_date,entity.getExpectCompleteDate());
        return responseObject;
    }

    /**
     * 更新表单详情数据
     * @param confirmVerify 入参 需更新字段信息
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONObject confirmVerify,JSONArray attachmentModels,JSONObject questionConfirm) throws IOException {
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(questionActionTraceEntity.getOid());
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        // 获取最外层 question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject("question_result");

        JSONArray planManagementInfo = dataDetail.getJSONArray("plan_arrange");

        dataDetail.remove("process_confirm_verify");
        dataDetail.put("process_confirm_verify",confirmVerify);

        dataDetail.remove("question_confirm");
        dataDetail.put("question_confirm",questionConfirm);

        JSONArray attachmentInfos = dataDetail.getJSONArray("attachment_info");

        JSONArray mustUploadAttachments = new JSONArray();
        //附件退回是否重复上传校验标识
        boolean repeatCheckFlag = false;
        for (Iterator<Object> iterator = attachmentModels.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            boolean status = true;
            for (Iterator<Object> it = attachmentInfos.iterator();it.hasNext();) {
                JSONObject attach = (JSONObject)it.next();
                if(!repeatCheckFlag){
                    repeatCheckFlag = "SE003009".equals(attach.getString("attachment_belong_stage"));
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
        //如果是退回就不进行校验
        if(questionActionTraceEntity.getQuestionProcessStatus() != 6 && questionActionTraceEntity.getQuestionProcessResult() != 5) {
            //校验附件是否必传
            for (Iterator<Object> ite = planManagementInfo.iterator(); ite.hasNext(); ) {
                JSONObject obj = (JSONObject) ite.next();
                if ("SE003009".equals(obj.get("step_no"))) {
                    if (obj.get("attachment_upload_flag").equals("Y")) {
                        if (mustUploadAttachments.size() == 0) {
                            // 查询数据库
                            List<AttachmentEntity> attachmentEntities = attachmentMapper.getAttachments((Long) DWServiceContext.getContext().getProfile().get("tenantSid"), dataInstanceVo.getOid());
                            if (attachmentEntities.size() == 0 && !repeatCheckFlag) {
                                throw new DWRuntimeException("attachment_upload_flag is Y, so attachment must be uploaded ! ");
                            }
                            break;
                        }
                    }
                }
            }
        }
        // 保存附件
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),
                dataInstanceVo.getOid(),"SE003009");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // 初始实体
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // 落存数据
        return universalQuestionBiz.handleUpdateForProcessConfirm(questionActionTraceEntity,attachmentEntities,entity);
    }

}
