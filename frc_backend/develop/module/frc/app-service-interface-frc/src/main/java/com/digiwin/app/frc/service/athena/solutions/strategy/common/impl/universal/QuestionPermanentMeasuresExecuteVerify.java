package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.universal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
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
import com.digiwin.app.frc.service.athena.qdh.domain.vo.QuestionDetailVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.PermanentMeasureVerifyInfoModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.QuestionInfoUniversalModel;
import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.PendingQuestionVo;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.DateUtil;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.TransferTool;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.frc.service.athena.util.rqi.EocUtils;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: xieps
 * @Date: 2022/4/11 14:12
 * @Version 1.0
 * @Description D5-2 恒久措施验证
 */
public class QuestionPermanentMeasuresExecuteVerify implements QuestionHandlerStrategy {

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);


    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    UniversalQuestionBiz universalQuestionBiz =  SpringContextHolder.getBean(UniversalQuestionBiz.class);

    @Autowired
    IamEocBiz iamEocBiz =   SpringContextHolder.getBean(IamEocBiz.class);

    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        JSONObject resultJsonObject = JSON.parseObject(parameters);

        //1.string转model，将待更新的字段转为model
        PermanentMeasureVerifyInfoModel measureVerifyInfoModel = TransferTool.convertString2Model(parameters, PermanentMeasureVerifyInfoModel.class);

        //2.参数校验
        ParamCheckUtil.checkPermanentMeasureVerifyParams(measureVerifyInfoModel);

        //3.更新任务卡状态
        QuestionInfoUniversalModel questionInfoModel = measureVerifyInfoModel.getQuestionInfoUniversalModel();
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONArray permanentMeasureVerify = resultJsonObject.getJSONArray("lasting_measure_execute_verify");
        JSONArray attachmentInfos =  resultJsonObject.getJSONArray("attachment_info");
        JSONObject questionConfirm =  resultJsonObject.getJSONObject("question_confirm");

        return handleDetail(entity,permanentMeasureVerify,attachmentInfos,questionConfirm,questionInfoModel.getOid());
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 获取最新的恒久措施分配数据，得到步骤号
            List<BeforeQuestionVo> permanentMeasureList = actionTraceMapper.getBeforeQuestionTraceForList1(TenantTokenUtil.getTenantSid(),
                    entity.getQuestionRecordOid(), entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                    QuestionUniversalSolveEnum.permanent_measures.getCode());
            int step = permanentMeasureList.get(0).getPrincipalStep();

            // 获取当前处理步骤的前一节点(获取恒久措施执行数据)
            List<BeforeQuestionVo> beforeQuestionVoList = actionTraceMapper.getBeforeQuestionTraceForList(TenantTokenUtil.getTenantSid(),
                    entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                    QuestionUniversalSolveEnum.permanent_measures_execute.getCode(),step);

            // 初始化-待审核问题-数据 entity
            String dataInstanceOid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            String oid = IdGenUtil.uuid();
            entity.setOid(oid);

            // 获取执行顺序
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
            // 表单数据流转
            DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVoList,permanentMeasureList.get(0),entity);
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

    @Override
    public JSONObject handleBack(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            QuestionDetailVo questionDetailVo = actionTraceMapper.getQuestionTrace(entity.getOid());
            // 表单数据流转
            JSONObject re = createDataInstanceForHandleBack(entity,questionDetailVo);
            responseParam.add(re);
        }
        responseObject.put("return_data",responseParam);
        return responseObject;
    }


    /**
     * 表单数据流转，生成待审核的表单内容
     * @param oid 当前节点的问题处理追踪表 主键
     * @param dataInstanceOid 待生成的当前处理步骤的问题实例表 主键
     * @param beforeQuestionVoList 围堵措施单数据
     */
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid,List<BeforeQuestionVo> beforeQuestionVoList,BeforeQuestionVo containmentMeasureEntity,QuestionActionTraceEntity actionEntity){
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(containmentMeasureEntity.getDataContent());

        // 获取最外层 question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);

        //追踪表实体类设置预期完成时间
        DateUtil.assignValueForExpectCompleteTime(actionEntity,dataDetail, QuestionUniversalSolveEnum.permanent_measures.getCode());


        JSONArray permanentMeasureInfo = (JSONArray) dataDetail.get("lasting_measure");

        dataDetail.remove("lasting_measure");

        JSONArray measureVerifyContent = new JSONArray();

        JSONArray attachments = new JSONArray();
        //循环恒久措施信息
        for (Iterator<Object> iterator = permanentMeasureInfo.iterator(); iterator.hasNext();) {
            JSONObject de = (JSONObject)iterator.next();
            for (BeforeQuestionVo beforeQuestionVo:beforeQuestionVoList) {
                JSONObject curbObject = JSON.parseObject(beforeQuestionVo.getDataContent());
                // 获取最外层 question_result
                JSONObject measureExecuteDetail = curbObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
                JSONArray files = measureExecuteDetail.getJSONArray("attachment_info");
                JSONArray measureExecuteInfo = (JSONArray) measureExecuteDetail.get("lasting_measure_execute");
                int flag = 0;
                for (Iterator<Object> ded = measureExecuteInfo.iterator(); ded.hasNext();) {
                    JSONObject curba = (JSONObject) ded.next();
                    //组建恒久措施执行验证信息
                    // 与前端约定uuid  结合恒久措施信息与恒久措施执行信息获取对应的恒久措施执行验证信息
                    // 使得组建后的数据有序  （与恒久措施顺序一致）
                    if (de.get("uuid").equals(curba.get("uuid"))) {
                        boolean filter = de.containsKey("is_history_data") && "Y".equals(de.get("is_history_data"));
                        if(!filter){
                            attachments.addAll(files);
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            if (null != beforeQuestionVo.getActualCompleteDate()) {
                                String dateString = formatter.format(beforeQuestionVo.getActualCompleteDate());
                                curba.put("actual_finish_date", dateString);
                            } else {
                                curba.put("actual_finish_date", "");
                            }
                            curba.put("verify_date", formatter.format(new Date()));
                            curba.put("verify_illustrate", "");
                            curba.put("verify_status", "Y");
                            curba.put("question_id",beforeQuestionVo.getOid());

                            measureVerifyContent.add(curba);
                            flag = 1;
                            break;
                        }
                    }
                }
                if (flag ==1) {
                    break;
                }
            }

        }

        //增加恒久措施执行验证历史数据置灰操作 is_history_date为Y
        //获取恒久措施验证历史数据
        List<BeforeQuestionVo> historicalDataList = actionTraceMapper.getHistoricalData(TenantTokenUtil.getTenantSid(),actionEntity.getQuestionRecordOid(),
                actionEntity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionUniversalSolveEnum.permanent_measures_execute_verify.getCode(),actionEntity.getPrincipalStep());
        if(!historicalDataList.isEmpty()){
                JSONObject parseObject = JSON.parseObject(historicalDataList.get(0).getDataContent());
                JSONObject questionResult = parseObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
                JSONArray measureExecuteVerify = questionResult.getJSONArray("lasting_measure_execute_verify");
                for ( Iterator<Object> iterator = measureExecuteVerify.iterator();iterator.hasNext();){
                    JSONObject obj = (JSONObject) iterator.next();
                    obj.put("is_history_data","Y");
                    measureVerifyContent.add(obj);
                }
        }

        Collections.reverse(measureVerifyContent);

        dataDetail.put("lasting_measure_execute_verify",measureVerifyContent);
        dataDetail.remove("attachment_info");
        dataDetail.put("attachment_info",packageAttachments(attachments));
        // 保留value为null的数据
        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        // jsonObject转string
        String dataContentString = JSON.toJSONString(resultJsonObject);

        // 恒久措施执行验证实例详细数据
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



    /**
     * 更新表单详情数据
     * @param permanentMeasureVerify 入参 需更新字段信息
     * @param oid 问题处理追踪主键
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONArray permanentMeasureVerify,JSONArray attachmentModels,JSONObject questionConfirm ,String oid) throws IOException {
        // 获取入参 需更新表单
        // 获取反馈单表单信息
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        // 获取最外层 question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject("question_result");

        dataDetail.remove("lasting_measure_execute");
        //处理预计完成时间 由于前端传值只有年月日 取生成这张卡对应每条记录的预计完成时间
        JSONArray beforeMeasureExecuteVerify = dataDetail.getJSONArray("lasting_measure_execute_verify");
        for(Iterator<Object> iterator = beforeMeasureExecuteVerify.iterator();iterator.hasNext();){
            JSONObject objectBefore = (JSONObject) iterator.next();
            for(Iterator<Object> iterator1 = permanentMeasureVerify.iterator();iterator1.hasNext();){
                JSONObject objectNow = (JSONObject) iterator1.next();
                if(objectBefore.get("uuid").equals(objectNow.get("uuid"))){
                    objectNow.remove("expect_solve_date");
                    objectNow.put("expect_solve_date",objectBefore.getString("expect_solve_date"));
                    break;
                }
            }
        }
        dataDetail.remove("lasting_measure_execute_verify");
        dataDetail.put("lasting_measure_execute_verify",permanentMeasureVerify);
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

        // 处理附件
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE003008");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // 初始实体
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // 落存数据
        return universalQuestionBiz.handleUpdateForPermanentMeasureVerify(questionActionTraceEntity,attachmentEntities,entity);
    }


    /**
     * 恒久措施验证退回生成相应退回的待审核卡
     *
     * @param entity
     * @param questionDetailVo
     * @return
     */
    private JSONObject createDataInstanceForHandleBack(QuestionActionTraceEntity entity, QuestionDetailVo questionDetailVo) throws Exception {
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(questionDetailVo.getDataContent());
        // 获取最外层 question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);

        JSONArray lastingMeasureExecuteInfo = dataDetail.getJSONArray("lasting_measure_execute");
        String liablePersonId = null;
        String liablePersonName = null;
        for (Iterator<Object> iterator = lastingMeasureExecuteInfo.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject) iterator.next();
            if ("N".equals(obj.get("is_history_data"))) {
                obj.put("execute_status", "1");
                liablePersonId = obj.getString("liable_person_id");
                liablePersonName = obj.getString("liable_person_name");
            }
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
        //加入预计完成时间
        entity.setExpectCompleteDate(questionDetailVo.getExpectCompleteDate());
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
