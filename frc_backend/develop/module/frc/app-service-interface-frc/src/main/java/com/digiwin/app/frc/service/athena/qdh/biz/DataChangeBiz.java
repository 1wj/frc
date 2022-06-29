package com.digiwin.app.frc.service.athena.qdh.biz;

import java.util.Map;

/**
 * @ClassName DataChangeBiz
 * @Description athena提供的侦测服务
 * @Author author
 * @Date 2021/11/10 17:37
 * @Version 1.0
 **/
public interface DataChangeBiz {
    /**
     * 侦测
     *
     * @param headers 侦测请求头参数
     * @param messageBody 侦测请求入参
     * @return 按athena规格返回
     * @throws Exception 异常处理
     */
    public Map<String, Object> dataChange(Map<String, String> headers, Map<String, Object> messageBody) throws Exception;
}
