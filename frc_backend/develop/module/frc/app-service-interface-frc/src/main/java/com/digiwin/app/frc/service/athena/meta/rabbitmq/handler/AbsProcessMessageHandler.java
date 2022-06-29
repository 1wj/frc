package com.digiwin.app.frc.service.athena.meta.rabbitmq.handler;

import java.util.Map;


/**
 * MQ消息处理抽象类
 */
public abstract class AbsProcessMessageHandler {
    /**
     * 消息类型
     */
    private String method;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public AbsProcessMessageHandler(String method) {
        this.method = method;
    }

    public abstract void execute(Map<String, Object> data) throws Exception;
}
