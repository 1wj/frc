package com.digiwin.app.frc.service.athena.qdh.mapper;

import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionRecordEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.QuestionRecordInfoModel;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.RecordMessageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName RecordMapper
 * @Description 问题记录mapper
 * @Author author
 * @Date 2021/11/15 21:57
 * @Version 1.0
 **/
@Mapper
public interface RecordMapper {
    /**
     * 更新
     * @param questionRecordEntity 入参实体
     * @return 成功与否
     **/
    int updateRecord(QuestionRecordEntity questionRecordEntity);

    int updateRecordForQF(QuestionRecordEntity questionRecordEntity);


    /**
     * 新增问题记录
     * @param questionRecordEntity 入参实体
     * @return 成功与否
     */
    int insertRecord(QuestionRecordEntity questionRecordEntity);



    /** ---------------- 分割线 version2 ---------------------- **/

    /**
     * 获取待审核任务卡信息
     *
     * @param processStep 处理阶段
     * @param questionSolveStep 解决步骤
     * @param questionRecordId 记录表主键
     * @param tenantsid 租户id
     * @return List<RecordMessageVo>
     */
    List<RecordMessageVo> getPendingRecordMsg(@Param("processStep") String processStep,@Param("questionSolveStep") String questionSolveStep,@Param("questionRecordId") String questionRecordId ,@Param("tenantsid") Long tenantsid);

    /**
     * 获取已完成任务卡信息
     * @param processStep 处理阶段
     * @param questionSolveStep 解决步骤
     * @param questionRecordId 记录表主键
     * @param tenantsid 租户id
     * @return List<RecordMessageVo>
     */
    List<RecordMessageVo> getRecordMsg(@Param("processStep") String processStep,@Param("questionSolveStep") String questionSolveStep,@Param("questionRecordId") String questionRecordId ,@Param("tenantsid") Long tenantsid);


    List<QuestionRecordEntity> getQuestionRecordByNos(@Param("tenantsid") Long tenantsid , @Param("questionNos") List<String> questionNos);

}
