package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionSourceEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionSourceQueryVo;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionSourceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/11 15:12
 * @Version 1.0
 * @Description
 */
@Mapper
public interface QuestionSourceMapper {

    /**
     * 添加问题来源信息
     *
     * @param entities 问题来源实体类集合
     * @return int
     */
    int addQuestionSourceInfo(List<QuestionSourceEntity> entities);

    /**
     * 删除问题来源信息
     *
     * @param oidList 待删除问题来源主键集合
     * @return int
     */
    int deleteQuestionSourceInfo(List<String> oidList);

    /**
     * 更新问题来源信息
     *
     * @param entities 问题来源实体类集合
     * @return int
     */
    int updateBatch(List<QuestionSourceEntity> entities);

    /**
     * 获取问题来源信息
     *
     * @param tenantSid      租户
     * @return List<QuestionSourceEntity>
     */
    List<QuestionSourceQueryVo> getQuestionSourceInfo(@Param("tenantSid") Long tenantSid,
                                                      @Param("vo") QuestionSourceVo vo);

    /**
     * 查询所有问题来源编号信息
     *
     * @param tenantSid
     * @return List<String>
     */
    List<String> queryAllSourceNos(@Param("tenantSid") Long tenantSid);

    /**
     * 查询所有问题来源的信息
     * @return
     */
    List<QuestionSourceEntity> queryAll(@Param("tenantSid") Long tenantSid);


}
