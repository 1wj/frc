package com.digiwin.app.frc.service.athena.util.qdh;

import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.config.annotation.ParamValidationHandler;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QA.IdentifyUpdateModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QuestionFeedbackModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.QuestionRecordModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.UnapprovedModel;
import com.digiwin.app.frc.service.athena.config.annotation.ValidationHandler;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.other.QuestionInfoModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.*;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.*;
import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.KeyReasonCorrectVo;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @ClassName ParamCheckUtil
 * @Description TODO
 * @Author author
 * @Date 2021/11/16 0:42
 * @Version 1.0
 **/
public class ParamCheckUtil {
    /**
     * 注解参数校验
     * @param objects
     */
    public static void checkParamsForAnnotation(List<Object> objects) {
        objects.stream().forEach(e->{
            ParamValidationHandler.validateParams(e);
        });
    }


    /**
     * 更新问题反馈 参数校验
     * @param questionFeedbackModel 更新问题反馈重要参数
     */
    public static void checkQuestionUpdateParams(QuestionFeedbackModel questionFeedbackModel) {
        if (StringUtils.isEmpty(questionFeedbackModel.getOid())) {
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("questionRecordId"));
        }
    }

    /**
     * 校验问题分析 更新入参
     * @param identifyUpdateModel
     * @throws DWArgumentException
     */
    public static void checkQAParams(IdentifyUpdateModel identifyUpdateModel) throws DWArgumentException {
        if (CollectionUtils.isEmpty(identifyUpdateModel.getQuestionInfos())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        QuestionInfoModel questionInfoModel = identifyUpdateModel.getQuestionInfos().get(0);
        if((questionInfoModel.getQuestionProcessStatus() != 6 && questionInfoModel.getQuestionProcessResult() != 5) && (questionInfoModel.getQuestionProcessStatus() != 5 && questionInfoModel.getQuestionProcessResult() != 3) ){
            ParamValidationHandler.validateParams(identifyUpdateModel.getQuestionInfos().get(0));
//        ValidationHandler.doValidator(identifyUpdateModel.getQuestionInfos().get(0));
            ValidationHandler.doValidator(identifyUpdateModel.getQuestionIdentifyInfo().get(0));
        }
    }


    /**
     * pending.approve.question.info.create 生成待处理问题信息 参数校验
     * @param unapprovedModel
     */
    public static void checkApprovedQuestionParams(UnapprovedModel unapprovedModel) throws DWArgumentException {
        if (StringUtils.isEmpty(unapprovedModel.getQuestionRecordOid())) {
            throw new DWArgumentException("question_record_id","question_record_id is null ");
        }
        if (StringUtils.isEmpty(unapprovedModel.getQuestionProcessStep())) {
            throw new DWRuntimeException("question_process_step is null");
        }
        if (StringUtils.isEmpty(unapprovedModel.getQuestionNo())) {
            throw new DWRuntimeException("question_no is null");
        }
        if (StringUtils.isEmpty(unapprovedModel.getQuestionDescription())) {
            throw new DWRuntimeException("question_description is null");
        }
        liableMessage(unapprovedModel.getLiablePersonId(), unapprovedModel.getLiablePersonName(), unapprovedModel.getLiablePersonPositionId(), unapprovedModel.getLiablePersonPositionName());
    }

    public static void checkApprovedQuestionCurbParams(UnapprovedModel unapprovedModel) {
        if (StringUtils.isEmpty(unapprovedModel.getQuestionRecordOid())) {
            throw new DWRuntimeException("question_record_id is null");
        }
        if (StringUtils.isEmpty(unapprovedModel.getQuestionProcessStep())) {
            throw new DWRuntimeException("question_process_step is null");
        }
        if (StringUtils.isEmpty(unapprovedModel.getQuestionNo())) {
            throw new DWRuntimeException("question_no is null");
        }
        if (StringUtils.isEmpty(unapprovedModel.getQuestionDescription())) {
            throw new DWRuntimeException("question_description is null");
        }
    }

    /**
     * question.record.info.create 新增问题记录 参数校验
     * @param recordModel 新增问题记录 参数
     */
    public static void checkQuestionRecordCreateParams(QuestionRecordModel recordModel) {
        if (StringUtils.isEmpty(recordModel.getQuestionProcessStage())) {
            throw new DWRuntimeException("question_process_stage is null");
        }
        if (StringUtils.isEmpty(recordModel.getQuestionNo())) {
            throw new DWRuntimeException("question_source_no is null");
        }
        if (StringUtils.isEmpty(recordModel.getQuestionNo())) {
            throw new DWRuntimeException("question_no is null");
        }
        if (StringUtils.isEmpty(recordModel.getQuestionNo())) {
            throw new DWRuntimeException("question_description is null");
        }
        liableMessage(recordModel.getLiablePersonId(), recordModel.getLiablePersonName(), recordModel.getLiablePersonPositionId(), recordModel.getLiablePersonPositionName());
    }


    private static void liableMessage(String liablePersonId, String liablePersonName, String liablePersonPositionId, String liablePersonPositionName) {
        if (StringUtils.isEmpty(liablePersonId)) {
            throw new DWRuntimeException("liable_person_id is null");
        }
        if (StringUtils.isEmpty(liablePersonName)) {
            throw new DWRuntimeException("liable_person_name is null");
        }
//        if (StringUtils.isEmpty(liablePersonPositionId)) {
//            throw new DWRuntimeException("liable_person_position_id is null");
//        }
//        if (StringUtils.isEmpty(liablePersonPositionName)) {
//            throw new DWRuntimeException("liable_person_position_name is null");
//        }
    }


    /**
     * 对组建团队必传参数进行校验
     *
     * @param teamBuilderModel
     * @throws DWArgumentException
     */
    public static void checkTeamBuilderParams(TeamBuilderModel teamBuilderModel) throws DWArgumentException {
        if (CollectionUtils.isEmpty(teamBuilderModel.getQuestionInfos())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        ParamValidationHandler.validateParams(teamBuilderModel.getQuestionInfos().get(0));
        QuestionInfo8DModel questionInfo8DModel = teamBuilderModel.getQuestionInfos().get(0);
        if(questionInfo8DModel.getQuestionProcessStatus() != 6 && questionInfo8DModel.getQuestionProcessResult() != 5){
            for (TeamMemberModel teamMemberModel : teamBuilderModel.getTeamMemberModels()) {
                ValidationHandler.doValidator(teamMemberModel);
            }
            for (PlanManagementModel planManagementModel : teamBuilderModel.getPlanManagementModels()) {
                ValidationHandler.doValidator(planManagementModel);
            }
        }

    }


    /**
     * 对围堵措施更新必传参数进行校验
     *
     * @param containmentMeasureInfoModel
     * @throws DWArgumentException
     */
    public static void checkContainmentMeasureParams(ContainmentMeasureInfoModel containmentMeasureInfoModel) throws DWArgumentException {
        if (CollectionUtils.isEmpty(containmentMeasureInfoModel.getQuestionInfos())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        ParamValidationHandler.validateParams(containmentMeasureInfoModel.getQuestionInfos().get(0));
        checkParamsForAnnotation2(containmentMeasureInfoModel.getContainmentMeasureModels());
    }

    /**
     * 校验预防措施执行-update入参
     * @param preventionMeasureExecuteInfoModel 入参
     * @throws DWArgumentException 异常处理
     */
    public static void checkPreventionMeasureExecuteParams(PreventionMeasureExecuteInfoModel preventionMeasureExecuteInfoModel) throws DWArgumentException {
        if (CollectionUtils.isEmpty(preventionMeasureExecuteInfoModel.getQuestionInfos())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        ParamValidationHandler.validateParams(preventionMeasureExecuteInfoModel.getQuestionInfos().get(0));
        checkParamsForAnnotation2(preventionMeasureExecuteInfoModel.getPrevention_measure_execute());
    }

    /**
     * 校验问题确认-update入参
     * @param confirmInfoModel 入参
     * @throws DWArgumentException 异常处理
     */
    public static void checkConfirmParams(ConfirmInfoModel confirmInfoModel) throws DWArgumentException {
        if (CollectionUtils.isEmpty(confirmInfoModel.getQuestionInfos())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        ParamValidationHandler.validateParams(confirmInfoModel.getQuestionInfos().get(0));
        ParamValidationHandler.validateParams(confirmInfoModel.getConfirmModel());
    }



    /**
     * 校验预防措施验证-update入参
     * @param preventionMeasureExecuteVerifyInfoModel update-入参
     * @throws DWArgumentException DWArgumentException
     */
    public static void checkPreventionMeasureExecuteVerifyParams(PreventionMeasureExecuteVerifyInfoModel preventionMeasureExecuteVerifyInfoModel) throws DWArgumentException {
        if (CollectionUtils.isEmpty(preventionMeasureExecuteVerifyInfoModel.getQuestionInfos())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        ParamValidationHandler.validateParams(preventionMeasureExecuteVerifyInfoModel.getQuestionInfos().get(0));
        checkParamsForAnnotation2(preventionMeasureExecuteVerifyInfoModel.getPreventionMeasureExecuteVerify());
    }



    /**
     * 注解参数校验2
     * @param objects
     */
    public static <T> void checkParamsForAnnotation2(List<T> objects) {
        objects.stream().forEach(e->{
            ParamValidationHandler.validateParams(e);
        });
    }

    /**
     * 对根本原因进行校验
     *
     * @param keyReasonCorrectModel
     * @throws DWArgumentException
     */
    public static void checkkeyReasonCorrectParams(KeyReasonCorrectModel keyReasonCorrectModel) throws DWArgumentException {
        if (CollectionUtils.isEmpty(keyReasonCorrectModel.getQuestionInfos())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        ParamValidationHandler.validateParams(keyReasonCorrectModel.getQuestionInfos().get(0));
        ValidationHandler.doValidator(keyReasonCorrectModel.getKeyReasonAnalysisModels().get(0));
        ValidationHandler.doValidator(keyReasonCorrectModel.getCorrectiveActionModels().get(0));
    }

    public static void checkMeasureExecuteParams(ContainmentMeasureExecuteInfoModel measureExecuteInfoModel) throws DWArgumentException {
        if (CollectionUtils.isEmpty(measureExecuteInfoModel.getQuestionInfos())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        ParamValidationHandler.validateParams(measureExecuteInfoModel.getQuestionInfos().get(0));
        checkParamsForAnnotation2(measureExecuteInfoModel.getContainmentMeasureExecuteModels());

    }



    public static void checkContainmentMeasureVerifyParams(ContainmentMeasureVerifyInfoModel measureVerifyInfoModel) throws DWArgumentException {
        if (CollectionUtils.isEmpty(measureVerifyInfoModel.getQuestionInfos())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        ParamValidationHandler.validateParams(measureVerifyInfoModel.getQuestionInfos().get(0));
        checkParamsForAnnotation2(measureVerifyInfoModel.getContainmentMeasureVerifyModels());


    }


    /**
     * 对根本原因进行校验
     *
     * @param keyReasonCorrectModel
     * @throws DWArgumentException
     */
    public static void checkkeyQuestionCorrectExecute(KeyReasonCorrectVo keyReasonCorrectModel) throws DWArgumentException {
        if (CollectionUtils.isEmpty(keyReasonCorrectModel.getQuestionInfos())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        ParamValidationHandler.validateParams(keyReasonCorrectModel.getQuestionInfos().get(0));
        ValidationHandler.doValidator(keyReasonCorrectModel.getKeyReasonAnalysisModels().get(0));
        ValidationHandler.doValidator(keyReasonCorrectModel.getCorrectiveActionVos().get(0));
    }
    /**
     * 对根本原因进行校验
     *
     * @param keyReasonCorrectModel
     * @throws DWArgumentException
     */
    public static void checkkeyQuestionRectifyVerify(RectifyVerifyModel keyReasonCorrectModel) throws DWArgumentException {
        if (CollectionUtils.isEmpty(keyReasonCorrectModel.getQuestionInfos())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        ParamValidationHandler.validateParams(keyReasonCorrectModel.getQuestionInfos().get(0));
        ValidationHandler.doValidator(keyReasonCorrectModel.getCorrectiveMeasureVerifyVos().get(0));
    }

    public static void checkPreventionMeasureParams(PreventionMeasureInfoModel preventionMeasureInfoModel) throws DWArgumentException {
        if (CollectionUtils.isEmpty(preventionMeasureInfoModel.getQuestionInfos())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        ParamValidationHandler.validateParams(preventionMeasureInfoModel.getQuestionInfos().get(0));
        checkParamsForAnnotation2(preventionMeasureInfoModel.getPreventionMeasureModels());
    }

    /**
     * 通用方案 D1&D2&D3
     *
     * @param planArrangeModel
     */
    public static void checkPlanArrangeParams(PlanArrangeModel planArrangeModel) {
        QuestionInfoUniversalModel questionInfo = planArrangeModel.getQuestionInfos();
        if(questionInfo.getQuestionProcessStatus() != 6 && questionInfo.getQuestionProcessResult() != 5){
            ParamValidationHandler.validateParams(planArrangeModel.getQuestionInfos());
            checkParamsForAnnotation2(planArrangeModel.getUniversalPlanArrangeModel());
            ValidationHandler.doValidator(planArrangeModel.getReasonAnalysisModel());
        }
    }
    /**
     * 通用方案D4
     *
     * @param temporaryMeasuresInfoModel
     */
    public static void checkTemporaryMeauser(TemporaryMeasuresInfoModel temporaryMeasuresInfoModel) throws DWArgumentException {
        if (StringUtils.isEmpty(temporaryMeasuresInfoModel.getQuestionInfoUniversalModels())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        ParamValidationHandler.validateParams(temporaryMeasuresInfoModel.getQuestionInfoUniversalModels());
        checkParamsForAnnotation2(temporaryMeasuresInfoModel.getTemporaryMeasureModels());
    }
    /**
     * 通用方案
     *
     * @param temporaryMeasuresInfoModel
     */
    public static void checkTemporaryShortTermCloseVerifyInfoModel(ShortTermCloseVerifyInfoModel temporaryMeasuresInfoModel) throws DWArgumentException {
        if (StringUtils.isEmpty(temporaryMeasuresInfoModel.getQuestionInfoUniversalModels())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        QuestionInfoUniversalModel questionInfoUniversalModel = temporaryMeasuresInfoModel.getQuestionInfoUniversalModels();
        if(questionInfoUniversalModel.getQuestionProcessStatus() != 6 && questionInfoUniversalModel.getQuestionProcessResult() !=5){
            ParamValidationHandler.validateParams(temporaryMeasuresInfoModel.getQuestionInfoUniversalModels());
            ValidationHandler.doValidator(temporaryMeasuresInfoModel.getShortTermCloseVerifyModels());
        }
    }
    public static void checkTemporaryMeauserExcute(TemporaryMeasuresExecuteInfoModel temporaryMeasuresExecuteInfoModel) throws DWArgumentException {
        if (StringUtils.isEmpty(temporaryMeasuresExecuteInfoModel.getQuestionInfoUniversalModels())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        ParamValidationHandler.validateParams(temporaryMeasuresExecuteInfoModel.getQuestionInfoUniversalModels());
        checkParamsForAnnotation2(temporaryMeasuresExecuteInfoModel.getTemporaryMeasureExecuteModels());
        //ValidationHandler.doValidator(temporaryMeasuresExecuteInfoModel.getQuestionConfirmModel());
    }
    public static void checkPermanmentMeasureParams(PermanentMeasureModel permanentMeasureModel) {
        ParamValidationHandler.validateParams(permanentMeasureModel.getQuestionInfos());
        checkParamsForAnnotation2(permanentMeasureModel.getLastingMeasureModels());
        //ValidationHandler.doValidator(permanmentMeasureModel.getQuestionConfirmModel());
    }

    public static void checkPermanentMeasureExecuteParams(PermanentMeasureExecuteInfoModel measureExecuteInfoModel) {
        ParamValidationHandler.validateParams(measureExecuteInfoModel.getQuestionInfo());
        checkParamsForAnnotation2(measureExecuteInfoModel.getMeasureExecuteModels());
        //ValidationHandler.doValidator(measureExecuteInfoModel.getQuestionConfirmModel());

    }
    public static void checkTemporaryMeauserExcuteVerify(TemporaryMeasuresExecuteVerifyInfoModel temporaryMeasuresExecuteInfoModel) throws DWArgumentException {
        if (StringUtils.isEmpty(temporaryMeasuresExecuteInfoModel.getQuestionInfoUniversalModels())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        ParamValidationHandler.validateParams(temporaryMeasuresExecuteInfoModel.getQuestionInfoUniversalModels());
        checkParamsForAnnotation2(temporaryMeasuresExecuteInfoModel.getTemporaryMeasureModels());
        //ValidationHandler.doValidator(temporaryMeasuresExecuteInfoModel.getQuestionConfirmModel());
    }
    public static void checkPermanentMeasureVerifyParams(PermanentMeasureVerifyInfoModel measureVerifyInfoModel) {

        ParamValidationHandler.validateParams(measureVerifyInfoModel.getQuestionInfoUniversalModel());
        checkParamsForAnnotation2(measureVerifyInfoModel.getPermanentMeasureVerifyModels());
    }

    public static void checkUniversalConfirmParams(UniversalConfirmInfoModel confirmInfoModel) {
        QuestionInfoUniversalModel questionInfoUniversalModel = confirmInfoModel.getQuestionInfoUniversalModel();
        if(questionInfoUniversalModel.getQuestionProcessStatus() != 6 && questionInfoUniversalModel.getQuestionProcessResult() !=5){
            ParamValidationHandler.validateParams(confirmInfoModel.getQuestionInfoUniversalModel());
            ValidationHandler.doValidator(confirmInfoModel.getQuestionInfoUniversalModel());
        }
    }

    public static void checkFeedBackVerifyParams(FeedBackVerifyModel feedBackVerifyModel) throws DWArgumentException {
        if (CollectionUtils.isEmpty(feedBackVerifyModel.getQuestionInfos())) {
            throw new DWArgumentException("question_info","question_info is null");
        }
        if(feedBackVerifyModel.getQuestionInfos().get(0).getQuestionProcessStatus() != 6 && feedBackVerifyModel.getQuestionInfos().get(0).getQuestionProcessResult() !=5) {
            ParamValidationHandler.validateParams(feedBackVerifyModel.getQuestionInfos().get(0));
            ParamValidationHandler.validateParams(feedBackVerifyModel.getShortTermVerifyModel());
        }
    }
}
