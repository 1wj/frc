package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.eightD;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.Question8DSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.EightDQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.IamEocBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.QuestionDetailVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.QuestionInfo8DSecondModel;
import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.KeyReasonCorrectVo;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.service.DWServiceContext;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName QuestionCorrectExecute
 * @Description 纠正措施执行逻辑处理
 * @Author HeX
 * @Date 2022/3/8 11:11
 * @Version 1.0
 **/
public class QuestionCorrectExecute implements QuestionHandlerStrategy {
    /**
     * 手动注入(工厂new出的对象)
     */
    @Autowired
    AttachmentMapper attachmentMapper  =  SpringContextHolder.getBean(AttachmentMapper.class);
    @Autowired
    IamEocBiz iamEocBiz =  SpringContextHolder.getBean(IamEocBiz.class);
    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    EightDQuestionBiz eightDQuestionBiz =  SpringContextHolder.getBean(EightDQuestionBiz.class);

    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        JSONObject resultJsonObject = JSON.parseObject(parameters);

        //1.string转model，将待更新的字段转为model
        KeyReasonCorrectVo keyReasonCorrectVo = TransferTool.convertString2Model(parameters, KeyReasonCorrectVo.class);

        //2.参数校验
        try {
            ParamCheckUtil.checkkeyQuestionCorrectExecute(keyReasonCorrectVo);
        } catch (DWArgumentException e) {
            e.printStackTrace();
        }

        //3.更新任务卡状态
        QuestionInfo8DSecondModel questionInfoModel = keyReasonCorrectVo.getQuestionInfos().get(0);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);
        JSONArray correctiveMeasureExecute = resultJsonObject.getJSONArray("corrective_measure_execute");
        JSONArray attachmentInfo=resultJsonObject.getJSONArray("attachment_info");

        return  updateDetailInfo(entity,correctiveMeasureExecute, attachmentInfo,questionInfoModel.getOid());
    }

    private JSONObject updateDetailInfo(QuestionActionTraceEntity entity, JSONArray correctiveMeasureExecute, JSONArray attachmentModels, String oid) throws OperationException{
        // 获取需更新的表单数据，并解析结构，获取核心数据
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        if (dataInstanceVo == null) {
            throw new OperationException("数据库无此id信息，请输入正确问题id");
        }
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        JSONObject dataDetail = getDataDetail(resultJsonObject);

        //处理预计完成时间年月日时分秒
        DateUtil.measuresExecute(correctiveMeasureExecute);
        //处理纠正措施执行信息
        dataDetail.remove("corrective_measure_execute");
        dataDetail.put("corrective_measure_execute",correctiveMeasureExecute);
        // 处理附件
        JSONArray attachmentInfos = (JSONArray) dataDetail.get("attachment_info");
        JSONArray mustUploadAttachments = new JSONArray();
        for (Iterator<Object> iterator = attachmentModels.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            boolean status = true;
            for (Iterator<Object> it = attachmentInfos.iterator();it.hasNext();) {
                JSONObject attach = (JSONObject)it.next();
                if (attach.get("attachment_id").equals(obj.get("attachment_id"))) {
                    status =false;
                    break;
                }
            }
            if (status) {
                mustUploadAttachments.add(obj);
            }
        }
        //新增附件必传校验
        JSONArray correctiveMeasure= dataDetail.getJSONArray("corrective_measure");
        //校验附件是否必传
        //获取当前任务卡的负责人信息
        QuestionDetailVo questionTrace = actionTraceMapper.getQuestionTrace(entity.getOid());
        String currentLiablePersonId = questionTrace.getLiablePersonId();
        //D4D5根据负责人id进行分组
        Map<String, List<Map<String, Object>>> listMap = processContainmentMeasureInfos(correctiveMeasure);

        for (String key : listMap.keySet()) {
            List<Map<String, Object>> mapList = listMap.get(key);
            //负责人id相同  判断附件是否必传
            if(currentLiablePersonId.equals(key)){
                boolean flag = mapList.stream().anyMatch(item -> !org.springframework.util.StringUtils.isEmpty(item.get("attachment_upload_flag")) && item.get("attachment_upload_flag") == "Y");
                if (flag) {
                    if (Collections.isEmpty(mustUploadAttachments)) {
                        // 查询数据库
                        List<AttachmentEntity> attachmentEntities = attachmentMapper.getAttachments((Long) DWServiceContext.getContext().getProfile().get("tenantSid"), dataInstanceVo.getOid());
                        if (Collections.isEmpty(attachmentEntities)) {
                            throw new DWRuntimeException("attachment_upload_flag is Y, so attachment must be uploaded ! ");
                        }
                        break;
                    }
                }
            }

        }


        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(), Question8DSolveEnum.correct_execute.getCode());
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());

        // 初始实体
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,dataInstanceEntity);

        return eightDQuestionBiz.correctExecuteBuilder(entity,attachmentEntities,dataInstanceEntity);
    }

    private JSONObject getDataDetail(JSONObject resultJsonObject) {
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        return questionResult.getJSONObject(0);
    }
    private Map<String, List<Map<String, Object>>> processContainmentMeasureInfos(JSONArray containmentMeasure) {
        List<Map<String, Object>> containmentMeasureDetails = new ArrayList<>();
        containmentMeasure.stream().forEach(pb -> {
            Map<String, Object> rightMap = (Map<String, Object>) pb;
            containmentMeasureDetails.add(rightMap);
        });

        // 将集合list按照liable_person_id进行分组
        Map<String, List<Map<String, Object>>> collect = containmentMeasureDetails.stream().collect(Collectors.groupingBy(this::customKey));
        collect.forEach((k, v) -> {
            System.out.println(k + " -> " + v);
        });
        return collect;
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 1-获取当前处理步骤前一步骤
            List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), Question8DSolveEnum.key_reason_correct.getCode());

            //D3-2
            List<BeforeQuestionVo> DThree = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),Question8DSolveEnum.containment_measure_verify.getCode());

            if (CollectionUtils.isEmpty(beforeQuestionVos)) {
                throw new DWRuntimeException( MultilingualismUtil.getLanguage("beforeStepNull"));
            }

            createDataInstance1(entity, beforeQuestionVos.get(0), responseParam,DThree);
        }
        responseObject.put("return_data",responseParam);
        return responseObject;
    }


    private JSONArray createDataInstance1(QuestionActionTraceEntity entity,BeforeQuestionVo beforeQuestionVo,JSONArray responseParam,List<BeforeQuestionVo> DThree) throws Exception {

        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(beforeQuestionVo.getDataContent());
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = questionResult.getJSONObject(0);

        // 预计完成时间
        DateUtil.assignValueForExpectCompleteTime(entity,dataDetail,Question8DSolveEnum.key_reason.getCode());

        //  处理循环复用问题
        JSONArray containmentMeasure = dataDetail.getJSONArray("corrective_measure");
        dataDetail.remove("corrective_measure_execute");
        JSONObject  keyReasonAnalysis1= dataDetail.getJSONArray("key_reason_analysis").getJSONObject(0);
        //  添加处理详情信根本原因
        JSONObject keyReasonAnalysisInfo = new JSONObject();
        //处理 更多信息 -加入 处理步骤+负责人+处理时间
        keyReasonAnalysisInfo.put("liable_person_id",beforeQuestionVo.getLiablePersonId());
        keyReasonAnalysisInfo.put("liable_person_name",beforeQuestionVo.getLiablePersonName());
        if (null != beforeQuestionVo.getActualCompleteDate()) {
            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
            String dateString1 = formatter1.format(beforeQuestionVo.getActualCompleteDate());
            keyReasonAnalysisInfo.put("process_date",dateString1);
        }else {
            keyReasonAnalysisInfo.put("process_date","");
        }

        if (!StringUtils.isEmpty(keyReasonAnalysis1.get("outflow_reason").toString())){
            keyReasonAnalysisInfo.put("outflow_reason",keyReasonAnalysis1.get("outflow_reason"));
        }else {
            keyReasonAnalysisInfo.put("outflow_reason","");
        }
        if (!StringUtils.isEmpty(keyReasonAnalysis1.get("outflow_reason").toString())){
            keyReasonAnalysisInfo.put("output_reason",keyReasonAnalysis1.get("output_reason"));
        }else {
            keyReasonAnalysisInfo.put("output_reason","");
        }
        if (!StringUtils.isEmpty(keyReasonAnalysis1.get("outflow_reason").toString())){
            keyReasonAnalysisInfo.put("system_reason",keyReasonAnalysis1.get("system_reason"));
        }else {
            keyReasonAnalysisInfo.put("system_reason","");
        }
        dataDetail.put("key_reason_analysis",keyReasonAnalysisInfo);

        if (!Collections.isEmpty(DThree)) {
            //D3-2验证数据
            JSONObject DthreeDetail = JSON.parseObject(DThree.get(0).getDataContent());
            JSONArray dthree=DthreeDetail.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
            JSONObject three=dthree.getJSONObject(0);

            //TODO 添加处理措施验证 如果存在措施验证则展示
            if (three.containsKey("containment_measure_verify")){
                JSONObject verifyDetailInfo = new JSONObject();
                //处理 更多信息 -加入 处理步骤+负责人+处理时间
                verifyDetailInfo.put("liable_person_id",DThree.get(0).getLiablePersonId());
                verifyDetailInfo.put("liable_person_name",DThree.get(0).getLiablePersonName());
                if (null != DThree.get(0).getActualCompleteDate()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String dateString = formatter.format(DThree.get(0).getActualCompleteDate());
                    verifyDetailInfo.put("process_date",dateString);
                }else {
                    verifyDetailInfo.put("process_date","");
                }
                // 处理循环复用问题
                JSONArray measureVerify = three.getJSONArray("containment_measure_verify");
                dataDetail.remove("containment_measure_verify");
                verifyDetailInfo.put("containment_measure_verify_detail",measureVerify);
                dataDetail.put("containment_measure_verify",verifyDetailInfo);
            }
        }
        String dataInstanceOid;
        String oid;
        //以personId分组遍历
        Map<String, List<Map<String, Object>>> collect = handleCorrectExecute(containmentMeasure);
        for (String key : collect.keySet()) {
            List<Map<String, Object>> list = collect.get(key);
            // 初始化-待审核问题-数据 entity
            dataInstanceOid = IdGenUtil.uuid();
            oid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            entity.setOid(oid);
            entity.setLiablePersonId((String) list.get(0).get("corrective_person_id"));
            entity.setLiablePersonName((String) list.get(0).get("corrective_person_name"));
            // 获取执行顺序
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
            JSONArray jArray= JSON.parseArray(JSON.toJSONString(list));
            dataDetail.put("corrective_measure_execute",jArray);
            // 保留value为null的数据
            JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
            // jsonObject转string
            String dataContentString = JSON.toJSONString(resultJsonObject);
            // 纠错信息执行信息
            DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
            dataInstanceEntity.setOid(dataInstanceOid);
            dataInstanceEntity.setDataContent(dataContentString);
            dataInstanceEntity.setQuestionTraceOid(oid);
            actionTraceBiz.insertActionTraceForCurb(entity,dataInstanceEntity);
            JSONObject responseObject = new JSONObject();
            responseObject.put("pending_approve_question_id",oid);
            responseObject.put("question_no",entity.getQuestionNo());
            responseObject.put("question_description",entity.getQuestionDescription());
            responseObject.put("return_flag_id",entity.getReturnFlagId());
            responseObject.put("return_flag_name",entity.getReturnFlagName());
            responseObject.put("expect_solve_date",entity.getExpectCompleteDate());
            Map<String,Object> user = iamEocBiz.getEmpUserId((String) list.get(0).get("corrective_person_id"));
            responseObject.put("liable_person_id",user.get("id"));
            responseObject.put("liable_person_name",user.get("name"));
            responseObject.put("employee_id",list.get(0).get("corrective_person_id"));
            responseObject.put("employee_name", list.get(0).get("corrective_person_name"));
            responseParam.add(responseObject);
        }
        return responseParam;
    }

    @Override
    public JSONObject handleBack(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        return null;
    }
    private Map<String, List<Map<String, Object>>> handleCorrectExecute(JSONArray containmentMeasure){
        List<Map<String, Object>> containmentMeasureDetails = new ArrayList<>();
        containmentMeasure.stream().forEach(pb -> {
            Map<String, Object> rightMap = (Map<String, Object>) pb;
            rightMap.put("corrective_content",((Map<String, Object>) pb).get("corrective_content"));
            rightMap.put("corrective_person_id",((Map<String, Object>) pb).get("corrective_person_id"));
            rightMap.put("corrective_person_name",((Map<String, Object>) pb).get("corrective_person_name"));
            rightMap.put("corrective_department_id",((Map<String, Object>) pb).get("corrective_department_id"));
            rightMap.put("corrective_department_name",((Map<String, Object>) pb).get("corrective_department_name"));
            rightMap.put("expect_solve_date",((Map<String, Object>) pb).get("expect_solve_date"));
            rightMap.put("corrective_execute_illustrate",((Map<String, Object>) pb).get("corrective_execute_illustrate"));
            rightMap.put("corrective_date",((Map<String, Object>) pb).get("corrective_date"));
            rightMap.put("complete_status","1");
            containmentMeasureDetails.add(rightMap);
        });

        // 将集合list按照liable_person_id进行分组
        Map<String, List<Map<String, Object>>> collect = containmentMeasureDetails.stream().collect(Collectors.groupingBy(this::customKey));
        collect.forEach((k, v) -> {
            System.out.println(k + " -> " + v);
        });

        return collect;
    }

    private  String customKey(Map<String,Object> map){
        return map.get("corrective_person_id").toString();
    }
}
