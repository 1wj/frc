/*
 * FileName: MQConfirmCallback
 * Author:   DONGSK
 * Datetime: 2021/5/11 17:31
 * Description:
 * History:
 * 作者姓名 --修改时间 --版本号--描述
 */
package com.digiwin.app.frc.service.athena.meta.rabbitmq.support;

import com.digiwin.app.frc.service.athena.meta.constant.MQConstants;
import com.digiwin.app.frc.service.athena.meta.rabbitmq.handler.MessageSendHandler;
import com.digiwin.app.frc.service.athena.qdh.biz.MessageQueueBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.MessageQueueEntity;
import com.digiwin.app.frc.service.athena.qdh.mapper.MessageQueueMapper;
import com.digiwin.app.frc.service.athena.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;


/**
 * @Description: 消息发送到交换器Exchange后触发回调。 使用该功能需要开启确认，publisher-confirms = true
 */
@Component("mqProducerConfirm")
public class ProducerConfirm implements RabbitTemplate.ConfirmCallback {

    private final Logger logger = LoggerFactory.getLogger(ProducerConfirm.class);

    @Autowired
    MessageQueueBiz messageQueueBiz;

    @Autowired
    MessageQueueMapper queueMapper;


    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        MessageSendHandler messageSendHandler = SpringContextUtil.getBean("messageSendHandler", MessageSendHandler.class);
        MessageQueueEntity messageQueueEntity = queueMapper.getMessageQueue(correlationData.getId());
        if (Objects.isNull(messageQueueEntity)) {
            logger.error("通过主键ID:[{}],未查询到消费记录日志,信息如下:", correlationData.getId());
            logger.error("消息主键ID:[{}]", correlationData.getId());
            logger.error("消息ACK为:[{}]", ack);
            logger.error("消息原因:[{}]", cause);
            return;
        }
        if (ack) {
            logger.info("主键ID:[{}] 消息已确定", correlationData.getId());
            queueMapper.updateStatus(correlationData.getId(), MQConstants.SEND_SUCCESS);
        } else {
            logger.error("主键ID:[{}] 消息未确认，原因为:[{}]", correlationData.getId(), cause);
            messageQueueEntity.setMessageStatus(MQConstants.STATUS_FAILURE_CONFIRM);
            messageQueueEntity.setMessageText(cause);
            if (messageQueueEntity.getMessageTimes() <= MQConstants.MAX_CONSUME_TIMES) {
                messageSendHandler.retrySend(messageQueueEntity);
            } else {
                messageQueueBiz.saveMessageQueue(messageQueueEntity);
            }
        }
    }
}
