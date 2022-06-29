package com.digiwin.app.frc.service.impl.athena.mtw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.mtw.biz.KeyBoardAuthorityBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IKeyBoardAuthorityService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2021/12/1 16:11
 * @Version 1.0
 * @Description
 */
public class KeyBoardAuthorityService implements IKeyBoardAuthorityService {

    @Autowired
    private KeyBoardAuthorityBiz keyBoardAuthorityBiz;

    @Override
    public DWServiceResult addKeyBoardAuthorityInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseData(messageBody, true);
            List<JSONObject> resultInfo = keyBoardAuthorityBiz.addKeyBoardAuthority(dataContent);
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("addSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult deleteKeyBoardAuthorityInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseData(messageBody, true);
            boolean resultInfo = keyBoardAuthorityBiz.deleteKeyBoardAuthority(dataContent);
            if (resultInfo) {
                result.setSuccess(true);
                result.setMessage(MultilingualismUtil.getLanguage("deleteSuccess"));
            } else {
                result.setSuccess(false);
                result.setMessage(MultilingualismUtil.getLanguage("deleteFail"));
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult updateKeyBoardAuthorityInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent = parseData(messageBody, true);
            boolean resultInfo = keyBoardAuthorityBiz.updateKeyBoardAuthority(dataContent);
            if (resultInfo) {
                result.setSuccess(true);
                result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
            } else {
                result.setSuccess(false);
                result.setMessage(MultilingualismUtil.getLanguage("updateFail"));
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult getKeyBoardAuthorityInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseData(messageBody, false);
            List<JSONObject> resultInfo = keyBoardAuthorityBiz.getKeyBoardAuthority(dataContent);
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(MultilingualismUtil.getLanguage("queryFail"));
        }
        return result;
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
