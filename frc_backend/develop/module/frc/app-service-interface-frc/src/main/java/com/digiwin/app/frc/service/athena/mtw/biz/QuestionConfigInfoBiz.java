package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/1/11 11:07
 * @Version 1.0
 * @Description 问题配置信息处理Biz
 */
public interface QuestionConfigInfoBiz {

    /**
     * 获取问题责任人配置信息
     *
     * @param dataContent 解析后数据
     * @return List<JSONObject>
     */
    List<JSONObject> getQuestionLiablePersonConfig(JSONArray dataContent);
}
