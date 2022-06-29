package com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.Question8DSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUniversalSolveEnum;
import com.digiwin.app.frc.service.athena.config.annotation.ParamValidationHandler;
import com.digiwin.app.frc.service.athena.file.biz.IPdfServiceBiz;
import com.digiwin.app.frc.service.athena.meta.rabbitmq.handler.MessageSendHandler;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.other.QuestionInfoModel;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.strategy.generalSolution.QuestionTraceStrategy;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.TransferTool;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName QuestionAcceptance
 * @Description 问题验收逻辑
 * @Author author
 * @Date 2021/11/22 22:31
 * @Version 1.0
 **/
public class QuestionAcceptance implements QuestionTraceStrategy {

    /**
     * 因从工厂new出的对象，所以这里采用手动注入
     */
    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    MessageSendHandler messageSendHandler =  SpringContextHolder.getBean(MessageSendHandler.class);

    @Autowired
    IPdfServiceBiz iPdfServiceBiz = SpringContextHolder.getBean(IPdfServiceBiz.class);
    @Override
    public JSONObject updateQuestionTrace(String parameters) throws Exception {
        // 解析结构
        JSONObject resultJsonObject = JSONObject.parseObject(parameters);
        // question_info抽取，并校验
        QuestionInfoModel questionInfoModel = TransferTool.convertString2Model(resultJsonObject.getJSONArray("question_info").getString(0), QuestionInfoModel.class);
        // 参数校验
        ParamValidationHandler.validateParams(questionInfoModel);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);
        JSONObject questionAcceptanceInfo  = resultJsonObject.getJSONArray("question_acceptance_info").getJSONObject(0);
        JSONArray attachmentInfos = resultJsonObject.getJSONArray("attachment_info");
        JSONObject re =  handleDetail(entity, resultJsonObject.getJSONArray("question_acceptance_info"),attachmentInfos, questionInfoModel.getOid());

        // 正常结案入库
        if (entity.getQuestionProcessStatus() == 8 && entity.getQuestionProcessResult() == 2) {
            if (questionAcceptanceInfo.getInteger("is_knowledge_base") == 0) {
                String routingKey = DWModuleConfigUtils.getCurrentModuleProperty("frc.to.kmo");
                JSONObject parameter = new JSONObject();
                parameter.put("questionOid",questionInfoModel.getOid());
                messageSendHandler.send(routingKey, parameter);
            }

        }
        return re;
    }

    @Override
    public JSONArray insertUnapprovedQuestionTrace(QuestionActionTraceEntity entity) throws Exception {
        // 获取当前处理步骤的前一节点
        List<BeforeQuestionVo> beforeQuestionVo = actionTraceMapper.getBeforeQuestionTraceForIdentity(TenantTokenUtil.getTenantSid(),
                entity.getQuestionNo(),null, null);
        if (CollectionUtils.isEmpty(beforeQuestionVo)) {
            throw new DWRuntimeException( MultilingualismUtil.getLanguage("beforeStepNull"));
        }
        // 初始化-待审核问题-数据 entity
        String dataInstanceOid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        String oid = IdGenUtil.uuid();
        entity.setOid(oid);
        // 获取执行顺序
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStepForIdentity((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
        // 表单数据流转
        DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVo.get(0));
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
     * @param beforeQuestionVo  表单数据
     */
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid,BeforeQuestionVo beforeQuestionVo){
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSONObject.parseObject(beforeQuestionVo.getDataContent());

        JSONObject dataDetail=null;
        // 获取最外层 question_result
        if (QuestionUniversalSolveEnum.process_confirmation.getCode().equals(beforeQuestionVo.getQuestionSolveStep())) {
            dataDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
        }else {
            JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
            dataDetail = questionResult.getJSONObject(0);
        }
        // 将问题验收信息加入
        JSONArray acceptances = new JSONArray();
        JSONObject acceptance = new JSONObject();
        acceptance.put("acceptance_description","");
        acceptance.put("is_knowledge_base",0);
        // 初始化 问题分配信息-question_distribute_info

        if (QuestionSolveEnum.question_close.getCode().equals(beforeQuestionVo.getQuestionSolveStep())) {
            JSONArray question_closure = dataDetail.getJSONArray("question_closure");
            JSONObject questionClose = question_closure.getJSONObject(0);
            questionClose.put("question_closure_id",beforeQuestionVo.getQuestionSolveStep());
            questionClose.put("question_closure_name", MultilingualismUtil.getLanguage(beforeQuestionVo.getQuestionSolveStep()));
            questionClose.put("process_person_id",beforeQuestionVo.getLiablePersonId());
            questionClose.put("process_person_name",beforeQuestionVo.getLiablePersonName());

            if (null != beforeQuestionVo.getActualCompleteDate()) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = formatter.format(beforeQuestionVo.getActualCompleteDate());
                questionClose.put("process_date",dateString);
            }else {
                questionClose.put("process_date","");
            }

            if (null != beforeQuestionVo.getExpectCompleteDate()) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String dateString = formatter.format(beforeQuestionVo.getExpectCompleteDate());
                questionClose.put("expect_complete_date",dateString);
            }else {
                questionClose.put("expect_complete_date","");
            }
        }
        acceptances.add(acceptance);
        dataDetail.put("question_acceptance_info",acceptances);
        String printReportId=iPdfServiceBiz.getReportPdf(resultJsonObject);
        acceptance.put("print_report_id",printReportId);

        // 保留value为null的数据
        JSONObject.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        //冲刺五 对象转数组
        if(QuestionUniversalSolveEnum.process_confirmation.getCode().equals(beforeQuestionVo.getQuestionSolveStep())){
            processFormatArray(dataDetail);
            resultJsonObject.remove(QuestionResponseConst.QUESTION_RESULT);
            resultJsonObject.put(QuestionResponseConst.QUESTION_RESULT,new JSONArray().fluentAdd(dataDetail));
        }

        // jsonObject转string
        String dataContentString = JSON.toJSONString(resultJsonObject);
        // 落存问题分配 详细数据
        DataInstanceEntity entity = new DataInstanceEntity();
        entity.setOid(dataInstanceOid);
        entity.setDataContent(dataContentString);
        entity.setQuestionTraceOid(oid);
        return entity;
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
     * 更新表单详情数据
     * @param questionAcceptanceInfo 入参 需更新字段信息
     * @param oid 问题处理追踪主键
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONArray questionAcceptanceInfo,JSONArray attachmentModels, String oid) throws IOException {
        // 获取入参 需更新表单
        // 获取反馈单表单信息
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        JSONObject resultJsonObject = JSONObject.parseObject(dataInstanceVo.getDataContent());

        // 获取最外层 question_result
        JSONObject dataDetail = resultJsonObject.getJSONArray("question_result").getJSONObject(0);
        dataDetail.remove("question_acceptance_info");
        dataDetail.put("question_acceptance_info",questionAcceptanceInfo);

        JSONArray attachmentInfos =  dataDetail.getJSONArray("attachment_info");

        //抽取本地上传的附件信息
        JSONArray mustUploadAttachments = new JSONArray();

        for (Iterator iterator = attachmentModels.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            boolean status = true;
            for (Iterator it = attachmentInfos.iterator();it.hasNext();) {
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
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SA");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // 初始实体
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // 落存数据
        JSONObject jsonObject=actionTraceBiz.shortTermUpdate(questionActionTraceEntity,attachmentEntities,entity);
        return jsonObject;
    }

}
