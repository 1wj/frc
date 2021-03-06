package com.digiwin.app.frc.service.impl.athena.mtw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.mtw.biz.KeyBoardDisplayBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IKeyBoardDisplayService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/12/1 13:58
 * @Version 1.0
 * @Description
 */
public class KeyBoardDisplayService implements IKeyBoardDisplayService {

    @Autowired
    private KeyBoardDisplayBiz keyBoardDisplayBiz;

    @Override
    public DWServiceResult addKeyBoardDisplayInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseData(messageBody, true);
            List<JSONObject> resultInfo = keyBoardDisplayBiz.addKeyBoardDisplay(dataContent);
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
    public DWServiceResult deleteKeyBoardDisplayInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseData(messageBody, true);
            boolean resultInfo = keyBoardDisplayBiz.deleteKeyBoardDisplay(dataContent);
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
    public DWServiceResult updateKeyBoardDisplayInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //??????????????????????????????????????????
            JSONArray dataContent = parseData(messageBody, true);
            boolean resultInfo = keyBoardDisplayBiz.updateKeyBoardDisplay(dataContent);
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
    public DWServiceResult getKeyBoardDisplayInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //?????????????????????????????????
            JSONArray dataContent = parseData(messageBody, false);
            List<Map<String, Object>> resultInfo = keyBoardDisplayBiz.getKeyBoardDisplay(dataContent);
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
     * ???????????????????????????????????????
     *
     * @param messageBody ?????????
     * @param check       ????????????
     * @return JSONArray
     */
    private JSONArray parseData(String messageBody, boolean check) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray keyboardModelData = (JSONArray) parameter.get("kanban_display_info");
        if (check) {
            if (StringUtils.isEmpty(keyboardModelData) || keyboardModelData.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return keyboardModelData;
    }
}
