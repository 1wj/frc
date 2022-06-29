package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionSolutionMeasureEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/18 9:25
 * @Version 1.0
 * @Description
 */
@Mapper
public interface QuestionSolutionMeasureMapper {


    /**
     * 添加解决方案步骤信息
     *
     * @param measureEntity 解决方案步骤实体类
     */
    void addQuestionMeasureInfo(QuestionSolutionMeasureEntity measureEntity);

    /**
     * 根据解决方案主键删除解决方案步骤信息
     *
     * @param solutionEditOid 解决方案主键
     */
    void deleteQuestionSolutionMeasureByEditOid(@Param("solutionEditOid") String solutionEditOid);

    /**
     * 根据解决方案id更新解决方案步骤信息
     *
     * @param measureOid    解决方案步骤id
     * @param editOid       解决方案id
     * @param measureEntity 解决方案实体类
     * @param tenantSid     租户id
     */
    void updateQuestionMeasureByEditOid(@Param("measureOid") String measureOid, @Param("editOid") String editOid, @Param("measureEntity") QuestionSolutionMeasureEntity measureEntity,
                                        @Param("tenantSid") Long tenantSid);

    /**
     * 根据解决方案id查询解决方案步骤信息
     *
     * @param editEntityOid 解决方案id
     * @param tenantSid
     * @return List<QuestionSolutionMeasureEntity>
     */
    List<QuestionSolutionMeasureEntity> queryMeasureInfoByEditOid(@Param("editOid") String editEntityOid,@Param("tenantSid") Long tenantSid);


    /**
     * 根据解决方案编号作为前缀查询步骤信息
     *
     * @param solutionId 解决方案编号
     * @param tenantSid
     * @return List<String>
     */
    List<String> queryAllMeasureNosByPrefix(@Param("solutionId") String solutionId,@Param("tenantSid") Long tenantSid);

    /**
     * 根据解决方案步骤主键删除步骤信息
     *
     * @param stepId 方案步骤主键 即oid
     */
    void deleteQuestionSolutionMeasureById(@Param("measureOid") String stepId);
}
