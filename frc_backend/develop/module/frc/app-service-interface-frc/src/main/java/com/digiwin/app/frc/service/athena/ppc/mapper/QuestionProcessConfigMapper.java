package com.digiwin.app.frc.service.athena.ppc.mapper;

import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionProcessConfigEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.model.QuestionProcessConfigModel;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.QuestionProcessConfigVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/17 10:42
 * @Version 1.0
 * @Description
 */
@Mapper
public interface QuestionProcessConfigMapper {


    /**
     * 添加问题处理配置信息
     *
     * @param entities 实体类集合
     * @return int
     */
    int addQuestionProcessConfigInfo(List<QuestionProcessConfigEntity> entities);

    /**
     * 删除问题处理配置信息
     *
     * @param oidList 主键集合
     * @param tenantSid 租户id
     * @return int
     */
    int deleteQuestionProcessConfigInfo(@Param("oidList") List<String> oidList, @Param("tenantSid") Long tenantSid);

    /**
     * 更新问题处理配置信息
     *
     * @param entities 实体类集合
     * @return int
     */
    int updateBatch(List<QuestionProcessConfigEntity> entities);

    /**
     * 获取问题处理配置信息
     *
     * @param tenantSid 租户id
     * @param model model
     * @return  List<QuestionProcessConfigVo>
     */
    List<QuestionProcessConfigVo> getQuestionProcessConfig(@Param("tenantSid") Long tenantSid,
                                                           @Param("model") QuestionProcessConfigModel model);
}
