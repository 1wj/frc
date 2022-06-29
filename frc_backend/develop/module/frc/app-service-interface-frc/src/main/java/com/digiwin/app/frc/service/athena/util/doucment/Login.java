package com.digiwin.app.frc.service.athena.util.doucment;

import com.digiwin.app.common.DWApplicationConfigUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


/**
 * @Author: Hx
 * @Datetime: 2021/12/29
 * @Description: 系统用户Login
 * @Version: 0.0.0.1
 */
public class Login {

    public static void main(String[] args) {
        System.out.println("userToken = " + getUserToken());
    }

    public static String getUserToken() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> login = new HashMap<>(16);
        login.put("username", DWApplicationConfigUtils.getProperty("dmcUserName"));
        login.put("pwdhash", SecurityUtil.getSha256(DWApplicationConfigUtils.getProperty("dmcPwd")));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity request = new HttpEntity<>(JsonUtils.createObjectMapper().writeValueAsString(login), headers);
            Map result = restTemplate.postForObject(UploadUtil.DMC_URI + "/api/dmc/v1/auth/login", request, Map.class);
            return result.get("userToken").toString();
        } catch (Exception e) {
            throw new RuntimeException("Obtain a userToken fail.", e);
        }
    }
}
