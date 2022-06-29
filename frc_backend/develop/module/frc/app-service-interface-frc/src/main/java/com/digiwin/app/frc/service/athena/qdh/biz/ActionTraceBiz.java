package com.digiwin.app.frc.service.athena.qdh.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.BaseEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.service.DWFile;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

/**
 * @ClassName ActionTraceBiz
 * @Description 问题追踪处理Biz
 * @Author author
 * @Date 2021/11/11 0:10
 * @Version 1.0
 **/
public interface ActionTraceBiz {
    /**
     * 生成退回数据
     * @param entity
     */
    JSONArray insertReturnBackDetail(QuestionActionTraceEntity entity);

    /**
     * 生成待审核遏制单（遏制审核退回）
     * @param entity
     * @return
     * @throws Exception
     */
    JSONArray insertUnapprovedCurbQuestionTrace(List<QuestionActionTraceEntity> entity) throws Exception;

    /**
     * 获取解决方案步骤
     * @param solutionOid
     * @return
     */
    JSONObject getSolutionStep(String solutionOid) throws JsonProcessingException;

    /**
     * 获取问题详情数据
     * @param questionId 问题主键
     * @return 表单json
     */
    JSONObject getQuestionDetail(String questionId);

    /**
     * 新增待审核问题追踪数据
     * 因工厂并未交给spring管理，不太方便进行回滚，所以，数据访问层单独提出
     * @param entity 待审核-问题处理追踪入参(athena-任务卡)
     * @param beforeQuestion 前一节点问题处理追踪入参(athena-任务卡)
     * @param dataInstanceEntity 待审核问题表单数据(athena-任务卡详情)
     * @return
     */
    int insertActionTrace(QuestionActionTraceEntity entity, QuestionActionTraceEntity beforeQuestion, DataInstanceEntity dataInstanceEntity);

    int insertActionTrace(QuestionActionTraceEntity entity, DataInstanceEntity dataInstanceEntity);


    int insertActionTraceForCurb(QuestionActionTraceEntity entity,DataInstanceEntity dataInstanceEntity);

    /**
     * 更新表单数据
     * @param questionActionTraceEntity 更新任务卡
     * @param attachmentEntities 附件信息
     * @param entity 更新任务卡表单
     */
    JSONObject handleUpdateForDistribution(QuestionActionTraceEntity questionActionTraceEntity,List<AttachmentEntity> attachmentEntities,DataInstanceEntity entity);


    /**
     * 更新表单数据
     * @param questionActionTraceEntity 更新任务卡
     * @param attachmentEntities 附件信息
     * @param entity 更新任务卡表单
     */
    JSONObject shortTermUpdate(QuestionActionTraceEntity questionActionTraceEntity,List<AttachmentEntity> attachmentEntities,DataInstanceEntity entity);
}
