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
 * @Description ??????????????????
 * @Author author
 * @Date 2021/11/22 22:31
 * @Version 1.0
 **/
public class QuestionAcceptance implements QuestionTraceStrategy {

    /**
     * ????????????new?????????????????????????????????????????????
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
        // ????????????
        JSONObject resultJsonObject = JSONObject.parseObject(parameters);
        // question_info??????????????????
        QuestionInfoModel questionInfoModel = TransferTool.convertString2Model(resultJsonObject.getJSONArray("question_info").getString(0), QuestionInfoModel.class);
        // ????????????
        ParamValidationHandler.validateParams(questionInfoModel);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);
        JSONObject questionAcceptanceInfo  = resultJsonObject.getJSONArray("question_acceptance_info").getJSONObject(0);
        JSONArray attachmentInfos = resultJsonObject.getJSONArray("attachment_info");
        JSONObject re =  handleDetail(entity, resultJsonObject.getJSONArray("question_acceptance_info"),attachmentInfos, questionInfoModel.getOid());

        // ??????????????????
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
        // ???????????????????????????????????????
        List<BeforeQuestionVo> beforeQuestionVo = actionTraceMapper.getBeforeQuestionTraceForIdentity(TenantTokenUtil.getTenantSid(),
                entity.getQuestionNo(),null, null);
        if (CollectionUtils.isEmpty(beforeQuestionVo)) {
            throw new DWRuntimeException( MultilingualismUtil.getLanguage("beforeStepNull"));
        }
        // ?????????-???????????????-?????? entity
        String dataInstanceOid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        String oid = IdGenUtil.uuid();
        entity.setOid(oid);
        // ??????????????????
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStepForIdentity((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
        // ??????????????????
        DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVo.get(0));
        actionTraceBiz.insertActionTrace(entity,dataInstanceEntity);
        JSONArray responseParam = new JSONArray();
        JSONObject responseObject = new JSONObject();
        responseObject.put("pending_approve_question_id",oid);
        responseParam.add(responseObject);
        return responseParam;
    }

    /**
     * ???????????????????????????????????????????????????
     * @param oid ???????????????????????????????????? ??????
     * @param dataInstanceOid ???????????????????????????????????????????????? ??????
     * @param beforeQuestionVo  ????????????
     */
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid,BeforeQuestionVo beforeQuestionVo){
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSONObject.parseObject(beforeQuestionVo.getDataContent());

        JSONObject dataDetail=null;
        // ??????????????? question_result
        if (QuestionUniversalSolveEnum.process_confirmation.getCode().equals(beforeQuestionVo.getQuestionSolveStep())) {
            dataDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
        }else {
            JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
            dataDetail = questionResult.getJSONObject(0);
        }
        // ???????????????????????????
        JSONArray acceptances = new JSONArray();
        JSONObject acceptance = new JSONObject();
        acceptance.put("acceptance_description","");
        acceptance.put("is_knowledge_base",0);
        // ????????? ??????????????????-question_distribute_info

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

        // ??????value???null?????????
        JSONObject.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        //????????? ???????????????
        if(QuestionUniversalSolveEnum.process_confirmation.getCode().equals(beforeQuestionVo.getQuestionSolveStep())){
            processFormatArray(dataDetail);
            resultJsonObject.remove(QuestionResponseConst.QUESTION_RESULT);
            resultJsonObject.put(QuestionResponseConst.QUESTION_RESULT,new JSONArray().fluentAdd(dataDetail));
        }

        // jsonObject???string
        String dataContentString = JSON.toJSONString(resultJsonObject);
        // ?????????????????? ????????????
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
     * ????????????????????????
     * @param questionAcceptanceInfo ?????? ?????????????????????
     * @param oid ????????????????????????
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONArray questionAcceptanceInfo,JSONArray attachmentModels, String oid) throws IOException {
        // ???????????? ???????????????
        // ???????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        JSONObject resultJsonObject = JSONObject.parseObject(dataInstanceVo.getDataContent());

        // ??????????????? question_result
        JSONObject dataDetail = resultJsonObject.getJSONArray("question_result").getJSONObject(0);
        dataDetail.remove("question_acceptance_info");
        dataDetail.put("question_acceptance_info",questionAcceptanceInfo);

        JSONArray attachmentInfos =  dataDetail.getJSONArray("attachment_info");

        //?????????????????????????????????
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
        // ????????????
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SA");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // ????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // ????????????
        JSONObject jsonObject=actionTraceBiz.shortTermUpdate(questionActionTraceEntity,attachmentEntities,entity);
        return jsonObject;
    }

}
