package com.digiwin.app.frc.service.athena.meta.rabbitmq.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @Description: mq消息转化类，
 * 必须要加，不然识别不了注解  json转换器，
 * 消息可以自动根据转换器转换格式，
 * 不配置时默认为java序列化，可以自行配置
 */
@Component("messageConverter")
public class FastJsonConverter extends AbstractMessageConverter {
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final Map<String, Function<Message, Object>> MSG_CONVERT_MAP = new HashMap<>();

    static {
        MSG_CONVERT_MAP.put(MessageProperties.CONTENT_TYPE_BYTES, message -> message.getBody());
        MSG_CONVERT_MAP.put(MessageProperties.CONTENT_TYPE_TEXT_PLAIN, message -> {
            try {
                return new String(message.getBody(), DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException e) {
                throw new MessageConversionException(
                        "Failed to convert Message content", e);
            }
        });
        MSG_CONVERT_MAP.put(MessageProperties.CONTENT_TYPE_JSON, message -> {
            try {
                return JSON.parse(new String(message.getBody(), DEFAULT_CHARSET));
            } catch (UnsupportedEncodingException e) {
                throw new MessageConversionException(
                        "Failed to convert Message content", e);
            }
        });
    }

    public FastJsonConverter() {
        super();
    }

    @Override
    public Object fromMessage(Message message)
            throws MessageConversionException {
        MessageProperties messageProperties = message.getMessageProperties();
        return MSG_CONVERT_MAP.containsKey(messageProperties.getContentType()) ?
                MSG_CONVERT_MAP.get(messageProperties.getContentType()).apply(message) : message.getBody();

    }

    @Override
    protected Message createMessage(Object objectToConvert,
                                    MessageProperties messageProperties)
            throws MessageConversionException {
        byte[] bytes = null;
        try {
            if (objectToConvert instanceof byte[]) {
                messageProperties.setContentType(MessageProperties.CONTENT_TYPE_BYTES);
                bytes = (byte[]) objectToConvert;
            } else if (objectToConvert instanceof CharSequence) {
                messageProperties.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
                messageProperties.setContentEncoding(DEFAULT_CHARSET);
                bytes = objectToConvert.toString().getBytes(DEFAULT_CHARSET);
            } else {
                messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                String jsonString = JSON.toJSONString(objectToConvert, SerializerFeature.WriteClassName);
                bytes = jsonString.getBytes(DEFAULT_CHARSET);
            }
        } catch (UnsupportedEncodingException e) {
            throw new MessageConversionException(
                    "Failed to convert Message content", e);
        }
        if (bytes != null) {
            messageProperties.setContentLength(bytes.length);
        }
        return new Message(bytes, messageProperties);
    }

}

