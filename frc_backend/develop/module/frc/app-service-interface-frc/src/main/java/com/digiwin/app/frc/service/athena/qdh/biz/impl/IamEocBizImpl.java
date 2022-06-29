package com.digiwin.app.frc.service.athena.qdh.biz.impl;

import com.alibaba.fastjson.JSON;
import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.frc.service.athena.qdh.biz.IamEocBiz;
import com.digiwin.app.frc.service.athena.util.IamClient;
import com.digiwin.app.service.restful.DWRequestMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName IamEocBizImpl
 * @Description TODO
 * @Author author
 * @Date 2020/9/16 15:25
 * @Version 1.0
 **/
@Service
public class IamEocBizImpl implements IamEocBiz {
    @Override
    public List<Map<String,Object>> getDepartments() throws Exception {
        String invokeURL = DWApplicationConfigUtils.getProperty("eocUrl")+DWApplicationConfigUtils.getProperty("departmentsUrl");
        List<Map<String,Object>> result = new ArrayList<>();
        String response = IamClient.requestIAM(invokeURL,result, DWRequestMethod.GET);


        HashMap<String,Object> hashMap = JSON.parseObject(response, HashMap.class);
        Map<String,Object> data = (Map<String, Object>) hashMap.get("data");
        if (Integer.valueOf(data.get("total").toString()) ==0) {
            return null;
        }
        List<Map<String,Object>> list = (List<Map<String, Object>>) data.get("list");
        return list;
    }

    @Override
    public List<Map<String, Object>> getUsers() throws Exception {
        String invokeURL = DWApplicationConfigUtils.getProperty("eocUrl")+DWApplicationConfigUtils.getProperty("userUrl")+"?pageNum=1&pageSize=9999999";
       Map param = new HashMap();
       String response = IamClient.requestIAM(invokeURL,param, DWRequestMethod.GET);


        HashMap<String,Object> hashMap = JSON.parseObject(response, HashMap.class);
        Map<String,Object> data = (Map<String, Object>) hashMap.get("data");
        if (Integer.valueOf(data.get("total").toString()) ==0) {
            return null;
        }
        List<Map<String,Object>> list = (List<Map<String, Object>>) data.get("list");
        return list;
    }

    @Override
    public List<Map<String, Object>> getDuty() throws Exception {
        String invokeURL = DWApplicationConfigUtils.getProperty("eocUrl")+DWApplicationConfigUtils.getProperty("dutyUrl");
        List<Map<String,Object>> result = new ArrayList<>();
        String response = IamClient.requestIAM(invokeURL,result, DWRequestMethod.GET);
        HashMap<String,Object> hashMap = JSON.parseObject(response, HashMap.class);
        Map<String,Object> data = (Map<String, Object>) hashMap.get("data");
        if (Integer.valueOf(data.get("total").toString()) ==0) {
            return null;
        }
        List<Map<String,Object>> list = (List<Map<String, Object>>) data.get("list");
        return list;
    }

    @Override
    public List<Map<String, Object>> getUsersByDutyId(String dutyId) throws Exception {
        String invokeURL = DWApplicationConfigUtils.getProperty("eocUrl")+DWApplicationConfigUtils.getProperty("dutydeptUrl");
        Map<String,Object> result = new HashMap<>();
        result.put("dutyId",dutyId);
        String response = IamClient.requestIAM(invokeURL,result, DWRequestMethod.POST);
        HashMap<String,Object> hashMap = JSON.parseObject(response, HashMap.class);
        List<Map<String,Object>> data = (List<Map<String, Object>>) hashMap.get("data");
        if (data.size() == 0) {
            return null;
        }
        List mapList = data.stream()
                .map(tar -> {
                    Map obj = new HashMap();
                    obj.put("title",tar.get("empName"));
                    obj.put("key",tar.get("empId"));
                    obj.put("type","user");
                    obj.put("isLeaf",true);
                    return obj;
                })
                .collect(Collectors.toList());
        return mapList;
    }

    @Override
    public String  getEmpDirector(String userId) throws Exception {
        String invokeURL = DWApplicationConfigUtils.getProperty("eocUrl")+DWApplicationConfigUtils.getProperty("empDirectorUrl");
        Map<String,Object> result = new HashMap<>();
        result.put("userId",userId);
        String response = IamClient.requestIAM(invokeURL,result, DWRequestMethod.POST);
        HashMap<String,Object> hashMap = JSON.parseObject(response, HashMap.class);
        Map<String,Object> data = (Map<String, Object>) hashMap.get("data");
        if (StringUtils.isEmpty(data.get("userId"))) {
            throw new DWException("该员工无直属主管！!");
        }
        StringBuilder message = new StringBuilder();
        message.append(data.get("userId") +"_"+data.get("name"));
        return message.toString();


    }

    @Override
    public Map<String,Object> getEmpUserId(String id) throws Exception {
        String invokeURL = DWApplicationConfigUtils.getProperty("eocUrl")+DWApplicationConfigUtils.getProperty("empInfoUrl");
        Map<String,Object> result = new HashMap<>();
        result.put("empId",id);
        String response = IamClient.requestIAM(invokeURL,result, DWRequestMethod.POST);
        HashMap<String,Object> hashMap = JSON.parseObject(response, HashMap.class);
        Map<String,Object> data = (Map<String, Object>) hashMap.get("data");
        Map<String,Object> userMap = (Map) data.get("user");
        if (StringUtils.isEmpty(userMap.get("id"))) {
            throw new DWException("該員工詳細信息為null");
        }
        return userMap;
    }

    @Override
    public String getDeptDirector(String deptId) throws Exception {
        String invokeURL = DWApplicationConfigUtils.getProperty("eocUrl")+DWApplicationConfigUtils.getProperty("deptDirectorUrl");
        Map<String,Object> result = new HashMap<>();
        result.put("deptId",deptId);
        String response = IamClient.requestIAM(invokeURL,result, DWRequestMethod.POST);
        HashMap<String,Object> hashMap = JSON.parseObject(response, HashMap.class);
        Map<String,Object> data = (Map<String, Object>) hashMap.get("data");
        if (StringUtils.isEmpty(data.get("userId"))) {
            throw new DWException("根据部门id获取部门主管信息失败");
        }
        StringBuilder message = new StringBuilder();
        message.append(data.get("userId") +"_"+data.get("name"));
        return message.toString();
    }

    @Override
    public List<String> getDeptUsers(String userId) throws Exception {
        String invokeURL = DWApplicationConfigUtils.getProperty("eocUrl")+DWApplicationConfigUtils.getProperty("deptUrl");
        Map<String,Object> result = new HashMap<>();
        result.put("empId",userId);
        String response = IamClient.requestIAM(invokeURL,result, DWRequestMethod.POST);
        HashMap<String,Object> hashMap = JSON.parseObject(response, HashMap.class);
        List<Map<String,Object>> data = (List<Map<String, Object>>) hashMap.get("data");
        if (data.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        List<String> resultData = new ArrayList<>();
        for (Map<String,Object> row : data) {
            String deptMessage = row.get("deptId")+"_"+row.get("deptName");
            resultData.add(deptMessage);
        }
        return resultData;

    }

    @Override
    public String getEmpIdByUserId(String userId) throws Exception {
        String invokeURL = DWApplicationConfigUtils.getProperty("eocUrl")+DWApplicationConfigUtils.getProperty("emptIdUrl");
        Map<String,Object> result = new HashMap<>();
        result.put("userId",userId);
        String response = IamClient.requestIAM(invokeURL,result, DWRequestMethod.POST);
        HashMap<String,Object> hashMap = JSON.parseObject(response, HashMap.class);
        String data = (String) hashMap.get("data");
        return data;
    }
}
