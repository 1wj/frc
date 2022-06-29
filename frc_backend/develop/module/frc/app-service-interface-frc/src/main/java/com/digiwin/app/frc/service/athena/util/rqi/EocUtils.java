package com.digiwin.app.frc.service.athena.util.rqi;

import com.alibaba.fastjson.JSON;
import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.frc.service.athena.util.IamClient;
import com.digiwin.app.service.restful.DWRequestMethod;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/1/17 23:54
 * @Version 1.0
 * @Description
 */
public class EocUtils {

    public static String getUserId(String empId) throws Exception {
        String invokeURL = DWApplicationConfigUtils.getProperty("eocUrl") + DWApplicationConfigUtils.getProperty("empInfoUrl");
        Map<String, Object> result = new HashMap<>();
        result.put("empId", empId);
        String response = IamClient.requestIAM(invokeURL, result, DWRequestMethod.POST);
        HashMap<String, Object> hashMap = JSON.parseObject(response, HashMap.class);
        Map<String, Object> data = (Map<String, Object>) hashMap.get("data");
        Map userMap = (Map) data.get("user");
        if (StringUtils.isEmpty(userMap.get("id"))) {
            throw new DWException("該員工詳細信息為null");
        }
        return (String) userMap.get("id");
    }



    public static String getEmpId(String userId) throws Exception {

        String invokeURL = DWApplicationConfigUtils.getProperty("eocUrl") + DWApplicationConfigUtils.getProperty("empInfoUrl");
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        String response = IamClient.requestIAM(invokeURL, result, DWRequestMethod.POST);
        HashMap<String, Object> hashMap = JSON.parseObject(response, HashMap.class);
        Map<String, Object> data = (Map<String, Object>) hashMap.get("data");
        String empId = (String) data.get("id");
        if (StringUtils.isEmpty(empId)) {
            throw new DWException("該員工詳細信息為null");
        }
        return empId;
    }

    public static Map getEmpIdForMap(String userId) throws Exception {
        String invokeURL = DWApplicationConfigUtils.getProperty("eocUrl") + DWApplicationConfigUtils.getProperty("empInfoUrl");
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        String response = IamClient.requestIAM(invokeURL, result, DWRequestMethod.POST);
        HashMap<String, Object> hashMap = JSON.parseObject(response, HashMap.class);
        Map<String, Object> data = (Map<String, Object>) hashMap.get("data");
        return data;
    }


    public static String getEmpName(String userId) throws Exception {

        String invokeURL = DWApplicationConfigUtils.getProperty("eocUrl") + DWApplicationConfigUtils.getProperty("empInfoUrl");
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        String response = IamClient.requestIAM(invokeURL, result, DWRequestMethod.POST);
        HashMap<String, Object> hashMap = JSON.parseObject(response, HashMap.class);
        Map<String, Object> data = (Map<String, Object>) hashMap.get("data");
        String empName = (String) data.get("name");
        if (StringUtils.isEmpty(empName)) {
            throw new DWException("該員工詳細信息為null");
        }
        return empName;
    }

    public static Map getEmpIdForMap2(String userId) throws Exception {
        String invokeURL = DWApplicationConfigUtils.getProperty("eocUrl") + DWApplicationConfigUtils.getProperty("empInfoUrl");
        Map<String, Object> result = new HashMap<>();
        result.put("empId", userId);
        String response = IamClient.requestIAM(invokeURL, result, DWRequestMethod.POST);
        HashMap<String, Object> hashMap = JSON.parseObject(response, HashMap.class);
        Map<String, Object> data = (Map<String, Object>) hashMap.get("data");
        return data;
    }
}
