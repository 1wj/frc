package com.digiwin.app.frc.service.athena.ppc.mapper;

import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionOccurStageEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author:zhangzlz
 * @Date 2022/2/11   11:37
 */
@Mapper
public interface QuestionOccurStageMapper {
    /**
     * 查询所有问题发生阶段编号
     * @return 问题发生阶段编号集合
     */
    List<String> getAllQesOccurStageNos(@Param("tenantSid") Long tenantSid);

    /**
     * 查询所有问题发生阶段的名称
     * @return 问题发生阶段名称集合
     */
    List<String> getAllQesOccurStageNames(@Param("tenantSid") Long tenantSid);

    /**
     * 查询所有问题发生阶段的主键
     * @return  返回问题发生阶段主键集合
     */
    List<String> getAllQesOccurStageIds(@Param("tenantSid") Long tenantSid);

    /**
     * 添加问题发生阶段信息
     *
     * @param entities 实体
     * @return int 返回添加执行结果
     */
    int addQuestionOccurStageInfo(List<QuestionOccurStageEntity> entities);

    /**
     * 删除问题发生阶段信息
     *
     * @param idList 问题发生阶段主键集合
     * @param tenantSid 租户id
     * @return 返回删除执行结果
     */
    int deleteQuestionOccurStageInfo(@Param("idList") List<String> idList, @Param("tenantSid") Long tenantSid);

    /**
     * 批量更新问题发生阶段
     * @param entities  更新的实体集合
     * @return 返回执行结果
     */
    int updateQuesOccurStageInfo(@Param("list") List<QuestionOccurStageEntity> entities, @Param("tenantSid") Long tenantSid);

    /**
     * 根据条件查询问题发生阶段
     * @param occurStageNo 问题发生阶段编号
     * @param occurStageName 问题发生阶段名称
     * @param manageStatus 问题发生阶段状态
     * @param tenantSid 租户id
     * @return 返回问题发生阶段实体
     */
    List<QuestionOccurStageEntity> getQuesOccurStageInfo(@Param("occurStageNo") String occurStageNo, @Param("occurStageName") String occurStageName,
                                                         @Param("manageStatus") String manageStatus, @Param("classificationOid") String classificationOid,
                                                         @Param("sourceOid") String sourceOid, @Param("attributionNo") String attributionNo,
                                                         @Param("tenantSid") Long tenantSid);
}
