package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.eightD;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.Const.solutions.eightD.ResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.Question8DSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.IamEocBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.ConfirmInfoModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.QuestionInfo8DModel;
import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.PendingQuestionVo;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName QuestionConfirm
 * @Description 问题确认
 * @Author HeX
 * @Date 2022/3/20 10:32
 * @Version 1.0
 **/
public class QuestionConfirm implements QuestionHandlerStrategy {

    @Autowired
    AttachmentMapper attachmentMapper  =  SpringContextHolder.getBean(AttachmentMapper.class);

    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    IamEocBiz iamEocBiz =  SpringContextHolder.getBean(IamEocBiz.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        //1.string转model，将待更新的字段转为model
        ConfirmInfoModel confirmInfoModel = TransferTool.convertString2Model(parameters, ConfirmInfoModel.class);
        //2. 参数校验
        ParamCheckUtil.checkConfirmParams(confirmInfoModel);
        //3.更新任务卡状态
        QuestionInfo8DModel questionInfoModel = confirmInfoModel.getQuestionInfos().get(0);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);
        // 更新实际完成时间
        entity.setActualCompleteDate(new Date());
        JSONObject resultJsonObject = JSON.parseObject(parameters);
        JSONObject actionConfirm = resultJsonObject.getJSONObject("process_confirm");
        JSONArray attachmentInfos = resultJsonObject.getJSONArray("attachment_info");
        return handleDetail(entity,actionConfirm,attachmentInfos);
    }

    /**
     * 更新表单详情数据
     * @param actionConfirm 入参 需更新字段信息
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONObject actionConfirm,JSONArray attachmentModels) throws IOException {
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(questionActionTraceEntity.getOid());
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        // 获取最外层 question_result
        JSONObject dataDetail = resultJsonObject.getJSONArray("question_result").getJSONObject(0);
        dataDetail.remove("process_confirm");
        dataDetail.put("process_confirm",actionConfirm);
        JSONArray attachmentInfos = dataDetail.getJSONArray("attachment_info");

        JSONArray planManagementInfo = dataDetail.getJSONArray("plan_arrange");

        //抽取 预防措施任务卡上上传的附件
        JSONArray mustUploadAttachments = new JSONArray();
        //附件退回是否重复上传校验标识
        boolean repeatCheckFlag = false;
        for (Iterator<Object> iterator = attachmentModels.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            boolean status = true;
            for (Iterator<Object> it = attachmentInfos.iterator();it.hasNext();) {
                JSONObject attach = (JSONObject)it.next();
                if(!repeatCheckFlag){
                    repeatCheckFlag = "SE001013".equals(attach.getString("attachment_belong_stage"));
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
                if ("SE001013".equals(obj.get("step_no"))) {
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
                dataInstanceVo.getOid(),Question8DSolveEnum.confirm.getCode());
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // 初始实体
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // 落存数据
        return actionTraceBiz.handleUpdateForDistribution(questionActionTraceEntity,attachmentEntities,entity);
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        if (CollectionUtils.isEmpty(actionTraceEntityList)) {
            throw new DWRuntimeException("list index is 0 ");
        }
        QuestionActionTraceEntity entity = actionTraceEntityList.get(0);
        // 获取[围堵措施]数据
        List<BeforeQuestionVo> feedBackPersonVerifyInfo = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), Question8DSolveEnum.feedback_person_verify.getCode());
        if (!"4".equals(feedBackPersonVerifyInfo.get(0).getProcessStatus()) || !"2".equals(feedBackPersonVerifyInfo.get(0).getProcessResult())) {
            throw new DWRuntimeException(" wait containment_measure_verify to finish");
        }
        // 获取[预防措施验证]数据
        List<BeforeQuestionVo> precautionMeasureVerifyInfo = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), Question8DSolveEnum.precaution_verify.getCode());
        if (CollectionUtils.isEmpty(precautionMeasureVerifyInfo)) {
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("beforeStepNull"));
        }
        //D3-2
//        List<BeforeQuestionVo> DThree = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
//                entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),Question8DSolveEnum.containment_measure_verify.getCode());
//        return new JSONObject().fluentPut("return_data",createDataInstance(entity, feedBackPersonVerifyInfo.get(0),precautionMeasureVerifyInfo.get(0),DThree));
        return new JSONObject().fluentPut("return_data",createDataInstance(entity, feedBackPersonVerifyInfo.get(0),precautionMeasureVerifyInfo.get(0)));

    }

    /**
     * 封装 预防措施执行数据
     * @param entity
     * @param feedBackPersonVerifyInfo [围堵措施]数据
     * @param precautionMeasureVerify [预防措施验证]数据
     * @return
     * @throws Exception
     */
    private JSONArray createDataInstance(QuestionActionTraceEntity entity,BeforeQuestionVo feedBackPersonVerifyInfo,BeforeQuestionVo precautionMeasureVerify){
//    private JSONArray createDataInstance(QuestionActionTraceEntity entity,BeforeQuestionVo feedBackPersonVerifyInfo,BeforeQuestionVo precautionMeasureVerify, List<BeforeQuestionVo> DThree){
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(precautionMeasureVerify.getDataContent());
        // 获取最外层 question_result
        JSONObject dataDetail = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).getJSONObject(0);
        // 待封装 预防措施验证table
        JSONArray preventionMeasureExecuteVerify =dataDetail.getJSONArray("prevention_measure_execute_verify");
//
//        if (!Collections.isEmpty(DThree)) {
//            //D3-2验证数据
//            JSONObject DthreeDetail = JSON.parseObject(DThree.get(0).getDataContent());
//            JSONArray dthree = DthreeDetail.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
//            JSONObject three = dthree.getJSONObject(0);
//
//            //添加处理措施验证 如果存在措施验证则展示
//            if (three.containsKey("containment_measure_verify")) {
//                JSONObject verifyDetailInfo = new JSONObject();
//                //处理 更多信息 -加入 处理步骤+负责人+处理时间
//                verifyDetailInfo.put("liable_person_id", DThree.get(0).getLiablePersonId());
//                verifyDetailInfo.put("liable_person_name", DThree.get(0).getLiablePersonName());
//                if (null != DThree.get(0).getActualCompleteDate()) {
//                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//                    String dateString = formatter.format(DThree.get(0).getActualCompleteDate());
//                    verifyDetailInfo.put("process_date", dateString);
//                } else {
//                    verifyDetailInfo.put("process_date", "");
//                }
//                // 处理循环复用问题
//                JSONArray measureVerify = three.getJSONArray("containment_measure_verify");
//                dataDetail.remove("containment_measure_verify");
//                verifyDetailInfo.put("containment_measure_verify_detail", measureVerify);
//                dataDetail.put("containment_measure_verify", verifyDetailInfo);
//            }
//        }
        // 待封装 围堵措施验证详情信息
        JSONObject containmentMeasureDetailInfo = getContainmentMeasureVerifyTable(feedBackPersonVerifyInfo);
        // 处理 8D - 处理详情
        processData(dataDetail,feedBackPersonVerifyInfo,precautionMeasureVerify,preventionMeasureExecuteVerify,containmentMeasureDetailInfo);
        // 处理 8D - 处理确认table
        processConfirmData(dataDetail);
        // 保留value为null的数据
        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);

        // 初始化-待审核问题-数据 entity
        QuestionActionTraceEntity initTraceEntity = initQuestionActionEntity(entity);
        //+ 预计完成时间
        DateUtil.assignValueForExpectCompleteTime(entity,dataDetail,Question8DSolveEnum.confirm.getCode());
        // 初始化-dataInstance
        DataInstanceEntity dataInstanceEntity = initDataInstance(resultJsonObject,initTraceEntity,feedBackPersonVerifyInfo);
        // dao
        actionTraceBiz.insertActionTraceForCurb(entity,dataInstanceEntity);
        // init -回参
        JSONArray responseParam = new JSONArray();
        responseParam.add(initResponse(initTraceEntity));
        return responseParam;
    }

    /**
     * 获取[围堵措施验证]table
     * @param feedBackPersonVerify 围堵措施验证
     * @return
     */
    private JSONObject getContainmentMeasureVerifyTable(BeforeQuestionVo feedBackPersonVerify){
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSONObject.parseObject(feedBackPersonVerify.getDataContent());
        // 获取最外层 question_result
        JSONObject dataDetail = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).getJSONObject(0);
        // 获取预防措施验证详情信息  table
        return dataDetail.getJSONObject("containment_measure_verify");
    }


    private DataInstanceEntity initDataInstance(JSONObject preventionMeasureJsonObject,QuestionActionTraceEntity entity,BeforeQuestionVo feedBackPersonVerifyInfo){
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        dataInstanceEntity.setOid(entity.getDataInstanceOid());
        //整合围堵措施执行验证和预防措施执行验证的附件信息
        JSONObject containmentMeasureJsonObject = JSON.parseObject(feedBackPersonVerifyInfo.getDataContent());
        JSONObject dataPreventionDetail = preventionMeasureJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).getJSONObject(0);
        JSONObject dataContainmentDetail = containmentMeasureJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).getJSONObject(0);
        JSONArray preventionAttachmentInfo = dataPreventionDetail.getJSONArray("attachment_info");
        JSONArray containmentAttachmentInfo = dataContainmentDetail.getJSONArray("attachment_info");
        List<Map<String,Object>> unionList = ListUtils.union(preventionAttachmentInfo, containmentAttachmentInfo);
        //根据attachment_id进行去重
        List<Map<String, Object>> restructuredAttachment = unionList.stream().filter(DeduplicationUtil.distinctByKey(item -> item.get("attachment_id"))).collect(Collectors.toList());
        dataPreventionDetail.remove("attachment_info");
        dataPreventionDetail.put("attachment_info",restructuredAttachment);
        dataInstanceEntity.setDataContent(JSON.toJSONString(preventionMeasureJsonObject));
        dataInstanceEntity.setQuestionTraceOid(entity.getOid());
        return dataInstanceEntity;
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
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
        return entity;
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
     * 处理 8D - 处理详情
     * @param dataDetail dataDetail
     * @param precautionMeasureVerify  预防措施验证 data
     * @param preventionMeasureExecuteVerify [预防措施验证]table
     * @param containmentMeasureDetailInfo [围堵措施验证]详细信息
     * @return dataDetail 8D-问题处理data
     */
    private void processData(JSONObject dataDetail,BeforeQuestionVo feedBackPersonVerify,BeforeQuestionVo precautionMeasureVerify,JSONArray preventionMeasureExecuteVerify,JSONObject containmentMeasureDetailInfo){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        // D8-处理确认需重新封装预防措施验证table,故remove
        dataDetail.remove("prevention_measure_execute_verify");
        dataDetail.put("prevention_measure_execute_verify",new JSONObject().fluentPut("liable_person_id",precautionMeasureVerify.getLiablePersonId())
                .fluentPut("liable_person_name",precautionMeasureVerify.getLiablePersonName())
                .fluentPut("process_date",formatter.format(precautionMeasureVerify.getActualCompleteDate()))
                .fluentPut("prevention_measure_execute_verify_detail",preventionMeasureExecuteVerify));
        dataDetail.put("containment_measure_verify",containmentMeasureDetailInfo);
    }

    /**
     * 处理 8D - 处理确认table
     * @param newDataDetail 8D data
     * @return 8D data
     */
    private void processConfirmData(JSONObject newDataDetail){
        newDataDetail.fluentPut("process_confirm",new JSONObject().fluentPut("process_confirm_illustrate","")
                .fluentPut("liable_person_id","")
                .fluentPut("liable_person_name","")
                .fluentPut("confirm_date",""));
    }
    @Override
    public JSONObject handleBack(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 获取退回的节点的数据
            List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTraceForIdentity((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionNo(), entity.getQuestionProcessStep(),
                    entity.getQuestionSolveStep());
            BeforeQuestionVo beforeQuestionVo = beforeQuestionVos.get(0);
            //预计完成时间赋值
            String content = beforeQuestionVos.get(0).getDataContent();
            JSONObject resultJsonObject = JSON.parseObject(content);
            JSONObject questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).getJSONObject(0);
            // 新增待审核问题追踪
            String dataInstanceOid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            String oid = IdGenUtil.uuid();
            entity.setOid(oid);
            entity.setTenantsid((Long) DWServiceContext.getContext().getProfile().get("tenantSid"));
            // 获取处理顺序
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
            // 加入退回人id、name
            entity.setReturnId((String) DWServiceContext.getContext().getProfile().get("userId"));
            entity.setReturnName((String) DWServiceContext.getContext().getProfile().get("userName"));
            entity.setLiablePersonId(beforeQuestionVo.getLiablePersonId());
            entity.setLiablePersonName(beforeQuestionVo.getLiablePersonName());
            entity.setExpectCompleteDate(beforeQuestionVo.getExpectCompleteDate());
            String solveStep = entity.getQuestionSolveStep();
            if(!StringUtils.isEmpty(solveStep)){
                List<String> strListRange1 = Arrays.asList("SE001005", "SE001006", "SE001007", "SE001008", "SE001009");
                if(strListRange1.contains(solveStep)){
                    DateUtil.assignValueForExpectCompleteTime(entity,questionResult,Question8DSolveEnum.key_reason.getCode());
                }
                List<String> strListRange2 = Arrays.asList("SE001011", "SE001012", "SE001010");
                if(strListRange2.contains(solveStep)){
                    DateUtil.assignValueForExpectCompleteTime(entity,questionResult,Question8DSolveEnum.precaution.getCode());
                }
                if("SE001013".equals(solveStep)){
                    DateUtil.assignValueForExpectCompleteTime(entity,questionResult,Question8DSolveEnum.confirm.getCode());
                }
            }


            DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
            dataInstanceEntity.setOid(dataInstanceOid);
           // 获取最外层 question_result
            dataInstanceEntity.setDataContent(resultJsonObject.toJSONString());
            dataInstanceEntity.setQuestionTraceOid(oid);

            actionTraceMapper.insertActionTrace(entity);
            dataInstanceMapper.insertDataInstance(dataInstanceEntity);


            PendingQuestionVo vo = new PendingQuestionVo();
            BeanUtils.copyProperties(entity, vo);
            Map<String,Object> user = iamEocBiz.getEmpUserId(beforeQuestionVo.getLiablePersonId());
            vo.setEmpId((String) user.get("id"));
            vo.setEmpName((String) user.get("name"));
            JSONObject object = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            object.put("pending_approve_question_id",oid);
            responseParam.add(object);
        }

        responseObject.put("return_data",responseParam);
        return responseObject;
    }
}
