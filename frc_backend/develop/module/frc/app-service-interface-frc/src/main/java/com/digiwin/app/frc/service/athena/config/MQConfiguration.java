package com.digiwin.app.frc.service.athena.config;

import com.digiwin.app.frc.service.athena.meta.rabbitmq.consumer.ProcessManualMessageListener;
import com.digiwin.app.frc.service.athena.meta.rabbitmq.support.FastJsonConverter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Description: MQ配置类，配置队列名称和交换机
 * @Author: zhupeng@digiwin.com
 * @Datetime: 2021/11/23 13:39
 * @Version: 0.0.0.1
 */

@Configuration
public class MQConfiguration {
    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    @Qualifier("processManualMessageListener")
    private ProcessManualMessageListener processManualMessageListener;
    @Autowired
    @Qualifier("messageConverter")
    private FastJsonConverter messageConverter;

    /**
     * 消费者数量
     */
    @Value("${mq.prefetch.count:250}")
    private int prefetchCount;

    /**
     * 消费者最小数量
     */
    @Value("${mq.concurrent.consumers:1}")
    private int concurrentConsumers;
    /**
     * 消费者最大数量
     */
    @Value("${mq.max.concurrent.consumers:10}")
    private int maxConcurrentConsumers;
    /**
     * 交换机名称
     */
    @Value("${direct.exchange.name}")
    private String directExchangeName;


    /**
     * 设置交换机 名称为kmo_cloud
     *
     * @return
     */
    @Bean
    DirectExchange frcDirectExchange() {
        return new DirectExchange(directExchangeName, true, false);
    }

    /**
     * 入库
     */
    @Value("${frc.to.kmo}")
    private String frcToKmo;

    @Bean
    public Queue frcToKmoHandle() {
        return new Queue(frcToKmo, true);
    }

    //绑定交换机
    @Bean
    Binding bindingDirectForFrcToKmoHandle() {
        return BindingBuilder.bind(frcToKmoHandle()).to(frcDirectExchange()).with(frcToKmo);
    }

    /**
     * 配置监听中心工程
     *
     * @return
     */
    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory = new SimpleRabbitListenerContainerFactory();
        SimpleMessageListenerContainer simpleMessageListenerContainer = simpleRabbitListenerContainerFactory.createListenerContainer();
        simpleMessageListenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
        simpleMessageListenerContainer.addQueues(
                frcToKmoHandle());
        simpleMessageListenerContainer.setPrefetchCount(prefetchCount);
        simpleMessageListenerContainer.setConcurrentConsumers(concurrentConsumers);
        simpleMessageListenerContainer.setMaxConcurrentConsumers(maxConcurrentConsumers);
        simpleMessageListenerContainer.setMessageListener(processManualMessageListener);
        simpleMessageListenerContainer.setMessageConverter(messageConverter);
        simpleMessageListenerContainer.setAutoStartup(true);
        simpleMessageListenerContainer.setExposeListenerChannel(true);
        simpleMessageListenerContainer.start();
        return simpleRabbitListenerContainerFactory;
    }
}
