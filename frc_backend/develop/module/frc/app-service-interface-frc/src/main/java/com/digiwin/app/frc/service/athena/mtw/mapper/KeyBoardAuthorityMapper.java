package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.KeyBoardAuthorityEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/24 10:12
 * @Version 1.0
 * @Description
 */
@Mapper
public interface KeyBoardAuthorityMapper {

    /**
     * 添加看板权限配置信息
     *
     * @param entities 看板权限配置实体类
     * @return int
     */
    int addKeyBoardAuthorityInfo(List<KeyBoardAuthorityEntity> entities);

    /**
     * 删除看板权限配置信息
     *
     * @param oidList 待删除主键集合
     * @return int
     */
    int deleteKeyBoardAuthorityInfo(List<String> oidList);

    /**
     * 更新看板权限配置信息
     *
     * @param entities 看板权限配置实体类
     * @return int
     */
    int updateBatch(List<KeyBoardAuthorityEntity> entities);

    /**
     * 获取看板权限配置信息
     *
     * @param tenantSid            租户id
     * @param keyBoardTemplateName 看板模板名称
     * @param specifyViewer        指定查看人
     * @return List<KeyBoardAuthorityEntity>
     */
    List<KeyBoardAuthorityEntity> getKeyBoardAuthorityInfo(@Param("tenantSid") Long tenantSid,
                                                           @Param("templateName") String keyBoardTemplateName, @Param("specifyViewer") String specifyViewer);

    /**
     * 查询所有看板模板主键
     *
     * @param tenantSid
     * @return  List<String>
     */
    List<String> queryAllTemplateIds(@Param("tenantSid") Long tenantSid);


    /**
     * 根据看板模板主键更新看板权限配置中相关的模板信息
     *
     * @param oid       看板模板主键
     * @param tenantSid 租户id
     * @param modelName 看板模板名称
     */
    void updateRelatedKanbanInfoByModelOid(@Param("modelOid") String oid, @Param("tenantSid") Long tenantSid,
                                           @Param("modelName") String modelName);



    /**
     * 根据看板模板主键删除相关看板权限相关信息
     *
     * @param keyboardTemplateOid
     */
    void deleteKeyBoardAuthorityInfoByModelOid(@Param("modelOid") String keyboardTemplateOid);


    /**
     * 根据模板主键查询出相应的指定查看人信息
     *
     * @param templateId
     * @param tenantSid
     * @return String
     */
    String querySpecifyViewerByTemplateId(@Param("templateId") String templateId,@Param("tenantSid")Long tenantSid);

    /**
     * 查询租户id查询所有的看板权限信息
     *
     * @param tenantSid
     * @return List<KeyBoardAuthorityEntity>
     */
    List<KeyBoardAuthorityEntity> querySpecifyViewer(@Param("tenantSid") Long tenantSid);
}
