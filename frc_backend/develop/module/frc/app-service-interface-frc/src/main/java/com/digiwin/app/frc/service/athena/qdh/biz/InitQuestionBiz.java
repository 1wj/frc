package com.digiwin.app.frc.service.athena.qdh.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionRecordEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.init.AttachmentModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.init.BasicModel;
import com.digiwin.app.frc.service.athena.qdh.domain.model.init.DetailModel;

import java.util.List;

/**
 * @ClassName InitQuestionBiz
 * @Description 初始问题数据
 * @Author author
 * @Date 2022/2/10 14:56
 * @Version 1.0
 **/
public interface InitQuestionBiz {
    /**
     * 问题发起逻辑处理
     * @param basicModel 基础信息
     * @param detailModel 详情信息
     * @param dataContent 任务数据
     */
    JSONObject initQuestionMessage(BasicModel basicModel, DetailModel detailModel, List<AttachmentModel> attachmentModels, String dataContent);

    /**
     * 更新问题确认卡
     * @param recordEntity 更新问题记录信息
     * @param actionTraceEntity 更新任务卡
     * @param dataInstanceEntity 更新数据
     * @return
     */
    JSONObject updateQF(QuestionRecordEntity recordEntity, QuestionActionTraceEntity actionTraceEntity, DataInstanceEntity dataInstanceEntity );

    /**
     * 更新问题分析
     * @param questionActionTraceEntity 问题处理追踪实体
     * @param attachmentEntities 附件
     * @param entity 数据实力
     */
    JSONObject updateQA(QuestionActionTraceEntity questionActionTraceEntity, List<AttachmentEntity> attachmentEntities, DataInstanceEntity entity,QuestionRecordEntity recordEntity);

}
