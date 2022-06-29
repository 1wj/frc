package com.digiwin.app.frc.service.athena.qdh.domain.entity;

import com.digiwin.app.frc.service.athena.meta.constant.MQConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName MessageQueueEntity
 * @Description 消息格式
 * @Author HeX
 * @Date 2022/3/20 18:04
 * @Version 1.0
 **/
@Data
public class MessageQueueEntity  extends BaseEntity {
    /**
     * 消息表主键
     */
    private String oid;

    /**
     * 交换机
     */
    @JsonProperty("exchange_name")
    private String exchangeName;
    /**
     * 路由键
     */
    @JsonProperty("routing_key")
    private String routingKey;
    /**
     * 队列名称
     */
    @JsonProperty("queue_name")
    private String queueName;
    /**
     * 消息
     */
    @JsonProperty("message")
    private String message;
    /**
     * 消息头内容
     */
    @JsonProperty("headers")
    private String headers;

    /**
     * 消息状态
     */
    @JsonProperty("message_status")
    private int messageStatus = MQConstants.STATUS_SENDING;

    /**
     * 消费次数 默认第一次消费
     */
    @JsonProperty("message_times")
    private int messageTimes = 0;

    /**
     * 消息信息
     */
    @JsonProperty("message_text")
    private String messageText;

//    public void addHeader(String key, String value) {
//        headers.put(key, value);
//    }


    public void doRetry() {
        this.messageTimes++;
    }

}
