package com.digiwin.app.frc.service.impl.athena.mtw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.mtw.biz.KeyBoardTemplateBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IKeyBoardTemplateService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/30 11:35
 * @Version 1.0
 * @Description
 */
public class KeyBoardTemplateService implements IKeyBoardTemplateService {

    @Autowired
    private KeyBoardTemplateBiz keyBoardTemplateBiz;

    @Override
    public DWServiceResult addKeyBoardTemplateInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseData(messageBody, true);
            List resultInfo = keyBoardTemplateBiz.addKeyBoardTemplate(dataContent);
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
    public DWServiceResult deleteKeyBoardTemplateInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseData(messageBody, true);
            keyBoardTemplateBiz.deleteKeyBoardTemplate(dataContent);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("deleteSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult updateKeyBoardTemplateInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //对参数进行解析并校验返回结果
            JSONArray keyboardModelData = parseData(messageBody, true);
            keyBoardTemplateBiz.updateKeyBoardTemplate(keyboardModelData);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult getKeyBoardTemplateInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //对参数进行解析返回结果
            JSONArray keyboardModelData = parseData(messageBody, false);
            List<Map<String,Object>> resultInfo = keyBoardTemplateBiz.getKeyBoardTemplate(keyboardModelData);
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }


    /**
     * 解析请求数据并是否进行校验
     *
     * @param messageBody 消息体
     * @param check 是否校验
     * @return  JSONArray
     */
    private JSONArray parseData(String messageBody,boolean check) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray keyboardModelData = (JSONArray) parameter.get("kanban_template_info");
        if(check){
            if (StringUtils.isEmpty(keyboardModelData) || keyboardModelData.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return keyboardModelData;
    }


}
