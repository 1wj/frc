package com.digiwin.app.frc.service.impl.athena.rqi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.rqi.biz.KanbanInfoDisplayBiz;
import com.digiwin.app.frc.service.athena.rqi.service.IKanbanInfoDisplayService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/1/25 9:37
 * @Version 1.0
 * @Description 看板信息展示
 */
public class KanbanInfoDisplayService implements IKanbanInfoDisplayService {

    @Autowired
    private KanbanInfoDisplayBiz kanbanInfoDisplayBiz;

    @Override
    public DWServiceResult getKanbanSearchFieldInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONObject resultInfo = kanbanInfoDisplayBiz.getKanbanSearchFieldInfo();
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(MultilingualismUtil.getLanguage("queryFail"));
        }
        return result;
    }

    @Override
    public DWServiceResult getKanbanInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            List<JSONObject> resultInfo = kanbanInfoDisplayBiz.getKanbanInfo();
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
//            result.setMessage(MultilingualismUtil.getLanguage("queryFail"));
            result.setMessage(e.getMessage());
        }
        return result;
    }


    @Override
    public DWServiceResult getIssueManagementMatrixOverviewInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //对参数进行解析并返回结果
            JSONObject jsonObject = JSONObject.parseObject(messageBody);
            JSONObject stdData = (JSONObject) jsonObject.get("std_data");
            JSONObject parameter = (JSONObject) stdData.get("parameter");
            JSONArray dataContent = (JSONArray) parameter.get("management_matrix_info");
            List<JSONObject> resultInfo = kanbanInfoDisplayBiz.getIssueManagementMatrixOverviewInfo(dataContent);
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
//            result.setMessage(MultilingualismUtil.getLanguage("queryFail"));
            result.setMessage(e.getMessage());
        }
        return result;
    }


    @Override
    public DWServiceResult getIssueManagementMatrixInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //对参数进行解析并返回结果
            JSONObject jsonObject = JSONObject.parseObject(messageBody);
            JSONObject stdData = (JSONObject) jsonObject.get("std_data");
            JSONObject parameter = (JSONObject) stdData.get("parameter");
            JSONArray dataContent = (JSONArray) parameter.get("management_matrix_info");
            JSONObject resultInfo = kanbanInfoDisplayBiz.getIssueManagementMatrixInfo(dataContent);
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
//            result.setMessage(MultilingualismUtil.getLanguage("queryFail"));
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 解析前端参数信息
     *
     * @param messageBody 消息体
     * @return JSONArray
     */
    private JSONArray parseDate(String messageBody) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray dataContent = (JSONArray) parameter.get("kanban_info");
        return dataContent;
    }
}
