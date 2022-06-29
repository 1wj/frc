package com.digiwin.app.frc.service.athena.ppc.mapper;

import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionAcceptanceConfigEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.model.QuestionAcceptanceConfigModel;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.QuestionAcceptanceConfigVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/17 16:36
 * @Version 1.0
 * @Description
 */
@Mapper
public interface QuestionAcceptanceConfigMapper {

    /**
     * 添加问题验收配置信息
     *
     * @param entities 实体类集合
     * @return int
     */
    int addQuestionAcceptanceConfigInfo(List<QuestionAcceptanceConfigEntity> entities);

    /**
     * 删除问题验收配置信息
     *
     * @param oidList   主键集合
     * @param tenantSid 租户id
     * @return int
     */
    int deleteQuestionAcceptanceConfigInfo(@Param("oidList") List<String> oidList, @Param("tenantSid") Long tenantSid);


    /**
     * 更新问题验收配置信息
     *
     * @param entities 实体类集合
     * @return int
     */
    int updateBatch(List<QuestionAcceptanceConfigEntity> entities);

    /**
     * 查询问题验收配置信息
     *
     * @param tenantSid 租户id
     * @param model model
     * @return List<QuestionAcceptanceConfigVo>
     */
    List<QuestionAcceptanceConfigVo> getQuestionAcceptanceConfig(@Param("tenantSid") Long tenantSid, @Param("model") QuestionAcceptanceConfigModel model,
                                                                 @Param("feedBackInfo") String feedBackInfo);
}
