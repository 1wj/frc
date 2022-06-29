package com.digiwin.app.frc.service.athena.app.mapper;

import com.digiwin.app.frc.service.athena.app.entity.FrcDingdingUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 钉钉用户mapper
 */
public interface FrcDingdingUserMapper {
    int deleteByPrimaryKey(@Param("id") String id, @Param("cropId") String cropId);

    int insert(FrcDingdingUser record);

    int insertSelective(FrcDingdingUser record);

    FrcDingdingUser selectByPrimaryKey(@Param("id") String id, @Param("cropId") String cropId);

    int updateByPrimaryKeySelective(FrcDingdingUser record);

    int updateByPrimaryKey(FrcDingdingUser record);


    /**
     * 根据钉钉用户id和租户sid获取企业id
     * @param id
     * @param tenantsid
     * @return
     */
    FrcDingdingUser selectCorpId(@Param("id") String id, @Param("tenantsid") Long tenantsid);

    /**
     * 根据企业id和管理员类型获取用户列表id
     * @param cropId
     * @param type
     * @return
     */
    List<String> selectUserByCorpId(@Param("cropId") String cropId,@Param("type") Integer type);

    /**
     * 查询该租户下管理员信息
     * @param cropId cropId
     * @return 管理员信息
     */
    FrcDingdingUser selectAdminMessage(@Param("cropId") String cropId);

    /**
     * 根据ID and cropId查询
     * @param id 钉钉id
     * @param cropId cropId
     * @return 普通用户信息
     */
    FrcDingdingUser selectByIdAndCropId(@Param("id") String id, @Param("cropId") String cropId);

    int insertList(List<FrcDingdingUser> records);

}