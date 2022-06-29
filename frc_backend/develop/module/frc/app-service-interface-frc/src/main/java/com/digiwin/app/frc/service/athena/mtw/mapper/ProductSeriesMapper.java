package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.ProductSeriesEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/12 16:03
 * @Version 1.0
 * @Description
 */
@Mapper
public interface ProductSeriesMapper {

    /**
     * 添加产品系列信息
     *
     * @param entities 产品系列信息实体类
     * @return int
     */
    int addProductSeriesInfo(List<ProductSeriesEntity> entities);

    /**
     * 删除产品系列信息
     *
     * @param oidList 待删除主键集合
     * @return int
     */
    int deleteProductSeriesInfo(List<String> oidList);

    /**
     * 更新产品系列信息
     *
     * @param entities 产品系列信息实体类
     * @return int
     */
    int updateBatch(List<ProductSeriesEntity> entities);

    /**
     * 获取产品系列信息
     *
     * @param tenantSid    租户id
     * @param seriesNo     产品系列编号
     * @param seriesName   产品系列名称
     * @param manageStatus 是否有效
     * @param remarks      备注
     * @return List<ProductSeriesEntity>
     */
    List<ProductSeriesEntity> getProductSeriesInfo(@Param("tenantSid") Long tenantSid,
                                                   @Param("seriesNo") String seriesNo,
                                                   @Param("seriesName") String seriesName,
                                                   @Param("manageStatus") String manageStatus,
                                                   @Param("remarks") String remarks);

    /**
     * 查询所有产品系列编号信息
     *
     * @param tenantSid
     * @return List<String>
     */
    List<String> queryAllProductNos(@Param("tenantSid") Long tenantSid);
}
