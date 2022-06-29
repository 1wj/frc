package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.eightD;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
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
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.ContainmentMeasureVerifyInfoModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.QuestionInfo8DModel;
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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @ClassName QuestionContainmentMeasureVerify
 * @Description 围堵措施验证
 * @Author HeX
 * @Date 2022/3/8 11:09
 * @Version 1.0
 **/
public class QuestionContainmentMeasureVerify implements QuestionHandlerStrategy {
    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);


    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    EightDQuestionBiz eightDQuestionBiz =  SpringContextHolder.getBean(EightDQuestionBiz.class);

    @Autowired
    IamEocBiz iamEocBiz =   SpringContextHolder.getBean(IamEocBiz.class);


    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {

        JSONObject resultJsonObject = JSON.parseObject(parameters);

        //1.string转model，将待更新的字段转为model
        ContainmentMeasureVerifyInfoModel measureVerifyInfoModel = TransferTool.convertString2Model(parameters, ContainmentMeasureVerifyInfoModel.class);

        //2.参数校验
        ParamCheckUtil.checkContainmentMeasureVerifyParams(measureVerifyInfoModel);

        //3.更新任务卡状态
        QuestionInfo8DModel questionInfoModel = measureVerifyInfoModel.getQuestionInfos().get(0);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONArray containmentMeasureVerify = resultJsonObject.getJSONArray("containment_measure_verify");
        JSONArray attachmentInfos =  resultJsonObject.getJSONArray("attachment_info");
        return handleDetail(entity,containmentMeasureVerify,attachmentInfos,questionInfoModel.getOid());
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 获取最新的围堵措施分配数据，得到步骤号
            List<BeforeQuestionVo> containmentMeasureList = actionTraceMapper.getBeforeQuestionTraceForList1(TenantTokenUtil.getTenantSid(),
                    entity.getQuestionRecordOid(), entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                    Question8DSolveEnum.containment_measure.getCode());
            int step = containmentMeasureList.get(0).getPrincipalStep();

            // 获取当前处理步骤的前一节点(获取围堵措施执行数据)
            List<BeforeQuestionVo> beforeQuestionVoList = actionTraceMapper.getBeforeQuestionTraceForList(TenantTokenUtil.getTenantSid(),
                    entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),
                    Question8DSolveEnum.containment_measure_execute.getCode(),step);

            // 初始化-待审核问题-数据 entity
            String dataInstanceOid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            String oid = IdGenUtil.uuid();
            entity.setOid(oid);

            // 获取执行顺序
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
            // 表单数据流转
            DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVoList,containmentMeasureList.get(0),entity);
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
     * 围堵措施验证退回生成相应退回的待审核卡
     *
     * @param entity
     * @param questionDetailVo
     * @return
     */
    private JSONObject createDataInstanceForHandleBack(QuestionActionTraceEntity entity, QuestionDetailVo questionDetailVo) throws Exception {
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(questionDetailVo.getDataContent());
        // 获取最外层 question_result
        JSONObject dataDetail = (JSONObject) resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).get(0);
        //预计完成时间赋值
        DateUtil.assignValueForExpectCompleteTime(entity,dataDetail,Question8DSolveEnum.containment_measure.getCode());
        JSONArray containmentMeasureExecuteInfo = dataDetail.getJSONArray("containment_measure_execute");
        String liablePersonId = null;
        String liablePersonName = null;
        for (Iterator<Object> iterator = containmentMeasureExecuteInfo.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            obj.put("containment_status","1");
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



    /**
     * 表单数据流转，生成待审核的表单内容
     * @param oid 当前节点的问题处理追踪表 主键
     * @param dataInstanceOid 待生成的当前处理步骤的问题实例表 主键
     * @param beforeQuestionVoList 围堵措施单数据
     */
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid,List<BeforeQuestionVo> beforeQuestionVoList,BeforeQuestionVo containmentMeasureEntity,QuestionActionTraceEntity traceEntity){
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(containmentMeasureEntity.getDataContent());

        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = (JSONObject) questionResult.get(0);
        JSONArray containmentMeasureInfo = (JSONArray) dataDetail.get("containment_measure");

        dataDetail.remove("containment_measure");

        String textNew = JSON.toJSONString(containmentMeasureInfo, SerializerFeature.DisableCircularReferenceDetect);
        JSONArray jsonArray = JSON.parseArray(textNew);
        dataDetail.put("containment_measure",jsonArray);
        JSONArray measureVerifyContent = new JSONArray();

        JSONArray attachments = new JSONArray();
        //循环围堵措施信息
        for (Iterator<Object> iterator = containmentMeasureInfo.iterator(); iterator.hasNext();) {
            JSONObject de = (JSONObject)iterator.next();
            for (BeforeQuestionVo beforeQuestionVo:beforeQuestionVoList) {
                JSONObject curbObject = JSON.parseObject(beforeQuestionVo.getDataContent());
                // 获取最外层 question_result
                JSONArray containmentMeasureExecuteResult = curbObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
                JSONObject measureExecuteDetail = (JSONObject) containmentMeasureExecuteResult.get(0);
                JSONArray files = measureExecuteDetail.getJSONArray("attachment_info");
                JSONArray measureExecuteInfo = (JSONArray) measureExecuteDetail.get("containment_measure_execute");
                int flag = 0;
                for (Iterator<Object> ded = measureExecuteInfo.iterator(); ded.hasNext();) {
                    JSONObject curba = (JSONObject) ded.next();
                    //组建围堵措施执行验证信息
                    // 与前端约定uuid  结合围堵措施信息与围堵措施执行信息获取对应的围堵措施执行验证信息
                    // 使得组建后的数据有序  （与围堵措施顺序一致）
                    if (de.get("uuid").equals(curba.get("uuid"))) {
                        attachments.addAll(files);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        if (null != beforeQuestionVo.getActualCompleteDate()) {
                            String dateString = formatter.format(beforeQuestionVo.getActualCompleteDate());
                            curba.put("actual_complete_date", dateString);
                        } else {
                            curba.put("actual_complete_date", "");
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
                if (flag ==1) {
                    break;
                }
            }

        }
        JSONObject curbObject = JSON.parseObject(beforeQuestionVoList.get(0).getDataContent());
        // 获取最外层 question_result
        JSONArray containmentMeasureExecuteResult = curbObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject measureExecuteDetail = (JSONObject) containmentMeasureExecuteResult.get(0);
        JSONArray measureExecute = (JSONArray) measureExecuteDetail.get("containment_measure_execute");
        dataDetail.put("containment_measure_execute",measureExecute);

        dataDetail.put("containment_measure_verify",measureVerifyContent);
        dataDetail.remove("attachment_info");
        dataDetail.put("attachment_info",packageAttachments(attachments));
        // 保留value为null的数据
        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        // jsonObject转string
        String dataContentString = JSON.toJSONString(resultJsonObject);

        //为traceEntity中的预计完成时间赋值
        DateUtil.assignValueForExpectCompleteTime(traceEntity,dataDetail,Question8DSolveEnum.containment_measure.getCode());

        // 围堵措施执行验证实例详细数据
        DataInstanceEntity entity = new DataInstanceEntity();
        entity.setOid(dataInstanceOid);
        entity.setDataContent(dataContentString);
        entity.setQuestionTraceOid(oid);
        return entity;
    }


    /**
     * 更新表单详情数据
     * @param containmentMeasureVerifyInfo 入参 需更新字段信息
     * @param oid 问题处理追踪主键
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONArray containmentMeasureVerifyInfo,JSONArray attachmentModels, String oid) throws IOException {
        // 获取入参 需更新表单
        // 获取反馈单表单信息
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray("question_result");
        JSONObject dataDetail = (JSONObject) questionResult.get(0);
        JSONArray beforeMeasureVerify = (JSONArray) dataDetail.get("containment_measure_verify");
        JSONArray containmentMeasureJsonArray = (JSONArray) dataDetail.get("containment_measure_execute");
        dataDetail.remove("containment_measure_execute");
        //处理预计完成时间时分秒
        DateUtil.measuresExecuteVerify(containmentMeasureVerifyInfo,beforeMeasureVerify);

        String textNew = JSON.toJSONString(containmentMeasureJsonArray, SerializerFeature.DisableCircularReferenceDetect);
        JSONArray jsonArray = JSON.parseArray(textNew);
        dataDetail.put("containment_measure_execute",jsonArray);

        dataDetail.remove("containment_measure_verify");
        dataDetail.put("containment_measure_verify",containmentMeasureVerifyInfo);

        JSONArray attachmentInfos = (JSONArray) dataDetail.get("attachment_info");

        //抽取本地上传的附件信息
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
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE001004");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // 初始实体
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // 落存数据
        return eightDQuestionBiz.handleUpdateForContainmentMeasureVerify(questionActionTraceEntity,attachmentEntities,entity);
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


}
