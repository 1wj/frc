package com.digiwin.app.frc.service.impl.athena.mtw;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionConfigInfoBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IESPQuestionConfigInfoService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWEAIResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/1/11 11:32
 * @Version 1.0
 * @Description
 */
public class ESPQuestionConfigInfoService implements IESPQuestionConfigInfoService {

    @Autowired
    private QuestionConfigInfoBiz questionConfigInfoBiz;

    @Override
    public DWEAIResult getQuestionLiablePersonConfigInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //对参数进行解析返回结果
            JSONObject jsonObject = JSONObject.parseObject(messageBody);
            JSONObject stdData = (JSONObject) jsonObject.get("std_data");
            JSONObject parameter = (JSONObject) stdData.get("parameter");
            JSONArray dataContent = (JSONArray) parameter.get("config_info");
            //如果config_info 取出来为null 赋值一个空的JSONArray
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                dataContent = new JSONArray();
            }
            List<JSONObject> result = questionConfigInfoBiz.getQuestionLiablePersonConfig(dataContent);
            dataResult.put("config_info", result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]", MultilingualismUtil.getLanguage("queryFail"), e.toString()));
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("querySuccess"), dataResult);
    }
}
