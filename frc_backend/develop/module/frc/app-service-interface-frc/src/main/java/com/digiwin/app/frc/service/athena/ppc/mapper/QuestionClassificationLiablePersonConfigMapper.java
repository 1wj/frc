package com.digiwin.app.frc.service.athena.ppc.mapper;


import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionClassificationLiablePersonConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
*@ClassName: QuestionClassificationLiablePersonConfigMapper
*@Description 问题分类和问题责任人配置关系mapper
*@Author Jiangyw
*@Date 2022/3/28
*@Version 1.0
*/
@Mapper
public interface QuestionClassificationLiablePersonConfigMapper {

    /**
     * @Description 新增问题分类和问题责任人配置关系主键
     * @param entities 实体类
     * @return int
     * @author Jiangyw
     * @Date 2022/3/28
     */
    int addQuestionClassificationLiablePersonConfig(List<QuestionClassificationLiablePersonConfigEntity> entities);

    /**
     * @Description 根据责任人配置id删除
     * @param LiablePersonId
     * @param tenantsid
     * @return int
     * @author Jiangyw
     * @Date 2022/3/29
     */
    int deleteQuestionClassificationLiablePersonConfigByLiablePersonId(@Param("oidList") List<String> LiablePersonId, @Param("tenantsid") Long tenantsid);
}
