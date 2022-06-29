package com.digiwin.app.frc.service.athena.rqi.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.text.ParseException;
import java.util.List;


/**
 * @Author: xieps
 * @Date: 2022/1/25 10:03
 * @Version 1.0
 * @Description 看板信息展示Biz
 */
public interface KanbanInfoDisplayBiz {

    /**
     * 获取看板搜索栏位信息
     *
     * @throws  JsonProcessingException
     * @return List<JSONObject>
     */
    JSONObject getKanbanSearchFieldInfo() throws JsonProcessingException;


    /**
     * 获取问题看板信息
     *
     * @throws DWArgumentException
     * @return List<JSONObject>
     */
    List<JSONObject> getKanbanInfo() throws Exception;

    /**
     * 获取问题看板信息,匹配所有看对显示栏位
     * @return 每条问题对应报表数据
     * @throws Exception
     */
    List<JSONObject> getKanbanInfoTest();



    /**
     * 获取议题管理矩阵总览信息
     *
     * @param dataContent 解析后的数据
     * @throws ParseException
     * @return List<JSONObject>
     */
    List<JSONObject> getIssueManagementMatrixOverviewInfo(JSONArray dataContent) throws ParseException;

    /**
     * 获取议题管理矩阵信息
     *
     * @param dataContent 解析后的数据
     * @throws DWArgumentException
     * @throws ParseException
     * @return  List<JSONObject>
     */
    JSONObject getIssueManagementMatrixInfo(JSONArray dataContent) throws DWArgumentException, ParseException;
}
