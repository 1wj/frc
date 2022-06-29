package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.EquipmentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/12 14:28
 * @Version 1.0
 * @Description
 */
@Mapper
public interface EquipmentMapper {

    /**
     * 添加生产线/设备信息
     *
     * @param entities 生产线/设备信息实体类集合
     * @return int
     */
    int addEquipmentInfo(List<EquipmentEntity> entities);

    /**
     * 删除生产线/设备信息
     *
     * @param oidList 待删除主键集合
     * @return int
     */
    int deleteEquipmentInfo(List<String> oidList);

    /**
     * 修改生产线/设备信息
     *
     * @param entities 生产线/设备信息实体类集合
     * @return int
     */
    int updateBatch(List<EquipmentEntity> entities);

    /**
     * 获取生产线/设备信息
     *
     * @param tenantSid     租户id
     * @param equipmentNo   生产线/设备编号
     * @param equipmentName 生产线/设备名称
     * @param manageStatus 是否生效
     * @return
     */
    List<EquipmentEntity> getEquipmentInfo(@Param("tenantSid") Long tenantSid, @Param("equipmentNo") String equipmentNo,
                                           @Param("equipmentName") String equipmentName,@Param("manageStatus") String manageStatus);

    /**
     * 获取所有生产线/设备编号信息
     *
     * @param tenantSid
     * @return List<String>
     */
    List<String> queryAllWorkStationIds(@Param("tenantSid") Long tenantSid);
}
