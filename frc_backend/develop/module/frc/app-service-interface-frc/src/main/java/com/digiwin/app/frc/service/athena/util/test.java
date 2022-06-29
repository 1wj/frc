package com.digiwin.app.frc.service.athena.util;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @ClassName test
 * @Description TODO
 * @Author author
 * @Date 2021/5/10 11:49
 * @Version 1.0
 **/
public class test {
    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject().fluentPut("return_data","test");
        System.out.println(jsonObject.toJSONString());

//        String decodedConnAuthKey = "xkmgyY/JV4tPsxsiVx7EtjqGYeqOS3tbqUcJu7oQ9x//Cb0gVQesakFtqCtFSyyu4Ooz+uphVyWVGTzc3wlNpA==";
//        byte[] base64encodedBytes = Base64.getEncoder().encode(decodedConnAuthKey.getBytes());
//        String encodedConnAuthKey = new String(base64encodedBytes, StandardCharsets.UTF_8);
//        System.out.println("result --"+encodedConnAuthKey);
        }
    }

