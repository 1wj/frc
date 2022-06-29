package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionItemEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/11 11:26
 * @Version 1.0
 * @Description
 */
@Mapper
public interface QuestionItemMapper {

    /**
     * 添加物料信息
     *
     * @param entities 物料信息实体类集合
     * @return int
     */
    int addQuestionItemInfo(List<QuestionItemEntity> entities);

    /**
     * 删除物料信息
     *
     * @param oidList 待删除主键集合
     * @return int
     */
    int deleteQuestionItemInfo(List<String> oidList);

    /**
     * 更新物料信息
     *
     * @param entities 物料信息实体类集合
     * @return int
     */
    int updateBatch(List<QuestionItemEntity> entities);

    /**
     * 查询物料信息
     *
     * @param tenantSid 租户id
     * @param itemNo    物料编号
     * @param itemName  物料名称
     * @return List<QuestionItemEntity>
     */
    List<QuestionItemEntity> getQuestionItemInfo(@Param("tenantSid") Long tenantSid,
                                                 @Param("itemNo") String itemNo, @Param("itemName") String itemName);

    /**
     * 获取所有物料编号信息
     *
     * @param tenantSid
     * @return List<String>
     */
    List<String> queryAllItemNos(@Param("tenantSid") Long tenantSid);
}
