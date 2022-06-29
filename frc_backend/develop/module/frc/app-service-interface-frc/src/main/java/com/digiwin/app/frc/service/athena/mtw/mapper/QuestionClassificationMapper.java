package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.ClassificationSourceMidEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionClassificationEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionClassificationQueryVo;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionClassificationVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/5 15:47
 * @Version 1.0
 * @Description
 */
@Mapper
public interface QuestionClassificationMapper {

    /**
     * 新增问题分类信息
     *
     * @param entities 问题分类实体类集合
     * @return int
     */
    int addQuestionClassificationInfo(List<QuestionClassificationEntity> entities);

    /**
     * 批量删除问题分类信息
     *
     * @param oidList 待删除主键集合
     * @return 是否删除成功
     */
    int deleteQuestionClassificationInfo(List<String> oidList);



    /**
     * 批量更新操作
     *
     * @param entities 问题分类实体类的集合
     * @return int
     */
    int updateBatch(List<QuestionClassificationEntity> entities);


    /**
     * 根据条件查询问题分类相关信息
     *
     * @param tenantSid  租户id
     * @param questionClassificationVo  vo
     * @return  List<QuestionClassificationQueryVo>
     */
    List<QuestionClassificationQueryVo> getQuestionClassificationInfo(@Param("tenantSid") Long tenantSid,
                                                                      @Param("vo") QuestionClassificationVo questionClassificationVo);

    /**
     * 查询所有问题分类编号信息
     *
     * @param tenantSid
     * @return List<String>
     */
    List<String> queryAllClassificationNos(@Param("tenantSid") Long tenantSid);


    void deleteAnalysisConfigIds(@Param("oidList") List<String> oidList,@Param("tenantSid") Long tenantSid);

    void updateAnalysisConfigId(@Param("classificationIds") List<String> classificationIds,@Param("analysisConfigId") String oid,@Param("tenantSid") Long tenantSid);

    void updateAcceptanceConfigId(@Param("classificationIds") List<String> classificationIds,@Param("acceptanceConfigId") String oid,@Param("tenantSid") Long tenantSid);

    void updateAnalysisConfigIdInfo(@Param("analysisConfigId") String oid,@Param("tenantSid") Long tenantSid);

    void deleteAcceptanceConfigIds(@Param("oidList") List<String> oidList, @Param("tenantSid") Long tenantSid);

    void updateAcceptanceConfigIdInfo(@Param("acceptanceConfigId") String oid,@Param("tenantSid") Long tenantSid);

    void updateProcessConfigId(@Param("classificationIds") List<String> classificationIds, @Param("processConfigId") String oid,@Param("tenantSid") Long tenantSid);

    void updateProcessConfigIdInfo(@Param("processConfigId") String oid,@Param("tenantSid") Long tenantSid);

    void updateProcessConfigIdByDelete(@Param("oidList") List<String> oidList,@Param("tenantSid") Long tenantSid);

    /**
     * 查询所有问题分类
     * @return
     */
    List<QuestionClassificationEntity> queryAll(@Param("tenantSid") Long tenantSid);

    /**
     * 根据问题分类主键删除关联表相关信息
     *
     * @param oidList 主键集合
     * @param tenantSid 租户
     */
    void deleteClassificationSourceInfo(@Param("classificationIds") List<String> oidList,
                                        @Param("tenantSid") Long tenantSid);

    /**
     * 根据问题分类主键删除问题分类来源关联表信息
     *
     * @param tenantSid 租户
     * @param oid 主键
     */
    void deleteClassificationSourceInfoByClassificationId(@Param("tenantSid") Long tenantSid,
                                                          @Param("classificationId") String oid);

    /**
     * 添加问题分类来源关联表信息
     *
     * @param midList 关联表实体类
     */
    void addClassificationSourceMidInfo(List<ClassificationSourceMidEntity> midList);

    /**
     * 根据来源主键集合删除关联表信息
     *
     * @param oidList
     * @param tenantSid
     */
    void deleteClassificationSourceInfoBySourceIds(@Param("sourceIds") List<String> oidList,
                                                   @Param("tenantSid") Long tenantSid);

    /**
     * 根据来源主键删除关联表信息
     *
     * @param tenantSid
     * @param oid
     */
    void deleteClassificationSourceInfoBySourceId(@Param("tenantSid") Long tenantSid,
                                                  @Param("sourceId") String oid);





    void deleteLiablePersonOidByIds(@Param("list")List<String> liableConfigId,@Param("tenantSid")Long tenantSid);

    int updateLiablePersonOidByIds(@Param("list")List<String> classificationIds,@Param("liablePersonOid")String liablePersonId,@Param("tenantSid")Long tenantSid);
}
