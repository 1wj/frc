package com.digiwin.app.frc.service.athena.util.doucment;

import  com.digiwin.app.frc.service.athena.util.doucment.serializer.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bson.types.ObjectId;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


/**
 * @Author: zhupeng@digiwin.com,HX
 * @Datetime: 2021/5/8 11:02
 * @Description: json相关的帮助类
 * @Version: 0.0.0.1
 */
public final class JsonUtils {
    static ObjectMapper objectMapper = null;

    /**
     * 返回全局唯一的ObjectMapper
     *
     * @param
     * @return
     */
    public static ObjectMapper createObjectMapper() {
        if (objectMapper == null) {
            synchronized (JsonUtils.class) {
                if (objectMapper == null) {
                    JavaTimeModule javaTimeModule = new JavaTimeModule();
                    javaTimeModule.addSerializer(ObjectId.class, new ObjectIdSerializer());
                    javaTimeModule.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
                    javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
                    javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
                    javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
                    javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
                    javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer());
                    javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer());
                    javaTimeModule.addSerializer(Timestamp.class, new TimestampSerializer());
                    javaTimeModule.addDeserializer(Timestamp.class, new TimestampDeserializer());
                    objectMapper = Jackson2ObjectMapperBuilder.json()
                            .serializationInclusion(JsonInclude.Include.NON_NULL)
                            .failOnUnknownProperties(false)
                            .modules(javaTimeModule)
                            .build();
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    // 允许出现特殊字符和转义符
                    objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
                    // 允许出现单引号
                    objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
                }
            }
        }
        return objectMapper;
    }
}
