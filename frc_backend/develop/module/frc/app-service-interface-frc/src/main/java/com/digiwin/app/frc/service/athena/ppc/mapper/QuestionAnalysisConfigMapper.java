package com.digiwin.app.frc.service.athena.ppc.mapper;

import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionAnalysisConfigEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.model.QuestionAnalysisConfigModel;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.QuestionAnalysisConfigVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/17 14:50
 * @Version 1.0
 * @Description
 */
@Mapper
public interface QuestionAnalysisConfigMapper {

    /**
     * 删除问题分析配置信息
     *
     * @param oidList 主键集合
     * @param tenantSid 租户id
     * @return  int
     */
    int deleteQuestionAnalysisConfigInfo(@Param("oidList") List<String> oidList, @Param("tenantSid") Long tenantSid);


    /**
     * 添加问题分析配置信息
     *
     * @param entities 实体类集合
     * @return int
     */
    int addQuestionAnalysisConfigInfo(List<QuestionAnalysisConfigEntity> entities);


    /**
     * 查询问题分析配置信息
     *
     * @param tenantSid 租户id
     * @param model model
     * @return List<QuestionAnalysisConfigVo>
     */
    List<QuestionAnalysisConfigVo> getQuestionAnalysisConfig(@Param("tenantSid") Long tenantSid,
                                                             @Param("model") QuestionAnalysisConfigModel model,
                                                             @Param("feedBackInfo") String feedBackInfo);

    /**
     * 更新问题分类信息
     *
     * @param entities 实体类集合
     * @return int
     */
    int updateBatch(List<QuestionAnalysisConfigEntity> entities);
}
