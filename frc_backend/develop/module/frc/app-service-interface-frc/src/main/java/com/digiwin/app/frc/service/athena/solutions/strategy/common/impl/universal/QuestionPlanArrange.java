package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.universal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.config.annotation.ParamValidationHandler;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionSolutionEditEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionSolutionMeasureEntity;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionSolutionEditMapper;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionSolutionMeasureMapper;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.UniversalQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.EightDQuestionMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.PlanArrangeModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.QuestionInfoUniversalModel;
import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.PendingQuestionVo;
import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.PersonPendingNumVo;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: xieps
 * @Date: 2022/4/6 15:14
 * @Version 1.0
 * @Description ?????????????????? D1&D2&D3-????????????????????????
 */
public class QuestionPlanArrange implements QuestionHandlerStrategy {
    @Autowired
    private QuestionSolutionMeasureMapper questionSolutionMeasureMapper =  SpringContextHolder.getBean(QuestionSolutionMeasureMapper.class);

    @Autowired
    private QuestionSolutionEditMapper questionSolutionEditMapper = SpringContextHolder.getBean(QuestionSolutionEditMapper.class);

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
    EightDQuestionMapper eightDQuestionMapper = SpringContextHolder.getBean(EightDQuestionMapper.class);


    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {

        JSONObject resultJsonObject = JSON.parseObject(parameters);

        //1.string???model??????????????????????????????model
        PlanArrangeModel planArrangeModel = TransferTool.convertString2Model(parameters, PlanArrangeModel.class);

        //2.????????????
        ParamCheckUtil.checkPlanArrangeParams(planArrangeModel);

        //3.?????????????????????
        QuestionInfoUniversalModel questionInfoModel = planArrangeModel.getQuestionInfos();
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONObject questionConfirm = resultJsonObject.getJSONObject("question_confirm");
        JSONObject reasonAnalysis = resultJsonObject.getJSONObject("reason_analysis");
        JSONArray planManagementInfo = resultJsonObject.getJSONArray("plan_arrange");
        JSONArray attachmentInfos =  resultJsonObject.getJSONArray("attachment_info");
        JSONObject questionInfo = resultJsonObject.getJSONObject("question_info");
        //4.??????????????????
        return updateDetailInfo(entity,questionInfo,questionConfirm,reasonAnalysis,planManagementInfo,attachmentInfos,questionInfoModel.getOid());
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 1-????????????????????????????????????
            List<BeforeQuestionVo> beforeQuestionVos;
            // ??????skip
            ParamValidationHandler.validateParams(entity);
            if ("2".equals(entity.getSkip())) {
                beforeQuestionVos = actionTraceMapper.getBeforeQuestionTraceForIdentity(TenantTokenUtil.getTenantSid(),entity.getQuestionNo(), QuestionUpdateEnum.question_identification.getCode(), null);
            }else {
                // ???????????????????????????????????????(??????????????????????????????)
                beforeQuestionVos = actionTraceMapper.getBeforeQuestionTraceForIdentity(TenantTokenUtil.getTenantSid(),entity.getQuestionNo(),QuestionUpdateEnum.question_identification_review.getCode(), null);
            }
            // 2-???????????????????????????
            String dataInstanceOid = IdGenUtil.uuid();
            String oid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            entity.setOid(oid);

            // 3-??????????????????
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStepForIdentity(TenantTokenUtil.getTenantSid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

            // 4-??????????????????
            DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVos,entity);
            actionTraceBiz.insertActionTrace(entity,dataInstanceEntity);

            // 5-??????response??????
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


    /**
     * ??????????????????????????????
     * @param dataInstanceOid   ???????????????????????????????????????????????? ??????
     * @param oid ???????????????????????????????????? ??????
     * @param beforeQuestionVos ?????????????????? ????????????
     * @return
     */
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid, List<BeforeQuestionVo> beforeQuestionVos,QuestionActionTraceEntity  traceEntity) throws Exception {
        String dataContent = beforeQuestionVos.get(0).getDataContent();

        //??????????????????????????????????????????????????????
        JSONObject resultJsonObject = JSON.parseObject(dataContent);

        //??????????????? question_result[].get(0)
        JSONObject questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).getJSONObject(0);

        //??????question_result ?????????????????????????????????????????? ?????????????????????????????????????????????Object????????????
        resultJsonObject.remove(QuestionResponseConst.QUESTION_RESULT);
        String textNew = JSON.toJSONString(questionResult, SerializerFeature.DisableCircularReferenceDetect);
        JSONObject jsonObject = JSON.parseObject(textNew);
        resultJsonObject.put(QuestionResponseConst.QUESTION_RESULT,new JSONArray().add(jsonObject));
        JSONObject restructuredData = restructuredData(questionResult);

        //??????  ???????????? reason_analysis ???  ???????????? plan_management ?????? ?????????????????? question_confirm
        JSONObject reasonAnalysis = new JSONObject();
        reasonAnalysis.put("reason_analysis_description","");
        restructuredData.put("reason_analysis",reasonAnalysis);
        JSONObject questionConfirm = new JSONObject();
        questionConfirm.put("status","");
        questionConfirm.put("return_step_no","");
        questionConfirm.put("return_reason","");
        restructuredData.put("question_confirm",questionConfirm);
        //??????????????????????????????????????????
        JSONArray planManagement = new JSONArray();
        QuestionSolutionEditEntity editEntity = questionSolutionEditMapper.getQuestionSolutionEditInfoByEditNo(TenantTokenUtil.getTenantSid(), "SE003");
        List<QuestionSolutionMeasureEntity> measureEntities = questionSolutionMeasureMapper.queryMeasureInfoByEditOid(editEntity.getOid(), TenantTokenUtil.getTenantSid());
        for (QuestionSolutionMeasureEntity measureEntity : measureEntities) {
            //???????????????????????????????????????
            if("SE003001".equals(measureEntity.getMeasureNo())){
                traceEntity.setExpectCompleteDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(DateUtil.getExpectDateWithHourMinute(measureEntity.getExpectCompleteTime())));
            }else{
                JSONObject object = new JSONObject();
                object.put("step_no",measureEntity.getMeasureNo());
                object.put("step_name",measureEntity.getMeasureName());
                //D6????????? ????????????????????????????????????
                if("SE003009".equals(measureEntity.getMeasureNo())){
                    String liablePersonId = traceEntity.getLiablePersonId();
                    Map<String, Object> map = EocUtils.getEmpIdForMap2(liablePersonId);
                    JSONArray depts = (JSONArray) map.get("depts");
                    JSONObject obj = (JSONObject) depts.get(0);
                    object.put("liable_person_id",traceEntity.getLiablePersonId());
                    object.put("liable_person_name",traceEntity.getLiablePersonName());
                    object.put("process_department_id",obj.get("id"));
                    object.put("process_department_name",obj.get("name"));
                }else{
                    String liablePersonId = measureEntity.getPrincipalId();
                    Map<String, Object> map = EocUtils.getEmpIdForMap2(liablePersonId);
                    JSONArray depts = (JSONArray) map.get("depts");
                    JSONObject obj = (JSONObject) depts.get(0);
                    object.put("liable_person_id",liablePersonId);
                    object.put("liable_person_name",measureEntity.getPrincipalName());
                    object.put("process_department_id",obj.get("id"));
                    object.put("process_department_name",obj.get("name"));
                }
                //????????????????????????????????????????????????
                if("SE003002".equals(measureEntity.getMeasureNo())){
                    String expectDate = DateUtil.getExpectDateWithHourMinute(measureEntity.getExpectCompleteTime());
                    JSONObject questionBasicInfo = restructuredData.getJSONObject("question_basic_info");
                    String expectSolveDate = questionBasicInfo.getString("expect_solve_date");
                    long expectSolveDateTime = new SimpleDateFormat("yyyy-MM-dd").parse(expectSolveDate).getTime();
                    object.put("expect_solve_date",expectSolveDateTime >
                            System.currentTimeMillis() ? expectDate : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(expectSolveDateTime)));
                }else{
                    object.put("expect_solve_date", DateUtil.getExpectDateWithHourMinute(measureEntity.getExpectCompleteTime()));
                }
                object.put("attachment_upload_flag","N");
                PersonPendingNumVo personPendingNumVo = eightDQuestionMapper.queryPersonPendingQuestionNum(traceEntity.getLiablePersonId(), TenantTokenUtil.getTenantSid());
                object.put("human_bottleneck_analysis",String.valueOf(personPendingNumVo.getPendingNums()));
                planManagement.add(object);
            }
        }
        restructuredData.put("plan_arrange",planManagement);

        // null ??? ??????
        JSONObject result = new JSONObject();
        result.put("question_result",restructuredData);
        String dataContentString = JSON.toJSONString(result, filter);
        // ????????????????????????????????????????????????
        DataInstanceEntity entity = new DataInstanceEntity();
        entity.setOid(dataInstanceOid);
        entity.setDataContent(dataContentString);
        entity.setQuestionTraceOid(oid);
        return entity;
    }

    /**
     * ??????????????????????????? ????????????????????????????????????????????? ?????????????????????
     *
     * @param questionResult
     * @return JSONObject
     */
    private JSONObject restructuredData(JSONObject questionResult) {
        JSONObject newResultJsonObject = new JSONObject();
        JSONObject questionIdentifyInfo = questionResult.getJSONArray("question_identify_info").getJSONObject(0);
        JSONObject questionBasicInfo = questionResult.getJSONArray("question_basic_info").getJSONObject(0);
        JSONObject questionDetailInfo = questionResult.getJSONArray("question_detail_info").getJSONObject(0);
        JSONArray attachmentInfo = questionResult.getJSONArray("attachment_info");
        newResultJsonObject.put("question_identify_info",questionIdentifyInfo);
        newResultJsonObject.put("question_basic_info",questionBasicInfo);
        newResultJsonObject.put("question_detail_info",questionDetailInfo);
        newResultJsonObject.put("attachment_info",attachmentInfo);
        return newResultJsonObject;
    }

    /**
     * null????????? ??????
     */
    private ValueFilter filter = (obj, s, v) -> {
        if (null == v) {
            return "";
        }
        return v;
    };



    private JSONObject updateDetailInfo(QuestionActionTraceEntity entity, JSONObject questionInfo,JSONObject questionConfirm,JSONObject reasonAnalysis, JSONArray planManagementInfo,JSONArray attachmentModels, String oid) {
        // ?????????????????????????????????????????????????????????????????????
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        JSONObject dataDetail = getDataDetail(resultJsonObject);

        //????????????????????????
        dataDetail.remove("reason_analysis");
        dataDetail.put("reason_analysis",reasonAnalysis);

        //????????????????????????  ???????????? ????????? ???????????????
        JSONArray beforePlanArrange = dataDetail.getJSONArray("plan_arrange");
        for (Iterator<Object> iterator = beforePlanArrange.iterator(); iterator.hasNext();) {
            JSONObject beforeObj = (JSONObject)iterator.next();
            for (Iterator<Object> iteratorNew = planManagementInfo.iterator(); iteratorNew.hasNext();) {
                JSONObject newObj = (JSONObject)iteratorNew.next();
                if(beforeObj.get("step_no").equals(newObj.get("step_no"))){
                    String beforeExpectSolveDate = (String)beforeObj.get("expect_solve_date");
                    String expectSolveDate = (String) newObj.get("expect_solve_date");
                    String substring = beforeExpectSolveDate.substring(10);
                    expectSolveDate = expectSolveDate+substring;
                    newObj.put("expect_solve_date", expectSolveDate);
                    break;
                }
            }
        }
        dataDetail.remove("plan_arrange");
        dataDetail.put("plan_arrange",planManagementInfo);

        //????????????????????????
        dataDetail.remove("question_confirm");
        dataDetail.put("question_confirm",questionConfirm);

        // ????????????
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

        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE003001");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());

        // ????????????
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,dataInstanceEntity);

        return universalQuestionBiz.handleUpdateForPlanArrange(entity,attachmentEntities,dataInstanceEntity);
    }

    /**
     * ?????????????????????????????????????????????????????????????????????
     * @param resultJsonObject
     * @return
     */
    private JSONObject getDataDetail(JSONObject resultJsonObject) {
        // ??????????????? question_result
        return resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
    }
}
