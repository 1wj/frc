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
import com.digiwin.app.frc.service.athena.qdh.biz.IamEocBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.other.QuestionInfoModel;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName QuestionDistribution
 * @Description ????????????-??????
 * @Author author
 * @Date 2021/11/21 1:00
 * @Version 1.0
 **/
public class QuestionCurb implements QuestionTraceStrategy {


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
    IamEocBiz iamEocBiz =  SpringContextHolder.getBean(IamEocBiz.class);

    @Override
    public JSONObject updateQuestionTrace(String parameters) throws Exception {
        JSONObject resultJsonObject = JSONObject.parseObject(parameters);
        // question_info??????????????????
        QuestionInfoModel questionInfoModel = TransferTool.convertString2Model(resultJsonObject.getJSONArray("question_info").getString(0), QuestionInfoModel.class);
        // ????????????
        ParamValidationHandler.validateParams(questionInfoModel);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONObject questionCurbDistributeInfo = resultJsonObject.getJSONArray("curb_distribute_info").getJSONObject(0);
        JSONArray attachmentInfos = resultJsonObject.getJSONArray("attachment_info");
        JSONObject re = handleDetail(entity,questionCurbDistributeInfo,attachmentInfos,questionInfoModel.getOid());
        return re;
    }

    @Override
    public JSONArray insertUnapprovedQuestionTrace(QuestionActionTraceEntity entity) throws Exception {
        // ???????????????????????????????????????(??????????????????-????????????????????????)
        List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionSolveEnum.question_curb_distribution.getCode());
        if (CollectionUtils.isEmpty(beforeQuestionVos)) {
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("beforeStepNull"));
        }
        // todo ???????????? ??????????????????
        // ??????????????????
        return createDataInstance1(entity,beforeQuestionVos.get(0));
    }

    /**
     * ????????????????????????
     * @param questionCurbDistributeInfo ?????? ?????????????????????
     * @param oid ????????????????????????
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONObject questionCurbDistributeInfo,JSONArray attachmentModels,  String oid) throws IOException {
        // ???????????? ???????????????
        // ???????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        JSONObject resultJsonObject = JSONObject.parseObject(dataInstanceVo.getDataContent());
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray("question_result");
        JSONObject dataDetail = (JSONObject) questionResult.get(0);
        // ???curb_verify_info????????????
        JSONArray questionCurbDistributeInfos = (JSONArray) dataDetail.get("curb_distribute_info");
        JSONObject curbDistributeInfo = (JSONObject) questionCurbDistributeInfos.get(0);
        curbDistributeInfo.put("curb_feedback",questionCurbDistributeInfo.get("curb_feedback"));
        //????????????
        JSONArray jsonArray=questionCurbDistributeInfo.getJSONArray("curb_distribute_detail");
        //????????????
        JSONArray jsonArrayDetail=curbDistributeInfo.getJSONArray("curb_distribute_detail");
        //?????????????????????????????????
        for(Iterator<Object> iterator = jsonArrayDetail.iterator();iterator.hasNext();){
            JSONObject objectBefore = (JSONObject) iterator.next();
            String date=objectBefore.get("expect_complete_date").toString();
            String second = date.substring(10);
            for(Iterator<Object> iteratorNew = jsonArray.iterator();iteratorNew.hasNext();){
                JSONObject objectNow = (JSONObject) iteratorNew.next();
                String dateNew=objectNow.get("expect_complete_date").toString();
                if(objectBefore.get("uuid").equals(objectNow.get("uuid"))){
                    objectNow.remove("expect_complete_date");
                    objectNow.put("expect_complete_date",dateNew+second);
                    break;
                }
            }
        }
        curbDistributeInfo.remove("curb_distribute_detail");
        curbDistributeInfo.put("curb_distribute_detail",questionCurbDistributeInfo.get("curb_distribute_detail"));

        JSONArray attachmentInfos = dataDetail.getJSONArray("attachment_info");

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

        // ????????????
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE002003");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // ????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // ????????????
        return actionTraceBiz.handleUpdateForDistribution(questionActionTraceEntity,attachmentEntities,entity);
    }


    private JSONArray createDataInstance1(QuestionActionTraceEntity entity,BeforeQuestionVo beforeQuestionVo) throws Exception {

        JSONArray responseParam = new JSONArray();

        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSONObject.parseObject(beforeQuestionVo.getDataContent());
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = questionResult.getJSONObject(0);
        //????????????????????????
        DateUtil.assignValueForCommonExpectCompleteTime(entity,dataDetail,QuestionSolveEnum.question_curb_distribution.getCode());

        // ??????????????????
        JSONArray curbDistributeInfo = dataDetail.getJSONArray("curb_distribute_info");
        JSONObject curbDistribute = curbDistributeInfo.getJSONObject(0);
        curbDistribute.put("curb_feedback","");

        JSONArray curbDistributeDetails = (JSONArray) curbDistribute.get("curb_distribute_detail");
        curbDistribute.remove("curb_distribute_detail");
        String dataInstanceOid;
        String oid;
        Map<String, List<Map<String, Object>>> collect = handleCurb(curbDistributeDetails);
        for (String key : collect.keySet()) {
            List<Map<String, Object>> re = collect.get(key);
            // ?????????-???????????????-?????? entity
            dataInstanceOid = IdGenUtil.uuid();
            oid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            entity.setOid(oid);
            entity.setLiablePersonId((String) re.get(0).get("process_person_id"));
            entity.setLiablePersonName((String) re.get(0).get("process_person_name"));
            // ??????????????????
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

            curbDistribute.put("curb_distribute_detail",re);

            // ??????value???null?????????
            JSONObject.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
            // jsonObject???string
            String dataContentString = JSON.toJSONString(resultJsonObject);

            // ?????????????????? ????????????
            DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
            dataInstanceEntity.setOid(dataInstanceOid);
            dataInstanceEntity.setDataContent(dataContentString);
            dataInstanceEntity.setQuestionTraceOid(oid);
            actionTraceBiz.insertActionTraceForCurb(entity,dataInstanceEntity);

            curbDistribute.remove("curb_distribute_detail");

            JSONObject responseObject = new JSONObject();
            responseObject.put("pending_approve_question_id",oid);
            responseObject.put("question_no",entity.getQuestionNo());
            responseObject.put("question_description",entity.getQuestionDescription());
            responseObject.put("return_flag_id",entity.getReturnFlagId());
            responseObject.put("return_flag_name",entity.getReturnFlagName());
            responseObject.put("return_no",entity.getReturnNo());
            responseObject.put("expect_complete_date",entity.getExpectCompleteDate());
            Map<String,Object> user = iamEocBiz.getEmpUserId((String) re.get(0).get("process_person_id"));
            responseObject.put("liable_person_id",user.get("id"));
            responseObject.put("liable_person_name",user.get("name"));
            responseObject.put("empId",re.get(0).get("process_person_id"));
            responseObject.put("empName", re.get(0).get("process_person_name"));
            responseObject.put("pending_approve_question_id",oid);
            responseParam.add(responseObject);

        }
        return responseParam;
    }

    private Map<String, List<Map<String, Object>>> handleCurb(JSONArray curbDistributeDetails){
        List<Map<String, Object>> curbDetails = new ArrayList<>();
        curbDistributeDetails.stream().forEach(pb -> {
            Map<String, Object> rightMap = (Map<String, Object>) pb;
            rightMap.put("process_status","1");
            curbDetails.add(rightMap);
        });

        // ?????????list??????process_person_id????????????
        Map<String, List<Map<String, Object>>> collect = curbDetails.stream().collect(Collectors.groupingBy(this::customKey));
        collect.forEach((k, v) -> {
            System.out.println(k + " -> " + v);
        });

        return collect;
    }

    private  String customKey(Map<String,Object> map){
        return map.get("process_person_id").toString();
    }
}
