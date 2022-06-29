package com.digiwin.app.frc.service.athena.qdh.mapper;

import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.QuestionDetailVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName DataInstanceMapper
 * @Description TODO
 * @Author author
 * @Date 2021/11/11 1:52
 * @Version 1.0
 **/
@Mapper
public interface DataInstanceMapper {
    /**
     * 获取问题详情数据
     * @param tenantsid 租户
     * @param questionId 问题处理追踪主键
     * @return
     */
    DataInstanceEntity getQuestionDetail(@Param("tenantsid") Long tenantsid, @Param("questionId") String questionId );

    /**
     * 获取问题详情数据+问题号，用于附件落存
     * @param questionId
     * @return
     */
    DataInstanceVo getQuestionDetailForQuestionNo(@Param("questionId") String questionId);

    /**
     * 更新 问题详情
     * @author author
     * @date 2021/11/11
     **/
    int updateDataInstance(DataInstanceEntity dataInstanceEntity);

    /**
     * 新增 问题详情
     * @author author
     * @date 2021/11/11
     **/
    int insertDataInstance(DataInstanceEntity dataInstanceEntity);

    /**
     * 批量新增 实例
     * @param list
     * @return
     */
    int insertBatchDataInstance(List<DataInstanceEntity> list);


}
