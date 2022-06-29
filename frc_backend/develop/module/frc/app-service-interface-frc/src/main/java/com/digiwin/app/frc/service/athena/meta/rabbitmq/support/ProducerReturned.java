
package com.digiwin.app.frc.service.athena.meta.rabbitmq.support;

import com.digiwin.app.frc.service.athena.meta.constant.MQConstants;
import com.digiwin.app.frc.service.athena.qdh.biz.MessageQueueBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.MessageQueueEntity;
import com.digiwin.app.frc.service.athena.qdh.mapper.MessageQueueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;


/**
 * @Description: 通过实现ReturnCallback接口，
 * 如果消息从交换器发送到对应队列失败时触发
 * 比如根据发送消息时指定的routingKey找不到队列时会触发
 * 使用该功能需要开启确认publisher-returns = true
 */

@Component("mqProducerReturned")
public class ProducerReturned implements RabbitTemplate.ReturnCallback {
    private final Logger logger = LoggerFactory.getLogger(ProducerReturned.class);

    @Autowired
    MessageQueueBiz messageQueueBiz;

    @Autowired
    MessageQueueMapper queueMapper;

    /**
     * returnedMessage
     *
     * @param message    消息对象
     * @param replyCode  错误码
     * @param replyText  错误信息
     * @param exchange   交换机
     * @param routingKey 路由键
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        String oid = message.getMessageProperties().getHeaders().get(MQConstants.MESSAGE_OBJECT_ID).toString();
        MessageQueueEntity messageQueueEntity = queueMapper.getMessageQueue(oid);
        if (Objects.isNull(messageQueueEntity)) {
            logger.error("通过主键ID:[{}],未查询到消费记录日志,信息如下:", oid);
            logger.error("消息主键ID:[{}]", oid);
            logger.error("消息使用的交换机:[{}]", exchange);
            logger.error("消息使用的路由键:[{}]", routingKey);
            logger.error("消息被退回:[{}]", message);
            logger.error("退回代号:[{}]", replyCode);
            logger.error("退回描述:[{}]", replyText);
        } else {
            messageQueueEntity.setMessageStatus(MQConstants.STATUS_FAILURE_RETURN);
            messageQueueEntity.setMessageText(String.format("消息图号代号:[%s];消息退回描述:[%s]", replyCode, replyText));
            messageQueueBiz.saveMessageQueue(messageQueueEntity);
        }

    }
}
