package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.eightD;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.Const.solutions.eightD.PreventionMeasureExecuteConst;
import com.digiwin.app.frc.service.athena.common.Const.solutions.eightD.ResponseConst;
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
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.PreventionMeasureExecuteInfoModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.QuestionInfo8DModel;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName QuestionPreventionMeasureExecute
 * @Description ?????????????????? ??????
 * @Author HeX
 * @Date 2022/3/18 15:51
 * @Version 1.0
 **/
public class QuestionPreventionMeasureExecute implements QuestionHandlerStrategy {

    @Autowired
    AttachmentMapper attachmentMapper  =  SpringContextHolder.getBean(AttachmentMapper.class);

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
    public JSONObject updateQuestion(String parameters) throws DWArgumentException, IOException {
        //1.string???model??????????????????????????????model
        PreventionMeasureExecuteInfoModel preventionMeasureExecuteInfoModel = TransferTool.convertString2Model(parameters, PreventionMeasureExecuteInfoModel.class);
        //2. ????????????
        ParamCheckUtil.checkPreventionMeasureExecuteParams(preventionMeasureExecuteInfoModel);
        //3.?????????????????????
        QuestionInfo8DModel questionInfoModel = preventionMeasureExecuteInfoModel.getQuestionInfos().get(0);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);
        // ????????????????????????
        entity.setActualCompleteDate(new Date());

        JSONObject resultJsonObject = JSON.parseObject(parameters);
        JSONArray preventionMeasuresExecute = resultJsonObject.getJSONArray("prevention_measure_execute");
        JSONArray attachmentInfos = resultJsonObject.getJSONArray("attachment_info");
        return handleDetail(entity,preventionMeasuresExecute,attachmentInfos);
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList){
        if (CollectionUtils.isEmpty(actionTraceEntityList)) {
            throw new DWRuntimeException("list index is 0 ");
        }
        QuestionActionTraceEntity entity = actionTraceEntityList.get(0);
        // ?????????????????? ??????
        List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),
                entity.getQuestionRecordOid(),entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), Question8DSolveEnum.precaution.getCode());

        //D3-2
        List<BeforeQuestionVo> threeTwo = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),Question8DSolveEnum.containment_measure_verify.getCode());
        if (CollectionUtils.isEmpty(beforeQuestionVos)) {
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("beforeStepNull"));
        }
        // ????????????
        return new JSONObject().fluentPut("return_data",createDataInstance(entity, beforeQuestionVos.get(0),threeTwo));
    }

    /**
     * ?????? ????????????????????????
     * @param entity
     * @param beforeQuestionVo
     * @return
     * @throws Exception
     */
    private JSONArray createDataInstance(QuestionActionTraceEntity entity, BeforeQuestionVo beforeQuestionVo,List<BeforeQuestionVo> dThreeVo){
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(beforeQuestionVo.getDataContent());
        // ??????????????? question_result
        JSONObject dataDetail = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).getJSONObject(0);
        // ?????????????????? table
        JSONArray preventionMeasure = dataDetail.getJSONArray(PreventionMeasureExecuteConst.prevention_measure);
        dataDetail.remove(PreventionMeasureExecuteConst.prevention_measure);
        //????????????????????????
        String textNew = JSON.toJSONString(preventionMeasure, SerializerFeature.DisableCircularReferenceDetect);
        JSONArray jsonArray = JSON.parseArray(textNew);
        dataDetail.put("prevention_measure",jsonArray);
        // ???????????????????????? ????????????????????????table,remove
        // /20220421?????? ?????????????????????????????????????????????
        // ???????????????????????????table,??????????????????id??????
        Map<String, List<Map<String, Object>>> collect = handlePreventionMeasure(preventionMeasure);
        if (!Collections.isEmpty(dThreeVo)) {
            //D3-2????????????
            JSONObject dThreeDetail = JSON.parseObject(dThreeVo.get(0).getDataContent());
            JSONArray dThree = dThreeDetail.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
            JSONObject three = dThree.getJSONObject(0);

            //???????????????????????? ?????????????????????????????????
            if (three.containsKey("containment_measure_verify")) {
                JSONObject verifyDetailInfo = new JSONObject();
                //?????? ???????????? -?????? ????????????+?????????+????????????
                verifyDetailInfo.put("liable_person_id", dThreeVo.get(0).getLiablePersonId());
                verifyDetailInfo.put("liable_person_name", dThreeVo.get(0).getLiablePersonName());
                if (null != dThreeVo.get(0).getActualCompleteDate()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    String dateString = formatter.format(dThreeVo.get(0).getActualCompleteDate());
                    verifyDetailInfo.put("process_date", dateString);
                } else {
                    verifyDetailInfo.put("process_date", "");
                }
                // ????????????????????????
                JSONArray measureVerify = three.getJSONArray("containment_measure_verify");
                dataDetail.remove("containment_measure_verify");
                verifyDetailInfo.put("containment_measure_verify_detail", measureVerify);
                dataDetail.put("containment_measure_verify", verifyDetailInfo);
            }
        }
        // ?????????response
        JSONArray responseParam = new JSONArray();
        for (Map.Entry<String, List<Map<String, Object>>> m : collect.entrySet()) {
            List<Map<String, Object>> re = collect.get(m.getKey());
            // ?????????-???????????????-?????? entity
            QuestionActionTraceEntity initTraceEntity = initQuestionActionEntity(entity,re);
            //+ ??????????????????
            DateUtil.assignValueForExpectCompleteTime(entity,dataDetail,Question8DSolveEnum.precaution.getCode());

            // ????????????????????????table
            dataDetail.put(PreventionMeasureExecuteConst.prevention_measure_execute,re);
            // ??????value???null?????????
            JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
            // ?????????-dataInstance
            DataInstanceEntity dataInstanceEntity = initDataInstance(JSON.toJSONString(resultJsonObject),initTraceEntity);
            // dao
            actionTraceBiz.insertActionTraceForCurb(entity,dataInstanceEntity);
            // init -??????
            responseParam.add(initResponse(initTraceEntity,re));
        }
        return responseParam;
    }

    /**
     * ????????? ??????????????????table
     * @param preventionMeasure ??????????????????
     * @return ??????????????????table??????
     */
    private Map<String, List<Map<String, Object>>> handlePreventionMeasure(JSONArray preventionMeasure){
        List<Map<String, Object>> containmentMeasureDetails = new ArrayList<>();
        preventionMeasure.stream().forEach(e -> {
            Map<String, Object> rightMap = (Map<String, Object>) e;
            // ?????????-???????????????
            rightMap.put(PreventionMeasureExecuteConst.execute_status,"1");
            // ?????????-????????????????????????
            rightMap.put(PreventionMeasureExecuteConst.prevention_measure_execute_illustrate,"");
            // ?????????-????????????
            rightMap.put(PreventionMeasureExecuteConst.complete_date,"");
            containmentMeasureDetails.add(rightMap);
        });
        // ?????????list??????liable_person_id????????????
        return containmentMeasureDetails.stream().collect(Collectors.groupingBy(this::customKey));
    }

    /**
     * ?????? ?????????id
     * @param map table??????
     * @return ?????????id
     */
    private  String customKey(Map<String,Object> map){
        return map.get(PreventionMeasureExecuteConst.liable_person_id).toString();
    }

    /**
     * ???????????????????????????-question_action_trace???
     * @param entity ????????????
     * @return ??????????????????
     */
    private QuestionActionTraceEntity initQuestionActionEntity(QuestionActionTraceEntity entity,List<Map<String, Object>> re){
        String dataInstanceOid = IdGenUtil.uuid();
        String oid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        entity.setOid(oid);
        entity.setLiablePersonId((String) re.get(0).get(PreventionMeasureExecuteConst.liable_person_id));
        entity.setLiablePersonName((String) re.get(0).get(PreventionMeasureExecuteConst.liable_person_name));
        // ??????????????????
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
        return entity;
    }

    /**
     * ????????????????????????
     * @param dataContentString data_content
     * @param entity action_trace?????????
     * @return DataInstanceEntity
     */
    private DataInstanceEntity initDataInstance(String dataContentString,QuestionActionTraceEntity entity){
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        dataInstanceEntity.setOid(entity.getDataInstanceOid());
        dataInstanceEntity.setDataContent(dataContentString);
        dataInstanceEntity.setQuestionTraceOid(entity.getOid());
        return dataInstanceEntity;
    }

    /**
     * init ??????
     * @param entity question_action_trace??????
     * @param re ??????????????????table
     * @return ??????-???DTD??????
     */
    private JSONObject initResponse(QuestionActionTraceEntity entity,List<Map<String, Object>> re){
        JSONObject responseObject = new JSONObject();
        responseObject.put(ResponseConst.pending_approve_question_id,entity.getOid());
        responseObject.put(ResponseConst.question_no,entity.getQuestionNo());
        responseObject.put(ResponseConst.question_description,entity.getQuestionDescription());
        responseObject.put(ResponseConst.return_flag_id,entity.getReturnFlagId());
        responseObject.put(ResponseConst.return_flag_name,entity.getReturnFlagName());
        responseObject.put(ResponseConst.expect_solve_date,entity.getExpectCompleteDate());
        //????????????userID 0??????
        try {
            Map<String,Object> user = iamEocBiz.getEmpUserId((String) re.get(0).get(ResponseConst.liable_person_id));
            responseObject.put(ResponseConst.liable_person_id,user.get("id"));
            responseObject.put(ResponseConst.liable_person_name,user.get("name"));
        }catch (Exception e) {
            e.printStackTrace();
        }
        responseObject.put(ResponseConst.employee_id,re.get(0).get(ResponseConst.liable_person_id));
        responseObject.put(ResponseConst.employee_name, re.get(0).get(ResponseConst.liable_person_name));
        return responseObject;
    }

    /**
     * ????????????????????????
     * @param preventionMeasuresExecute ?????? ?????????????????????
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONArray preventionMeasuresExecute,JSONArray attachmentModels) throws IOException {
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(questionActionTraceEntity.getOid());
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        // ??????????????? question_result
        JSONObject dataDetail = resultJsonObject.getJSONArray("question_result").getJSONObject(0);
        JSONArray preventionMeasure = dataDetail.getJSONArray("prevention_measure");
        //?????????????????????????????????
        DateUtil.measures(preventionMeasuresExecute);
        dataDetail.remove("prevention_measure_execute");
        dataDetail.put("prevention_measure_execute",preventionMeasuresExecute);

        JSONArray attachmentInfos = dataDetail.getJSONArray("attachment_info");

        //?????? ???????????????????????????????????????
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
        Map<String, List<Map<String, Object>>> listMap = processPreventionMeasureInfos(preventionMeasure);

        for (String key : listMap.keySet()) {
            List<Map<String, Object>> mapList = listMap.get(key);
            //?????????id??????  ????????????????????????
            if(currentLiablePersonId.equals(key)){
                boolean flag = mapList.stream().anyMatch(item -> !StringUtils.isEmpty(item.get("attachment_upload_flag")) && item.get("attachment_upload_flag") == "Y");
                if (flag) {
                    if (Collections.isEmpty(mustUploadAttachments)){
                        // ???????????????
                        List<AttachmentEntity> attachmentEntities = attachmentMapper.getAttachments((Long) DWServiceContext.getContext().getProfile().get("tenantSid"), dataInstanceVo.getOid());
                        if (Collections.isEmpty(attachmentEntities)) {
                            throw new DWRuntimeException("attachment_upload_flag is Y, so attachment must be uploaded ! ");
                        }
                        break;
                    }
                }
            }

        }

        // ????????????
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),
                dataInstanceVo.getOid(),Question8DSolveEnum.precaution_execute.getCode());
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // ????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // ????????????
        return eightDQuestionBiz.handleUpdateForPreventionMeasureExecute(questionActionTraceEntity,attachmentEntities,entity);
    }


    @Override
    public JSONObject handleBack(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        return null;
    }

    private Map<String, List<Map<String, Object>>> processPreventionMeasureInfos(JSONArray preventionMeasure) {
        List<Map<String, Object>> preventionMeasureDetails = new ArrayList<>();
        preventionMeasure.stream().forEach(pb -> {
            Map<String, Object> rightMap = (Map<String, Object>) pb;
            preventionMeasureDetails.add(rightMap);
        });

        // ?????????list??????liable_person_id????????????
        Map<String, List<Map<String, Object>>> collect = preventionMeasureDetails.stream().collect(Collectors.groupingBy(this::customKey));
        collect.forEach((k, v) -> {
            System.out.println(k + " -> " + v);
        });
        return collect;
    }



}
