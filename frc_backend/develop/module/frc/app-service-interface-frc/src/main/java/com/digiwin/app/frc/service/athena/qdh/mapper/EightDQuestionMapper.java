package com.digiwin.app.frc.service.athena.qdh.mapper;

import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.PersonPendingNumVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: xieps
 * @Date: 2022/3/16 16:44
 * @Version 1.0
 * @Description 8D解决方案Mapper
 */
@Mapper
public interface EightDQuestionMapper {

    /**
     * 查询人力瓶颈分析信息
     * @param userId 用户id
     * @param tenantId 租户id
     * @return  PersonPendingNumVo
     */
    PersonPendingNumVo queryPersonPendingQuestionNum(@Param("userId") String userId, @Param("tenantSid") Long tenantId);
}
