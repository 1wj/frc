package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.eightD;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.Const.qdh.update.QuestionUpdateConst;
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
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.QuestionInfo8DSecondModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.RectifyVerifyModel;
import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.PendingQuestionVo;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.frc.service.athena.util.rqi.EocUtils;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName QuestionRectifyVerify
 * @Description 围堵措施验证
 * @Author HeX
 * @Date 2022/3/8 11:09
 * @Version 1.0
 **/
public class QuestionRectifyVerify implements QuestionHandlerStrategy {
    /**
     * 手动注入(工厂new出的对象)
     */
    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    EightDQuestionBiz eightDQuestionBiz =  SpringContextHolder.getBean(EightDQuestionBiz.class);

    @Autowired
    IamEocBiz iamEocBiz =  SpringContextHolder.getBean(IamEocBiz.class);



    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        JSONObject resultJsonObject = JSON.parseObject(parameters);

        //1.string转model，将待更新的字段转为model
        RectifyVerifyModel keyReasonCorrectModel = TransferTool.convertString2Model(parameters, RectifyVerifyModel.class);

        //2.参数校验
        try {
            ParamCheckUtil.checkkeyQuestionRectifyVerify(keyReasonCorrectModel);
        } catch (DWArgumentException e) {
            e.printStackTrace();
        }

        //3.更新任务卡状态
        QuestionInfo8DSecondModel questionInfoModel = keyReasonCorrectModel.getQuestionInfos().get(0);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);
        JSONArray correctiveMeasureVerify = resultJsonObject.getJSONArray("corrective_measure_verify");
        JSONArray attachmentInfo=resultJsonObject.getJSONArray("attachment_info");
        //4.处理更新数据
        return updateDetailInfo(entity,correctiveMeasureVerify,attachmentInfo,questionInfoModel.getOid());
    }
    private JSONObject updateDetailInfo(QuestionActionTraceEntity entity, JSONArray correctiveMeasureVerify, JSONArray attachmentModels, String oid) throws OperationException{
        // 获取需更新的表单数据，并解析结构，获取核心数据
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        if (dataInstanceVo == null) {
          throw new OperationException("数据库无此id信息，请输入正确问题id");
        }
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        JSONObject dataDetail = getDataDetail(resultJsonObject);

        //时间比较预计完成时间不能大于期望关闭时间
        JSONArray  questionBasicInfo=dataDetail.getJSONArray(QuestionUpdateConst.question_basic_info);

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(ReuseConstant.YEAR_MONTH_DAY);
        for (Iterator<Object> iterator =questionBasicInfo.iterator();iterator.hasNext();){
            JSONObject qObject = (JSONObject)iterator.next();
            for (Iterator<Object> ite =correctiveMeasureVerify.iterator();ite.hasNext();){
                JSONObject correctObject = (JSONObject)ite.next();
                if(correctObject.containsKey(ReuseConstant.expect_solve_date)){
                    try {
                        String correctDate =correctObject.get(ReuseConstant.expect_solve_date).toString();
                        String qDate=qObject.get(ReuseConstant.expect_solve_date).toString();
                        long timestamp = simpleDateFormat.parse(qDate).getTime();
                        long correctTimestamp = simpleDateFormat.parse(correctDate).getTime();
                        if (correctTimestamp>timestamp){
                            throw new OperationException("预计完成时间: "+correctDate+"应该不大于期望关闭时间: "+qDate);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        //时间比较预计完成时间不能大于期望关闭时间
        JSONArray  correctiveMeasureVerifyBefore=dataDetail.getJSONArray("corrective_measure_verify");
        //处理预计完成时间时分秒
        DateUtil.measuresExecuteVerify(correctiveMeasureVerify,correctiveMeasureVerifyBefore);
        //处理计划安排信息
        dataDetail.remove("corrective_measure_verify");
        dataDetail.put("corrective_measure_verify",correctiveMeasureVerify);
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
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(), Question8DSolveEnum.correct_verify.getCode());
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());

        // 初始实体
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,dataInstanceEntity);

        return eightDQuestionBiz.rectifyVerifyBuilder(entity,attachmentEntities,dataInstanceEntity);
    }

    private JSONObject getDataDetail(JSONObject resultJsonObject) {
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        return questionResult.getJSONObject(0);
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception  {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 获取最新的围堵措施分配数据，得到步骤号
            List<BeforeQuestionVo> keyReasonCorrectList = actionTraceMapper.getBeforeQuestionTraceForList1(TenantTokenUtil.getTenantSid(),
                    entity.getQuestionRecordOid(), entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                    Question8DSolveEnum.key_reason_correct.getCode());
            int step = keyReasonCorrectList.get(0).getPrincipalStep();

            // 获取当前处理步骤的前一节点(获取围堵措施执行数据)
            List<BeforeQuestionVo> beforeQuestionVoList = actionTraceMapper.getBeforeQuestionTraceForList(TenantTokenUtil.getTenantSid(),
                    entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                    Question8DSolveEnum.correct_execute.getCode(),step);

            //D3-2
            List<BeforeQuestionVo> DThree = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),Question8DSolveEnum.containment_measure_verify.getCode());

            // 初始化-待审核问题-数据 entity
            String dataInstanceOid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            String oid = IdGenUtil.uuid();
            entity.setOid(oid);

            // 获取执行顺序
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
            // 表单数据流转
            DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVoList,keyReasonCorrectList.get(0),DThree,entity);
            actionTraceBiz.insertActionTraceForCurb(entity,dataInstanceEntity);

            // 5-封装response数据
            PendingQuestionVo vo = new PendingQuestionVo();
            BeanUtils.copyProperties(entity, vo);
            String userId = TenantTokenUtil.getUserId();
            vo.setEmpId(EocUtils.getEmpId(userId));
            vo.setEmpName(EocUtils.getEmpName(userId));
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            responseParam.add(jsonObject);


        }
        responseObject.put("return_data",responseParam);
        return responseObject;
    }

    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid,List<BeforeQuestionVo> beforeQuestionVoList,BeforeQuestionVo keyReasonEntity,List<BeforeQuestionVo> DThree,QuestionActionTraceEntity traceEntity){
        // 获取上上的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(keyReasonEntity.getDataContent());

        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = (JSONObject) questionResult.get(0);
        //+ 预计完成时间
        DateUtil.assignValueForExpectCompleteTime(traceEntity,dataDetail,Question8DSolveEnum.key_reason.getCode());
        JSONArray containmentMeasureInfo = (JSONArray) dataDetail.get("corrective_measure");
        dataDetail.remove("corrective_measure");

        JSONArray measureVerifyContent = new JSONArray();

        JSONArray attachments = new JSONArray();

        JSONObject newObject = JSON.parseObject(beforeQuestionVoList.get(0).getDataContent());
        // 获取最外层 question_result
        JSONArray newExecuteResult = newObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject newMeasureExecuteDetail = (JSONObject) newExecuteResult.get(0);

        if (!Collections.isEmpty(DThree)) {
            //D3-2验证数据
            JSONObject DthreeDetail = JSON.parseObject(DThree.get(0).getDataContent());
            JSONArray dthree = DthreeDetail.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
            JSONObject three = dthree.getJSONObject(0);
            if (three.containsKey("containment_measure_verify")) {
                JSONObject verifyDetailInfo = new JSONObject();
                //处理 更多信息 -加入 处理步骤+负责人+处理时间
                verifyDetailInfo.put(ReuseConstant.liable_person_id, DThree.get(0).getLiablePersonId());
                verifyDetailInfo.put(ReuseConstant.liable_person_name, DThree.get(0).getLiablePersonName());
                if (null != DThree.get(0).getActualCompleteDate()) {
                    SimpleDateFormat formatter = new SimpleDateFormat(ReuseConstant.YEAR_MONTH_DAY);
                    String dateString = formatter.format(DThree.get(0).getActualCompleteDate());
                    verifyDetailInfo.put("process_date", dateString);
                } else {
                    verifyDetailInfo.put("process_date", "");
                }
                // 处理循环复用问题
                JSONArray measureVerify = three.getJSONArray("containment_measure_verify");
                newMeasureExecuteDetail.remove("containment_measure_verify");
                verifyDetailInfo.put("containment_measure_verify_detail", measureVerify);
                newMeasureExecuteDetail.put("containment_measure_verify", verifyDetailInfo);
            }
        }
        //循环围堵措施信息
        for (Iterator<Object> iterator = containmentMeasureInfo.iterator(); iterator.hasNext();) {
            JSONObject de = (JSONObject)iterator.next();
            for (BeforeQuestionVo beforeQuestionVo:beforeQuestionVoList) {
                JSONObject curbObject = JSON.parseObject(beforeQuestionVo.getDataContent());
                // 获取最外层 question_result
                JSONArray containmentMeasureExecuteResult = curbObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
                JSONObject measureExecuteDetail = (JSONObject) containmentMeasureExecuteResult.get(0);
                JSONArray files = measureExecuteDetail.getJSONArray("attachment_info");
                JSONArray measureExecuteInfo = (JSONArray) measureExecuteDetail.get("corrective_measure_execute");
                int flag = 0;
                for (Iterator<Object> ded = measureExecuteInfo.iterator(); ded.hasNext();) {
                    JSONObject curba = (JSONObject) ded.next();
                    JSONObject mapObject =new JSONObject();
                    //组建围堵措施执行验证信息
                    // 与前端约定uuid  结合围堵措施信息与围堵措施执行信息获取对应的围堵措施执行验证信息
                    // 使得组建后的数据有序  （与围堵措施顺序一致）
                    if (de.get("uuid").equals(curba.get("uuid"))) {
                        attachments.addAll(files);
                        SimpleDateFormat formatter = new SimpleDateFormat(ReuseConstant.YEAR_MONTH_DAY);
                        mapObject.put("corrective_content", curba.get("corrective_content"));
                        mapObject.put("corrective_execute_illustrate", curba.get("corrective_execute_illustrate"));
                        mapObject.put("corrective_person_id", curba.get("corrective_person_id"));
                        mapObject.put("corrective_person_name", curba.get("corrective_person_name"));
                        mapObject.put("corrective_status",curba.get("complete_status"));
                        mapObject.put("process_work_hours",curba.get("process_work_hours"));
                        mapObject.put("process_work_date",curba.get("corrective_date"));
                        mapObject.put("verify_date", formatter.format(new Date()));
                        mapObject.put("expect_solve_date", curba.get("expect_solve_date"));
                        mapObject.put("verify_illustrate", "");
                        mapObject.put("verify_status", "Y");
                        mapObject.put("uuid",curba.get("uuid"));
                        mapObject.put("question_id",beforeQuestionVo.getOid());
                        measureVerifyContent.add(mapObject);
                        flag = 1;
                        break;
                    }
                }
                if (flag ==1) {
                    break;
                }
            }
        }
        newMeasureExecuteDetail.put("corrective_measure_verify",measureVerifyContent);
        newMeasureExecuteDetail.remove("attachment_info");
        newMeasureExecuteDetail.put("attachment_info",packageAttachments(attachments));
        // 保留value为null的数据

        JSON.toJSONString(newMeasureExecuteDetail, SerializerFeature.WriteMapNullValue);
        // jsonObject转string
        String dataContentString = JSON.toJSONString(newObject);

        // 围堵措施执行验证实例详细数据
        DataInstanceEntity entity = new DataInstanceEntity();
        entity.setOid(dataInstanceOid);
        entity.setDataContent(dataContentString);
        entity.setQuestionTraceOid(oid);
        return entity;
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
        DateUtil.assignValueForExpectCompleteTime(entity,dataDetail,Question8DSolveEnum.key_reason.getCode());
        JSONArray lastingMeasureExecuteInfo = dataDetail.getJSONArray("corrective_measure_execute");
        String liablePersonId = "";
        String liablePersonName = "";
        for (Iterator<Object> iterator = lastingMeasureExecuteInfo.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            obj.put("complete_status","1");
            liablePersonId= obj.getString("corrective_person_id");
            liablePersonName= obj.getString("corrective_person_name");
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
        return  JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
    }

}
