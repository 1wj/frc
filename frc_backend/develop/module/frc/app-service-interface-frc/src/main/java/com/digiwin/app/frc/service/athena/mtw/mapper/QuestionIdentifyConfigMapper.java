package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionIdentifyConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/1/4 11:48
 * @Version 1.0
 * @Description
 */
@Mapper
public interface QuestionIdentifyConfigMapper {

    /**
     * 新增问题识别配置信息
     *
     * @param entities 实体类集合
     * @return int
     */
    int addQuestionIdentifyConfigInfo(List<QuestionIdentifyConfigEntity> entities);

    /**
     * 删除问题识别配置信息
     *
     * @param oidList 待删除主键集合
     * @return int
     */
    int deleteQuestionIdentifyConfigInfo(List<String> oidList);

    /**
     * 批量更新问题识别配置信息
     *
     * @param entities 实体类集合
     * @return int
     */
    int updateBatch(List<QuestionIdentifyConfigEntity> entities);


    /**
     * 查询所有问题分类编号
     *
     * @param tenantSid
     * @return List<String>
     */
    List<String> queryAllClassificationNos(@Param("tenantSid") Long tenantSid);

    /**
     * 查询问题识别配置信息
     *
     * @param tenantSid          租户id
     * @param classificationNo   分类编号
     * @param classificationName 分类名称
     * @param liablePersonName   负责人名称
     * @return List<QuestionIdentifyConfigEntity>
     */
    List<QuestionIdentifyConfigEntity> getQuestionIdentifyConfigInfo(@Param("tenantSid") Long tenantSid, @Param("classificationNo") String classificationNo,
                                                                     @Param("classificationName") String classificationName, @Param("liablePersonName") String liablePersonName);
}
