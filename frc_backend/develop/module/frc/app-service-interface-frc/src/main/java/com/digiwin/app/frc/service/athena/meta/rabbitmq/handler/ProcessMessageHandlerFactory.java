package com.digiwin.app.frc.service.athena.meta.rabbitmq.handler;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.util.SpringContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * @Description: 消息消费者业务接口实现工厂
 */
@Component("processMessageHandlerFactory")
public class ProcessMessageHandlerFactory {

    private final Logger logger = LoggerFactory.getLogger(ProcessMessageHandlerFactory.class);


    private Map<String, AbsProcessMessageHandler> messageHandler;

    private Map<String, AbsProcessMessageHandler> getHandler() {
        if (Objects.isNull(messageHandler)) {
            synchronized (ProcessMessageHandlerFactory.class) {
                if (Objects.isNull(messageHandler)) {
                    messageHandler = new HashMap<>();
                    SpringContextUtil.getBeansOfType(AbsProcessMessageHandler.class).values().forEach(processMQMessageHandler ->
                            messageHandler.put(processMQMessageHandler.getMethod(), processMQMessageHandler)
                    );
                }
            }
        }
        return messageHandler;
    }

    /**
     * 应对内部mq发送以及消费
     *
     * @param message
     * @throws Exception
     */
    public void processMQMessage(Message message) throws Exception {
        //解析参数
        JSONObject jsonObject = JSONObject.parseObject(message.getBody(), JSONObject.class);
        logger.info("展示消息信息"+jsonObject.toJSONString());
        String method = jsonObject.getString("method");
        Map<String, Object> data = jsonObject.getJSONObject("data");
        AbsProcessMessageHandler absProcessMessageHandler = getHandler().get(method);
        if (Objects.isNull(absProcessMessageHandler)) {
            logger.error("{} ProcessMessage method is invalid, method={}", this.getClass().getSimpleName(), method);
            return;
        }
        logger.info("absProcessMessageHandler.execute即将调用");
        absProcessMessageHandler.execute(data);
    }

}
