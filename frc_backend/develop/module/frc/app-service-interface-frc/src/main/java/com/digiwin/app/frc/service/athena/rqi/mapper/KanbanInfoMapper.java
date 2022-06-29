package com.digiwin.app.frc.service.athena.rqi.mapper;

import com.digiwin.app.frc.service.athena.rqi.domain.entity.IssueManagementDetailEntity;

import com.digiwin.app.frc.service.athena.rqi.domain.entity.IssueManagementEntity;
import com.digiwin.app.frc.service.athena.rqi.domain.entity.KanbanInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/1/25 14:42
 * @Version 1.0
 * @Description 看板信息Mapper
 */
@Mapper
public interface KanbanInfoMapper {

    /**
     * 获取问题看板信息
     *
     * @param tenantSid    租户id
     * @return List<KanbanInfoEntity>
     */
    List<KanbanInfoEntity> getKanbanInfo(@Param("tenantSid") Long tenantSid);


    /**
     * 根据问题单号获取发生时间
     *
     * @param tenantSid
     * @param questionNo
     * @return
     */
    Date getHappenTimeByQuestionNo(@Param("tenantSid") Long tenantSid,@Param("questionNo") String questionNo);


    /**
     * 根据时间条件 议题号  以及重要和紧急性 进行查询出符合条件的处理时间数和议题总数
     *
     * @param tenantSid
     * @param startTime
     * @param endTime
     * @param projectNo
     * @return
     */
    List<IssueManagementEntity> queryHandleDateAndIssueCountByTimeAndDemand(@Param("tenantSid") Long tenantSid,
                                                                            @Param("startTime") String startTime, @Param("endTime") String endTime,
                                                                            @Param("projectNo") String projectNo);


    /**
     * 根据条件查询议题管理矩阵详情
     *
     * @param tenantSid
     * @param projectNo
     * @param issueStatus
     * @param importantFlag
     * @param urgencyFlag
     * @param startTime
     * @param endTime
     * @return
     */
    List<IssueManagementDetailEntity> queryIssueManageDetailInfoByCondition(@Param("tenantSid") Long tenantSid, @Param("projectNo") String projectNo,
                                                                            @Param("issueStatus") String issueStatus,@Param("importantFlag") Integer importantFlag,
                                                                            @Param("urgencyFlag") Integer urgencyFlag,@Param("startTime") String startTime,
                                                                            @Param("endTime") String endTime);
}
