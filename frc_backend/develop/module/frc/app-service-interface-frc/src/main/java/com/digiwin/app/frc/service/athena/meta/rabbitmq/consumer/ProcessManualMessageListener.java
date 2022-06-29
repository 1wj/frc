package com.digiwin.app.frc.service.athena.meta.rabbitmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.meta.constant.MQConstants;
import com.digiwin.app.frc.service.athena.meta.rabbitmq.handler.ProcessMessageHandlerFactory;
import com.digiwin.app.frc.service.athena.qdh.biz.MessageQueueBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.MessageQueueEntity;
import com.digiwin.app.frc.service.athena.qdh.mapper.MessageQueueMapper;
import com.digiwin.app.service.DWServiceContext;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;


/**
 * @Description: 消息监听类(手动确认消息)
 */

@Component("processManualMessageListener")
public class ProcessManualMessageListener implements ChannelAwareMessageListener {

    @Autowired
    MessageQueueBiz messageQueueBiz;

    @Autowired
    MessageQueueMapper messageQueueMapper;

    private final Logger logger = LoggerFactory.getLogger(ProcessManualMessageListener.class);

    /**
     * MQ消息处理工厂类
     */
    @Autowired
    @Qualifier("processMessageHandlerFactory")
    private ProcessMessageHandlerFactory processMessageHandlerFactory;


    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        logger.info("进入消息监听服务");
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        MessageQueueEntity messageQueueEntity = null;
        String oid = "";
        try {
            oid = message.getMessageProperties().getHeaders().get(MQConstants.MESSAGE_OBJECT_ID).toString();
            // 根据oid查询mq日志表
            logger.info("查询日志信息");
            messageQueueEntity = messageQueueMapper.getMessageQueue(oid);
            if (StringUtils.isEmpty(messageQueueEntity)) {
                logger.error("根据主键ID:{},未查出队列消息.", oid);
                channel.basicReject(deliveryTag, false);
            }
            //dw上下文获取
//            JSONObject headers = JSONObject.parseObject(messageQueueEntity.getHeaders());
            HashMap headers = JSON.parseObject(messageQueueEntity.getHeaders(), HashMap.class);
            String dwServiceContext = (String) headers.get("dwServiceContext");
            DWServiceContext serviceContext = JSON.parseObject(dwServiceContext, DWServiceContext.class);
            DWServiceContext.setContext(serviceContext);
            //消息次数+1
            messageQueueEntity.setMessageTimes(messageQueueEntity.getMessageTimes() + 1);
            // 消费消息
            logger.info("进入消息消费服务");
            processMessageHandlerFactory.processMQMessage(message);
            // 消息确认
            messageConfirm(deliveryTag, messageQueueEntity, channel, 1);
        } catch (Exception e) {
            logger.error("~~~MQ执行异常~~~~");
            logger.error(ExceptionUtils.getMessage(e));
            e.printStackTrace();
            if (messageQueueEntity.getMessageTimes() <= MQConstants.MAX_CONSUME_TIMES) {
                // 消息否认，重新回到队列
                messageConfirm(deliveryTag, messageQueueEntity, channel, 3);
            } else {
                // 消息否认，不回到队列
                messageConfirm(deliveryTag, messageQueueEntity, channel, 2);
            }
        } finally {
            messageQueueBiz.saveMessageQueue(messageQueueEntity);
        }
    }

    private void messageConfirm(long deliveryTag, MessageQueueEntity messageQueueEntity, Channel channel, int type) {
        try {
            switch (type) {
                case 1:
                    logger.info("主键ID:[{}] 执行第[{}]次,消费成功", messageQueueEntity.getOid(), messageQueueEntity.getMessageTimes());
                    messageQueueEntity.setMessageText("消费成功");
                    messageQueueEntity.setMessageStatus(MQConstants.CONSUME_SUCCESS);
                    channel.basicAck(deliveryTag, false);
                    break;
                case 2:
                    logger.error("主键ID:[{}] 执行第[{}]次,不在消费，丢弃", messageQueueEntity.getOid(), messageQueueEntity.getMessageTimes());
                    messageQueueEntity.setMessageText("消费失败");
                    messageQueueEntity.setMessageStatus(MQConstants.CONSUME_FAILURE);
                    channel.basicReject(deliveryTag, false);
                    break;
                case 3:
                    Thread.sleep(5000);
                    logger.error("主键ID:[{}] 执行第[{}]次,继续返回队列执行", messageQueueEntity.getOid(), messageQueueEntity.getMessageTimes());
                    messageQueueEntity.setMessageText("消费失败,重新消费");
                    channel.basicNack(deliveryTag, false, true);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("主键ID:{};异常message:{}", messageQueueEntity.getOid(), ExceptionUtils.getStackTrace(e));
        }

    }
}
