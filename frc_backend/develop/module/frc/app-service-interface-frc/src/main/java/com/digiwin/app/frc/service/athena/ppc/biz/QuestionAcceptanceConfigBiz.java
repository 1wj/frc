package com.digiwin.app.frc.service.athena.ppc.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/2/17 16:33
 * @Version 1.0
 * @Description  问题验收处理Biz
 */
public interface QuestionAcceptanceConfigBiz {


    List<JSONObject> addQuestionAcceptanceConfig(JSONArray dataContent) throws IOException, DWArgumentException;

    boolean deleteQuestionAcceptanceConfig(JSONArray dataContent);

    boolean updateQuestionAcceptanceConfigInfo(JSONArray dataContent) throws IOException, DWArgumentException;

    List<JSONObject> getQuestionAcceptanceConfig(JSONArray dataContent) throws JsonProcessingException;
}
