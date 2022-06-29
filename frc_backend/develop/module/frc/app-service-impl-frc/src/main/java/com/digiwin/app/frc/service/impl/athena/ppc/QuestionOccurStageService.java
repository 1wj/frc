package com.digiwin.app.frc.service.impl.athena.ppc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionOccurStageBiz;
import com.digiwin.app.frc.service.athena.ppc.service.IQuestionOccurStageService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:zhangzlz
 * @Date 2022/2/11   10:20
 */
public class QuestionOccurStageService implements IQuestionOccurStageService {
    @Autowired
    QuestionOccurStageBiz questionOccurStageBiz;

    @Override
    public DWServiceResult addQuestionOccurStageInfo(String messageBody) throws Exception {
        //返回结果
        DWServiceResult result = new DWServiceResult();
        try {
            //判断消息体并解析
            JSONArray content = parseMessage(messageBody,"occur_stage_info");
            //biz层进行添加的逻辑处理
            Map map = questionOccurStageBiz.addQuestionOccurStageInfo(content);
            result.setData(map);
            result.setSuccess(true);
            result.setMessage("success");
        }catch (Exception e){
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @Override
    public DWServiceResult deleteQuestionOccurStageInfo(String messageBody) throws Exception {
        //返回结果
        DWServiceResult result = new DWServiceResult();
        try {
            //判断消息体并解析
            JSONArray content = parseMessage(messageBody,"occur_stage_info");
            //biz层进行添加的逻辑处理
            Boolean delete = questionOccurStageBiz.deleteQuestionOccurStageInfo(content);
            if (delete){
                result.setSuccess(true);
                result.setMessage(MultilingualismUtil.getLanguage("deleteSuccess"));
            }else {
                result.setSuccess(false);
                result.setMessage(MultilingualismUtil.getLanguage("deleteFail"));
            }
        }catch (Exception e){
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @Override
    public DWServiceResult updateQuestionOccurStageInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //判断消息体并解析
            JSONArray content = parseMessage(messageBody,"occur_stage_info");
            //biz层进行添加的逻辑处理
            Boolean update = questionOccurStageBiz.updateQuestionOccurStageInfo(content);
            if (update){
                result.setSuccess(true);
                result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
            }else {
                result.setSuccess(false);
                result.setMessage(MultilingualismUtil.getLanguage("updateFail"));
            }
        }catch (Exception e){
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
    }

    @Override
    public DWServiceResult getQuestionOccurStageInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //判断消息体并解析
            JSONArray content = JSONObject.parseObject(messageBody).getJSONObject("std_data").getJSONObject("parameter").getJSONArray("occur_stage_info");
            List<Map<String, Object>> list = questionOccurStageBiz.getQuestionOccurStageInfo(content);
            //封装为前端需要的格式
            Map<String,Object> map = new HashMap<>();
            map.put("occur_stage_info",list);
            result.setData(map);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        }catch (Exception e){
            result.setMessage(e.getMessage());
            result.setSuccess(false);
        }
        return result;
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
