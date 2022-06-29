package com.digiwin.app.frc.service.impl.athena.mtw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.mtw.biz.KanbanFoolproofBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IKanbanFoolproofService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/1/10 13:03
 * @Version 1.0
 * @Description
 */
public class KanbanFoolproofService implements IKanbanFoolproofService {

    @Autowired
    private KanbanFoolproofBiz kanbanFoolproofBiz;

    @Override
    public DWServiceResult getKanbanFoolproofInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //对参数进行解析返回结果
            JSONArray dataContent = parseData(messageBody);
            List<Object> resultInfo = kanbanFoolproofBiz.getKanbanFoolproofInfo(dataContent);
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
