package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.eightD;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.Const.qdh.update.QuestionUpdateConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.Question8DSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.EightDQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.KeyReasonCorrectModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.QuestionInfo8DSecondModel;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.frc.service.athena.util.rqi.EocUtils;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.digiwin.app.frc.service.athena.util.ReuseConstant.YEAR_MONTH_DAY;

/**
 * @ClassName QuestionKeyReasonCorrect
 * @Description ????????????&???????????? ????????????
 * @Author HeX
 * @Date 2022/3/8 3:16
 * @Version 1.0
 **/
public class QuestionKeyReasonCorrect implements QuestionHandlerStrategy {
    /**
     * ????????????(??????new????????????)
     */
    @Autowired
    AttachmentMapper attachmentMapper  =  SpringContextHolder.getBean(AttachmentMapper.class);

    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    EightDQuestionBiz eightDQuestionBiz =  SpringContextHolder.getBean(EightDQuestionBiz.class);



    @Override
    public JSONObject updateQuestion(String parameters) throws Exception{

        JSONObject resultJsonObject = JSON.parseObject(parameters);

        //1.string???model??????????????????????????????model
        KeyReasonCorrectModel keyReasonCorrectModel = TransferTool.convertString2Model(parameters, KeyReasonCorrectModel.class);

        //2.????????????
        try {
            ParamCheckUtil.checkkeyReasonCorrectParams(keyReasonCorrectModel);
        } catch (DWArgumentException e) {
            e.printStackTrace();
        }

        //3.?????????????????????
        QuestionInfo8DSecondModel questionInfoModel = keyReasonCorrectModel.getQuestionInfos().get(0);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONArray keyReasonAnalysis = resultJsonObject.getJSONArray("key_reason_analysis");
        JSONArray correctiveAction = resultJsonObject.getJSONArray("corrective_measure");
        JSONArray attachmentInfo=resultJsonObject.getJSONArray("attachment_info");
        //4.??????????????????
        return updateDetailInfo(entity,keyReasonAnalysis,correctiveAction,attachmentInfo,questionInfoModel.getOid());
    }

    private JSONObject updateDetailInfo(QuestionActionTraceEntity entity, JSONArray keyReasonAnalysis, JSONArray correctiveAction, JSONArray attachmentModels, String oid) throws OperationException {
        // ?????????????????????????????????????????????????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        if (dataInstanceVo == null) {
            throw new OperationException("???????????????id??????????????????????????????id");
        }
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        JSONObject dataDetail = getDataDetail(resultJsonObject);
        JSONArray planManagementInfo = dataDetail.getJSONArray("plan_arrange");
        //????????????????????????????????????????????????????????????
        JSONArray  questionBasicInfo=dataDetail.getJSONArray(QuestionUpdateConst.question_basic_info);

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(ReuseConstant.YEAR_MONTH_DAY);
        for (Iterator<Object> iterator =questionBasicInfo.iterator();iterator.hasNext();){
            JSONObject qObject = (JSONObject)iterator.next();
            for (Iterator<Object> ite =correctiveAction.iterator();ite.hasNext();){
                JSONObject correctObject = (JSONObject)ite.next();
                try {
                    String correctDate =correctObject.get(ReuseConstant.expect_solve_date).toString();
                    String qDate=qObject.get(ReuseConstant.expect_solve_date).toString();
                    long timestamp = simpleDateFormat.parse(qDate).getTime();
                    long correctTimestamp = simpleDateFormat.parse(correctDate).getTime();
                    if (correctTimestamp>timestamp){
                        throw new OperationException("??????????????????: "+correctDate+"?????????????????????????????????: "+qDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }


        //????????????????????????
        dataDetail.remove("key_reason_analysis");
        dataDetail.put("key_reason_analysis",keyReasonAnalysis);

        //?????????????????????????????????
        DateUtil.measures(correctiveAction);
        //????????????????????????
        dataDetail.remove("corrective_measure");
        dataDetail.put("corrective_measure",correctiveAction);
        // ????????????
        JSONArray attachmentInfos = (JSONArray) dataDetail.get("attachment_info");
        JSONArray mustUploadAttachments = new JSONArray();
        //??????????????????????????????????????????
        boolean repeatCheckFlag = false;
        for (Iterator<Object> iterator = attachmentModels.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            boolean status = true;
            for (Iterator<Object> it = attachmentInfos.iterator();it.hasNext();) {
                JSONObject attach = (JSONObject)it.next();
                if(!repeatCheckFlag){
                    repeatCheckFlag = "SE001007".equals(attach.getString("attachment_belong_stage"));
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
        //????????????????????????
        for (Iterator<Object> ite = planManagementInfo.iterator(); ite.hasNext();) {
            JSONObject obj = (JSONObject)ite.next();
            //?????????????????? ??? ???????????? ????????????????????? ???????????????????????????????????????????????????????????????????????????
            if ("SE001005".equals(obj.get("step_no"))) {
                if (obj.get("attachment_upload_flag").equals("Y")) {
                    if (Collections.isEmpty(mustUploadAttachments)) {
                        // ???????????????
                        List<AttachmentEntity> attachmentEntities = attachmentMapper.getAttachments((Long) DWServiceContext.getContext().getProfile().get("tenantSid"), dataInstanceVo.getOid());
                        if (Collections.isEmpty(attachmentEntities) && !repeatCheckFlag) {
                            throw new DWRuntimeException("attachment_upload_flag is Y, so attachment must be uploaded ! ");
                        }
                        break;
                    }
                }
            }
        }

        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),Question8DSolveEnum.key_reason_correct.getCode());
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());

        // ????????????
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,dataInstanceEntity);

        return eightDQuestionBiz.keyReasonCorrectBuilder(entity,attachmentEntities,dataInstanceEntity);
    }

    private JSONObject getDataDetail(JSONObject resultJsonObject) {
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        return questionResult.getJSONObject(0);
    }


    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) {
        List<Map<String, Object>> list=new ArrayList<>();
        for (QuestionActionTraceEntity entity:actionTraceEntityList) {
            // 1-???????????????????????????????????? ??? ????????????????????????
            List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(),Question8DSolveEnum.containment_measure.getCode());
            // 2-???????????????????????????
            String dataInstanceOid = IdGenUtil.uuid();
            String oid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            entity.setOid(oid);
            // ??????????????????
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

            // 4-??????????????????
            DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
            try {
                dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVos,entity);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            actionTraceBiz.insertActionTrace(entity,dataInstanceEntity);
            // 5-??????response??????
            Map<String,Object> hashMap=new HashMap<>();
            String userId = TenantTokenUtil.getUserId();
            hashMap.put("pending_approve_question_id",oid);
            hashMap.put(QuestionUpdateConst.question_no,questionActionTraceEntities.get(0).getQuestionNo());
            hashMap.put(QuestionUpdateConst.question_description,questionActionTraceEntities.get(0).getQuestionDescription());
            hashMap.put(ReuseConstant.liable_person_id,questionActionTraceEntities.get(0).getLiablePersonId());
            hashMap.put(ReuseConstant.liable_person_name,questionActionTraceEntities.get(0).getLiablePersonName());
            hashMap.put("return_flag_id",questionActionTraceEntities.get(0).getReturnFlagId());
            hashMap.put("return_flag_name",questionActionTraceEntities.get(0).getReturnFlagName());
            hashMap.put("expect_complete_date",questionActionTraceEntities.get(0).getExpectCompleteDate());
            try {
                hashMap.put("employee_id", EocUtils.getEmpId(userId));
                hashMap.put("employee_name",EocUtils.getEmpName(userId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            list.add(hashMap);
        }
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("return_data",list);
        return jsonObject;
    }

    @Override
    public JSONObject handleBack(List<QuestionActionTraceEntity> actionTraceEntityList)  {
        return null;
    }



    /**
     * ???????????????????????????????????????????????????
     * @param oid ???????????????????????????????????? ??????
     * @param dataInstanceOid ???????????????????????????????????????????????? ??????
     */
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid, List<BeforeQuestionVo> beforeQuestionVos,QuestionActionTraceEntity traceEntity) throws JsonProcessingException {

        String dataContent = beforeQuestionVos.get(0).getDataContent();

        // ??????????????????QF???????????????
        JSONObject resultJsonObject = JSON.parseObject(dataContent);
        // ??????????????? question_result[].get(0)
        JSONObject dataDetail = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).getJSONObject(0);
        //  ????????????????????????????????????
        JSONObject processContainmentMeasure = new JSONObject();
        //?????? ???????????? -?????? ????????????+?????????+????????????
        processContainmentMeasure.put(ReuseConstant.expect_solve_date,beforeQuestionVos.get(0).getLiablePersonId());
        processContainmentMeasure.put(ReuseConstant.liable_person_name,beforeQuestionVos.get(0).getLiablePersonName());
        if (null != beforeQuestionVos.get(0).getActualCompleteDate()) {
            SimpleDateFormat formatter1 = new SimpleDateFormat(YEAR_MONTH_DAY);
            String dateString1 = formatter1.format(beforeQuestionVos.get(0).getActualCompleteDate());
            processContainmentMeasure.put("process_date",dateString1);
        }else {
            processContainmentMeasure.put("process_date","");
        }
        // plan_arrange ????????????????????????
        JSONArray planContainmentMeasure = dataDetail.getJSONArray("containment_measure");
        dataDetail.remove("containment_measure");
        processContainmentMeasure.put("containment_measure_detail",planContainmentMeasure);
        dataDetail.put("containment_measure",processContainmentMeasure);

        //???????????????????????? ?????????????????????????????????
        if (dataDetail.containsKey("containment_measure_verify")){
            JSONObject verifyDetailInfo = new JSONObject();
            //?????? ???????????? -?????? ????????????+?????????+????????????
            verifyDetailInfo.put(ReuseConstant.liable_person_id,beforeQuestionVos.get(0).getLiablePersonId());
            verifyDetailInfo.put(ReuseConstant.liable_person_name,beforeQuestionVos.get(0).getLiablePersonName());
            if (null != beforeQuestionVos.get(0).getActualCompleteDate()) {
                SimpleDateFormat formatter = new SimpleDateFormat(ReuseConstant.YEAR_MONTH_DAY);
                String dateString = formatter.format(beforeQuestionVos.get(0).getActualCompleteDate());
                verifyDetailInfo.put("process_date",dateString);
            }else {
                verifyDetailInfo.put("process_date","");
            }
            // plan_arrange ????????????????????????
            JSONArray measureVerify = dataDetail.getJSONArray("containment_measure_verify");
            dataDetail.remove("containment_measure_verify");
            verifyDetailInfo.put("containment_measure_verify_detail",measureVerify);
            dataDetail.put("containment_measure_verify",verifyDetailInfo);
        }


        //?????? ???????????????????????????
        JSONArray keyReasonAnalysis = new JSONArray();
        JSONObject keyReasonAnalysisInfo = new JSONObject();
        JSONArray correctiveAction = new JSONArray();
        keyReasonAnalysisInfo.put("outflow_reason","");
        keyReasonAnalysisInfo.put("output_reason","");
        keyReasonAnalysisInfo.put("system_reason","");
        keyReasonAnalysis.add(keyReasonAnalysisInfo);
        dataDetail.put("key_reason_analysis",keyReasonAnalysis);

        dataDetail.put("corrective_measure",correctiveAction);
        // ??????value???null?????????
        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        //???traceEntity??????????????????????????????
        DateUtil.assignValueForExpectCompleteTime(traceEntity,dataDetail,Question8DSolveEnum.key_reason.getCode());
        // null ??? ??????
        String dataContentString = JSON.toJSONString(resultJsonObject, filter);
        // ?????????????????? ????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        entity.setOid(dataInstanceOid);
        entity.setDataContent(dataContentString);
        entity.setQuestionTraceOid(oid);
        return entity;
    }

    private ValueFilter filter = (obj, s, v) -> {
        if (null == v) {
            return "";
        }
        return v;
    };
}
