package com.digiwin.app.frc.service.impl.athena.ppc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.ppc.biz.LiablePersonConfigBiz;
import com.digiwin.app.frc.service.athena.ppc.service.IESPLiablePersonConfigService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWEAIResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:zhangzlz
 * @Date 2022/3/14   10:27
 */
public class ESPLiablePersonConfigService implements IESPLiablePersonConfigService {

    @Autowired
    private LiablePersonConfigBiz liablePersonConfigBiz;

    @Override
    public DWEAIResult postAddLiablePersonConfigInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        //对参数进行解析并校验返回结果
        JSONArray dataContent =  parseData(messageBody, true);
        List<Map<String, Object>> maps = liablePersonConfigBiz.addLiablePersonConfig(dataContent);
        dataResult.put("config_info", maps);
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("addSuccess"), dataResult);    }

    @Override
    public DWEAIResult postDeleteLiablePersonConfigInfo(Map<String, Object> headers, String messageBody) throws Exception {
        boolean result;
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent =  parseData(messageBody, true);
            result = liablePersonConfigBiz.deleteLiablePersonConfig(dataContent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return result ? new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("deleteSuccess"), null) :
                new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("deleteFail"), null);
    }

    @Override
    public DWEAIResult postUpdateLiablePersonConfigInfo(Map<String, Object> headers, String messageBody) throws Exception {
        boolean result;
        //对参数进行解析并校验返回结果
        JSONArray dataContent =  parseData(messageBody, true);
        result = liablePersonConfigBiz.updateLiablePersonConfig(dataContent);
        return result ? new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("updateSuccess"), null) :
                new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("updateFail"), null);
    }

    @Override
    public DWEAIResult getLiablePersonConfigInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseData(messageBody, true);
            List<JSONObject> result = liablePersonConfigBiz.getLiablePersonConfig(dataContent);
            dataResult.put("config_info", result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]", MultilingualismUtil.getLanguage("queryFail"), e.toString()));
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("querySuccess"), dataResult);
    }

    /**
     * 解析前端参数信息
     *
     * @param messageBody 消息体
     * @param check       是否进行校验
     * @return JSONArray
     */
    private JSONArray parseData(String messageBody, boolean check) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray dataContent = (JSONArray) parameter.get("config_info");
        if (check) {
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return dataContent;
    }
}
