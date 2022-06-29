package com.digiwin.app.frc.service.athena.qdh.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/3/9 0:23
 * @Version 1.0
 * @Description 8D解决方案处理Biz
 */
public interface EightDQuestionBiz {

    /**
     * 处理组建团队更新
     * @param entity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param dataInstanceEntity 数据实例实体类
     * @return JSONObject
     */
    JSONObject handleUpdateForTeamBuilder(QuestionActionTraceEntity entity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity);

    /**
     * 处理围堵措施更新
     * @param entity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param dataInstanceEntity 数据实例实体类
     * @return JSONObject
     */
    JSONObject handleUpdateForContainmentMeasure(QuestionActionTraceEntity entity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity);

    /**
     * 处理围堵措施执行更新
     * @param questionActionTraceEntity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param entity 数据实例实体类
     * @return JSONObject
     */
    JSONObject handleUpdateForContainmentMeasureExecute(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity);

    /**
     * 处理围堵措施执行验证更新
     * @param questionActionTraceEntity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param entity 数据实例实体类
     * @return JSONObject
     */
    JSONObject handleUpdateForContainmentMeasureVerify(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity);

    /**
     * 处理根本原因&纠正措施更新
     * @param questionActionTraceEntity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param dataInstanceEntity 数据实例实体类
     * @return JSONObject
     */
    JSONObject keyReasonCorrectBuilder(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity);

    /**
     * 处理纠正措施执行更新
     * @param questionActionTraceEntity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param dataInstanceEntity 数据实例实体类
     * @return JSONObject
     */
    JSONObject correctExecuteBuilder(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity);

    /**
     * 处理纠正措施验证更新
     * @param questionActionTraceEntity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param dataInstanceEntity 数据实例实体类
     * @return JSONObject
     */
    JSONObject rectifyVerifyBuilder(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity);

    /**
     * 获取人力瓶颈分析结果
     * @param dataContent  解析后的数据
     * @return  List<JSONObject>
     * @throws JsonProcessingException
     */
    List<JSONObject> getPersonPendingQuestionNum(JSONArray dataContent) throws JsonProcessingException;

    /**
     * D7 预防措施更新
     * @param entity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param dataInstanceEntity 数据实例实体类
     * @return JSONObject
     */
    JSONObject handleUpdateForPreventionMeasure(QuestionActionTraceEntity entity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity);

    /**
     * D3-3 反馈者验收信 息更新
     * @param entity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param dataInstanceEntity 数据实例实体类
     * @return JSONObject
     */
    JSONObject handleUpdateForFeedBackVerify(QuestionActionTraceEntity entity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity);

    /**
     * D7 预防措施更新
     * @param entity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param dataInstanceEntity 数据实例实体类
     * @return JSONObject
     */
    JSONObject handleUpdateForPreventionMeasureExecute(QuestionActionTraceEntity entity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity);

    /**
     * D7 预防措施更新
     * @param entity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param dataInstanceEntity 数据实例实体类
     * @return JSONObject
     */
    JSONObject handleUpdateForPreventionMeasureExecuteVerify(QuestionActionTraceEntity entity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity);
}
