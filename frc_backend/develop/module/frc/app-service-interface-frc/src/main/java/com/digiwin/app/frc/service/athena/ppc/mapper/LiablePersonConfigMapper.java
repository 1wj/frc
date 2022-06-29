package com.digiwin.app.frc.service.athena.ppc.mapper;

import com.digiwin.app.frc.service.athena.ppc.domain.entity.LiablePersonConfigEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.LiablePersonConfigVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author:zhangzlz
 * @Date 2022/3/11   10:51
 */
@Mapper
public interface LiablePersonConfigMapper {
    /**
     * 增加问题责任人配置信息
     *
     * @param entities  实体类集合
     * @return 执行结果
     */
    int addLiablePersonConfigInfo(@Param("list") List<LiablePersonConfigEntity> entities);

    /**
     * 删除问题责任人配置
     * @param oidList  问题责任人主键集合
     * @param tenantSid 租户id
     * @return  执行结果
     */
    int deleteLiablePersonConfigInfo(@Param("oidList") List<String> oidList, @Param("tenantSid") Long tenantSid);

    /**
     *批量修改问题责任人配置
     *
     * @param entities  实体类集合
     * @return 执行结果
     */
    int updateBatch(@Param("list") List<LiablePersonConfigEntity> entities);


    /**
     * 查询问题责任人配置信息
     *
     * @param configFlag 配置标识
     * @param attributionNo 问题归属编号
     * @param riskLevelId 风险等级主键
     * @param sourceId 来源主键
     * @param classificationId 分类主键
     * @param solutionId 解决方案主键
     * @param tenantsid 租户id
     * @return 返回LiablePersonConfigVo类型集合
     */
    List<LiablePersonConfigVo> getLiablePersonConfigInfo(@Param("configFlag") String configFlag, @Param("attributionNo") String attributionNo,
                                                         @Param("riskLevelId") String riskLevelId, @Param("sourceId") String sourceId,
                                                         @Param("classificationId") String classificationId, @Param("solutionId") String solutionId,
                                                         @Param("tenantSid") Long tenantsid);
}
