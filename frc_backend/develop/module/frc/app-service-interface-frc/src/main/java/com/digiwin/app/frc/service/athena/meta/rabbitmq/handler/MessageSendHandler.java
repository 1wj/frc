package com.digiwin.app.frc.service.athena.meta.rabbitmq.handler;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.meta.constant.MQConstants;
import com.digiwin.app.frc.service.athena.qdh.biz.MessageQueueBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.MessageQueueEntity;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.module.DWModuleConfigUtils;
import com.digiwin.app.service.DWServiceContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;


/**
 * @Description: mq服务发起
 */

@Service("messageSendHandler")
public class MessageSendHandler {

    private final Logger logger = LoggerFactory.getLogger(MessageSendHandler.class);

    private final RabbitTemplate rabbitTemplate;

    private final MessageQueueBiz messageQueueBiz;

    public MessageSendHandler(RabbitTemplate rabbitTemplate,MessageQueueBiz messageQueueBiz) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageQueueBiz = messageQueueBiz;
    }


    /**
     * 发送消息，使用默认交换机
     *
     * @param routingKey 路由，默认队列名称
     * @param message    消息体
     */
    public String send(String routingKey, JSONObject message) {
        String exchangeName = DWModuleConfigUtils.getCurrentModuleProperty("direct.exchange.name");
        return this.send(exchangeName, routingKey, message);
    }

    /**
     * 发送消息
     *
     * @param exchangeName 交换机名称
     * @param routingKey   路由key或队列名称
     * @param message      消息体
     */
    public String send(String exchangeName, String routingKey, JSONObject message) {
        return this.send(exchangeName, routingKey, message, null);
    }

    /**
     * 发送消息
     *
     * @param exchangeName 交换机名称
     * @param routingKey   路由key或队列名称
     * @param message      消息体
     * @param headers      消息头,设置到消息属性中:MessageProperties.setHeader
     */
    public String send(String exchangeName, String routingKey, JSONObject message, String headers) {
        MessageQueueEntity messageQueueEntity = new MessageQueueEntity();
        messageQueueEntity.setExchangeName(exchangeName);
        messageQueueEntity.setRoutingKey(routingKey);
        messageQueueEntity.setQueueName(routingKey);
        messageQueueEntity.setMessageStatus(MQConstants.STATUS_SENDING); // 已发送，未确认
//        messageQueueEntity.setMessage(ProcessMessage.create(routingKey, message));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("method",routingKey);
        jsonObject.put("data",message.toJSONString());
        messageQueueEntity.setMessage(JSONObject.toJSONString(jsonObject));
        // 发送消息
        return this.send(messageQueueEntity, headers);
    }

    /**
     * 发送
     *
     * @param messageQueueEntity 消息记录模型
     * @param headers         消息头,设置到消息属性中:MessageProperties.setHeader
     */
    public String send(MessageQueueEntity messageQueueEntity,String headers) {
        MessageProperties messageProperties = new MessageProperties();
        if (Objects.nonNull(headers)) {
            JSONObject headObjects = JSONObject.parseObject(headers);
            headObjects.forEach(messageProperties::setHeader);
        }
        return this.send(messageQueueEntity, messageProperties);
    }

    /**
     * 发送
     * @param messageQueueEntity   消息记录模型
     * @param messageProperties 消息属性
     */
    public String send(MessageQueueEntity messageQueueEntity, MessageProperties messageProperties) {
        try {
            // 主键
            String oid = IdGenUtil.uuid();
            DWServiceContext dwServiceContext = DWServiceContext.getContext();
            dwServiceContext.setRequestBody(null);
            messageProperties.setHeader("dwServiceContext", JSON.toJSONString(dwServiceContext));
            messageQueueEntity.setOid(oid);
            messageProperties.setHeader(MQConstants.MESSAGE_OBJECT_ID,oid);
            messageQueueEntity.setHeaders(JSONObject.toJSONString(toJsonObj(messageProperties.getHeaders())));
            // 保存消息模板在日志表
            messageQueueBiz.saveMessageQueue(messageQueueEntity);
            // body
            byte[] body = messageQueueEntity.getMessage().getBytes(StandardCharsets.UTF_8);
            logger.info("即将发送消息");
            rabbitTemplate.send(messageQueueEntity.getExchangeName(), messageQueueEntity.getRoutingKey(),
                    new Message(body, messageProperties), new CorrelationData(messageQueueEntity.getOid()));
            logger.info("消息已放入队列");
        } catch (Exception e) {
            logger.error("MQ发送失败，相关信息如下：");
            logger.error("消息主键ID:{}", messageQueueEntity.getOid());
            logger.error("消息使用的交换机:{}", messageQueueEntity.getExchangeName());
            logger.error("消息使用的路由键:{}", messageQueueEntity.getRoutingKey());
            logger.error("异常消息:{}", ExceptionUtils.getMessage(e));
            logger.error("异常消息详情:{}", ExceptionUtils.getStackTrace(e));
            // 发送失败
            messageQueueEntity.setMessageStatus(MQConstants.STATUS_FAILURE_SENDING);
            // 更新日志表
            messageQueueBiz.saveMessageQueue(messageQueueEntity);
        }
        return messageQueueEntity.getOid();
    }

    private JSONObject toJsonObj(Map<String, Object> map) {
        JSONObject resultJson = new JSONObject();
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            resultJson.put(key, map.get(key));
        }
        return resultJson;
    }

    /**
     * 重新发送消息
     *
     * @param messageQueueEntity 消息记录模型
     */
    @Async
    public void retrySend(MessageQueueEntity messageQueueEntity) {
        // 3秒后重新发送
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 重试次数+1
        messageQueueEntity.doRetry();
        this.send(messageQueueEntity, messageQueueEntity.getHeaders());
    }


}
