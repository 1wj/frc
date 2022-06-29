package com.digiwin.app.frc.service.athena.qdh.mapper;

import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.MessageQueueEntity;
import org.apache.ibatis.annotations.Param;

/**
 * @ClassName MessageQueueMapper
 * @Description 队列日志表-mapper
 * @Author HeX
 * @Date 2022/3/20 21:15
 * @Version 1.0
 **/
public interface MessageQueueMapper {
    /**
     *保存日志
     * @param messageQueueEntity
     * @return
     */
    int saveMessageQueue(MessageQueueEntity messageQueueEntity);

    /**
     * 更新日志
     * @param messageQueueEntity
     * @return
     */
    int updateMessageQueue(MessageQueueEntity messageQueueEntity);

    /**
     * 更新状态
     * @param oid
     * @param messageStatus
     * @return
     */
    int updateStatus(@Param("oid") String oid,@Param("messageStatus") int messageStatus);

    /**
     * 根据主键查询
     * @param oid
     * @return
     */
    MessageQueueEntity getMessageQueue( @Param("oid") String oid );

}
