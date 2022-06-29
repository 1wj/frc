package com.digiwin.app.frc.service.athena.util;

import com.alibaba.fastjson.JSON;
import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.container.DWContainerContext;
import com.digiwin.app.container.exceptions.DWException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RequestClient
 * @Description (地 - 云)呼叫Client
 * @Author HeX
 * @Date 2020/3/17 11:36
 * @Version 1.0
 **/
public class RequestClient {
    /**
     * 通用请求调用
     *
     * @param requestJson
     * @return
     * @throws DWException
     */
    public static <T> T request(String url, String token, String requestJson,Class<T> clazz) throws Exception {
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_UTF8.toString());
        headers.add("token", token);
        headers.add("digi-middleware-auth-app", DWApplicationConfigUtils.getProperty("iamApToken"));

        // 解决响应压缩问题
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        // 封装请求头
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        // 请求rest,注意 .net接口需要用Map接收
        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, entity, Map.class);
        if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND || responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED || responseEntity.getStatusCode() == HttpStatus.FORBIDDEN) {
            throw new DWException(String.format("statusCode: %s", responseEntity.getStatusCode()));
        }
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new DWException(String.format("statusCode: %s", responseEntity.getStatusCode()));
        }
        Map<String, Object> body = responseEntity.getBody();
        Map<String, Object> response = (Map<String, Object>) body.get("response");
        boolean success = (boolean) response.get("success");
        if (!success) {
            throw new DWException((String) response.get("message"));
        }
        T data = JSON.parseObject(JSON.toJSONString(response.get("data")), clazz);
        return data;
    }

    /**
     * app-post通用请求调用
     *
     * @param requestJson
     * @return
     * @throws DWException
     */
    public static <T> T appRequest(String url, String token, String requestJson,Class<T> clazz) throws Exception {
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_UTF8.toString());
        headers.add("token", token);
        headers.add("digi-middleware-auth-user", token);

        // 解决响应压缩问题
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        // 封装请求头
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        // 请求rest,注意 .net接口需要用Map接收
        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, entity, Map.class);
        if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND || responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED || responseEntity.getStatusCode() == HttpStatus.FORBIDDEN) {
            throw new DWException(String.format("statusCode: %s", responseEntity.getStatusCode()));
        }
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new DWException(String.format("statusCode: %s", responseEntity.getStatusCode()));
        }
        Map<String, Object> body = responseEntity.getBody();
        Integer status = (Integer) body.get("status");
        if (status != HttpStatus.OK.value()) {
            throw new DWException((String) body.get("statusDescription"));
        }
        T data = JSON.parseObject(JSON.toJSONString(((List)body.get("response")).get(0)), clazz);
        return data;
    }

    /**
     * app-post通用请求调用
     *
     * @param requestJson
     * @return
     * @throws DWException
     */
    public static <T> T appGetRequest(String url, String token, String requestJson,String locale,Class<T> clazz) throws Exception {
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_UTF8.toString());
        headers.add("token", token);
        headers.add("digi-middleware-auth-user", token);
        headers.add("routerkey", TenantTokenUtil.getTenantSid()+"");
        if(StringUtils.isNotEmpty(locale)){
            headers.add("locale", locale);
        }
        headers.add("client-agent", "webplatform");
        // 解决响应压缩问题
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        // 封装请求头
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        // 请求rest,注意 .net接口需要用Map接收
        ResponseEntity<Map> responseEntity = restTemplate.exchange(url,HttpMethod.GET,entity,Map.class);
        if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND || responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED || responseEntity.getStatusCode() == HttpStatus.FORBIDDEN) {
            throw new DWException(String.format("statusCode: %s", responseEntity.getStatusCode()));
        }
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new DWException(String.format("statusCode: %s", responseEntity.getStatusCode()));
        }
        Map<String, Object> body = responseEntity.getBody();
        Integer status = (Integer) body.get("status");
        if (status != HttpStatus.OK.value()) {
            throw new DWException((String) body.get("statusDescription"));
        }
        Object response = body.get("response");
        String responseStr = "";
        if(response instanceof List){
            responseStr = JSON.toJSONString(((List)body.get("response")).get(0));
        }else if (response instanceof Map){
            responseStr = JSON.toJSONString(body.get("response"));
        }
        T data = JSON.parseObject(responseStr, clazz);
        return data;
    }

    /**
     * 下载文件
     *
     * @param requestJson
     * @return
     * @throws DWException
     */
    public static Resource downloadFile(String url, HttpHeaders headers, String requestJson) throws Exception {
        // 解决响应压缩问题
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        // 封装请求头
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        // 请求rest,注意 .net接口需要用Map接收
        ResponseEntity<Resource> responseEntity = restTemplate.postForEntity(url, entity, Resource.class);
        if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND || responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED || responseEntity.getStatusCode() == HttpStatus.FORBIDDEN) {
            throw new DWException(String.format("statusCode: %s", responseEntity.getStatusCode()));
        }
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new DWException(String.format("statusCode: %s", responseEntity.getStatusCode()));
        }
        return responseEntity.getBody();
    }

    /**
     * 发起一个服务编排
     *
     * @param requestJson
     * @return
     * @throws DWException
     */
    public static Map<String, Object> startWorkflow(String url, String token, String requestJson) throws Exception {
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_UTF8.toString());
        headers.add("token", token);
        headers.add("locale", "zh_CN");
        headers.add("invokerId", "KSC");
        headers.add("digi-middleware-auth-app", DWApplicationConfigUtils.getProperty("iamApToken"));
        // 解决响应压缩问题
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                HttpClientBuilder.create().build());
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        // 封装请求头
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        // 请求rest,注意 .net接口需要用Map接收
        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, entity, Map.class);
        if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND || responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED || responseEntity.getStatusCode() == HttpStatus.FORBIDDEN) {
            throw new DWException(String.format("statusCode: %s", responseEntity.getStatusCode()));
        }
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            throw new DWException(String.format("statusCode: %s", responseEntity.getStatusCode()));
        }
        Map<String, Object> body = responseEntity.getBody();
        Map<String, Object> response = (Map<String, Object>) body.get("response");
        Integer status = (Integer) body.get("status");
//        if (status.compareTo(200) != 0) {
//            throw new DWException((String) response.get("message"));
//        }
//        T data = (T) response.get("data");
        return response;
    }

    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while (-1 != (n = inputStream.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

}
