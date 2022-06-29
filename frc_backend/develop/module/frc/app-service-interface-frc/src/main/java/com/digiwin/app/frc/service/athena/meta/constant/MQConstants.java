package com.digiwin.app.frc.service.athena.meta.constant;


/**
 * @Description: MQ 相关常量
 * @Author: zhupeng@digiwin.com
 * @Datetime: 2021/11/24 9:32
 * @Version: 0.0.0.1
 */

public class MQConstants {

    /**
     * 消息追踪OBJECT_ID
     */
    public static final String MESSAGE_OBJECT_ID = "message_no";

    /**
     * 消息送出:已发送，未确认
     */
    public static final int STATUS_SENDING = 100;
    /**
     * 消息发送失败
     */
    public static final int STATUS_FAILURE_SENDING = 101;
    /**
     * 消息确认：消息发送成功
     */
    public static final int SEND_SUCCESS = 190;
    /**
     * 消息消费：消费成功
     */
    public static final int CONSUME_SUCCESS = 200;
    /**
     * 消息消费：消费失败
     */
    public static final int CONSUME_FAILURE = 400;
    /**
     * 消息确认：消息失败，未到达交换机
     */
    public static final int STATUS_FAILURE_CONFIRM = 501;
    /**
     * 消息确认：消息失败，未到达指定队列
     */
    public static final int STATUS_FAILURE_RETURN = 502;
    /**
     * 最大消费次数，默认设置为3，说明就可以执行3次，3次执行不成功，丢弃
     */
    public static final int MAX_CONSUME_TIMES = 3;
}
