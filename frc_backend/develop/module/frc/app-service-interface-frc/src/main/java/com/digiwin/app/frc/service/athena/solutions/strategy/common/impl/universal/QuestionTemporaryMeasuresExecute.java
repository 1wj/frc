package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.universal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWRuntimeException;
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
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.QuestionInfoUniversalModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.TemporaryMeasuresExecuteInfoModel;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.service.DWServiceContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *???????????? ??????????????????
 * @author cds
 * @date 2022/4/7
 * @param
 * @return
 */

public class QuestionTemporaryMeasuresExecute implements QuestionHandlerStrategy {
    @Autowired
    AttachmentMapper attachmentMapper  =  SpringContextHolder.getBean(AttachmentMapper.class);

    /**
     * ????????????(??????new????????????)
     */
    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    UniversalQuestionBiz universalQuestionBiz =  SpringContextHolder.getBean(UniversalQuestionBiz.class);

    @Autowired
    IamEocBiz iamEocBiz =  SpringContextHolder.getBean(IamEocBiz.class);


    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        JSONObject resultJsonObject = JSON.parseObject(parameters);
        //1.string???model??????????????????????????????model
        TemporaryMeasuresExecuteInfoModel containmentMeasureInfoModel = TransferTool.convertString2Model(parameters, TemporaryMeasuresExecuteInfoModel.class);
        //2.????????????
        ParamCheckUtil.checkTemporaryMeauserExcute(containmentMeasureInfoModel);
        //3.?????????????????????
        QuestionInfoUniversalModel questionInfoModel = containmentMeasureInfoModel.getQuestionInfoUniversalModels();
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONObject questionConfirm = resultJsonObject.getJSONObject("question_confirm");
        JSONArray attachmentInfos =  resultJsonObject.getJSONArray("attachment_info");
        JSONArray temporaryMeasureExecute = resultJsonObject.getJSONArray("temporary_measure_execute");

        //4.??????????????????
        return updateDetailInfo(entity,questionConfirm,temporaryMeasureExecute, attachmentInfos,questionInfoModel.getOid());
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    JSONObject updateDetailInfo(QuestionActionTraceEntity questionActionTraceEntity, JSONObject questionConfirm, JSONArray temporaryMeasureExecute, JSONArray attachmentModels, String oid) throws ParseException {
        // ???????????? ???????????????
        // ???????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        // ??????????????? question_result
        JSONObject dataDetail = (JSONObject) resultJsonObject.get("question_result");

        JSONArray containmentMeasure = dataDetail.getJSONArray("temporary_measure");

        // ???containment_measures_execute????????????
        dataDetail.remove("temporary_measure_execute");
        dataDetail.put("temporary_measure_execute",temporaryMeasureExecute);
        dataDetail.put("question_confirm",questionConfirm);

        for (Iterator<Object> iteratorNew = temporaryMeasureExecute.iterator(); iteratorNew.hasNext();) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            JSONObject newObj = (JSONObject)iteratorNew.next();
            String processWorkHours = (String) newObj.get("process_work_hours");
            //????????????????????????????????????
            if (NumberUtil.isNumericByRegEx(processWorkHours)){
                double num=Double.parseDouble(processWorkHours);
                if (num<0){
                    throw new DWRuntimeException("??????????????????????????????");
                }
            }else {
                throw new DWRuntimeException("??????????????????????????????");
            }
            String expectSolveDate = (String) newObj.get("expect_solve_date");
            newObj.put("expect_solve_date", format.format(format.parse(expectSolveDate)).substring(0,10)+" "+formatter.format(new Date()));

        }


        JSONArray attachmentInfos = (JSONArray) dataDetail.get("attachment_info");

        //???????????????????????????
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

        //????????????????????????
        //???????????????????????????????????????
        QuestionDetailVo questionTrace = actionTraceMapper.getQuestionTrace(questionActionTraceEntity.getOid());
        String currentLiablePersonId = questionTrace.getLiablePersonId();
        //???????????????????????????id????????????
        Map<String, List<Map<String, Object>>> listMap = processContainmentMeasureInfos(containmentMeasure);

        for (String key : listMap.keySet()) {
            List<Map<String, Object>> mapList = listMap.get(key);
            //?????????id??????  ????????????????????????
            if(currentLiablePersonId.equals(key)){
                boolean flag = mapList.stream().anyMatch(item -> !StringUtils.isEmpty(item.get("attachment_upload_flag")) && item.get("attachment_upload_flag") == "Y");
                if (flag) {
                    if (mustUploadAttachments.size() == 0) {
                        // ???????????????
                        List<AttachmentEntity> attachmentEntities = attachmentMapper.getAttachments(TenantTokenUtil.getTenantSid(), dataInstanceVo.getOid());
                        if (attachmentEntities.size() == 0) {
                            throw new DWRuntimeException("attachment_upload_flag is Y, so attachment must be uploaded ! ");
                        }
                        break;
                    }
                }
            }

        }

        // ????????????
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE003003");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // ????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // ????????????
        return universalQuestionBiz.handleUpdateForTemporaryMeasuresExecute(questionActionTraceEntity,attachmentEntities,entity);

    }
    private Map<String, List<Map<String, Object>>> processContainmentMeasureInfos(JSONArray containmentMeasure) {
        List<Map<String, Object>> containmentMeasureDetails = new ArrayList<>();
        containmentMeasure.stream().forEach(pb -> {
            Map<String, Object> rightMap = (Map<String, Object>) pb;
            containmentMeasureDetails.add(rightMap);
        });

        // ?????????list??????liable_person_id????????????
        Map<String, List<Map<String, Object>>> collect = containmentMeasureDetails.stream().collect(Collectors.groupingBy(this::customKey));
        return collect;
    }


    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 1-????????????????????????????????????
            List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),QuestionUniversalSolveEnum.temporary_measures.getCode());
            if (CollectionUtils.isEmpty(beforeQuestionVos)) {
                throw new DWRuntimeException( MultilingualismUtil.getLanguage("beforeStepNull"));
            }

            createDataInstance(entity, beforeQuestionVos.get(0), responseParam);
        }
        responseObject.put("return_data",responseParam);
        return responseObject;
    }



    @Override
    public JSONObject handleBack(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // ??????????????????????????????
            List<BeforeQuestionVo> beforeQuestionVo = actionTraceMapper.getBeforeQuestionTraceForIdentity(TenantTokenUtil.getTenantSid(),entity.getQuestionNo(), entity.getQuestionProcessStep(),
                    entity.getQuestionSolveStep());
            // ???????????????????????????
            String dataInstanceOid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            String oid = IdGenUtil.uuid();
            entity.setOid(oid);
            entity.setTenantsid(TenantTokenUtil.getTenantSid());
            // ??????????????????
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
            // ???????????????id???name
            entity.setReturnId((String) DWServiceContext.getContext().getProfile().get("userId"));
            entity.setReturnName((String) DWServiceContext.getContext().getProfile().get("userName"));
            //????????????????????????
            entity.setExpectCompleteDate(beforeQuestionVo.get(0).getExpectCompleteDate());
            DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
            dataInstanceEntity.setOid(dataInstanceOid);
            dataInstanceEntity.setDataContent(beforeQuestionVo.get(0).getDataContent());
            dataInstanceEntity.setQuestionTraceOid(oid);

            actionTraceMapper.insertActionTrace(entity);
            dataInstanceMapper.insertDataInstance(dataInstanceEntity);

            JSONObject object = new JSONObject();
            object.put("pending_approve_question_id",oid);
            responseParam.add(object);


        }
        responseObject.put("return_data",responseParam);
        return responseObject;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    JSONArray createDataInstance(QuestionActionTraceEntity entity, BeforeQuestionVo beforeQuestionVo, JSONArray responseParam) throws Exception {
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(beforeQuestionVo.getDataContent());
        // ??????????????? question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);

        //??????????????????????????????????????????
        DateUtil.assignValueForExpectCompleteTime(entity,dataDetail, QuestionUniversalSolveEnum.temporary_measures.getCode());


        //????????????????????????
        JSONArray temporaryMeasure = dataDetail.getJSONArray("temporary_measure");

        String textNew = JSON.toJSONString(temporaryMeasure, SerializerFeature.DisableCircularReferenceDetect);
        JSONArray jsonArray = JSON.parseArray(textNew);
        dataDetail.put("temporary_measure",jsonArray);
        // plan_arrange ????????????????????????
        String dataInstanceOid;
        String oid;
        //???personId????????????
        Map<String, List<Map<String, Object>>> collect = handleCorrectExecute(temporaryMeasure);
        for (String key : collect.keySet()) {
            List<Map<String, Object>> list = collect.get(key);
            // ??????????????????
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

            //????????????????????????
            List<BeforeQuestionVo> beforeQuestionVosGrey = actionTraceMapper.getHistoricalData(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),QuestionUniversalSolveEnum.temporary_measures_execute_verify.getCode(),questionActionTraceEntities.get(0).getPrincipalStep()+1);
            //??????
            String personId= (String) list.get(0).get("liable_person_id");
            String personName= (String) list.get(0).get("liable_person_name");
            if(!beforeQuestionVosGrey.isEmpty()){
                for (BeforeQuestionVo grey: beforeQuestionVosGrey) {
                    String dataContentGrey= grey.getDataContent();
                    JSONObject resultGrey = JSON.parseObject(dataContentGrey);
                    JSONObject resultMeasure=getDataDetail(resultGrey);
                    //??????????????????????????????????????????
                    JSONArray temporaryGrey = resultMeasure.getJSONArray("temporary_measure_execute_verify");
                    for (Iterator<Object> iterator =temporaryGrey.iterator();iterator.hasNext();){
                        JSONObject greyObject = (JSONObject)iterator.next();
                        if (personId.equals(greyObject.get("liable_person_id").toString())){
                            greyObject.put("is_history_data","Y");
                            greyObject.remove("verify_illustrate");
                            greyObject.remove("verify_status");
                            greyObject.remove("verify_date");
                            list.add(greyObject);
                        }
                    }
                }
            }
            // ?????????-???????????????-?????? entity
            dataInstanceOid = IdGenUtil.uuid();
            oid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            entity.setOid(oid);
            entity.setLiablePersonId(personId);
            entity.setLiablePersonName(personName);
            //??????list
            Collections.reverse(list);
            JSONArray jArray= JSON.parseArray(JSON.toJSONString(list));
            dataDetail.put("temporary_measure_execute",jArray);
            // ??????value???null?????????
            JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
            // jsonObject???string
            String dataContentString = JSON.toJSONString(resultJsonObject);
            // ????????????????????????
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
            Map<String,Object> user = iamEocBiz.getEmpUserId(personId);
            responseObject.put("liable_person_id",user.get("id"));
            responseObject.put("liable_person_name",user.get("name"));
            responseObject.put("employee_id",personId);
            responseObject.put("employee_name",personName);
            responseParam.add(responseObject);
        }
        return responseParam;
    }

    private Map<String, List<Map<String, Object>>> handleCorrectExecute(JSONArray containmentMeasure){
        List<Map<String, Object>> containmentMeasureDetails = new ArrayList<>();
        containmentMeasure.stream().forEach(pb -> {
            Map<String, Object> rightMap = (Map<String, Object>) pb;
            //????????????????????????????????????
            boolean flag=rightMap.containsKey("is_history_data") && "Y".equals(rightMap.get("is_history_data"));
            if (!flag){
                rightMap.put("measure_content",((Map<String, Object>) pb).get("measure_content"));
                rightMap.put("liable_person_id",((Map<String, Object>) pb).get("liable_person_id"));
                rightMap.put("liable_person_name",((Map<String, Object>) pb).get("liable_person_name"));
                rightMap.put("process_department_id",((Map<String, Object>) pb).get("process_department_id"));
                rightMap.put("process_department_name",((Map<String, Object>) pb).get("process_department_name"));
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat format=  new SimpleDateFormat("yyyy-MM-dd");
                String expectSolveDate = (String) ((Map<String, Object>) pb).get("expect_solve_date");
                try {
                    rightMap.put("expect_solve_date", format.format(format.parse(expectSolveDate)).substring(0,10)+" "+formatter.format(new Date()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                rightMap.put("execute_illustrate",((Map<String, Object>) pb).get("corrective_execute_illustrate"));
                rightMap.put("actual_finish_date","");
                rightMap.put("process_work_hours","");
                //????????????
                rightMap.put("is_history_data","N");
                rightMap.put("execute_status","1");
                containmentMeasureDetails.add(rightMap);
            }
        });
        // ?????????list??????liable_person_id????????????
        Map<String, List<Map<String, Object>>> collect = containmentMeasureDetails.stream().collect(Collectors.groupingBy(this::customKey));
        return collect;
    }
    private  String customKey(Map<String,Object> map){
        return map.get("liable_person_id").toString();
    }
    /**
     * ?????????????????????????????????????????????????????????????????????
     * @param resultJsonObject
     * @return
     */
    private JSONObject getDataDetail(JSONObject resultJsonObject) {
        // ??????????????? question_result
        return  resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
    }
}
