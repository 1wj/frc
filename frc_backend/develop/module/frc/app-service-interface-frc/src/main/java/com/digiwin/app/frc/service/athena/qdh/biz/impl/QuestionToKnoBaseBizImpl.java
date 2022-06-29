package com.digiwin.app.frc.service.athena.qdh.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.frc.service.athena.common.Const.SolutionStepConstant;
import com.digiwin.app.frc.service.athena.config.annotation.ParamValidationHandler;
import com.digiwin.app.frc.service.athena.qdh.biz.QuestionToKnoBaseBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.model.toBase.*;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.util.DmcClient;
import com.digiwin.app.frc.service.athena.util.RequestClient;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.TransferTool;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @ClassName QuestionToKnoBase
 * @Description 问题快反入库服务
 * @Author author
 * @Date 2022/1/21 10:51
 * @Version 1.0
 **/
@Service
public class QuestionToKnoBaseBizImpl implements QuestionToKnoBaseBiz {

    private final Logger logger = LoggerFactory.getLogger(QuestionToKnoBaseBizImpl.class);


    @Autowired
    DataInstanceMapper dataInstanceMapper;

    private final static List<KnowledgeSourceModel> sourceList = new ArrayList<>();
    private final static List<KnowledgeTypeModel> typeList = new ArrayList<>();
    private final static List<KnowledgeTagModel> tagList = new ArrayList<>();



    static {
        sourceList.add(new KnowledgeSourceModel("FRC"));
        typeList.add(new KnowledgeTypeModel("62398b654e4b8105462c6da8","LB1005"));
        typeList.add(new KnowledgeTypeModel("62398b6e4e4b8105462c6da9","LB1005"));
        tagList.add(new KnowledgeTagModel("62398bcd4e4b8105462c6dad"));
        tagList.add(new KnowledgeTagModel("62398bdd4e4b8105462c6dae"));
    }


    @Override
    public JSONObject questionToBase(String questionNo,JSONObject dataDetail) throws JsonProcessingException {
        // 1.处理知识公共字段
        KnowledgeModel knowledgeModel = processPublicParam(dataDetail,questionNo);
        // 2.获取自定义信息
        JSONObject knowledgeCustomize = processCustomize(dataDetail);
        // 3.远程推知识中台
        JSONObject knowObject = JSONObject.parseObject(new ObjectMapper().writeValueAsString(knowledgeModel));
        knowObject.put("knowledge_customize",knowledgeCustomize);
        return requestKMO(JSON.toJSONString(knowObject));
    }

    @Override
    public void dataToKmo(String oid) throws JsonProcessingException {
        // 根据主键查询数据
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        if (Objects.isNull(dataInstanceVo)) {
            // todo 是否需要记录
            return;
        }
        JSONObject dataDetail= JSON.parseObject(dataInstanceVo.getDataContent()).getJSONArray("question_result").getJSONObject(0);
        // 1.处理知识公共字段
        KnowledgeModel knowledgeModel = processPublicParam(dataDetail,dataInstanceVo.getQuestionNo());
        // 2.获取自定义信息
        JSONObject knowledgeCustomize = processCustomize(dataDetail);
        // 3.远程推知识中台
        JSONObject knowObject = JSON.parseObject(new ObjectMapper().writeValueAsString(knowledgeModel));
        knowObject.put("knowledge_customize",knowledgeCustomize);
        logger.info("即将请求kmo,传入数据");
        requestKMO(JSON.toJSONString(knowObject));
    }


    /**
     * 处理知识公共字段
     * @param dataDetail 问题信息json
     * @param questionNo 问题号
     */
    private KnowledgeModel processPublicParam(JSONObject dataDetail,String questionNo){
        String data = dataDetail.getString("question_basic_info");
        BasicModel basicModel = null;
        if(!isJsonArray(data)){
            basicModel = TransferTool.convertString2Model(dataDetail.getJSONObject("question_basic_info").toString(), BasicModel.class);
        }else{
            basicModel = TransferTool.convertString2Model(dataDetail.getJSONArray("question_basic_info").getString(0), BasicModel.class);
        }
        String userName = TenantTokenUtil.getUserName();
        // 获取基础信息 string 转 model
        //BasicModel basicModel = TransferTool.convertString2Model(dataDetail.getJSONArray("question_basic_info").getString(0), BasicModel.class);
        // 参数校验
        ParamValidationHandler.validateParams(basicModel);
        KnowledgeModel knowledgeModel = new KnowledgeModel();
        // 知识编号
        knowledgeModel.setKnowledgeNo(questionNo);
        //知识作者
        knowledgeModel.setKnowledgeAuthor(userName);
        // 知识名称
        knowledgeModel.setKnowledgeName(basicModel.getQuestionDescription());
        // 知识描述
        knowledgeModel.setKnowledgeDesc(basicModel.getQuestionDescription());

        List<KnowledgeTypeModel> types = new ArrayList<>();
        types.add(new KnowledgeTypeModel(basicModel.getQuestionClassificationOId(),basicModel.getQuestionClassificationNo()));

        // todo 假数据
        knowledgeModel.setKnowledgeSourceModels(sourceList);
        if(!CollectionUtils.isEmpty(types)){
            knowledgeModel.setKnowledgeTypeModels(types);
        }else {
            knowledgeModel.setKnowledgeTypeModels(typeList);
        }
        knowledgeModel.setKnowledgeTagModels(tagList);
        // 抽取附件，处理附件
        List<AttachmentModel> attachmentModels = processAttachment(dataDetail.getJSONArray("attachment_info"));
        if (!CollectionUtils.isEmpty(basicModel.getPictureModels())) {
            basicModel.getPictureModels().stream().forEach(e ->{
                Map fileInfo = DmcClient.getFileInfo(e.getPictureId(),DWApplicationConfigUtils.getProperty("dmcFRCBucket"));
                attachmentModels.add(new AttachmentModel(e.getPictureId(),DWApplicationConfigUtils.getProperty("dmcFRCBucket"),"","",fileInfo));
            });
        }
        knowledgeModel.setAttachmentModels(attachmentModels);
        return knowledgeModel;
    }

    /**
     * 抽取附件，处理附件
     * @param attachments
     * @return
     */
    private List<AttachmentModel> processAttachment(JSONArray attachments){
        if (attachments.size() == 0) {
            return new ArrayList<>();
        }
        // 只获取 attachment_id
        List<AttachmentModel> attachmentModels = new ArrayList<>();
        for (Iterator iterator = attachments.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            //请求dmc
            Map fileInfo = DmcClient.getFileInfo(obj.getString("attachment_id"),DWApplicationConfigUtils.getProperty("dmcFRCBucket"));
            attachmentModels.add(new AttachmentModel(obj.getString("attachment_id"),"FRC",
                    obj.getString("upload_person_id"),obj.getString("upload_person_name"),fileInfo));
        }
        return attachmentModels;
    }



    public static boolean isJsonArray(String content) {
        if(StringUtils.isBlank(content)) {
            return false;
        }
        try {
            JSONArray jsonStr = JSONArray.parseArray(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 处理自定义
     * @param knowledgeCustomize
     */
    private JSONObject processCustomize(JSONObject knowledgeCustomize){
        BasicModel basicModel = null;
        JSONObject identifyInfo = null;
        basicModel = TransferTool.convertString2Model(knowledgeCustomize.getJSONArray("question_basic_info").getString(0), BasicModel.class);
        identifyInfo = knowledgeCustomize.getJSONArray("question_identify_info").getJSONObject(0);
        knowledgeCustomize.put("knowledge_description",basicModel.getQuestionDescription());
        // 抽取问题解决方案信息
        knowledgeCustomize.put("solution_id",identifyInfo.get("solution_id"));
        JSONArray attachmentInfos = knowledgeCustomize.getJSONArray("attachment_info");
        for (int i = 0; i < attachmentInfos.size(); i ++){
            JSONObject attachmentInfo = attachmentInfos.getJSONObject(i);
            attachmentInfo.put("attachment_belong_stage_name", SolutionStepConstant.SOLUTION_STEP_MAP.get(attachmentInfo.getString("attachment_belong_stage")));
        }
        knowledgeCustomize.put("solution_name",identifyInfo.get("solution_name"));
        //生成预览图片url和列印报告下载url
        String previewUrl =  DWApplicationConfigUtils.getProperty("dmcUrl") + "/api/dmc/v2/file/FRC/preview/";
        String downloadUrl = DWApplicationConfigUtils.getProperty("dmcUrl") + "/api/dmc/v2/file/FRC/download/";
        JSONObject basicInfo = knowledgeCustomize.getJSONArray("question_basic_info").getJSONObject(0);
        JSONArray questionPicture = basicInfo.getJSONArray("question_picture");
        for(int i = 0 ; i < questionPicture.size();i++){
            JSONObject picture = questionPicture.getJSONObject(i);
            picture.put("picture_url",previewUrl + picture.getString("picture_id"));
        }
        JSONArray acceptanceInfo = knowledgeCustomize.getJSONArray("question_acceptance_info");
        if (acceptanceInfo != null){
            for(int i = 0 ; i < acceptanceInfo.size();i++){
                JSONObject printReport = acceptanceInfo.getJSONObject(i);
                printReport.put("print_report_preview_url",previewUrl + printReport.getString("print_report_id"));
                printReport.put("print_report_download_url",downloadUrl + printReport.getString("print_report_id"));
            }
        }
        switch (identifyInfo.getString("solution_id")){
            case "SE001": processCustomize8D(knowledgeCustomize);break;
            case "SE002": processCustomizeCommon(knowledgeCustomize);break;
            case "SE003": processCustomizeUniversal(knowledgeCustomize);break;
        }
        return knowledgeCustomize;
    }

    private JSONObject processCustomizeCommon(JSONObject knowledgeCustomize) {
        JSONObject questionProcessInfo = knowledgeCustomize.getJSONArray("question_process_info").getJSONObject(0);
        if(questionProcessInfo != null){
            questionProcessInfo.remove("process_date");
            JSONArray questionDistributeDetail = questionProcessInfo.getJSONArray("question_distribute_detail");
            if(questionDistributeDetail != null){
                for(int i = 0 ; i < questionDistributeDetail.size() ; i++){
                    JSONObject questionDetail = questionDistributeDetail.getJSONObject(i);
                    questionDetail.remove("attachment_upload_flag");
                    questionDetail.remove("expect_complete_date");
                }
            }
        }
        JSONObject curbVerifyInfo = knowledgeCustomize.getJSONArray("curb_verify_info").getJSONObject(0);
        if(curbVerifyInfo != null){
            curbVerifyInfo.remove("process_date");
            JSONArray curbVerifyDetail = curbVerifyInfo.getJSONArray("curb_verify_detail");
            if(curbVerifyDetail != null){
                for(int i = 0 ; i < curbVerifyDetail.size() ; i++){
                    JSONObject curbDetail = curbVerifyDetail.getJSONObject(i);
                    curbDetail.remove("edit_type");
                    curbDetail.remove("uuid");
                    curbDetail.remove("expect_complete_date");
                    curbDetail.remove("actual_complete_date");
                    curbDetail.remove("process_status");
                }
            }
        }
        JSONArray questionClosure = knowledgeCustomize.getJSONArray("question_closure");
        if(questionClosure != null) {
            for(int i = 0 ; i < questionClosure.size() ; i++){
                JSONObject questionClosureJSONObject = questionClosure.getJSONObject(i);
                questionClosureJSONObject.remove("actual_complete_date");
                questionClosureJSONObject.remove("expect_complete_date");
                questionClosureJSONObject.remove("process_date");
            }
        }
        JSONObject questionDistributeInfo = knowledgeCustomize.getJSONArray("question_distribute_info").getJSONObject(0);
        if(questionDistributeInfo != null) {
            JSONArray questionDistributeDetail2 = questionDistributeInfo.getJSONArray("question_distribute_detail");
            if(questionDistributeDetail2 != null) {
                for(int i = 0 ; i < questionDistributeDetail2.size() ; i++){
                    JSONObject questionDetail = questionDistributeDetail2.getJSONObject(i);
                    questionDetail.remove("attachment_upload_flag");
                    questionDetail.remove("expect_complete_date");
                }
            }
        }
        return knowledgeCustomize;
    }


    private JSONObject processCustomize8D(JSONObject knowledgeCustomize) {
        //去除计划安排中的预计完成时间、附件必传标识、人力瓶颈分析
        JSONObject teamBuild = knowledgeCustomize.getJSONObject("team_build");
        if(teamBuild != null) {
            teamBuild.remove("process_date");
            JSONArray planArrange = teamBuild.getJSONArray("plan_arrange");
            if(planArrange != null) {
                for(int i = 0 ; i < planArrange.size() ; i++){
                    JSONObject plan = planArrange.getJSONObject(i);
                    plan.remove("expect_solve_date");
                    plan.remove("attachment_upload_flag");
                    plan.remove("human_bottleneck_analysis");
                }
            }
        }
        JSONArray planArrange = knowledgeCustomize.getJSONArray("plan_arrange");
        if(planArrange != null) {
            for(int i = 0 ; i < planArrange.size() ; i++){
                JSONObject plan = planArrange.getJSONObject(i);
                plan.remove("expect_solve_date");
                plan.remove("attachment_upload_flag");
                plan.remove("human_bottleneck_analysis");
            }
        }
        //去除围堵措施中的预计完成时间、附件必传标识、UUID
        JSONObject containmentMeasure = knowledgeCustomize.getJSONObject("containment_measure");
        if(containmentMeasure != null) {
            containmentMeasure.remove("process_date");
            JSONArray containmentMeasureDetail = containmentMeasure.getJSONArray("containment_measure_detail");
            if(containmentMeasureDetail != null){
                for(int i = 0 ; i < containmentMeasureDetail.size() ; i++){
                    JSONObject containment = containmentMeasureDetail.getJSONObject(i);
                    containment.remove("expect_solve_date");
                    containment.remove("attachment_upload_flag");
                    containment.remove("uuid");
                }
            }
        }
        //去除围堵措施执行验证中的实际完成时间、uuid、围堵状态、验收时间、验收状态
        JSONObject containmentMeasureVerify = knowledgeCustomize.getJSONObject("containment_measure_verify");
        if(containmentMeasureVerify != null) {
            containmentMeasureVerify.remove("process_date");
            JSONArray containmentMeasureVerifyDetail =containmentMeasureVerify.getJSONArray("containment_measure_verify_detail");
            if(containmentMeasureVerifyDetail != null) {
                for(int i = 0 ; i < containmentMeasureVerifyDetail.size() ; i++){
                    JSONObject containmentVerify = containmentMeasureVerifyDetail.getJSONObject(i);
                    containmentVerify.remove("actual_complete_date");
                    containmentVerify.remove("actual_finish_date");
                    containmentVerify.remove("expect_solve_date");
                    containmentVerify.remove("uuid");
                    containmentVerify.remove("question_id");
                    containmentVerify.remove("containment_status");
                    containmentVerify.remove("process_work_hours");
                    containmentVerify.remove("verify_date");
                    containmentVerify.remove("verify_status");
                }
            }
        }
        //去除纠正措施中的非入库字段
        JSONArray correctiveMeasure = knowledgeCustomize.getJSONArray("corrective_measure");
        if(correctiveMeasure != null) {
            for(int i = 0 ; i < correctiveMeasure.size() ; i++){
                JSONObject corrective = correctiveMeasure.getJSONObject(i);
                corrective.remove("expect_solve_date");
                corrective.remove("edit_type");
                corrective.remove("attachment_upload_flag");
                corrective.remove("complete_status");
                corrective.remove("uuid");
                corrective.remove("uibot_checked");
            }
        }
        //去除纠正措施执行中的非入库字段
        JSONArray correctiveMeasureExecute = knowledgeCustomize.getJSONArray("corrective_measure_execute");
        if(correctiveMeasureExecute != null) {
            for(int i = 0 ; i < correctiveMeasureExecute.size() ; i++){
                JSONObject correctiveExecute = correctiveMeasureExecute.getJSONObject(i);
                correctiveExecute.remove("process_work_hours");
                correctiveExecute.remove("expect_solve_date");
                correctiveExecute.remove("corrective_date");
                correctiveExecute.remove("edit_type");
                correctiveExecute.remove("attachment_upload_flag");
                correctiveExecute.remove("complete_status");
                correctiveExecute.remove("uuid");
                correctiveExecute.remove("uibot_checked");
            }
        }
        //去除纠正措施执行验证中的非入库字段
        JSONObject correctiveMeasureVerify =   knowledgeCustomize.getJSONObject("corrective_measure_verify");
        if(correctiveMeasureVerify != null) {
            correctiveMeasureVerify.remove("process_date");
            JSONArray correctiveMeasureVerifyDetail = correctiveMeasureVerify.getJSONArray("corrective_measure_verify_detail");
            if(correctiveMeasureVerifyDetail != null) {
                for(int i = 0 ; i < correctiveMeasureVerifyDetail.size() ; i++){
                    JSONObject correctiveVerify = correctiveMeasureVerifyDetail.getJSONObject(i);
                    correctiveVerify.remove("process_work_hours");
                    correctiveVerify.remove("expect_solve_date");
                    correctiveVerify.remove("verify_date");
                    correctiveVerify.remove("corrective_status");
                    correctiveVerify.remove("verify_status");
                    correctiveVerify.remove("process_work_date");
                    correctiveVerify.remove("uuid");
                    correctiveVerify.remove("uibot_checked");
                    correctiveVerify.remove("question_id");
                }
            }
        }
        //去除预防措施中的非入库字段
        JSONArray preventionMeasure = knowledgeCustomize.getJSONArray("prevention_measure");
        if(preventionMeasure != null) {
            for(int i = 0 ; i < preventionMeasure.size() ; i++){
                JSONObject prevention = preventionMeasure.getJSONObject(i);
                prevention.remove("expect_solve_date");
                prevention.remove("attachment_upload_flag");
                prevention.remove("uuid");
            }
        }
        //去除预防措施执行中的非入库字段
        JSONArray preventionMeasureExecute = knowledgeCustomize.getJSONArray("prevention_measure_execute");
        if(preventionMeasureExecute != null) {
            for(int i = 0 ; i < preventionMeasureExecute.size() ; i++){
                JSONObject preventionExecute = preventionMeasureExecute.getJSONObject(i);
                preventionExecute.remove("process_work_hours");
                preventionExecute.remove("expect_solve_date");
                preventionExecute.remove("execute_status");
                preventionExecute.remove("attachment_upload_flag");
                preventionExecute.remove("complete_date");
                preventionExecute.remove("uuid");
            }
        }
        //去除预防措施执行验证中的非入库字段
        JSONObject preventionMeasureVerify = knowledgeCustomize.getJSONObject("prevention_measure_execute_verify");
        if(preventionMeasureVerify != null) {
            preventionMeasureVerify.remove("process_date");
            JSONArray preventionMeasureVerifyDetail = preventionMeasureVerify.getJSONArray("prevention_measure_execute_verify_detail");
            if(preventionMeasureVerifyDetail != null) {
                for(int i = 0 ; i < preventionMeasureVerifyDetail.size() ; i++){
                    JSONObject preventionVerify = preventionMeasureVerifyDetail.getJSONObject(i);
                    preventionVerify.remove("process_work_hours");
                    preventionVerify.remove("expect_solve_date");
                    preventionVerify.remove("execute_status");
                    preventionVerify.remove("attachment_upload_flag");
                    preventionVerify.remove("verify_date");
                    preventionVerify.remove("uuid");
                    preventionVerify.remove("question_id");
                    preventionVerify.remove("process_work_date");
                    preventionVerify.remove("verify_status");
                }
            }
        }
        JSONObject keySeason = knowledgeCustomize.getJSONObject("key_reason_analysis");
        if(keySeason != null){
            keySeason.remove("process_date");
        }
        JSONObject processConfirm = knowledgeCustomize.getJSONObject("process_confirm");
        if(processConfirm != null){
            processConfirm.remove("confirm_date");
        }
        return knowledgeCustomize;
    }

    private JSONObject processCustomizeUniversal(JSONObject knowledgeCustomize) {
        //去除计划安排中的预计完成时间、附件必传标识、人力瓶颈分析
        JSONObject planArrangeInfo= knowledgeCustomize.getJSONObject("plan_arrange_info");
        if(planArrangeInfo != null) {
            planArrangeInfo.remove("process_date");
            JSONArray planArrange = planArrangeInfo.getJSONArray("plan_arrange");
            if(planArrange != null) {
                for(int i = 0 ; i < planArrange.size() ; i++){
                    JSONObject plan = planArrange.getJSONObject(i);
                    plan.remove("expect_solve_date");
                    plan.remove("attachment_upload_flag");
                    plan.remove("human_bottleneck_analysis");
                    plan.remove("human_bottleneck_analysis_desc");
                }
            }
        }
        JSONArray planArrange = knowledgeCustomize.getJSONArray("plan_arrange");
        if(planArrange != null) {
            for (int i = 0; i < planArrange.size(); i++) {
                JSONObject plan = planArrange.getJSONObject(i);
                plan.remove("expect_solve_date");
                plan.remove("attachment_upload_flag");
                plan.remove("human_bottleneck_analysis");
                plan.remove("human_bottleneck_analysis_desc");
            }
        }
        //去除临时措施验证中的多余字段
        JSONObject temporaryMeasureExecuteVerify= knowledgeCustomize.getJSONObject("temporary_measure_execute_verify");
        if(temporaryMeasureExecuteVerify != null){
            temporaryMeasureExecuteVerify.remove("process_date");
            JSONArray temporaryMeasureExecuteVerifyDetail = temporaryMeasureExecuteVerify.getJSONArray("temporary_measure_execute_verify_detail");
            if(temporaryMeasureExecuteVerifyDetail != null){
                for(int i = 0 ; i < temporaryMeasureExecuteVerifyDetail.size() ; i++){
                    JSONObject temporaryDetailJSONObject = temporaryMeasureExecuteVerifyDetail.getJSONObject(i);
                    temporaryDetailJSONObject.remove("process_work_hours");
                    temporaryDetailJSONObject.remove("expect_solve_date");
                    temporaryDetailJSONObject.remove("actual_finish_date");
                    temporaryDetailJSONObject.remove("human_bottleneck_analysis");
                    temporaryDetailJSONObject.remove("human_bottleneck_analysis_desc");
                    temporaryDetailJSONObject.remove("execute_status");
                    temporaryDetailJSONObject.remove("edit_type");
                    temporaryDetailJSONObject.remove("attachment_upload_flag");
                    temporaryDetailJSONObject.remove("verify_date");
                    temporaryDetailJSONObject.remove("uuid");
                    temporaryDetailJSONObject.remove("is_history_data");
                    temporaryDetailJSONObject.remove("verify_status");
                }
            }
        }
        //去除恒久措施验证中的多余字段
        JSONObject lastingMeasureExecuteVerify= knowledgeCustomize.getJSONObject("lasting_measure_execute_verify");
        if(lastingMeasureExecuteVerify != null){
            lastingMeasureExecuteVerify.remove("process_date");
            JSONArray lastingMeasureExecuteVerifyJSONArray = lastingMeasureExecuteVerify.getJSONArray("lasting_measure_execute_verify_detail");
            if(lastingMeasureExecuteVerifyJSONArray != null){
                for(int i = 0 ; i < lastingMeasureExecuteVerifyJSONArray.size() ; i++){
                    JSONObject temporaryDetailJSONObject = lastingMeasureExecuteVerifyJSONArray.getJSONObject(i);
                    temporaryDetailJSONObject.remove("process_work_hours");
                    temporaryDetailJSONObject.remove("expect_solve_date");
                    temporaryDetailJSONObject.remove("actual_finish_date");
                    temporaryDetailJSONObject.remove("human_bottleneck_analysis");
                    temporaryDetailJSONObject.remove("execute_status");
                    temporaryDetailJSONObject.remove("verify_date");
                    temporaryDetailJSONObject.remove("uuid");
                    temporaryDetailJSONObject.remove("question_id");
                    temporaryDetailJSONObject.remove("is_history_data");
                    temporaryDetailJSONObject.remove("verify_status");
                }
            }
        }
        //去除处理确认的多余字段
        JSONObject processConfirmVerify = knowledgeCustomize.getJSONObject("process_confirm_verify");
        if(processConfirmVerify != null)
            processConfirmVerify.remove("verify_date");
        return knowledgeCustomize;
    }

    /**
     * 远程调取KMO
     * @return 返回参数 问题号+问题描述
     */
    private JSONObject requestKMO(String param){
        JSONObject result = null;
        try {
//            result = RequestClient.request(DWApplicationConfigUtils.getProperty("kmoUrl"),"8b1d59ee-7782-4771-b2b9-8b039972ac9b",param,JSONObject.class);
            result = RequestClient.request(DWApplicationConfigUtils.getProperty("kmoUrl"),DWServiceContext.getContext().getToken(),param,JSONObject.class);

//            result = RequestClient.request(DWApplicationConfigUtils.getProperty("kmoUrl"),DWServiceContext.getContext().getToken(),param,JSONObject.class);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }
}
