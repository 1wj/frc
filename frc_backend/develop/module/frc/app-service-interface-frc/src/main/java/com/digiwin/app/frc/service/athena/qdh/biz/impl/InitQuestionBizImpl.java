package com.digiwin.app.frc.service.athena.qdh.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.qdh.update.QuestionUpdateConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionProcessStage;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.RecordProcessStatusEnum;
import com.digiwin.app.frc.service.athena.config.annotation.ValidationHandler;
import com.digiwin.app.frc.service.athena.qdh.biz.InitQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionRecordEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.QuestionPictureModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.init.AttachmentModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.init.BasicModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.init.DetailModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.init.InitModel;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.RecordMapper;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.rqi.EocUtils;
import com.digiwin.app.service.DWServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName InitQuestionBizImpl
 * @Description 问题发起以及迭代三逻辑处理
 * @Author author
 * @Date 2022/2/10 15:00
 * @Version 1.0
 **/
@Service
public class InitQuestionBizImpl implements InitQuestionBiz {

    @Autowired
    RecordMapper recordMapper;

    @Autowired
    ActionTraceMapper actionTraceMapper;

    @Autowired
    DataInstanceMapper dataInstanceMapper;

    @Autowired
    AttachmentMapper attachmentMapper;


    private final Logger logger = LoggerFactory.getLogger(InitQuestionBizImpl.class);

    private final static List<InitModel> initProcessSteps = new ArrayList<>();

    static {
        initProcessSteps.add(new InitModel(4,2,0,"QFL"));
        initProcessSteps.add(new InitModel(2,1,1,"QF"));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject initQuestionMessage(BasicModel basicModel, DetailModel detailModel, List<AttachmentModel> attachmentModels,String dataContent) {
        // 必要参数校验
        ValidationHandler.doValidator(basicModel);
        //初始化问题记录(项目卡数据)
        QuestionRecordEntity recordEntity = initRecordData(basicModel,detailModel);
        // 初始化问题处理追踪(任务卡数据)
        List<QuestionActionTraceEntity> actionTraceEntities = initActionTraceData(recordEntity);
        // 初始化数据实例
        List<DataInstanceEntity> dataInstanceEntities = initDataInstanceData(actionTraceEntities,dataContent);
        // 初始化附件
        List<AttachmentEntity> attachmentEntities = initAttachmentData(attachmentModels, basicModel.getPictureModels(), actionTraceEntities);

        recordMapper.insertRecord(recordEntity);
        actionTraceMapper.insertBatchActionTrace(actionTraceEntities);
        dataInstanceMapper.insertBatchDataInstance(dataInstanceEntities);
        if (actionTraceEntities.size()==0) {
            attachmentMapper.insertBatchAttachment(attachmentEntities);
        }
        return returnResult(actionTraceEntities);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject updateQF(QuestionRecordEntity recordEntity, QuestionActionTraceEntity actionTraceEntity, DataInstanceEntity dataInstanceEntity) {
        if (null != recordEntity) {
            int updateRecordStatus = recordMapper.updateRecordForQF(recordEntity);
            if (updateRecordStatus < 0) {
                throw new DWRuntimeException(String.format("updateRecord failed ,detail :%s", recordEntity.toString()));
            }
        }
        int updateActionTraceStatus = actionTraceMapper.updateActionTrace(actionTraceEntity);
        if (updateActionTraceStatus < 0) {
            throw new DWRuntimeException(String.format("updateActionTrace failed ,detail :%s", actionTraceEntity.toString()));
        }
        if (null != dataInstanceEntity) {
            int updateDataInstanceStatus = dataInstanceMapper.updateDataInstance(dataInstanceEntity);
            if (updateDataInstanceStatus < 0) {
                throw new DWRuntimeException(String.format("updateDataInstance failed ,detail :%s", dataInstanceEntity.toString()));
            }
        }
        // response data封装
        JSONObject result = new JSONObject();
        result.put(QuestionUpdateConst.question_id,actionTraceEntity.getOid());
        result.put(QuestionUpdateConst.question_process_status,actionTraceEntity.getQuestionProcessStatus());
        result.put(QuestionUpdateConst.question_process_result,actionTraceEntity.getQuestionProcessResult());
        result.put(QuestionUpdateConst.question_description,actionTraceEntity.getQuestionDescription());
        result.put(QuestionUpdateConst.question_no,actionTraceEntity.getQuestionNo());
        result.put(QuestionUpdateConst.question_record_id,actionTraceEntity.getQuestionRecordOid());
        result.put(QuestionUpdateConst.update_date,actionTraceEntity.getUpdateTime());
        /**
         * 冲刺六调整
         */
        result.put(QuestionUpdateConst.close_reason,StringUtils.isEmpty(actionTraceEntity.getCloseReason()) ? "":actionTraceEntity.getCloseReason());

        JSONArray re = new JSONArray();
        re.add(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(QuestionUpdateConst.question_info,re);
        return jsonObject;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "Exception")
    public JSONObject updateQA(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities,
                         DataInstanceEntity entity,QuestionRecordEntity recordEntity) {
        int updateRecordStatus =  recordMapper.updateRecord(recordEntity);
        if (updateRecordStatus <0) {
            throw new DWRuntimeException("updateRecord fail ! ");
        }
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

        // 根据主键查询返回信息
        JSONObject result = new JSONObject();
        List<QuestionActionTraceEntity> list = actionTraceMapper.getQuestionDetails(questionActionTraceEntity.getOid());
        if (CollectionUtils.isEmpty(list)) {
            return result;
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
        result.put("return_no", StringUtils.isEmpty(actionEntity.getReturnNo()) ? "" : actionEntity.getReturnNo());
        result.put(QuestionUpdateConst.update_date,questionActionTraceEntity.getUpdateTime());
        /**
         * 冲刺六调整
         */
        result.put(QuestionUpdateConst.close_reason,questionActionTraceEntity.getCloseReason());
        result.put(QuestionUpdateConst.return_reason,questionActionTraceEntity.getReturnReason());
        result.put(QuestionUpdateConst.return_step_no,questionActionTraceEntity.getReturnStepNo());


        JSONArray re = new JSONArray();
        re.add(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("question_info",re);
        return jsonObject;
    }

    /**
     * 获取QF 主键
     * @param actionTraceEntities
     * @return
     */
    private JSONObject returnResult(List<QuestionActionTraceEntity> actionTraceEntities){
        QuestionActionTraceEntity actionTraceEntity =
                actionTraceEntities.stream().filter(e -> e.getQuestionProcessStep().equals(QuestionUpdateEnum.question_feedback.getCode())).collect(Collectors.toList()).get(0);
        JSONObject re = new JSONObject();
        re.put("question_id",actionTraceEntity.getOid());
        re.put("question_record_id",actionTraceEntity.getQuestionRecordOid());
        re.put("question_no",actionTraceEntity.getQuestionNo());
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(re);
        JSONObject result = new JSONObject();
        result.put("question_result",jsonArray);
        return result;
    }
    /**
     * 初始化问题记录表（项目卡）
     * @param basicModel
     * @return
     */
    private QuestionRecordEntity initRecordData(BasicModel basicModel,DetailModel detailModel){
        QuestionRecordEntity recordEntity = new QuestionRecordEntity();
        recordEntity.setOid(IdGenUtil.uuid());
        recordEntity.setQuestionNo(QuestionNoUtil.initQuestionNo());
        recordEntity.setQuestionProcessStage(QuestionProcessStage.QH.getCode());
        recordEntity.setQuestionDescription(basicModel.getQuestionDescription());
        recordEntity.setQuestionSourceNo(basicModel.getQuestionSourceNo());
        recordEntity.setQuestionSourceOId(basicModel.getQuestionSourceOId());
        recordEntity.setQuestionClassificationOId(basicModel.getQuestionClassificationOId());
        // 0 -未开始
        recordEntity.setCurrentQuestionProcessStatus(RecordProcessStatusEnum.unStart.getCode());
        // 归属类型
        recordEntity.setQuestionSourceType(basicModel.getQuestionAttributionNo());
        // 重要性
        recordEntity.setImportant(basicModel.getImportant());
        // 紧急度
        recordEntity.setUrgency(basicModel.getUrgency());
        // 项目代号
        recordEntity.setProjectNo(detailModel.getProjectNo());
        // 3.30日修改 赋值 预计完成时间
//        recordEntity.setExpectFinishTime(DateUtil.string2Date(basicModel.getExpectFinishTime(),"yyyy-MM-dd"));
        recordEntity.setExpectFinishTime(basicModel.getExpectFinishTime());

        // 风险等级
        recordEntity.setRiskLevelOId(basicModel.getRiskLevelOId());
        return recordEntity;
    }

    /**
     * 初始化问题处理追踪数据(任务卡)
     * @param recordEntity 根据问题记录实体数据获取 问题描述等信息
     * @return 待新增问题处理更新集合
     */
    private List<QuestionActionTraceEntity> initActionTraceData(QuestionRecordEntity recordEntity){
        List<QuestionActionTraceEntity> actionTraceEntities = initProcessSteps.stream().map(s ->{
            QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
            String oid = IdGenUtil.uuid();
            entity.setOid(oid);
            entity.setQuestionRecordOid(recordEntity.getOid());
            entity.setQuestionProcessStep(s.getQuestionProcessStep());
            entity.setQuestionNo(recordEntity.getQuestionNo());
            entity.setQuestionDescription(recordEntity.getQuestionDescription());
            entity.setDataInstanceOid(IdGenUtil.uuid());
            entity.setPrincipalStep(s.getPrincipalStep());
            entity.setQuestionProcessStatus(s.getQuestionProcessStatus());
            entity.setQuestionProcessResult(s.getQuestionProcessResult());
            entity.setExpectCompleteDate(recordEntity.getExpectFinishTime());
            try {
                if (s.getQuestionProcessStep().equals(QuestionUpdateEnum.question_feedbackL.getCode())) {
                    Map user = EocUtils.getEmpIdForMap(TenantTokenUtil.getUserId());
                    entity.setLiablePersonId((String) user.get("id"));
                    entity.setLiablePersonName((String) user.get("name"));
                }
            }catch (Exception e) {
                e.printStackTrace();
                logger.error("get users error");
            }
           return entity;
        }).collect(Collectors.toList());
        return actionTraceEntities;
    }

    /**
     * 初始化数据实例
     * @param actionTraceEntities 通过问题处理追踪赋值
     * @return 已初始化的数据实例
     */
    private List<DataInstanceEntity> initDataInstanceData(List<QuestionActionTraceEntity> actionTraceEntities,String dataContent){
        return actionTraceEntities.stream().map(e -> {
            DataInstanceEntity entity = new DataInstanceEntity();
            entity.setOid(e.getDataInstanceOid());
            entity.setQuestionTraceOid(e.getOid());
            entity.setDataContent(dataContent);
            return entity;
        }).collect(Collectors.toList());
    }

    /**
     * 初始化附件
     * @param attachmentModels 上传的附件信息
     * @param questionPictureModels 图片信息
     * @param actionTraceEntities 获取问题确认问题号以及数据实例主键
     * @return 问题发起阶段上传的附件以及图片
     */
    private List<AttachmentEntity> initAttachmentData(List<AttachmentModel> attachmentModels, List<QuestionPictureModel> questionPictureModels,List<QuestionActionTraceEntity> actionTraceEntities){
        if (CollectionUtils.isEmpty(attachmentModels) && CollectionUtils.isEmpty(questionPictureModels)) {
            return Collections.emptyList();
        }
        // 获取问题确认问题号以及数据实例主键
        QuestionActionTraceEntity actionTraceEntity =
                actionTraceEntities.stream().filter(e -> e.getQuestionProcessStep().equals(QuestionUpdateEnum.question_feedback.getCode())).collect(Collectors.toList()).get(0);
        List<AttachmentEntity> attachmentEntities = new ArrayList<>();
        if (!CollectionUtils.isEmpty(questionPictureModels)) {
            List<AttachmentEntity> pictureAttachments = questionPictureModels.stream().map( m ->{
                AttachmentEntity entity = new AttachmentEntity();
                entity.setAttachmentType(1);
                entity.setDmcId(m.getPictureId());
                entity.setQuestionNo(actionTraceEntity.getQuestionNo());
                entity.setDataInstanceOid(actionTraceEntity.getDataInstanceOid());
                entity.setOid(IdGenUtil.uuid());
                entity.setTenantsid(TenantTokenUtil.getTenantSid());
                return entity;
            }).collect(Collectors.toList());
            attachmentEntities.addAll(pictureAttachments);
        }
        List<AttachmentEntity> attachmentEntityList =  attachmentModels.stream().map(e -> {
            AttachmentEntity entity = new AttachmentEntity();
            entity.setAttachmentTitle(e.getAttachmentName());
            // 附件类型
            String fileExtensionName = e.getAttachmentName().substring(e.getAttachmentName().lastIndexOf(".") + 1);
            entity.setExtensionName(fileExtensionName);
            entity.setAttachmentType(2);
            entity.setDmcId(e.getAttachmentId());
            entity.setQuestionNo(actionTraceEntity.getQuestionNo());
            entity.setDataInstanceOid(actionTraceEntity.getDataInstanceOid());
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantsid((Long) DWServiceContext.getContext().getProfile().get("tenantSid"));
            return entity;
        }).collect(Collectors.toList());
        attachmentEntities.addAll(attachmentEntityList);
        // 顺带处理图片
        return attachmentEntities;
    }


}
