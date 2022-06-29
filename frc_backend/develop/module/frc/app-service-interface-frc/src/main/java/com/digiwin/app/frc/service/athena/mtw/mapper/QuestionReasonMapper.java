package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionReasonEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/16 17:09
 * @Version 1.0
 * @Description
 */
@Mapper
public interface QuestionReasonMapper {

    /**
     * 添加原因代码
     *
     * @param entities 原因代码实体类集合
     * @return int
     */
    int addQuestionReasonInfo(List<QuestionReasonEntity> entities);

    /**
     * 删除原因代码
     *
     * @param oidList 待删除主键集合
     * @return int
     */
    int deleteQuestionReasonInfo(List<String> oidList);

    /**
     * 更新原因代码
     *
     * @param entities 解析后数据
     * @return int
     */
    int updateBatch(List<QuestionReasonEntity> entities);

    /**
     * 查询原因代码
     *
     * @param tenantSid          租户
     * @param classificationNo   分类编号
     * @param classificationName 分类名称
     * @param reasonCode         原因代码
     * @param reasonName         原因名称
     * @param manageStatus       是否生效
     * @param remarks            备注
     * @return List<QuestionReasonEntity>
     */
    List<QuestionReasonEntity> getQuestionReasonInfo(@Param("tenantSid") Long tenantSid, @Param("classificationNo") String classificationNo,
                                                     @Param("classificationName") String classificationName, @Param("reasonCode") String reasonCode,
                                                     @Param("reasonName") String reasonName, @Param("manageStatus") String manageStatus, @Param("remarks") String remarks);

    /**
     * 查询所有类别编号信息
     *
     * @param tenantSid
     * @return List<String>
     */
    List<String> queryAllCategoryNos(@Param("tenantSid") Long tenantSid);
}
