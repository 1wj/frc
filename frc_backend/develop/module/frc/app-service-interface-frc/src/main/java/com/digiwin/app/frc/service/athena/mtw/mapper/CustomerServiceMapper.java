package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.CustomerServiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/12 11:26
 * @Version 1.0
 * @Description
 */
@Mapper
public interface CustomerServiceMapper {

    /**
     * 添加客服对接经销商信息
     *
     * @param entities 客服对接经销商信息实体类集合
     * @return int
     */
    int addCustomerServiceInfo(List<CustomerServiceEntity> entities);

    /**
     * 删除客服对接经销商信息
     *
     * @param oidList 待删除主键集合
     * @return int
     */
    int deleteCustomerServiceInfo(List<String> oidList);

    /**
     * 更新客服对接经销商信息
     *
     * @param entities 客服对接经销商信息实体类集合
     * @return int
     */
    int updateBatch(List<CustomerServiceEntity> entities);

    /**
     * 获取客服对接经销商信息
     *
     * @param tenantSid           租户
     * @param customerServiceId   客服人员id
     * @param customerServiceName 客服人员姓名
     * @param supplierId          供应商联络人id
     * @param supplierName        供应商联络人姓名
     * @return List<CustomerServiceEntity>
     */
    List<CustomerServiceEntity> getCustomerServiceInfo(@Param("tenantSid") Long tenantSid,
                                                       @Param("customerServiceId") String customerServiceId, @Param("customerServiceName") String customerServiceName,
                                                       @Param("supplierId") String supplierId, @Param("supplierName") String supplierName);

    /**
     * 查询所有经销商名称
     *
     * @param tenantSid
     * @return List<String>
     */
    List<String> queryAllDealerIds(@Param("tenantSid") Long tenantSid);
}
