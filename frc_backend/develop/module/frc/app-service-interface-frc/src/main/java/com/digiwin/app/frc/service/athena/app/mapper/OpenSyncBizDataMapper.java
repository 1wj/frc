package com.digiwin.app.frc.service.athena.app.mapper;

import com.digiwin.app.frc.service.athena.app.entity.OpenSyncBizData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OpenSyncBizDataMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OpenSyncBizData record);

    int insertSelective(OpenSyncBizData record);

    OpenSyncBizData selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(OpenSyncBizData record);

    int updateByPrimaryKeyWithBLOBs(OpenSyncBizData record);

    int updateByPrimaryKey(OpenSyncBizData record);

    /**
     * 根据类型获取数据
     * @param types
     * @return
     */
    List<OpenSyncBizData> selectByType(@Param("types") List<Integer> types);
    /**
     * 根据公司Id和类型获取数据
     * @param types
     * @return
     */
    List<OpenSyncBizData> selectTypeByCorpId(@Param("types") List<Integer> types,
                                             @Param("corpId") String corpId);
}