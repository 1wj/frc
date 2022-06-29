package com.digiwin.app.frc.service.athena.ppc.mapper;

import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionRiskLevelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/10 12:58
 * @Version 1.0
 * @Description
 */
@Mapper
public interface QuestionRiskLevelMapper {

    /**
     * 添加问题风险等级信息
     *
     * @param entities
     * @return int
     */
    int addQuestionRiskLevelInfo(List<QuestionRiskLevelEntity> entities);

    /**
     * 删除风险等级信息
     *
     * @param oidList
     * @param tenantSid
     * @return int
     */
    int deleteQuestionRiskLevelInfo(@Param("oidList") List<String> oidList, @Param("tenantSid") Long tenantSid);

    /**
     * 更新问题风险等级信息
     *
     * @param entities
     * @return int
     */
    int updateBatch(List<QuestionRiskLevelEntity> entities);



    /**
     * 查询问题风险等级信息
     *
     * @param tenantSid
     * @param riskLevelNo
     * @param riskLevelName
     * @param isModify
     * @param manageStatus
     * @return
     */
    List<QuestionRiskLevelEntity> getQuestionRiskLevelInfo(@Param("tenantSid") Long tenantSid, @Param("riskLevelNo") String riskLevelNo,
                                                           @Param("riskLevelName") String riskLevelName, @Param("isModify") String isModify,
                                                           @Param("manageStatus") String manageStatus,@Param("isUpload") String isUpload);


    /**
     * 查询所有风险等级编号
     *
     * @param tenantSid 租户id
     * @return  List<String>
     */
    List<String> queryAllRiskLevelNos(@Param("tenantSid") Long tenantSid);

    /**
     * 查询所有风险等级名称
     *
     * @param tenantSid 租户id
     * @return  List<String>
     */
    List<String> queryAllRiskLevelNames(@Param("tenantSid") Long tenantSid);


    /**
     * 根据风险等级主键批量更新问题确认主键
     *
     * @param levelIds
     * @param oid
     * @param tenantSid
     */
    void updateConfirmConfigId(@Param("levelIds") List<String> levelIds,@Param("confirmConfigId") String oid,@Param("tenantSid")Long tenantSid);

    /**
     * 批量删除问题确认主键(设置为NULL)
     *
     * @param oidList
     * @param tenantSid
     */
    void deleteConfirmConfigIds(@Param("oidList") List<String> oidList,@Param("tenantSid") Long tenantSid);

    void updateConfirmConfigIdInfo(@Param("confirmConfigId") String oid, @Param("tenantSid") Long tenantSid);

    void updateAnalysisConfigId(@Param("levelIds") List<String> levelIds,@Param("analysisConfigId") String oid,@Param("tenantSid") Long tenantSid);

    void updateAnalysisConfigIdInfo(@Param("analysisConfigId") String oid, @Param("tenantSid") Long tenantSid);

    void deleteAnalysisConfigIds(@Param("oidList") List<String> oidList,@Param("tenantSid") Long tenantSid);

    void updateAcceptanceConfigId(@Param("levelIds") List<String> levelIds,@Param("acceptanceConfigId") String oid,@Param("tenantSid") Long tenantSid);

    void deleteAcceptanceConfigIds(@Param("oidList") List<String> oidList,@Param("tenantSid") Long tenantSid);

    void updateAcceptanceConfigIdInfo(@Param("acceptanceConfigId") String oid, @Param("tenantSid") Long tenantSid);


    void updateProcessConfigId(@Param("levelIds") List<String> levelIds, @Param("processConfigId") String oid,@Param("tenantSid") Long tenantSid);

    void updateProcessConfigIdInfo(@Param("processConfigId") String oid, @Param("tenantSid") Long tenantSid);

    void updateProcessConfigIdByDelete(@Param("oidList") List<String> oidList,@Param("tenantSid") Long tenantSid);

}
