package com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionDistributionConst;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.config.annotation.ParamValidationHandler;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.other.QuestionInfoModel;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.SolutionStepVo;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName QuestionDistribution
 * @Description 问题分配
 * @Author author
 * @Date 2021/11/19 1:00
 * @Version 1.0
 **/
public class QuestionDistribution  implements QuestionTraceStrategy {

    /**
     * 因从工厂new出的对象，所以这里采用手动注入
     */
    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Override
    public JSONObject updateQuestionTrace(String parameters) throws Exception {
        // 处理更新数据
        JSONObject resultJsonObject = JSONObject.parseObject(parameters);
        // question_info抽取，并校验
        QuestionInfoModel questionInfoModel = TransferTool.convertString2Model(resultJsonObject.getJSONArray("question_info").getString(0), QuestionInfoModel.class);
        // 参数校验
        ParamValidationHandler.validateParams(questionInfoModel);
        JSONArray attachmentInfos = resultJsonObject.getJSONArray("attachment_info");
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONObject questionDistributeInfo = resultJsonObject.getJSONArray("question_distribute_info").getJSONObject(0);
        JSONObject re = handleDetail(entity,questionDistributeInfo,attachmentInfos,questionInfoModel.getOid());
        return re;
    }

    @Override
    public JSONArray insertUnapprovedQuestionTrace(QuestionActionTraceEntity entity) throws Exception {
        List<BeforeQuestionVo> beforeQuestionVos;
        // 校验skip
        ParamValidationHandler.validateParams(entity);
        if ("2".equals(entity.getSkip())) {
            beforeQuestionVos = actionTraceMapper.getBeforeQuestionTraceForIdentity(TenantTokenUtil.getTenantSid(),entity.getQuestionNo(),QuestionUpdateEnum.question_identification.getCode(), null);
        }else {
            // 获取当前处理步骤的前一节点(获取问题识别审核数据)
            beforeQuestionVos = actionTraceMapper.getBeforeQuestionTraceForIdentity(TenantTokenUtil.getTenantSid(),entity.getQuestionNo(),QuestionUpdateEnum.question_identification_review.getCode(), null);
        }
        if (CollectionUtils.isEmpty(beforeQuestionVos)) {
            throw new DWRuntimeException( MultilingualismUtil.getLanguage("beforeStepNull"));
        }
        // 初始化-待审核问题-数据 entity
        String dataInstanceOid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        String oid = IdGenUtil.uuid();
        entity.setOid(oid);

        // 获取执行顺序
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStepForIdentity(TenantTokenUtil.getTenantSid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

        // 表单数据流转
        DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVos.get(0).getDataContent(),entity);
        actionTraceBiz.insertActionTrace(entity,dataInstanceEntity);

        // 封装response
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
     * @param dataContent 问题识别审核 表单数据
     */
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid,String dataContent,QuestionActionTraceEntity traceEntity) throws Exception {
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSONObject.parseObject(dataContent);
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = (JSONObject) questionResult.get(0);

        // 识别单

        // 获取解决方案id
        JSONArray identifyInfos = (JSONArray) dataDetail.get("question_identify_info");
        JSONObject identify = (JSONObject) identifyInfos.get(0);
        String solutionId = (String) identify.get("solution_id");


        List<SolutionStepVo> solutionStepVos = actionTraceMapper.getSolutionStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),solutionId);


        // 将问题分配信息加入
        JSONArray distributions = new JSONArray();
        JSONObject distribution = new JSONObject();
        // 初始化空 问题分配要求-question_distribute_request
        distribution.put(QuestionDistributionConst.QUESTION_DISTRIBUTE_REQUEST,"");
        // 初始化空 问题分配明细-question_distribute_detail
        JSONArray distributeDetail = new JSONArray();
        //添加解决方案配置信息
        for (SolutionStepVo solutionStepVo : solutionStepVos) {
            JSONObject distribute = new JSONObject();
            distribute.put("step_id",solutionStepVo.getStepId());
            distribute.put("step_name",solutionStepVo.getStepName());
            if ("SE002001".equals(solutionStepVo.getStepId())) {
                distribute.put("process_person_id",identify.get("liable_person_id"));
                distribute.put("process_person_name",identify.get("liable_person_name"));
                //添加第一关卡的预计完成时间  yyyy-MM-dd HH:mm:ss
                traceEntity.setExpectCompleteDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(DateUtil.getExpectDateWithHourMinute(solutionStepVo.getExpectCompleteDays())));
            }else {
                distribute.put("process_person_id",solutionStepVo.getProcessPersonId());
                distribute.put("process_person_name",solutionStepVo.getProcessPersonName());
            }
            distribute.put("expect_complete_date", DateUtil.getExpectDateWithHourMinute(solutionStepVo.getExpectCompleteDays()));
            distribute.put("attachment_upload_flag","");
            distributeDetail.add(distribute);
        }
        distribution.put(QuestionDistributionConst.QUESTION_DISTRIBUTE_DETAIL,distributeDetail);
        distributions.add(distribution);
        // 初始化 问题分配信息-question_distribute_info
        dataDetail.put(QuestionDistributionConst.QUESTION_DISTRIBUTE_INFO,distributions);

        // 保留value为null的数据
        JSONObject.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
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
     * @param questionActionTraceEntity 需更新的任务卡实体
     * @param distributionInfo 入参 需更新字段信息
     * @param oid 问题处理追踪主键
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONObject distributionInfo,JSONArray attachmentModels,String oid) throws IOException {
        // 获取入参 需更新表单
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);

        JSONObject resultJsonObject = JSONObject.parseObject(dataInstanceVo.getDataContent());
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray("question_result");
        JSONObject dataDetail = questionResult.getJSONObject(0);

        JSONArray questionDistributeInfos = dataDetail.getJSONArray("question_distribute_info");
        JSONObject questionDistributeInfo = questionDistributeInfos.getJSONObject(0);
        questionDistributeInfo.remove("question_distribute_request");
        questionDistributeInfo.put("question_distribute_request",distributionInfo.get("question_distribute_request"));
        // 加入预计完成时间
        //前端传过来的节点数据
        JSONArray jsonArray=distributionInfo.getJSONArray("question_distribute_detail");
        //数据库中存的创建卡时候
        JSONArray jsonArrayDb=questionDistributeInfo.getJSONArray("question_distribute_detail");
        for(Iterator<Object> iterator = jsonArrayDb.iterator();iterator.hasNext();){
            JSONObject objectBefore = (JSONObject) iterator.next();
            String date=objectBefore.get("expect_complete_date").toString();
            String second = date.substring(10);
            for(Iterator<Object> iteratorNew = jsonArray.iterator();iteratorNew.hasNext();){
                JSONObject objectNow = (JSONObject) iteratorNew.next();
                if (objectBefore.get("step_id").equals(objectNow.get("step_id"))){
                    String dateNew=objectNow.get("expect_complete_date").toString();
                    objectNow.remove("expect_complete_date");
                    objectNow.put("expect_complete_date",dateNew+second);
                    break;
                }
            }
        }
        questionDistributeInfo.remove("question_distribute_detail");
        questionDistributeInfo.put("question_distribute_detail",distributionInfo.get("question_distribute_detail"));
        JSONArray attachmentInfos = dataDetail.getJSONArray("attachment_info");
        // 处理需上传的附件信息
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleAttachments(questionActionTraceEntity,attachmentInfos,attachmentModels,
                dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE002001",true,distributionInfo.getJSONArray("question_distribute_detail"));
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // 初始实体
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // 落存数据
        return actionTraceBiz.handleUpdateForDistribution(questionActionTraceEntity,attachmentEntities,entity);
    }


}
