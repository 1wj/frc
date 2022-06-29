package com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.config.annotation.ParamValidationHandler;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.other.QuestionInfoModel;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.QuestionDetailVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.QuestionTraceStrategy;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName QuestionDistribution
 * @Description 遏制分配
 * @Author author
 * @Date 2021/11/21 1:00
 * @Version 1.0
 **/
public class QuestionCurbDistribution implements QuestionTraceStrategy {


    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    AttachmentMapper attachmentMapper = SpringContextHolder.getBean(AttachmentMapper.class);

    @Override
    public JSONObject updateQuestionTrace(String parameters) throws Exception {
        JSONObject resultJsonObject = JSON.parseObject(parameters);

        // question_info抽取，并校验
        QuestionInfoModel questionInfoModel = TransferTool.convertString2Model(resultJsonObject.getJSONArray("question_info").getString(0), QuestionInfoModel.class);
        // 参数校验
        ParamValidationHandler.validateParams(questionInfoModel);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONObject questionCurbDistributeInfo = resultJsonObject.getJSONArray("curb_distribute_info").getJSONObject(0);
        JSONArray attachmentInfos = (JSONArray) resultJsonObject.get("attachment_info");
        //  加入预计完成时间 根据question_id查询 问题号和record_oid，在查询上一个结点数据
        QuestionDetailVo questionDetailVo = actionTraceMapper.getQuestionTrace(questionInfoModel.getOid());
        if (!StringUtils.isEmpty(questionDetailVo.getQuestionNo())) {
            List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(), questionDetailVo.getQuestionRecordId(),
                    questionDetailVo.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionSolveEnum.question_distribution.getCode());
            // 获取前一结点的表单数据 string转json
            JSONObject re = JSON.parseObject(beforeQuestionVos.get(0).getDataContent());
            // 获取最外层 question_result
            JSONArray questionResult = re.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
            JSONObject dataDetail = (JSONObject) questionResult.get(0);
            JSONArray questionDistributeInfos = (JSONArray) dataDetail.get("question_distribute_info");
            JSONObject questionDistributeInfo = (JSONObject) questionDistributeInfos.get(0);


        }
        JSONObject response = handleDetail(entity,questionCurbDistributeInfo,attachmentInfos,questionInfoModel.getOid());
        return response;
    }

    @Override
    public JSONArray insertUnapprovedQuestionTrace(QuestionActionTraceEntity entity) throws Exception {
        // 获取当前处理步骤的前一节点(获取问题识别审核数据)
        List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(), entity.getQuestionRecordOid(),
                entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionSolveEnum.question_distribution.getCode());
        if (CollectionUtils.isEmpty(beforeQuestionVos)) {
            throw new DWRuntimeException( MultilingualismUtil.getLanguage("beforeStepNull"));
        }
        // 初始化-待审核问题-数据 entity
        String dataInstanceOid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        String oid = IdGenUtil.uuid();
        entity.setOid(oid);
        // 获取执行顺序
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
        // 表单数据流转
        DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVos.get(0),entity);
        actionTraceBiz.insertActionTrace(entity,dataInstanceEntity);

        JSONArray responseParam = new JSONArray();
        JSONObject responseObject = new JSONObject();
        responseObject.put("pending_approve_question_id",oid);
        responseParam.add(responseObject);
        return responseParam;
    }

    /**
     * 表单数据流转，生成待审核的表单内容
     * @param oid 当前节点的问题处理追踪表 主键
     * @param dataInstanceOid 待生成的当前处理步骤的问题实例表 主键
     * @param beforeQuestionVo 问题分配单数据
     */
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid,BeforeQuestionVo beforeQuestionVo,QuestionActionTraceEntity traceEntity) throws Exception {
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(beforeQuestionVo.getDataContent());
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = (JSONObject) questionResult.get(0);

        // 将问题遏制信息加入
        JSONArray curbDistributeInfo = new JSONArray();
        JSONObject curbDistribute = new JSONObject();
        // 初始化空 问题遏制要求-curb_request
        curbDistribute.put("curb_request","");
        // 初始化空 问题分配明细-question_distribute_detail
        JSONArray distributeDetail = new JSONArray();
        curbDistribute.put("curb_distribute_detail",distributeDetail);
        curbDistributeInfo.add(curbDistribute);
        // 初始化 问题分配信息-question_distribute_info
        dataDetail.put("curb_distribute_info",curbDistributeInfo);


        // 处理 更多信息
        JSONArray questionDistributeInfos = (JSONArray) dataDetail.get("question_distribute_info");
        JSONObject questionDistributeInfo = (JSONObject) questionDistributeInfos.get(0);

        JSONArray questionProcessInfo = new JSONArray();
        JSONObject processInfo = new JSONObject();
        //处理 更多信息 -加入 处理步骤+负责人+处理时间
        //12.16 新增
        processInfo.put("question_distribute_request",questionDistributeInfo.get("question_distribute_request"));
        processInfo.put("question_distribute_no",beforeQuestionVo.getQuestionSolveStep());
        //多语系
        processInfo.put("question_distribute_name", MultilingualismUtil.getLanguage(beforeQuestionVo.getQuestionSolveStep()));
        processInfo.put("process_person_id",beforeQuestionVo.getLiablePersonId());
        processInfo.put("process_person_name",beforeQuestionVo.getLiablePersonName());
        if (null != beforeQuestionVo.getActualCompleteDate()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter.format(beforeQuestionVo.getActualCompleteDate());
            processInfo.put("process_date",dateString);
        }else {
            processInfo.put("process_date","");
        }
        processInfo.put("question_distribute_detail",questionDistributeInfo.get("question_distribute_detail"));
        questionProcessInfo.add(processInfo);
        dataDetail.put("question_process_info",questionProcessInfo);
        //加入预计完成时间
        DateUtil.assignValueForCommonExpectCompleteTime(traceEntity,dataDetail,QuestionSolveEnum.question_curb_distribution.getCode());

        JSONArray DistributeInfos = (JSONArray) dataDetail.get("question_distribute_info");

        dataDetail.remove("question_distribute_info");
        String textNew = JSON.toJSONString(DistributeInfos, SerializerFeature.DisableCircularReferenceDetect);
        JSONArray jsonArray = JSONArray.parseArray(textNew);
        dataDetail.put("question_distribute_info",jsonArray);
        // 保留value为null的数据
        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        // jsonObject转string
        String dataContentString = JSON.toJSONString(resultJsonObject);

        // 落存问题分配 详细数据
        DataInstanceEntity entity = new DataInstanceEntity();
        entity.setOid(dataInstanceOid);
        entity.setDataContent(dataContentString);
        entity.setQuestionTraceOid(oid);
        return entity;
    }

    /**
     * 更新表单详情数据
     * @param questionActionTraceEntity 任务卡实体
     * @param questionCurbVerifyInfo 入参 需更新字段信息
     * @param oid 问题处理追踪主键
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONObject questionCurbVerifyInfo,JSONArray attachmentModels, String oid) throws IOException {
        // 获取入参 需更新表单
        // 获取反馈单表单信息
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray("question_result");
        JSONObject dataDetail = questionResult.getJSONObject(0);

        //  附件是否必传
        //数据库中的时分秒
        JSONArray processInfos= dataDetail.getJSONArray("question_process_info");
        JSONObject processInfo =  processInfos.getJSONObject(0);
        JSONArray distributeDetails= processInfo.getJSONArray("question_distribute_detail");

        // 将curb_verify_info信息加入
        JSONArray questionCurbDistributeInfos = dataDetail.getJSONArray("curb_distribute_info");
        JSONObject curbDistributeInfo = questionCurbDistributeInfos.getJSONObject(0);
        curbDistributeInfo.remove("curb_request");
        curbDistributeInfo.put("curb_request",questionCurbVerifyInfo.get("curb_request"));
        //前端传的
        JSONArray jsonArray =questionCurbVerifyInfo.getJSONArray("curb_distribute_detail");
        //添加预计完成时间时分秒
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

            for(Iterator iteratorNew = jsonArray.iterator();iteratorNew.hasNext();){
                JSONObject objectNow = (JSONObject) iteratorNew.next();
                String dateNew=objectNow.get("expect_complete_date").toString();
                    objectNow.remove("expect_complete_date");
                    objectNow.put("expect_complete_date",dateNew+" "+formatter.format(new Date()));

            }

        curbDistributeInfo.remove("curb_distribute_detail");
        curbDistributeInfo.put("curb_distribute_detail",questionCurbVerifyInfo.get("curb_distribute_detail"));
        JSONArray attachmentInfos = (JSONArray) dataDetail.get("attachment_info");

        // 抽取本地上传的附件信息
        JSONArray mustUploadAttachments = new JSONArray();
        //附件退回是否重复上传校验标识
        boolean repeatCheckFlag = false;
        for (Iterator<Object> iterator = attachmentModels.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            boolean status = true;
            for (Iterator<Object> it = attachmentInfos.iterator();it.hasNext();) {
                JSONObject attach = (JSONObject)it.next();
                if(!repeatCheckFlag){
                    repeatCheckFlag = "SE002002".equals(attach.getString("attachment_belong_stage"));
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
        for (Iterator<Object> ite = distributeDetails.iterator(); ite.hasNext();) {
            JSONObject obj = (JSONObject)ite.next();
            if ("SE002002".equals(obj.get("step_id"))) {
                if (obj.get("attachment_upload_flag").equals("Y")) {
                    if (mustUploadAttachments.size()==0) {
                        // 查询数据库
                        List<AttachmentEntity> attachmentEntities = attachmentMapper.getAttachments((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),dataInstanceVo.getOid());
                        if (attachmentEntities.size() == 0 && !repeatCheckFlag) {
                            throw new DWRuntimeException("attachment must upload");
                        }
                        break;
                    }

                }
            }
        }
        // 处理附件
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE002002");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // 初始实体
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // 落存数据
        return actionTraceBiz.handleUpdateForDistribution(questionActionTraceEntity,attachmentEntities,entity);
    }
}
