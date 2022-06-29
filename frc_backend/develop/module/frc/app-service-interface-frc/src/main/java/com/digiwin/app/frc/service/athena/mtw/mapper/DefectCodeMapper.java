package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.DefectCodeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/10 16:54
 * @Version 1.0
 * @Description
 */
@Mapper
public interface DefectCodeMapper {

    /**
     * 新增缺陷代码信息
     *
     * @param entities 缺陷代码实体类集合
     * @return int
     */
    int addDefectCodeInfo(List<DefectCodeEntity> entities);

    /**
     * 删除缺陷代码信息
     *
     * @param oidList 待删除主键集合
     * @return int
     */
    int deleteDefectCodeInfo(List<String> oidList);

    /**
     * 更新缺陷代码信息
     *
     * @param entities 缺陷代码实体类集合
     * @return int
     */
    int updateBatch(List<DefectCodeEntity> entities);

    /**
     * 查询缺陷代码信息
     *
     * @param tenantSid      租户id
     * @param defectTypeNo   缺陷类别编号
     * @param defectTypeName 缺陷类别名称
     * @param defectCode     缺陷代码
     * @param defectName     缺陷名称
     * @param defectGrade    缺陷等级
     * @param manageStatus   是否有效
     * @param remarks        备注
     * @return List<DefectCodeEntity>
     */
    List<DefectCodeEntity> getDefectCodeInfo(@Param("tenantSid") Long tenantSid, @Param("defectTypeNo") String defectTypeNo,
                                             @Param("defectTypeName") String defectTypeName, @Param("defectCode") String defectCode, @Param("defectName") String defectName,
                                             @Param("defectGrade") String defectGrade, @Param("manageStatus") String manageStatus, @Param("remarks") String remarks);

    /**
     * 查询所有缺陷类别编号信息
     *
     * @param  tenantSid
     * @return List<String>
     */
    List<String> queryAllCategoryNo(@Param("tenantSid") Long tenantSid);

    /**
     * 查询所有缺陷代码编号
     *
     * @param tenantSid
     * @return List<String>
     */
    List<String> queryAllDefectNos(@Param("tenantSid") Long tenantSid);
}
