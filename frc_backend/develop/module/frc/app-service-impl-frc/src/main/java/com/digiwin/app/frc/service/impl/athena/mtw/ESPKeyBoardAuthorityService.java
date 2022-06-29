package com.digiwin.app.frc.service.impl.athena.mtw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.mtw.biz.KeyBoardAuthorityBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IESPKeyBoardAuthorityService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWEAIResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/12/1 20:01
 * @Version 1.0
 * @Description
 */
public class ESPKeyBoardAuthorityService implements IESPKeyBoardAuthorityService {

    @Autowired
    private KeyBoardAuthorityBiz keyBoardAuthorityBiz;

    @Override
    public DWEAIResult postAddKeyBoardAuthorityInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        //对参数进行解析并校验返回结果
        JSONArray dataContent =  parseData(messageBody, true);
        List<JSONObject> result = keyBoardAuthorityBiz.addKeyBoardAuthority(dataContent);
        dataResult.put("kanban_permissions_info", result);
        //处理成功
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("addSuccess"), dataResult);
    }

    @Override
    public DWEAIResult postDeleteKeyBoardAuthorityInfo(Map<String, Object> headers, String messageBody) throws Exception {
        boolean result;
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent =  parseData(messageBody, true);
            result = keyBoardAuthorityBiz.deleteKeyBoardAuthority(dataContent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return result ? new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("deleteSuccess"), null) :
                new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("deleteFail"), null);
    }

    @Override
    public DWEAIResult postUpdateKeyBoardAuthorityInfo(Map<String, Object> headers, String messageBody) throws Exception {
        boolean result;
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent = parseData(messageBody, true);
            result = keyBoardAuthorityBiz.updateKeyBoardAuthority(dataContent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return result ? new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("updateSuccess"), null) :
                new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("updateFail"), null);
    }


    @Override
    public DWEAIResult getKeyBoardAuthorityInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseData(messageBody, false);
            List<JSONObject> result = keyBoardAuthorityBiz.getKeyBoardAuthority(dataContent);
            dataResult.put("kanban_permissions_info", result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]", MultilingualismUtil.getLanguage("queryFail"), e.toString()));
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("querySuccess"), dataResult);
    }


    /**
     * 解析请求数据并是否进行校验
     *
     * @param messageBody 消息体
     * @param check       是否校验
     * @return JSONArray
     */
    private JSONArray parseData(String messageBody, boolean check) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray keyboardModelData = (JSONArray) parameter.get("kanban_permissions_info");
        if (check) {
            if (StringUtils.isEmpty(keyboardModelData) || keyboardModelData.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return keyboardModelData;
    }
}
