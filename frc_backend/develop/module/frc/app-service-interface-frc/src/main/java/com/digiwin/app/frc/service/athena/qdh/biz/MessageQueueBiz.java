package com.digiwin.app.frc.service.athena.qdh.biz;

import com.digiwin.app.frc.service.athena.qdh.domain.entity.MessageQueueEntity;

/**
 * @ClassName MessageQueueBiz
 * @Description 队列日志处理
 * @Author HeX
 * @Date 2022/3/20 21:46
 * @Version 1.0
 **/
public interface MessageQueueBiz {
    /**
     * 入库队列日志落存
     * @param messageQueueEntity
     * @return
     */
    void saveMessageQueue(MessageQueueEntity messageQueueEntity);
}
