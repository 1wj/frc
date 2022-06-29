package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.KeyBoardFieldEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/22 10:34
 * @Version 1.0
 * @Description
 */
@Mapper
public interface KeyBoardFieldMapper {

    /**
     * 添加看板栏位信息
     *
     * @param measureEntity 看板栏位实体类
     */
    void addKeyBoardFieldInfo(KeyBoardFieldEntity measureEntity);

    /**
     * 查询看板栏位信息
     *
     * @param templateEntityOid 看板栏位主键
     * @param fieldName         栏位名称
     * @param manageStatus      是否有效
     * @param remarks           备注
     * @return List<KeyBoardFieldEntity>
     */
    List<KeyBoardFieldEntity> queryFieldInfoByConditions(@Param("templateOid") String templateEntityOid,
                                                         @Param("fieldName") String fieldName, @Param("manageStatus") String manageStatus, @Param("remarks") String remarks);

    /**
     * 删除看板栏位信息
     *
     * @param keyboardTemplateOid 看板模板主键
     */
    void deleteKeyBoardFieldByTemplateOid(@Param("templateOid") String keyboardTemplateOid);

    /**
     * 更新看板栏位信息
     *
     * @param keyboardFieldOid    看板栏位主键
     * @param keyboardTemplateOid 看板模板主键
     * @param fieldEntity         看板栏位实体类
     * @param tenantSid           租户id
     */
    void updateKeyBoardFieldByEditOid(@Param("fieldOid") String keyboardFieldOid,
                                      @Param("templateOid") String keyboardTemplateOid, @Param("fieldEntity") KeyBoardFieldEntity fieldEntity,
                                      @Param("tenantSid") Long tenantSid);


    /**
     * 根据看板模板id查询栏位信息
     *
     * @param kanbanTemplateId 看板模板id
     * @param tenantSid
     * @return List<KeyBoardFieldEntity>
     */
    List<KeyBoardFieldEntity> queryFieldInfoByTemplateId(@Param("templateId") String kanbanTemplateId,@Param("tenantSid") Long tenantSid);


    /**
     * 根据看板编号查询所对应的栏位编号信息 并升序排列
     *
     * @param templateNo 看板模板编号
     * @param tenantSid
     * @return List<String>
     */
    List<String> queryAllFieldNosByPrefix(@Param("modelNo") String templateNo,@Param("tenantSid")Long tenantSid);

    /**
     * 根据看板栏位主键删除看板栏位信息
     *
     * @param fieldOid 看板栏位主键
     */
    void deleteKeyBoardFieldById(@Param("fieldOid") String fieldOid);
}
