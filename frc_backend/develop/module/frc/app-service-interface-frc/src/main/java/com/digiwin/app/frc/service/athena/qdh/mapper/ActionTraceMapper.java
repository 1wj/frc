package com.digiwin.app.frc.service.athena.qdh.mapper;

import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.QuestionDetailVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.SolutionStepVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName ActionTraceBiz
 * @Description TODO
 * @Author author
 * @Date 2021/11/11 1:49
 * @Version 1.0
 **/
@Mapper
public interface ActionTraceMapper {

    /**
     * 获取解决方案步骤
     * @param solutionId
     * @return
     */
    List<SolutionStepVo> getSolutionStep(@Param("tenantsid") Long tenantsid,@Param("solutionId") String solutionId);

    /**
     * 更新 问题处理追踪
     * @author author
     * @date 2021/11/11
     **/
    int updateActionTrace(QuestionActionTraceEntity questionActionTraceEntity);

    /**
     * 新增 问题处理数据
     * @param questionActionTraceEntity
     * @return
     */
    int insertActionTrace(QuestionActionTraceEntity questionActionTraceEntity);

    /**
     * 批量新增 问题处理数据
     * @param list
     * @return
     */
    int insertBatchActionTrace(List<QuestionActionTraceEntity> list);

    /**
     * 新增 问题处理数据 -用于初始数据
     * @param questionActionTraceEntity
     * @return
     */
    int insertInitActionTrace(QuestionActionTraceEntity questionActionTraceEntity);

    /**
     * 获取前一处理追踪数据
     * @param tenantsid 租户
     * @param questionRecordOid 问题记录主键
     * @param questionNo 问题号
     * @param questionProcessStep 处理步骤
     * @param questionSolveStep 解决步骤
     * @return QuestionActionTraceEntity 上一问题节点信息
     */
    List<BeforeQuestionVo> getBeforeQuestionTrace(@Param("tenantsid") Long tenantsid, @Param("questionRecordOid") String questionRecordOid, @Param("questionNo") String questionNo,
                                            @Param("questionProcessStep") String questionProcessStep, @Param("questionSolveStep") String questionSolveStep);

    List<BeforeQuestionVo> getBeforeQuestionTraceForIdentity(@Param("tenantsid") Long tenantsid,@Param("questionNo") String questionNo,
                                                  @Param("questionProcessStep") String questionProcessStep, @Param("questionSolveStep") String questionSolveStep);


    /**
     *功能描述 历史数据置灰
     * @author cds
     * @date 2022/5/13
     * @param
     * @return 
     */
    
    List<BeforeQuestionVo> getBeforeQuestionTraceGrey(@Param("tenantsid") Long tenantsid, @Param("questionRecordOid") String questionRecordOid, @Param("questionNo") String questionNo,
                                                  @Param("questionProcessStep") String questionProcessStep, @Param("questionSolveStep") String questionSolveStep ,@Param("principalStep")Integer principalStep);

    /**
     * 获取前一处理追踪数据-list
     * @param tenantsid 租户
     * @param questionRecordOid 问题记录主键
     * @param questionNo 问题号
     * @param questionProcessStep 处理步骤
     * @param questionSolveStep 解决步骤
     * @return QuestionActionTraceEntity 上一问题节点信息
     */
    List<BeforeQuestionVo> getBeforeQuestionTraceForList(@Param("tenantsid") Long tenantsid, @Param("questionRecordOid") String questionRecordOid, @Param("questionNo") String questionNo,
                                                         @Param("questionProcessStep") String questionProcessStep, @Param("questionSolveStep") String questionSolveStep,@Param("step") int step);

    List<BeforeQuestionVo> getBeforeQuestionTraceForList1(@Param("tenantsid") Long tenantsid, @Param("questionRecordOid") String questionRecordOid, @Param("questionNo") String questionNo,
                                                         @Param("questionProcessStep") String questionProcessStep, @Param("questionSolveStep") String questionSolveStep);

    /**
     * 查询处理追踪数据
     * @return
     */
    QuestionDetailVo getQuestionTrace( @Param("oid") String oid );

    /**
     * 获取该项目、问题号最后一个步骤
     * @param tenantsid 租户
     * @param questionRecordOid 问题记录(项目卡)
     * @param questionNo 问题号
     * @return 问题追踪处理(athena-任务卡执行顺序)
     */
    List<QuestionActionTraceEntity> getLastStep(@Param("tenantsid") Long tenantsid, @Param("questionRecordOid") String questionRecordOid,@Param("questionNo") String questionNo);

    /**
     * 获取该项目、问题号最后一个步骤，注意不需要questionRecordOid查询
     * @param tenantsid
     * @param questionNo
     * @return
     */
    List<QuestionActionTraceEntity> getLastStepForIdentity(@Param("tenantsid") Long tenantsid,@Param("questionNo") String questionNo);

    /**
     * 根据主键查询问题详情
     * @param oid
     * @return
     */
    List<QuestionActionTraceEntity> getQuestionDetails(@Param("oid") String oid);


    /**
     * 查询通用方案验收的历史数据
     * @param tenantsid  租户id
     * @param questionRecordOid 记录表主键
     * @param questionNo 问题号
     * @param questionProcessStep
     * @param questionSolveStep
     * @param i   principal_step
     * @return
     */
    List<BeforeQuestionVo> getHistoricalData(@Param("tenantsid") Long tenantsid, @Param("questionRecordOid") String questionRecordOid, @Param("questionNo") String questionNo,
                                             @Param("questionProcessStep") String questionProcessStep, @Param("questionSolveStep") String questionSolveStep,@Param("principalStep") int i);
}
