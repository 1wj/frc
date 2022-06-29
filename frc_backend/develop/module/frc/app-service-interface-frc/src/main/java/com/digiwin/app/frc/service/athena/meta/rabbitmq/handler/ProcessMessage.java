package com.digiwin.app.frc.service.athena.meta.rabbitmq.handler;

import java.io.Serializable;
import java.util.Map;


/**
 * @Description: 发送消息对象
 */

public class ProcessMessage implements Serializable {
    /**
     * 消息类型 之后消息监听到后要执行的方法
     */
    private String method;

    /**
     * 消息体数据
     */
    private Map<String, Object> data;

    public static ProcessMessage create(String method, Map<String, Object> data) {
        return new ProcessMessage(method, data);
    }

    public ProcessMessage(String method, Map<String, Object> data) {
        this.method = method;
        this.data = data;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
