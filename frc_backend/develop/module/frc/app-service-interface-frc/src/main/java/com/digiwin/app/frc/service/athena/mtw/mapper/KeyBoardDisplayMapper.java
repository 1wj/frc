package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.KeyBoardDisplayEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/23 10:10
 * @Version 1.0
 * @Description
 */
@Mapper
public interface KeyBoardDisplayMapper {

    /**
     * 新增看板模板显示信息
     *
     * @param entities 看板模板显示信息实体类集合
     * @return int
     */
    int addKeyBoardDisplayInfo(List<KeyBoardDisplayEntity> entities);

    /**
     * 删除看板模板显示信息
     *
     * @param oidList 待删除主键集合
     * @return int
     */
    int deleteKeyBoardDisplayInfo(List<String> oidList);

    /**
     * 更新看板模板显示信息
     *
     * @param entities 看板模板显示信息实体类集合
     * @return int
     */
    int updateBatch(List<KeyBoardDisplayEntity> entities);

    /**
     * 获取看板模板显示信息
     *
     * @param tenantSid        租户id
     * @param kanbanTemplateId 看板模板主键
     * @param solutionId       解决方案编号
     * @return List<KeyBoardDisplayEntity>
     */
    List<KeyBoardDisplayEntity> getKeyBoardDisplayInfo(@Param("tenantSid") Long tenantSid,
                                                       @Param("kanbanTemplateId") String kanbanTemplateId,
                                                       @Param("solutionId") String solutionId);

    /**
     * 获取看板模板显示信息
     *
     * @param tenantSid  租户id
     * @param solutionNo 解决方案编号
     * @return List<KeyBoardDisplayEntity>
     */
    List<KeyBoardDisplayEntity> getKeyBoardDisplayInfoBySolutionNo(@Param("tenantSid") Long tenantSid, @Param("solutionNo") String solutionNo);

    /**
     * 根据解决方案查询看板信息
     *
     * @param tenantSid 租户id
     * @return List<KeyBoardDisplayEntity>
     */
    List<KeyBoardDisplayEntity> getAllKeyBoardDisplayInfo(@Param("tenantSid") Long tenantSid);


    /**
     * 根据删除的模板主键返回相关联的解决方案名称
     *
     * @param deletedKeyId 解决方案主键
     * @param tenantSid
     * @return List<String>
     */
    List<String> getSolutionNamesByDeletedModelOid(@Param("modelOid") String deletedKeyId,@Param("tenantSid")Long tenantSid);

    /**
     * 根据待删除的方案编号查询出模板名称
     *
     * @param solutionNo 方案编号
     * @param tenantSid
     * @return List<String>
     */
    List<String> getModelNamesByDeletedSolutionNo(@Param("solutionNo") String solutionNo,@Param("tenantSid") Long tenantSid);

    /**
     * 解决方案信息更新时同步更新看板显示相关信息
     *
     * @param tenantSid    租户id
     * @param solutionNo   解决方案编号
     * @param solutionName 解决方案名称
     * @param measureNo    步骤编号
     * @param measureName  步骤名称
     */
    void updateDisplayInfoBySolutionNo(@Param("tenantSid") Long tenantSid,
                                       @Param("solutionNo") String solutionNo, @Param("solutionName") String solutionName,
                                       @Param("measureNo") String measureNo, @Param("measureName") String measureName);


    /**
     * 看板模板信息更新时进行同步更新相关信息
     *
     * @param tenantSid 租户id
     * @param oid       模板主键
     * @param modelName 模板名称
     * @param fieldName 栏位名称
     * @param fieldId   栏位id
     */
    void updateDisplayInfoByModelOid(@Param("tenantSid") Long tenantSid,
                                     @Param("modelOid") String oid, @Param("modelName") String modelName,
                                     @Param("fieldName") String fieldName, @Param("fieldId") String fieldId);

}
