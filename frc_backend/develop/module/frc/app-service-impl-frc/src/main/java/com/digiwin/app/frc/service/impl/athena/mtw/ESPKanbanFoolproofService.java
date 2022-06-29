package com.digiwin.app.frc.service.impl.athena.mtw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.mtw.biz.KanbanFoolproofBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IESPKanbanFoolproofService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWEAIResult;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * @Author: xieps
 * @Date: 2022/1/10 17:12
 * @Version 1.0
 * @Description
 */
public class ESPKanbanFoolproofService implements IESPKanbanFoolproofService {

    @Autowired
    private KanbanFoolproofBiz kanbanFoolproofBiz;

    @Override
    public DWEAIResult getKanbanFoolproofInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseData(messageBody);
            List<Object> result = kanbanFoolproofBiz.getKanbanFoolproofInfo(dataContent);
            dataResult.put("wait_delete_info", result);
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
     * @return JSONArray
     */
    private JSONArray parseData(String messageBody) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray deletedInfo = (JSONArray) parameter.get("wait_delete_info");
        return deletedInfo;
    }
}
