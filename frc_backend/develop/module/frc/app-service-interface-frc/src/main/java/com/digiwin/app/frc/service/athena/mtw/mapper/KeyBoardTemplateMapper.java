package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.KeyBoardTemplateEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/22 10:27
 * @Version 1.0
 * @Description
 */
@Mapper
public interface KeyBoardTemplateMapper {

    /**
     * 添加看板模板信息
     *
     * @param templateEntity 看板模板实体类
     */
    void addKeyBoardTemplateInfo(KeyBoardTemplateEntity templateEntity);


    /**
     * 删除看板模板信息
     *
     * @param keyboardTemplateOid 待删除主键
     */
    void deleteKeyBoardTemplateById(@Param("templateOid") String keyboardTemplateOid);

    /**
     * 更新看板模板信息
     *
     * @param templateEntity 看板模板实体类
     * @param tenantSid      租户id
     */
    void updateKeyBoardTemplateInfo(@Param("entity") KeyBoardTemplateEntity templateEntity, @Param("tenantSid") Long tenantSid);

    /**
     * 获取看板模板信息
     *
     * @param tenantSid     租户id
     * @param modelName     模板名称
     * @param manageStatus  是否有效
     * @param defaultChoice 默认选择
     * @param remarks       备注
     * @return List<KeyBoardTemplateEntity>
     */
    List<KeyBoardTemplateEntity> getKeyBoardTemplateInfo(@Param("tenantSid") Long tenantSid,
                                                         @Param("modelName") String modelName, @Param("manageStatus") String manageStatus,
                                                         @Param("defaultChoice") Integer defaultChoice, @Param("remarks") String remarks);

    /**
     * 根据看板模板id查询看板模板信息
     *
     * @param kanbanTemplateId 看板模板主键
     * @param tenantSid
     * @return KeyBoardTemplateEntity
     */
    KeyBoardTemplateEntity queryTemplateById(@Param("templateId") String kanbanTemplateId,@Param("tenantSid") Long tenantSid);

    /**
     * 查询所有看板模板编号并进行升序排列
     *
     * @param tenantSid
     * @return
     */
    List<String> queryAllTemplateNos(@Param("tenantSid") Long tenantSid);
}
