package com.digiwin.app.frc.service.athena.ppc.mapper;

import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionConfirmConfigEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.model.QuestionConfirmConfigQueryModel;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.QuestionConfirmConfigVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/15 15:02
 * @Version 1.0
 * @Description
 */
@Mapper
public interface QuestionConfirmConfigMapper {



    /**
     * 添加问题确认配置信息
     *
     * @param entities 实体类集合
     * @return int
     */
    int addQuestionConfirmConfigInfo(List<QuestionConfirmConfigEntity> entities);

    /**
     * 删除问题确认信息
     *
     * @param oidList 主键集合
     * @param tenantSid 租户id
     * @return int
     */
    int deleteQuestionConfirmConfigInfo(@Param("oidList") List<String> oidList, @Param("tenantSid") Long tenantSid);

    /**
     * 更新问题确认信息
     *
     * @param entities 实体类集合
     * @return int
     */
    int updateBatch(List<QuestionConfirmConfigEntity> entities);

    /**
     * 添加问题确认信息
     *
     * @param tenantSid 租户id
     * @param model  moel
     * @return List<QuestionConfirmConfigVo>
     */
    List<QuestionConfirmConfigVo> getQuestionConfirmConfig(@Param("tenantSid") Long tenantSid,
                                                           @Param("model") QuestionConfirmConfigQueryModel model,
                                                           @Param("feedBackInfo") String feedBackInfo);
}
