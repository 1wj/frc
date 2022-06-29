package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.eightD;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.PreventionMeasureExecuteVerifyInfoModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.QuestionInfo8DSecondModel;
import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.PendingQuestionVo;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.DateUtil;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.TransferTool;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName QuestionPreventionMeasureExecuteVerify
 * @Description 预防措施验证
 * @Author HeX
 * @Date 2022/3/20 0:00
 * @Version 1.0
 **/
public class QuestionPreventionMeasureExecuteVerify implements QuestionHandlerStrategy {

    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    IamEocBiz iamEocBiz =  SpringContextHolder.getBean(IamEocBiz.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);
    @Autowired
    EightDQuestionBiz eightDQuestionBiz =  SpringContextHolder.getBean(EightDQuestionBiz.class);

    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        //1.string转model，将待更新的字段转为model
        PreventionMeasureExecuteVerifyInfoModel preventionMeasureExecuteVerifyInfoModel = TransferTool.convertString2Model(parameters, PreventionMeasureExecuteVerifyInfoModel.class);
        //2. 参数校验
        ParamCheckUtil.checkPreventionMeasureExecuteVerifyParams(preventionMeasureExecuteVerifyInfoModel);
        //3.更新任务卡状态
        QuestionInfo8DSecondModel questionInfoModel = preventionMeasureExecuteVerifyInfoModel.getQuestionInfos().get(0);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);
        // 更新实际完成时间
        entity.setActualCompleteDate(new Date());
        JSONObject resultJsonObject = JSON.parseObject(parameters);
        JSONArray preventionMeasuresVerify = resultJsonObject.getJSONArray("prevention_measure_execute_verify");
        JSONArray attachmentInfos = resultJsonObject.getJSONArray("attachment_info");
        return handleDetail(entity,preventionMeasuresVerify,attachmentInfos);
    }
    /**
     * 更新表单详情数据
     * @param preventionMeasuresVerify 入参 需更新字段信息
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONArray preventionMeasuresVerify,JSONArray attachmentModels) throws IOException {
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(questionActionTraceEntity.getOid());
        // 获取前一结点的表单数据 string转json

        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        // 获取最外层 question_result
        JSONObject dataDetail = resultJsonObject.getJSONArray("question_result").getJSONObject(0);
        //数据库中的数据
        JSONArray  correctiveMeasureVerifyBefore=dataDetail.getJSONArray("prevention_measure_execute_verify");
        //处理预计完成时间时分秒
        DateUtil.measuresExecuteVerify(preventionMeasuresVerify,correctiveMeasureVerifyBefore);
        dataDetail.remove("prevention_measure_execute_verify");
        dataDetail.put("prevention_measure_execute_verify",preventionMeasuresVerify);

        JSONArray attachmentInfos = dataDetail.getJSONArray("attachment_info");

        //抽取 预防措施任务卡上上传的附件
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
        // 保存附件
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),
                dataInstanceVo.getOid(),Question8DSolveEnum.precaution_verify.getCode());
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // 初始实体
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // 落存数据
        return eightDQuestionBiz.handleUpdateForPreventionMeasureExecuteVerify(questionActionTraceEntity,attachmentEntities,entity);
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        if (CollectionUtils.isEmpty(actionTraceEntityList)) {
            throw new DWRuntimeException("list index is 0 ");
        }
        QuestionActionTraceEntity entity = actionTraceEntityList.get(0);

        // 初始化-待审核问题-数据 entity
        //初始化预计完成时间
        String dataInstanceOid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        String oid = IdGenUtil.uuid();
        entity.setOid(oid);
        // 获取执行顺序
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
        // init data_instance
        DataInstanceEntity dataInstanceEntity = initPrecautionMeasureVerify(entity);
        actionTraceBiz.insertActionTraceForCurb(entity,dataInstanceEntity);

        // 5-封装response数据
        PendingQuestionVo vo = new PendingQuestionVo();
        BeanUtils.copyProperties(entity, vo);
        JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
        JSONArray responseParam = new JSONArray();
        responseParam.add(jsonObject);
        JSONObject responseObject = new JSONObject();
        responseObject.put("return_data",responseParam);
        return responseObject;
    }

    /**
     *初始化 预防措施验证table
     * @param entity data_instance表数据
     */
    private DataInstanceEntity initPrecautionMeasureVerify(QuestionActionTraceEntity entity){
        // 获取预防措施table
        List<BeforeQuestionVo> precautionMeasureList = actionTraceMapper.getBeforeQuestionTraceForList1(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(), entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                Question8DSolveEnum.precaution.getCode());
        int step = precautionMeasureList.get(0).getPrincipalStep();
        // 获取预防措施执行数据
        List<BeforeQuestionVo> precautionMeasureExecuteList = actionTraceMapper.getBeforeQuestionTraceForList(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                Question8DSolveEnum.precaution_execute.getCode(),step);
        //D3-2
        List<BeforeQuestionVo> DThree = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),Question8DSolveEnum.containment_measure_verify.getCode());

        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(precautionMeasureList.get(0).getDataContent());
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = questionResult.getJSONObject(0);
        //+ 预计完成时间
        DateUtil.assignValueForExpectCompleteTime(entity,dataDetail,Question8DSolveEnum.precaution.getCode());

        JSONObject resultExecuteObject = JSON.parseObject(precautionMeasureExecuteList.get(0).getDataContent());
        // 获取最外层 question_result
        JSONArray questionResultExecute = resultExecuteObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetailExecute = questionResultExecute.getJSONObject(0);
        // 预防措施table
        // containment_measure 处理循环复用问题
        JSONArray preventionMeasureTable = dataDetail.getJSONArray("prevention_measure");
        dataDetail.remove("prevention_measure");
        String textNew = JSON.toJSONString(preventionMeasureTable, SerializerFeature.DisableCircularReferenceDetect);
        JSONArray jsonArray = JSON.parseArray(textNew);
        dataDetail.put("prevention_measure",jsonArray);
        if (!Collections.isEmpty(DThree)) {
            //D3-2验证数据
            JSONObject DthreeDetail = JSON.parseObject(DThree.get(0).getDataContent());
            JSONArray dthree = DthreeDetail.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
            JSONObject three = dthree.getJSONObject(0);

            if (three.containsKey("containment_measure_verify")) {
                JSONObject verifyDetailInfo = new JSONObject();
                //处理 更多信息 -加入 处理步骤+负责人+处理时间
                verifyDetailInfo.put("liable_person_id", DThree.get(0).getLiablePersonId());
                verifyDetailInfo.put("liable_person_name", DThree.get(0).getLiablePersonName());
                if (null != DThree.get(0).getActualCompleteDate()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String dateString = formatter.format(DThree.get(0).getActualCompleteDate());
                    verifyDetailInfo.put("process_date", dateString);
                } else {
                    verifyDetailInfo.put("process_date", "");
                }
                // 处理循环复用问题
                JSONArray measureVerify = three.getJSONArray("containment_measure_verify");
                dataDetail.remove("containment_measure_verify");
                verifyDetailInfo.put("containment_measure_verify_detail", measureVerify);
                dataDetail.put("containment_measure_verify", verifyDetailInfo);
            }
        }
        JSONArray attachments=new JSONArray();
        for (Iterator<Object> iterator = preventionMeasureTable.iterator(); iterator.hasNext();) {
            JSONObject de = (JSONObject)iterator.next();

            for (BeforeQuestionVo beforeQuestionVo:precautionMeasureExecuteList) {
                JSONObject curbObject = JSON.parseObject(beforeQuestionVo.getDataContent());
                // 获取最外层 question_result
                JSONArray precautionMeasureExecuteResult = curbObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
                JSONObject precautionMeasureExecutes = precautionMeasureExecuteResult.getJSONObject(0);
                //措施执行附件汇总
                JSONArray files=precautionMeasureExecutes.getJSONArray("attachment_info");
                JSONArray precautionMeasureExecuteInfo = precautionMeasureExecutes.getJSONArray("prevention_measure_execute");

                int flag = 0;

                for (Iterator<Object> ded = precautionMeasureExecuteInfo.iterator(); ded.hasNext();) {
                    JSONObject precautionMeasureExecute = (JSONObject)ded.next();

                    if (de.get("uuid").equals(precautionMeasureExecute.get("uuid"))) {
                        attachments.addAll(files);
                        de.put("prevention_measure_execute_illustrate",precautionMeasureExecute.get("prevention_execute_illustrate"));
                        de.put("process_work_date",precautionMeasureExecute.get("complete_date"));
                        de.put("process_work_hours",precautionMeasureExecute.get("process_work_hours"));
                        de.put("execute_status",precautionMeasureExecute.get("execute_status"));
                        de.put("question_id",beforeQuestionVo.getOid());
                        flag = 1;
                        break;
                    }
                }
                if (flag ==1) {
                    break;
                }
            }
            de.put("verify_illustrate","");
            de.put("verify_date","");
            de.put("verify_status","");

        }
        dataDetailExecute.put("prevention_measure_execute_verify",preventionMeasureTable);
        dataDetailExecute.remove("attachment_info");
        dataDetailExecute.put("attachment_info",packageAttachments(attachments));
        // 保留value为null的数据
        JSON.toJSONString(dataDetailExecute, SerializerFeature.WriteMapNullValue);
        // jsonObject转string
        String dataContentString = JSON.toJSONString(resultExecuteObject);

        // 落存问题分配 详细数据
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        dataInstanceEntity.setOid(entity.getDataInstanceOid());
        dataInstanceEntity.setDataContent(dataContentString);
        dataInstanceEntity.setQuestionTraceOid(entity.getOid());
        return dataInstanceEntity;

    }
    /**
     * 附件去重,保证有序
     * @param attachments 需去重附件信息
     * @return JSONArray
     */
    public static JSONArray packageAttachments(JSONArray attachments){
        JSONArray tempArray = new JSONArray();
        for (int i = 0; i < attachments.size(); i++) {
            JSONObject file =attachments.getJSONObject(i);
            if (i == 0) {
                // 添加第一条数据
                tempArray.add(file);
            }else {
                int flag = 1;//是否有重复数据 标识
                for (int j = 0; j < tempArray.size(); j++) {
                    JSONObject tempFile =tempArray.getJSONObject(j);
                    if (file.getString("attachment_id").equals(tempFile.getString("attachment_id"))) {
                        flag = 0;
                    }
                }
                if (flag == 1) {
                    tempArray.add(file);
                }
            }
        }
        return tempArray;
    }

    @Override
    public JSONObject handleBack(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            QuestionDetailVo questionDetailVo = actionTraceMapper.getQuestionTrace(entity.getOid());
            // 表单数据流转
            JSONObject re = createForHandleBack(entity, questionDetailVo);
            responseParam.add(re);
        }
        responseObject.put("return_data", responseParam);
        return responseObject;
    }

    /**
     * 纠正措施验证退回生成相应退回的待审核卡
     *
     * @param entity
     * @param questionDetailVo
     * @return
     */
    private JSONObject createForHandleBack(QuestionActionTraceEntity entity, QuestionDetailVo questionDetailVo) throws Exception {
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(questionDetailVo.getDataContent());
        // 获取最外层 question_result
        JSONObject dataDetail = (JSONObject) resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).get(0);
        //预计完成时间赋值
        DateUtil.assignValueForExpectCompleteTime(entity,dataDetail,Question8DSolveEnum.precaution.getCode());
        JSONArray lastingMeasureExecuteInfo = dataDetail.getJSONArray("prevention_measure_execute");
        String liablePersonId = "";
        String liablePersonName = "";
        for (Iterator<Object> iterator = lastingMeasureExecuteInfo.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            obj.put("execute_status","1");
            liablePersonId= obj.getString("liable_person_id");
            liablePersonName= obj.getString("liable_person_name");
        }

        // 初始化-待审核问题-数据 entity
        String dataInstanceOid = IdGenUtil.uuid();
        String oid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        entity.setOid(oid);
        entity.setLiablePersonId(liablePersonId);
        entity.setLiablePersonName(liablePersonName);
        entity.setReturnId((String) DWServiceContext.getContext().getProfile().get("userId"));
        entity.setReturnName((String) DWServiceContext.getContext().getProfile().get("userName"));
        // 获取执行顺序
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        // jsonObject转string
        String dataContentString = JSON.toJSONString(resultJsonObject);

        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        dataInstanceEntity.setOid(dataInstanceOid);
        dataInstanceEntity.setDataContent(dataContentString);
        dataInstanceEntity.setQuestionTraceOid(oid);
        //将退回新生成的待审核卡数据进行落库
        actionTraceMapper.insertActionTrace(entity);
        dataInstanceMapper.insertDataInstance(dataInstanceEntity);

        // 5-封装response数据
        PendingQuestionVo vo = new PendingQuestionVo();
        BeanUtils.copyProperties(entity, vo);
        Map<String,Object> user = iamEocBiz.getEmpUserId(liablePersonId);
        vo.setLiablePersonId((String) user.get("id"));
        vo.setLiablePersonName((String) user.get("name"));
        vo.setEmpId(liablePersonId);
        vo.setEmpName(liablePersonName);
        return JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
    }
}
