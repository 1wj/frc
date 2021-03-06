package com.digiwin.app.frc.service.athena.qdh.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.dao.*;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.Const.qdh.update.QuestionUpdateConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionSolveEnum;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionSolutionEditEntity;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionSolutionEditMapper;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionLiablePersonDepartmentLatitudeConfigBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.IamEocBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.UniversalQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.QuestionDetailVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.SolutionStepVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.util.DateUtil;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ActionTraceBizImpl
 * @Description ??????????????????Biz -impl
 * @Author author
 * @Date 2021/11/11 0:10
 * @Version 1.0
 **/
@Service
public class ActionTraceBizImpl implements ActionTraceBiz {


    @Autowired
    DataInstanceMapper dataInstanceMapper;

    @Autowired
    ActionTraceMapper actionTraceMapper;


    @Autowired
    AttachmentMapper attachmentMapper;

    @Autowired
    IamEocBiz iamEocBiz;


    @Autowired
    private QuestionLiablePersonDepartmentLatitudeConfigBiz latitudeConfigBiz;

    @Autowired
    UniversalQuestionBiz universalQuestionBiz;

    @Autowired
    QuestionSolutionEditMapper questionSolutionEditMapper;

    @Override
    public JSONObject getQuestionDetail(String questionId) {
        // ??????questionId??????????????????
        QuestionDetailVo questionDetailVo =
                actionTraceMapper.getQuestionTrace(questionId);
        if (null == questionDetailVo) {
            // ??????????????????????????????
            return null;
        }
        // ?????????????????????
        JSONObject jsonObject = JSON.parseObject(questionDetailVo.getDataContent());
        //?????? question_result??????????????? ?????????JSONObject  ???????????????   ?????????8D
        Object choiceMethod = jsonObject.get(QuestionResponseConst.QUESTION_RESULT);
        if(choiceMethod instanceof JSONObject){
            JSONObject questionDetail = null;
            try {
                questionDetail = universalQuestionBiz.getQuestionDetailConvertArray(questionId);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return questionDetail;
        }
        // detail???????????????????????????
        JSONObject detail = jsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).getJSONObject(0);


        //?????????  ????????????????????????????????????????????????yyyy-MM-dd
        try {
            processExpectSolveDate(detail);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // ?????????????????????????????????
        JSONArray attachmentInfos = detail.getJSONArray("attachment_info");
        for (Iterator<Object> iterator = attachmentInfos.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            String step = (String) obj.get("attachment_belong_stage");
            obj.put("attachment_belong_stage",MultilingualismUtil.getLanguage(step));
        }
        // ??????question_info
        detail.put(QuestionResponseConst.QUESTION_INFO,packageQuestionInfo(questionId,questionDetailVo));
        // ???????????????????????????????????????
        packageDetail(detail,questionDetailVo);

        return jsonObject;
    }

    private void processExpectSolveDate(JSONObject detail) throws ParseException {
        //????????? planArrange?????????????????????????????????yyyy-MM-dd
        if (detail.containsKey("plan_arrange")) {
            JSONArray planArrange = detail.getJSONArray("plan_arrange");
            for (Iterator iterator = planArrange.iterator(); iterator.hasNext(); ) {
                JSONObject obj = (JSONObject) iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }
        //????????? D3?????????????????????????????????yyyy-MM-dd
        if (detail.containsKey("containment_measure") && detail.get("containment_measure") instanceof JSONArray){
            JSONArray temporaryMeasure = detail.getJSONArray("containment_measure");
            for (Iterator iterator = temporaryMeasure.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }
        if (detail.containsKey("team_build")  && detail.get("team_build") instanceof JSONObject){
            JSONObject teamBuild = detail.getJSONObject("team_build");
            JSONArray planArrange1 = teamBuild.getJSONArray("plan_arrange");
            for (Iterator iterator = planArrange1.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }

        //????????? D3-1?????????????????????????????????yyyy-MM-dd
        if (detail.containsKey("containment_measure_execute") && detail.get("containment_measure_execute") instanceof JSONArray){
            JSONArray temporaryMeasure = detail.getJSONArray("containment_measure_execute");
            for (Iterator iterator = temporaryMeasure.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }
        //????????? D3-2?????????????????????????????????yyyy-MM-dd
        if (detail.containsKey("containment_measure_verify") && detail.get("containment_measure_verify") instanceof JSONArray){
            JSONArray temporaryMeasure = detail.getJSONArray("containment_measure_verify");
            for (Iterator iterator = temporaryMeasure.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }

        //????????? D4&5?????????????????????????????????yyyy-MM-dd
        if (detail.containsKey("corrective_measure") && detail.get("corrective_measure") instanceof JSONArray){
            JSONArray lastingMeasure = detail.getJSONArray("corrective_measure");
            for (Iterator iterator = lastingMeasure.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }
        if (detail.containsKey("containment_measure")  && detail.get("containment_measure") instanceof JSONObject){
            JSONObject planArrangeInfo = detail.getJSONObject("containment_measure");
            JSONArray containmentMeasureDetail = planArrangeInfo.getJSONArray("containment_measure_detail");
            for (Iterator iterator = containmentMeasureDetail.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }
        if (detail.containsKey("containment_measure_verify")  && detail.get("containment_measure_verify") instanceof JSONObject){
            JSONObject planArrangeInfo = detail.getJSONObject("containment_measure_verify");
            JSONArray containmentMeasureVerifyDetail = planArrangeInfo.getJSONArray("containment_measure_verify_detail");
            for (Iterator iterator = containmentMeasureVerifyDetail.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }

        //????????? D5-1?????????????????????????????????yyyy-MM-dd
        if (detail.containsKey("corrective_measure_execute")  && detail.get("corrective_measure_execute") instanceof JSONArray){
            JSONArray lastingMeasureExecute = detail.getJSONArray("corrective_measure_execute");
            for (Iterator iterator = lastingMeasureExecute.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }
        //????????? D6?????????????????????????????????yyyy-MM-dd
        if (detail.containsKey("corrective_measure_verify")  && detail.get("corrective_measure_verify") instanceof JSONArray){
            JSONArray lastingMeasureExecute = detail.getJSONArray("corrective_measure_verify");
            for (Iterator iterator = lastingMeasureExecute.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }

        //????????? D7?????????????????????????????????yyyy-MM-dd
        if (detail.containsKey("prevention_measure") && detail.get("prevention_measure") instanceof JSONArray){
            JSONArray lastingMeasureExecuteVerify = detail.getJSONArray("prevention_measure");
            for (Iterator iterator = lastingMeasureExecuteVerify.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }
        if (detail.containsKey("corrective_measure_verify")  && detail.get("corrective_measure_verify") instanceof JSONObject){
            JSONObject planArrangeInfo = detail.getJSONObject("corrective_measure_verify");
            JSONArray containmentMeasureVerifyDetail = planArrangeInfo.getJSONArray("corrective_measure_verify_detail");
            for (Iterator iterator = containmentMeasureVerifyDetail.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }

        //????????? D7-1??????????????? ??????????????????????????????yyyy-MM-dd
        if (detail.containsKey("prevention_measure_execute") && detail.get("prevention_measure_execute") instanceof JSONArray){
            JSONArray lastingMeasureExecuteVerify = detail.getJSONArray("prevention_measure_execute");
            for (Iterator iterator = lastingMeasureExecuteVerify.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }

        //????????? D7-2??????????????? ??????????????????????????????yyyy-MM-dd
        if (detail.containsKey("prevention_measure_execute_verify") && detail.get("prevention_measure_execute_verify") instanceof JSONArray){
            JSONArray lastingMeasureExecuteVerify = detail.getJSONArray("prevention_measure_execute_verify");
            for (Iterator iterator = lastingMeasureExecuteVerify.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }
        //????????? D8??????????????? ??????????????????????????????yyyy-MM-dd
        if (detail.containsKey("prevention_measure_execute_verify")  && detail.get("prevention_measure_execute_verify") instanceof JSONObject){
            JSONObject planArrangeInfo = detail.getJSONObject("prevention_measure_execute_verify");
            JSONArray containmentMeasureVerifyDetail = planArrangeInfo.getJSONArray("prevention_measure_execute_verify_detail");
            for (Iterator iterator = containmentMeasureVerifyDetail.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_solve_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_solve_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }
        //?????????????????? ??????????????????
        if (detail.containsKey("question_distribute_info")){
            JSONArray planArrangeInfo = detail.getJSONArray("question_distribute_info");
            JSONObject questionDistributeInfo = (JSONObject) planArrangeInfo.get(0);
            JSONArray jsonArrayDetail=questionDistributeInfo.getJSONArray("question_distribute_detail");
            for (Iterator iterator = jsonArrayDetail.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_complete_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_complete_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }
        //?????????????????? ??????????????????
        if (detail.containsKey("question_process_info")){
            JSONArray planArrangeInfo = detail.getJSONArray("question_process_info");
            JSONObject questionDistributeInfo = (JSONObject) planArrangeInfo.get(0);
            JSONArray jsonArrayDetail=questionDistributeInfo.getJSONArray("question_distribute_detail");
            for (Iterator iterator = jsonArrayDetail.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_complete_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_complete_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }
        //?????????????????? ??????????????????
        if (detail.containsKey("curb_distribute_info")){
            JSONArray planArrangeInfo = detail.getJSONArray("curb_distribute_info");
            JSONObject questionDistributeInfo = (JSONObject) planArrangeInfo.get(0);
            JSONArray jsonArrayDetail=questionDistributeInfo.getJSONArray("curb_distribute_detail");
            for (Iterator iterator = jsonArrayDetail.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_complete_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_complete_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }
        //?????????????????? ????????????????????????
        if (detail.containsKey("curb_verify_info")){
            JSONArray planArrangeInfo = detail.getJSONArray("curb_verify_info");
            JSONObject questionDistributeInfo = (JSONObject) planArrangeInfo.get(0);
            JSONArray jsonArrayDetail=questionDistributeInfo.getJSONArray("curb_verify_detail");
            for (Iterator iterator = jsonArrayDetail.iterator(); iterator.hasNext();) {
                JSONObject obj = (JSONObject)iterator.next();
                String expectSolveDate = (String) obj.get("expect_complete_date");
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expectSolveDate);
                obj.put("expect_complete_date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            }
        }


    }

    private ValueFilter filter = (obj, s, v) -> {
        if (null == v) {
            return "";
        }
        return v;
    };


    /**
     * ??????question_info???
     * @param questionId ?????????
     * @param questionDetailVo ????????????????????????
     * @return JSONArray
     */
    private JSONArray packageQuestionInfo(String questionId,QuestionDetailVo questionDetailVo){
        JSONObject object = new JSONObject();
        object.put(QuestionResponseConst.QUESTION_ID,questionId);
        object.put(QuestionResponseConst.QUESTION_PROCESS_STATUS,questionDetailVo.getQuestionProcessStatus());
        object.put(QuestionResponseConst.QUESTION_PROCESS_RESULT,questionDetailVo.getQuestionProcessResult());
        object.put(QuestionResponseConst.QUESTION_NO,questionDetailVo.getQuestionNo());
        object.put(QuestionResponseConst.QUESTION_RECORD_ID,questionDetailVo.getQuestionRecordId());
        object.put(QuestionResponseConst.QUESTION_FLAG_ID,questionDetailVo.getReturnFlagId());
        object.put(QuestionResponseConst.QUESTION_FLAG_NAME,questionDetailVo.getReturnFlagName());
        object.put(QuestionResponseConst.QUESTION_DESCRIPTION,questionDetailVo.getQuestionDescription());
        object.put("liable_person_id",questionDetailVo.getLiablePersonId());
        object.put("liable_person_name",questionDetailVo.getLiablePersonName());
        object.put("liable_person_position_id",questionDetailVo.getLiablePersonPositionId());
        object.put("liable_person_position_name",questionDetailVo.getLiablePersonPositionName());
        object.put(QuestionResponseConst.RETURN_REASON_NO,questionDetailVo.getReturnNo());
        object.put("close_reason",questionDetailVo.getCloseReason());
        object.put("create_date", DateFormatUtils.format(questionDetailVo.getCreateDate(), "yyyy-MM-dd"));
        /**
         * ?????????????????????
         */
        object.put("return_reason",questionDetailVo.getReturnReason());
        object.put("return_step_no",questionDetailVo.getReturnStepNo());

        // null ??? ??????
        String dataContentString = JSON.toJSONString(object, filter);
        // ????????????????????? question_info?????????????????????
        JSONArray result = new JSONArray();
        result.add(JSON.parse(dataContentString));
        return result;
    }

    /**
     * ????????????????????????????????????????????????????????????athena???????????????????????????????????????????????????????????????????????????
     * @param detail data_content??????
     * @param questionDetailVo ??????????????????????????????????????????
     */
    private void packageDetail(JSONObject detail,QuestionDetailVo questionDetailVo) {
        // ????????????????????????
        detail.put(QuestionResponseConst.RETURN_REASON_NO,"");
        // ????????????
        detail.put(QuestionResponseConst.RETURN_REASON,StringUtils.isEmpty(questionDetailVo.getReturnReason())?"":questionDetailVo.getReturnReason());
        // ?????????id
        detail.put("return_id","");
        // ???????????????
        detail.put("return_name","");
        // ????????????????????????????????????
        detail.put("return_flag_name","");
        if (!StringUtils.isEmpty(questionDetailVo.getReturnFlagId())) {
            detail.put("return_step_no",questionDetailVo.getReturnStepNo());
            // ?????????????????????
            detail.put("return_id",questionDetailVo.getReturnId());
            detail.put("return_name",questionDetailVo.getReturnName());
        }
        // ?????????
        detail.put("question_no",questionDetailVo.getQuestionNo());
        // ????????????
        detail.put("question_description",questionDetailVo.getQuestionDescription());
        if (!StringUtils.isEmpty(questionDetailVo.getReturnFlagName())) {
            detail.put("return_flag_name",questionDetailVo.getReturnFlagName());
        }
        // ?????????????????????
        JSONObject basicInfo = detail.getJSONArray("question_basic_info").getJSONObject(0);
        detail.put("question_proposer_id",basicInfo.get("question_proposer_id"));
        detail.put("question_proposer_name",basicInfo.get("question_proposer_name"));
        // ???????????????
        if ("QF".equals(questionDetailVo.getQuestionProcessStep())) {
            detail.put("risk_level","");
            detail.put("urgency","");
        }else {
            JSONArray questionIdentifyInfo = detail.getJSONArray("question_identify_info");
            JSONObject identify = questionIdentifyInfo.getJSONObject(0);
            detail.put("risk_level",identify.get("risk_level_name"));
            detail.put("urgency",identify.get("urgency"));
        }
        // ??????????????????????????????
        detail.put("liable_person_id",questionDetailVo.getLiablePersonId());
        detail.put("liable_person_name",questionDetailVo.getLiablePersonName());
        detail.put("liable_person_position_id",questionDetailVo.getLiablePersonPositionId());
        detail.put("liable_person_position_name",questionDetailVo.getLiablePersonPositionName());

        // ???????????????????????????????????????
        try {
            if (QuestionSolveEnum.question_distribution.getCode().equals(questionDetailVo.getQuestionSolveStep()) ||
                    QuestionSolveEnum.question_curb_distribution.getCode().equals(questionDetailVo.getQuestionSolveStep())){
                //?????????????????????
                List<Map<String,Object>> list = iamEocBiz.getUsers();
                JSONArray processPersonInfos = new JSONArray();
                for (Map<String,Object> map : list) {
                    JSONObject personInfo = new JSONObject();
                    personInfo.put("process_person_id",map.get("id"));
                    personInfo.put("process_person_name",map.get("name"));
                    processPersonInfos.add(personInfo);
                }

                detail.put("process_person_info",processPersonInfos);
            }
            if("QIA".equals(questionDetailVo.getQuestionProcessStep()) || "QIR".equals(questionDetailVo.getQuestionProcessStep())) {
                packageQIPerson(detail);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ???????????????????????????
     * @param detail data_content
     */
    private void packageQIPerson(JSONObject detail) throws JsonProcessingException {
        // ???question_basic_info????????? risk_level_oid???question_source_oid???question_attribution_no???question_attribution_no
        JSONObject basicInfo = detail.getJSONArray("question_basic_info").getJSONObject(0);
        // 4???1????????????????????????null????????? ??????
        //?????????????????????????????????  ??????????????? ????????????????????????  ???????????????????????????????????????????????????
        JSONObject questionIdentifyInfo = detail.getJSONArray("question_identify_info").getJSONObject(0);
        if (StringUtils.isEmpty(questionIdentifyInfo.getString("solution_id"))) {
            // ??????????????????????????????
            List<QuestionSolutionEditEntity> edits = questionSolutionEditMapper.getQuestionSolutionEditInfo(TenantTokenUtil.getTenantSid(), null, null, "Y", "0", null, null);
            if(!edits.isEmpty()){
                QuestionSolutionEditEntity edit = edits.get(0);
                questionIdentifyInfo.put("solution_id",edit.getSolutionNo());
                questionIdentifyInfo.put("solution_name",edit.getSolutionName());
                // ??????????????????????????????????????????
                Map<String, Object> liablePersonMessage = latitudeConfigBiz.getLiablePersonMessage(basicInfo.getString("question_attribution_no"), basicInfo.getString("risk_level_oid"),
                        basicInfo.getString("proposer_department_id"),
                        basicInfo.getString("question_source_oid"), basicInfo.getString("question_classification_oid"), edit.getOid());
                questionIdentifyInfo.put("liable_person_id",liablePersonMessage.get("liablePersonId"));
                questionIdentifyInfo.put("liable_person_name",liablePersonMessage.get("liablePersonName"));
            }
        }
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public int insertActionTrace(QuestionActionTraceEntity entity, QuestionActionTraceEntity beforeQuestion, DataInstanceEntity dataInstanceEntity) {
        actionTraceMapper.insertActionTrace(entity);
        actionTraceMapper.updateActionTrace(beforeQuestion);
        dataInstanceMapper.insertDataInstance(dataInstanceEntity);
        return 1;
    }

    @Override
    public int insertActionTrace(QuestionActionTraceEntity entity, DataInstanceEntity dataInstanceEntity) {
        try {
            actionTraceMapper.insertActionTrace(entity);
            dataInstanceMapper.insertDataInstance(dataInstanceEntity);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public int insertActionTraceForCurb(QuestionActionTraceEntity entity, DataInstanceEntity dataInstanceEntity) {
        actionTraceMapper.insertActionTrace(entity);
        dataInstanceMapper.insertDataInstance(dataInstanceEntity);
        return 1;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject handleUpdateForDistribution(QuestionActionTraceEntity questionActionTraceEntity,List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
        JSONObject resultJsonObject = JSON.parseObject(entity.getDataContent());
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = questionResult.getJSONObject(0);
        //??????????????????
        JSONObject processConfirm = (JSONObject) dataDetail.get("process_confirm");

        int actionTraceResponse = actionTraceMapper.updateActionTrace(questionActionTraceEntity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse =  dataInstanceMapper.updateDataInstance(entity);
        if (updateResponse < 0) {
            throw new DWRuntimeException("update table dataInstance or attachment fail ! ");
        }
        // ??????????????????????????????
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(questionActionTraceEntity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        SimpleDateFormat sdf ;
        if("QA".equals(questionActionTraceEntity.getQuestionProcessStep())){
            sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }else{
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }
        QuestionActionTraceEntity actionEntity = list.get(0);
        result.put("question_id",actionEntity.getOid());
        result.put("question_process_status",actionEntity.getQuestionProcessStatus());
        result.put("question_process_result",actionEntity.getQuestionProcessResult());
        result.put("question_no",actionEntity.getQuestionNo());
        result.put("question_record_id",actionEntity.getQuestionRecordOid());
        result.put("question_description",actionEntity.getQuestionDescription());
        result.put("return_flag_id",StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "":actionEntity.getReturnFlagId());
        result.put("return_flag_name",StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "":actionEntity.getReturnFlagName());
        result.put("return_no",StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        result.put("return_reason",StringUtils.isEmpty(actionEntity.getReturnReason()) ? "" : actionEntity.getReturnReason());
        result.put("return_step_no",StringUtils.isEmpty(actionEntity.getReturnStepNo()) ? "" : actionEntity.getReturnStepNo());
        String str=sdf.format(questionActionTraceEntity.getUpdateTime());
        result.put(QuestionUpdateConst.update_date,str);
        result.put("process_confirm",processConfirm);
        JSONArray newJson = new JSONArray();
        
        newJson.add(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",newJson);
        return jsonObject;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject shortTermUpdate(QuestionActionTraceEntity questionActionTraceEntity,List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity) {
        JSONObject resultJsonObject = JSON.parseObject(entity.getDataContent());
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = questionResult.getJSONObject(0);
        //??????????????????
        JSONObject processConfirm = (JSONObject) dataDetail.get("process_confirm");

        int actionTraceResponse = actionTraceMapper.updateActionTrace(questionActionTraceEntity);
        if (actionTraceResponse < 0) {
            throw new DWRuntimeException("update questionActionTrace fail ! ");
        }
        if (!CollectionUtils.isEmpty(attachmentEntities)) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        int updateResponse =  dataInstanceMapper.updateDataInstance(entity);
        if (updateResponse < 0) {
            throw new DWRuntimeException("update table dataInstance or attachment fail ! ");
        }
        // ??????????????????????????????
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(questionActionTraceEntity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        SimpleDateFormat sdf ;
            sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm");

        QuestionActionTraceEntity actionEntity = list.get(0);
        result.put("question_id",actionEntity.getOid());
        result.put("question_process_status",actionEntity.getQuestionProcessStatus());
        result.put("question_process_result",actionEntity.getQuestionProcessResult());
        result.put("question_no",actionEntity.getQuestionNo());
        result.put("question_record_id",actionEntity.getQuestionRecordOid());
        result.put("question_description",actionEntity.getQuestionDescription());
        result.put("return_flag_id",StringUtils.isEmpty(actionEntity.getReturnFlagId()) ? "":actionEntity.getReturnFlagId());
        result.put("return_flag_name",StringUtils.isEmpty(actionEntity.getReturnFlagName()) ? "":actionEntity.getReturnFlagName());
        result.put("return_no",StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        result.put("return_reason",StringUtils.isEmpty(actionEntity.getReturnReason()) ? "" : actionEntity.getReturnReason());
        result.put("return_step_no",StringUtils.isEmpty(actionEntity.getReturnStepNo()) ? "" : actionEntity.getReturnStepNo());
        String str=sdf.format(questionActionTraceEntity.getUpdateTime());
        result.put(QuestionUpdateConst.update_date,str);
        result.put("process_confirm",processConfirm);
        JSONArray newJson = new JSONArray();

        newJson.add(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",newJson);
        return jsonObject;
    }
    @Override
    public JSONArray insertUnapprovedCurbQuestionTrace(List<QuestionActionTraceEntity> entities) throws Exception {
        // ??????????????? ????????????pending.approve.curb.info.create?????? question_id??????????????????????????????????????????????????????????????????
        // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity actionTraceEntity : entities) {
            QuestionDetailVo questionDetailVo = actionTraceMapper.getQuestionTrace(actionTraceEntity.getOid());
            // ??????????????????
            JSONObject response = createDataInstance1(actionTraceEntity,questionDetailVo);
            responseParam.add(response);
        }
        return responseParam;
    }

    /**
     * ???????????????????????????????????????????????????
     * @param questionDetailVo ?????????????????????
     */
    private JSONObject createDataInstance1(QuestionActionTraceEntity entity,QuestionDetailVo questionDetailVo) throws Exception {

        // ????????????????????????????????? string???json
        JSONObject resultJsonObject = JSON.parseObject(questionDetailVo.getDataContent());
        // ??????????????? question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = (JSONObject) questionResult.get(0);
        //????????????????????????
        DateUtil.assignValueForCommonExpectCompleteTime(entity,dataDetail,QuestionSolveEnum.question_curb_distribution.getCode());

        JSONArray curbDistributeInfo = (JSONArray) dataDetail.get("curb_distribute_info");
        JSONObject curbDistribute = (JSONObject) curbDistributeInfo.get(0);

        JSONArray curbDistributeDetails = (JSONArray) curbDistribute.get("curb_distribute_detail");
        String dataInstanceOid;
        String oid;
        String processPersonId = null;
        String liablePersonName = null;
        for (Iterator<Object> iterator = curbDistributeDetails.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            obj.put("process_status","1");
            processPersonId= obj.getString("process_person_id");
            liablePersonName= obj.getString("process_person_name");
        }

        // ?????????-???????????????-?????? entity
        dataInstanceOid = IdGenUtil.uuid();
        oid = IdGenUtil.uuid();
        entity.setDataInstanceOid(dataInstanceOid);
        entity.setOid(oid);
        entity.setLiablePersonId(processPersonId);
        entity.setLiablePersonName(liablePersonName);
        entity.setReturnId((String) DWServiceContext.getContext().getProfile().get("userId"));
        entity.setReturnName((String) DWServiceContext.getContext().getProfile().get("userName"));
        // ??????????????????
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

        JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
        // jsonObject???string
        String dataContentString = JSON.toJSONString(resultJsonObject);

        // ?????????????????? ????????????
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        dataInstanceEntity.setOid(dataInstanceOid);
        dataInstanceEntity.setDataContent(dataContentString);
        dataInstanceEntity.setQuestionTraceOid(oid);
        insertActionTraceForCurb(entity,dataInstanceEntity);

        JSONObject responseObject = new JSONObject();
        responseObject.put("pending_approve_question_id",oid);
        responseObject.put("question_no",entity.getQuestionNo());
        responseObject.put("question_description",entity.getQuestionDescription());
        responseObject.put("return_flag_id",entity.getReturnFlagId());
        responseObject.put("return_flag_name",entity.getReturnFlagName());
        responseObject.put("expect_complete_date",entity.getExpectCompleteDate());
        //????????????userID,?????????0??????
        Map<String,Object> user = iamEocBiz.getEmpUserId(processPersonId);
        responseObject.put("liable_person_id",user.get("id"));
        responseObject.put("liable_person_name",user.get("name"));
        responseObject.put("empId",processPersonId);
        responseObject.put("empName",liablePersonName);
        responseObject.put("pending_approve_question_id",oid);

        return responseObject;
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONArray insertReturnBackDetail(QuestionActionTraceEntity entity) {
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
        List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),null,entity.getQuestionNo());
        entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
        // ???????????????id???name
        entity.setReturnId((String) DWServiceContext.getContext().getProfile().get("userId"));
        entity.setReturnName((String) DWServiceContext.getContext().getProfile().get("userName"));
        //????????????????????????
        String solveStep = entity.getQuestionSolveStep();
        if(!StringUtils.isEmpty(solveStep)){
            JSONObject jsonObject = JSON.parseObject(beforeQuestionVo.get(0).getDataContent());
            JSONObject dataDetail = jsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).getJSONObject(0);
            if("SE002002".equals(solveStep)){
                DateUtil.assignValueForCommonExpectCompleteTime(entity,dataDetail,QuestionSolveEnum.question_curb_distribution.getCode());
            }
            if("SE002001".equals(solveStep)){
                DateUtil.assignValueForCommonExpectCompleteTime(entity,dataDetail,QuestionSolveEnum.question_distribution.getCode());
            }
            if("SE002005".equals(solveStep)){
                DateUtil.assignValueForCommonExpectCompleteTime(entity,dataDetail,QuestionSolveEnum.question_close.getCode());
            }
        }
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        dataInstanceEntity.setOid(dataInstanceOid);
        dataInstanceEntity.setDataContent(beforeQuestionVo.get(0).getDataContent());
        dataInstanceEntity.setQuestionTraceOid(oid);

        actionTraceMapper.insertActionTrace(entity);
        dataInstanceMapper.insertDataInstance(dataInstanceEntity);

        JSONArray responseParam = new JSONArray();
        JSONObject responseObject = new JSONObject();
        responseObject.put("pending_approve_question_id",oid);
        responseParam.add(responseObject);
        return responseParam;
    }

    @Override
    public JSONObject getSolutionStep(String solutionOid) throws JsonProcessingException {
        List<SolutionStepVo> solutionStepVos = actionTraceMapper.getSolutionStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),solutionOid);
        JSONArray result =new JSONArray();
        for (SolutionStepVo solutionStepVo : solutionStepVos) {
            String detailInfoVoJson = new ObjectMapper().writeValueAsString(solutionStepVo);
            result.add(JSON.parse(detailInfoVoJson));
        }
        JSONObject response = new JSONObject();
        response.put("solution_step_info",result);
        return response;
    }
}
