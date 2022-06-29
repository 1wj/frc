package com.digiwin.app.frc.service.impl.athena.mtw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionConfigInfoBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IQuestionConfigInfoService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/1/11 11:04
 * @Version 1.0
 * @Description
 */
public class QuestionConfigInfoService implements IQuestionConfigInfoService {

    @Autowired
    private QuestionConfigInfoBiz questionConfigInfoBiz;

    @Override
    public DWServiceResult getQuestionLiablePersonConfigInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
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
            List<JSONObject> resultInfo = questionConfigInfoBiz.getQuestionLiablePersonConfig(dataContent);
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(MultilingualismUtil.getLanguage("queryFail"));
        }
        return result;
    }


}
