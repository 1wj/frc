package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.CraftDataEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/16 13:55
 * @Version 1.0
 * @Description
 */
@Mapper
public interface CraftDataMapper {

    /**
     * 添加工艺信息
     *
     * @param entities 工艺信息实体类集合
     * @return int
     */
    int addCraftDataInfo(List<CraftDataEntity> entities);

    /**
     * 删除工艺信息
     *
     * @param oidList 待删除主键集合
     * @return int
     */
    int deleteCraftDataInfo(List<String> oidList);

    /**
     * 更新工艺信息
     *
     * @param entities 工艺信息实体类集合
     * @return in
     */
    int updateBatch(List<CraftDataEntity> entities);

    /**
     * 获取工艺信息
     *
     * @param tenantSid 租户id
     * @param craftNo   工艺编号
     * @param craftName 工艺名称
     * @param manageStatus 是否生效
     * @return List<CraftDataEntity>
     */
    List<CraftDataEntity> getCraftDataInfo(@Param("tenantSid") Long tenantSid,@Param("craftNo") String craftNo,
                                           @Param("craftName") String craftName,@Param("manageStatus") String manageStatus);

    /**
     * 获取所有工艺编号信息
     *
     * @param tenantSid
     * @return List<String>
     */
    List<String> queryAllOpNos(@Param("tenantSid") Long tenantSid);
}
