package com.digiwin.app.frc.service.athena.mtw.mapper;

import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionBackEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/11/15 17:13
 * @Version 1.0
 * @Description 问题退回处理Mapper层
 */
@Mapper
public interface QuestionBackMapper {

    /**
     * 添加问题退回信息
     *
     * @param entities 问题退回实体类集合
     * @return int
     */
    int addQuestionBackInfo(List<QuestionBackEntity> entities);

    /**
     * 删除问题退回信息
     *
     * @param oidList 待删除主键集合
     * @return int
     */
    int deleteQuestionBackInfo(List<String> oidList);

    /**
     * 批量更新问题退回信息
     *
     * @param entities 问题退回实体类集合
     * @return int
     */
    int updateBatch(List<QuestionBackEntity> entities);

    /**
     * 获取问题退回信息
     *
     * @param tenantSid  租户id
     * @param backId     退回编号
     * @param backReason 退回原因
     * @param nodeName   节点名称
     * @param nodeId     节点id
     * @return List<QuestionBackEntity>
     */
    List<QuestionBackEntity> getQuestionBackInfo(@Param("tenantSid") Long tenantSid,
                                                 @Param("backId") String backId, @Param("backReason") String backReason, @Param("nodeName") String nodeName,@Param("nodeId") String nodeId);

    /**
     * 查询所有问题编号信息
     *
     * @param tenantSid
     * @return List<String>
     */
    List<String> queryAllReturnNos(@Param("tenantSid") Long tenantSid);
}
