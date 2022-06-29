package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionSolutionEditEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/16 15:28
 * @Version 1.0
 * @Description
 */
@Mapper
public interface QuestionSolutionEditMapper {

    /**
     * 添加解决方案信息
     *
     * @param editEntity 解决方案实体类
     */
    void addQuestionSolutionEditInfo(QuestionSolutionEditEntity editEntity);

    /**
     * 根据解决方案id删除解决方案信息
     *
     * @param solutionEditOid 待删除主键
     */
    void deleteQuestionSolutionEditById(@Param("oid") String solutionEditOid);

    /**
     * 更新解决方案信息
     *
     * @param editEntity 解决方案实体类
     * @param tenantSid  租户id
     */
    void updateQuestionSolutionEditInfo(@Param("entity") QuestionSolutionEditEntity editEntity, @Param("tenantSid") Long tenantSid);

    /**
     * 获取解决方案信息
     *
     * @param tenantSid     租户id
     * @param solutionNo    解决方案编号
     * @param solutionName  解决方案名称
     * @param manageStatus  是否有效
     * @param defaultChoice 是否默认
     * @param directorId    负责人id
     * @param directorName  负责人名称
     * @return List<QuestionSolutionEditEntity>
     */
    List<QuestionSolutionEditEntity> getQuestionSolutionEditInfo(@Param("tenantSid") Long tenantSid, @Param("solutionNo") String solutionNo,
                                                                 @Param("solutionName") String solutionName, @Param("manageStatus") String manageStatus,
                                                                 @Param("defaultChoice") String defaultChoice, @Param("directorId") String directorId, @Param("directorName") String directorName);

    /**
     * 查询所有解决方案名称信息
     *
     * @param tenantSid
     * @return List<String>
     */
    List<String> queryAllSolutionNames(@Param("tenantSid") Long tenantSid);

    /**
     * 查询所有解决方案编号信息
     *
     * @param tenantSid
     * @return List<String>
     */
    List<String> queryAllSolutionIds(@Param("tenantSid") Long tenantSid);


    /**
     * 根据id查询解决方案信息
     *
     * @param solutionKeyId 解决方案主键
     * @param tenantSid
     * @return QuestionSolutionEditEntity
     */
    QuestionSolutionEditEntity querySolutionEditById(@Param("solutionKeyId") String solutionKeyId,@Param("tenantSid") Long tenantSid);

    /**
     * 根据待删除方案主键查询出解决方案编号
     *
     * @param deletedKeyId 待删除解决方案主键
     * @param tenantSid
     * @return String
     */
    String getSolutionNoByDeletedKeyId(@Param("oid") String deletedKeyId,@Param("tenantSid") Long tenantSid);


    /**
     * 根据方案编号查询相应的方案信息
     * @param tenantSid
     * @param editNo
     * @return  QuestionSolutionEditEntity
     */
    QuestionSolutionEditEntity getQuestionSolutionEditInfoByEditNo(@Param("tenantSid") Long tenantSid,@Param("editNo") String editNo);
}
