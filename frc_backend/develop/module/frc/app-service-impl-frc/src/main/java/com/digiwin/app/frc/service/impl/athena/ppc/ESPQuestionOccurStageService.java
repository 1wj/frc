package com.digiwin.app.frc.service.impl.athena.ppc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionOccurStageBiz;
import com.digiwin.app.frc.service.athena.ppc.service.IESPQuestionOccurStageService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWEAIResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:zhangzlz
 * @Date 2022/2/17   19:02
 */
public class ESPQuestionOccurStageService implements IESPQuestionOccurStageService {
    @Autowired
    QuestionOccurStageBiz questionOccurStageBiz;

    @Override
    public DWEAIResult postAddQuestionOccurStageInfo(Map<String, Object> headers, String messageBody) throws Exception {
        //返回结果
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent =  parseMessage(messageBody,"occur_stage_info");
            dataResult = questionOccurStageBiz.addQuestionOccurStageInfo(dataContent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]", MultilingualismUtil.getLanguage("addFail"), e.toString()));
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("addSuccess"), dataResult);
    }

    @Override
    public DWEAIResult postDeleteQuestionOccurStageInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Boolean result;
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent = parseMessage(messageBody, "occur_stage_info");
            result = questionOccurStageBiz.deleteQuestionOccurStageInfo(dataContent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return result ? new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("deleteSuccess"), null) :
                new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("deleteFail"), null);
    }

    @Override
    public DWEAIResult postUpdateQuestionOccurStageInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Boolean result;
        try {
            //对参数进行解析并校验返回结果
            JSONArray dataContent = parseMessage(messageBody, "occur_stage_info");
            result = questionOccurStageBiz.updateQuestionOccurStageInfo(dataContent);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(e.getMessage());
        }
        return result ? new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("updateSuccess"), null) :
                new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("updateFail"), null);
    }


    @Override
    public DWEAIResult getQuestionOccurStageInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = JSONObject.parseObject(messageBody).getJSONObject("std_data").getJSONObject("parameter").getJSONArray("occur_stage_info");
            List<Map<String, Object>> maps = questionOccurStageBiz.getQuestionOccurStageInfo(dataContent);

            dataResult.put("occur_stage_info", maps);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]", MultilingualismUtil.getLanguage("queryFail"), e.toString()));
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("querySuccess"), dataResult);
    }

    /**
     * 解析前端传来的消息体
     *
     * @param messageBody 消息体
     * @param paramKey 需要获取数据的key
     * @return JSONArray  返回json数组
     */
    private JSONArray parseMessage(String messageBody, String paramKey){
        JSONArray jsonArray = JSONObject.parseObject(messageBody).getJSONObject("std_data").getJSONObject("parameter").getJSONArray(paramKey);
        //判断取到的数据是否为空
        if (jsonArray.isEmpty()|| StringUtils.isEmpty(jsonArray)){
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
        }
        return jsonArray;
    }
}
