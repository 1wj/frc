package com.digiwin.app.frc.service.athena.ppc.mapper;

import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionLiablePersonDepartmentLatitudeConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
*@Author Jiangyw
*@Date 2022/3/11
*@Time 15:21
*@Version
*/
@Mapper
public interface QuestionLiablePersonDepartmentLatitudeConfigMapper {

   /**
    * @Description 新增问题责任人部门纬度配置
    * @param entities
    * @return int
    * @author Jiangyw
    * @Date 2022/3/14
    */
   int addQuestionLiablePersonDepartmentLatitudeConfig(List<QuestionLiablePersonDepartmentLatitudeConfigEntity> entities);

   /**
    * @Description 删除问题责任人部门纬度配置
    * @param oids  主键list集合
    * @param tenantsid 租户id
    * @return int 影响行数
    * @author Jiangyw
    * @Date 2022/3/14
    */
   int deleteQuestionLiablePersonDepartmentLatitudeConfig(@Param("oids") List<String> oids, @Param("tenantsid") Long tenantsid);

   /**
    * @Description  更新问题责任人部门纬度配置
    * @param entities 实体类
    * @return int 影响行数
    * @author Jiangyw
    * @Date 2022/3/14
    */
   int updateQuestionLiablePersonDepartmentLatitudeConfig(List<QuestionLiablePersonDepartmentLatitudeConfigEntity> entities);

   /**
    * @Description 获取问题责任人部门纬度配置
    * @param configFlag 配置标识
    * @param attributionNo 问题归属编号
    * @param riskLevelId 风险等级主键
    * @param feedbackDepartmentId 部门Id
    * @param tenantSid 租户id
    * @return java.util.List<com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionLiablePersonDepartmentLatitudeConfigEntity>
    * @author Jiangyw
    * @Date 2022/3/14
    */
   List<QuestionLiablePersonDepartmentLatitudeConfigEntity> getQuestionLiablePersonDepartmentLatitudeConfig(@Param("configFlag") String configFlag, @Param("attributionNo") String attributionNo, @Param("riskLevelId") String riskLevelId, @Param("feedbackDepartmentId") String feedbackDepartmentId, @Param("acceptanceRole") String acceptanceRole, @Param("tenantsid") Long tenantSid);

   /**
    * 获取问题责任人信息
    * @param attributionNo 问题归属
    * @param riskLevelOid 风险等级
    * @param feedbackDepartmentOid 部门主键
    * @return
    */
   List<Map<String,Object>> getLiablePersonDepartmentLatitudeMessage(@Param("attributionNo") String attributionNo, @Param("riskLevelOid") String riskLevelOid, @Param("feedbackDepartmentOid") String feedbackDepartmentOid, @Param("tenantsid") Long tenantSid);

   List<Map<String,Object>> getLiablePersonMessage(@Param("attributionNo") String attributionNo, @Param("riskLevelId") String riskLevelOid,
                                                   @Param("sourceId") String sourceOid, @Param("classificationId") String classificationOid,
                                                   @Param("solutionId") String solutionId,@Param("tenantSid") Long tenantSid);
}
