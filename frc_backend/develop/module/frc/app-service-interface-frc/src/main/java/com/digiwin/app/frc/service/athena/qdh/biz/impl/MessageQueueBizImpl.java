package com.digiwin.app.frc.service.athena.qdh.biz.impl;

import com.digiwin.app.frc.service.athena.qdh.biz.MessageQueueBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.MessageQueueEntity;
import com.digiwin.app.frc.service.athena.qdh.mapper.MessageQueueMapper;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @ClassName MessageQueueBizImpl
 * @Description 队列日志处理
 * @Author HeX
 * @Date 2022/3/20 21:46
 * @Version 1.0
 **/
@Service
public class MessageQueueBizImpl implements MessageQueueBiz {

    @Autowired
    MessageQueueMapper messageQueueMapper;

    @Override
    public void saveMessageQueue(MessageQueueEntity messageQueueEntity) {
        MessageQueueEntity entity = messageQueueMapper.getMessageQueue(messageQueueEntity.getOid());
        if (Objects.isNull(entity)) {
            messageQueueMapper.saveMessageQueue(messageQueueEntity);
        }else {
            messageQueueMapper.updateMessageQueue(messageQueueEntity);
        }
    }
}
