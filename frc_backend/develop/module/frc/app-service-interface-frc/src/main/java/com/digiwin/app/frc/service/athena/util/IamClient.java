package com.digiwin.app.frc.service.athena.util;

import com.alibaba.fastjson.JSONArray;
import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.resource.DWResourceBundleUtils;
import com.digiwin.app.service.DWServiceContext;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.iam.*;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @ClassName IAMClient
 * @Description IAM请求中心
 * @Author author
 * @Date 2020/5/26 14:47
 * @Version 1.0
 **/
public class IamClient {

    /**
     * 请求IAM相关URL
     * @param invokeURL url
     * @param params requestBody
     * @return 出参
     * @throws Exception 抛异常
     */
    public static List<Map<String,Object>> requestIAM1(String invokeURL,Map<String, Object> params) throws Exception {
        //宣告ServiceModel
        ServiceModel serviceModel = new ServiceModel();
        //調用IAM request body
        serviceModel.setInvokeURL(invokeURL);
        serviceModel.setParams(params);
        String token = DWServiceContext.getContext().getToken();
        if(!StringUtils.isEmpty(token)) {
            serviceModel.setToken(token);
        }
        //調用IAM http method
        serviceModel.setRequestMethod(DWRequestMethod.GET);
        //調用IAM，取得回應物件
        //request header平台會自動加上digi-middleware-auth-app和digi-middleware-auth-user
        HttpResponseModel iamResponse = (HttpResponseModel) IAMService.invoke(serviceModel);
        //取得status code
        int statusCode = iamResponse.getHttpStatusCode();
        if (statusCode != 200) {
            throw new DWException("获取IAM人员信息失败！");
        }
        //取得response body
        String responseBody = iamResponse.getResponseBody();
        List<Map<String,Object>> purchaseOrders = (List<Map<String,Object>>) JSONArray.parseObject(responseBody, List.class);
        return purchaseOrders;
    }

    public static String requestIAM(String invokeURL,Object params,DWRequestMethod dwRequestMethod) throws Exception {
        //宣告ServiceModel
        ServiceModel serviceModel = new ServiceModel();
        //調用IAM http method
        serviceModel.setRequestMethod(dwRequestMethod);
        //傳遞user token
        String token = DWServiceContext.getContext().getToken();
        if(StringUtils.isNotBlank(token)) {
            serviceModel.setToken(token);
        }

        //調用IAM，取得回應物件
        //request header平台會自動加上digi-middleware-auth-app
        Map<String, String> requestHeader = IAMHttpRequester.createRequestHeader(serviceModel);
        //requestBody自行處理
        Gson jsonParser = new Gson();
        String requestBody = jsonParser.toJson(params);

        HttpRequestModel requestModel = IAMHttpRequester.getHttpRequestModel();

        requestModel.setRequestBody(requestBody);
        requestModel.setRequestHeader(requestHeader);
        requestModel.setRequestMethod(serviceModel.getRequestMethod());
        requestModel.setUrl(invokeURL);

        HttpResponseModel iamResponse = HttpRequester.submitRequest(requestModel);
        //取得status code
        int httpStatusCode = iamResponse.getHttpStatusCode();
        if (httpStatusCode != IAMHttpStatusCode.SUCCESS) {
            throw new DWException(String.format("statusCode: %s,responseBody:%s", httpStatusCode,iamResponse.getResponseBody().toString()));
        }
        //取得response body
        String responseBody = iamResponse.getResponseBody();
        return responseBody;
    }

    /**
     * iam-request,入参传入token
     * @param invokeURL url
     * @param params 参数
     * @param dwRequestMethod 请求方式
     * @param token user_token
     * @param headerFlag true=自定义appToken  false = 自动
     * @return response
     * @throws Exception 异常检测
     */
    public static String requestIAMWithToken(String invokeURL,Object params,DWRequestMethod dwRequestMethod,String token,Boolean headerFlag) throws Exception {
        //宣告ServiceModel
        ServiceModel serviceModel = new ServiceModel();
        //調用IAM http method
        serviceModel.setRequestMethod(dwRequestMethod);
        //傳遞user token
        serviceModel.setToken(token);
        //調用IAM，取得回應物件
        Map<String, String> requestHeader = null;
        if (headerFlag) {
            // 自定义header
            requestHeader = createRequestHeader(serviceModel);
        }else {
            //request header平台會自動加上digi-middleware-auth-app
            requestHeader = IAMHttpRequester.createRequestHeader(serviceModel);
        }
        //requestBody自行處理
        Gson jsonParser = new Gson();
        String requestBody = jsonParser.toJson(params);

        HttpRequestModel requestModel = IAMHttpRequester.getHttpRequestModel();

        requestModel.setRequestBody(requestBody);
        requestModel.setRequestHeader(requestHeader);
        requestModel.setRequestMethod(serviceModel.getRequestMethod());
        requestModel.setUrl(invokeURL);

        HttpResponseModel iamResponse = HttpRequester.submitRequest(requestModel);
        //取得status code
        int httpStatusCode = iamResponse.getHttpStatusCode();
        if (httpStatusCode != IAMHttpStatusCode.SUCCESS) {
            throw new DWException(String.format("statusCode: %s,responseBody:%s", httpStatusCode,iamResponse.getResponseBody().toString()));
        }
        //取得response body
        String responseBody = iamResponse.getResponseBody();
        return responseBody;
    }

    public static Map<String, String> createRequestHeader(ServiceModel model) throws Exception {
        Map<String, String> header = new HashMap();
        header.put("Content-Type", DWApplicationConfigUtils.getProperty("iamContentType") + ";" + DWApplicationConfigUtils.getProperty("iamCharset"));
        String tokenMode = System.getProperty("serverHttpTokenmode");
        if (StringUtils.isNotBlank(tokenMode) && tokenMode.equals("IAM")) {
            header.put("digi-middleware-auth-user", model.getToken());
        }
        Locale locale = DWResourceBundleUtils.getCurrentLocale();
        if (locale != null) {
            header.put("Accept-Language", locale.toLanguageTag());
        }

        return header;
    }

}
