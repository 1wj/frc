package com.digiwin.app.frc.service.athena.qdh.biz;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;

import java.text.ParseException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/4/6 19:37
 * @Version 1.0
 * @Description  针对通用解决方案 添加处理Biz
 */
public interface UniversalQuestionBiz {

    /**
     * 通用方案查询详情数据
     *
     * @param questionId
     * @return
     */
    JSONObject getQuestionDetail(String questionId) throws ParseException;


    /**
     * D1&D2&D3  计划安排更新
     *
     * @param entity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param dataInstanceEntity 数据实例实体类
     * @return  JSONObject
     */
    JSONObject handleUpdateForPlanArrange(QuestionActionTraceEntity entity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity);



    /**
     * D4 临时措施更新
     *
     * @param entity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param dataInstanceEntity 数据实例实体类
     * @return  JSONObject
     */
    JSONObject handleUpdateForTemporaryMeasures(QuestionActionTraceEntity entity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity);

    /**
     * D4-1 临时措施执行
     *
     * @param entity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param dataInstanceEntity 数据实例实体类
     * @return  JSONObject
     */
    JSONObject handleUpdateForTemporaryMeasuresExecute(QuestionActionTraceEntity entity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity);

    /**
     * D4-2 临时措施执行
     *
     * @param entity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param dataInstanceEntity 数据实例实体类
     * @return  JSONObject
     */
    JSONObject handleUpdateForTemporaryMeasuresExecuteVerify(QuestionActionTraceEntity entity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity);

    /**
     * D4-2 临时措施执行
     *
     * @param entity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param dataInstanceEntity 数据实例实体类
     * @return  JSONObject
     */
    JSONObject handleUpdateForQuestionShortTermClosingAcceptance(QuestionActionTraceEntity entity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity);

    /**
     * D5 恒久措施更新
     *
     * @param entity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param dataInstanceEntity 数据实例实体类
     * @return JSONObject
     */
    JSONObject handleUpdateForPermanentMeasure(QuestionActionTraceEntity entity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity dataInstanceEntity);


    /**
     * D5-1 恒久措施执行更新
     *
     * @param questionActionTraceEntity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param entity 数据实例实体类
     * @return JSONObject
     */
    JSONObject handleUpdateForPermanentMeasureExecute(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity);


    /**
     *D5-2 恒久措施执行验证更新
     *
     * @param questionActionTraceEntity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param entity 数据实例实体类
     * @return JSONObject
     */
    JSONObject handleUpdateForPermanentMeasureVerify(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity);


    /**
     * D6 处理确认更新
     *
     * @param questionActionTraceEntity 追踪实体类
     * @param attachmentEntities 附件实体类集合
     * @param entity 数据实例实体类
     * @return JSONObject
     */
    JSONObject handleUpdateForProcessConfirm(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity);


    /**
     * 针对问题验收 将通用方案组装的数据根据规格转换成数组
     *
     * @param questionId
     * @return
     */
    JSONObject getQuestionDetailConvertArray(String questionId) throws ParseException;
}
