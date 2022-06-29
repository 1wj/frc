package com.digiwin.app.frc.service.impl.athena.rqi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.rqi.biz.KanbanInfoDisplayBiz;
import com.digiwin.app.frc.service.athena.rqi.service.IESPKanbanInfoDisplayService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWEAIResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/1/25 9:36
 * @Version 1.0
 * @Description 看板信息展示
 */
public class ESPKanbanInfoDisplayService implements IESPKanbanInfoDisplayService {

    @Autowired
    private KanbanInfoDisplayBiz kanbanInfoDisplayBiz;

    @Override
    public DWEAIResult getKanbanSearchFieldInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            JSONObject result = kanbanInfoDisplayBiz.getKanbanSearchFieldInfo();
            dataResult.put("kanban_field_info", result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]", MultilingualismUtil.getLanguage("queryFail"), e.toString()));
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("querySuccess"), dataResult);
    }


    @Override
    public DWEAIResult getKanbanInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            List<JSONObject> result = kanbanInfoDisplayBiz.getKanbanInfo();
            dataResult.put("kanban_info", result);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]", MultilingualismUtil.getLanguage("queryFail"), e.toString()));
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("querySuccess"), dataResult);
    }


    @Override
    public DWEAIResult getIssueManagementMatrixOverviewInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //解析数据并返回结果
            //对参数进行解析并返回结果
            JSONObject jsonObject = JSONObject.parseObject(messageBody);
            JSONObject stdData = (JSONObject) jsonObject.get("std_data");
            JSONObject parameter = (JSONObject) stdData.get("parameter");
            JSONArray dataContent = (JSONArray) parameter.get("management_matrix_info");
            //List<JSONObject> result = kanbanInfoDisplayBiz.getIssueManagementMatrixOverviewInfo(dataContent);
            JSONObject jsonObject1 = JSON.parseObject("{\"unsolved_question_count\": 3,\"proportion\": \"35%\",\"process_time_rate\": \"34%\",\"total_question_count\": 6,\"process_time\": 35,\"issue_quadrant\": \"重要且紧急\"}");
            JSONObject jsonObject2 = JSON.parseObject("{\"unsolved_question_count\": 0,\"proportion\": \"24%\",\"process_time_rate\": \"27%\",\"total_question_count\": 4,\"process_time\": 28,\"issue_quadrant\": \"重要但不紧急\"}");
            JSONObject jsonObject3 = JSON.parseObject("{\"unsolved_question_count\": 2,\"proportion\": \"18%\",\"process_time_rate\": \"19%\",\"total_question_count\": 3,\"process_time\": 19,\"issue_quadrant\": \"不重要但紧急\"}");
            JSONObject jsonObject4 = JSON.parseObject("{\"unsolved_question_count\": 1,\"proportion\": \"24%\",\"process_time_rate\": \"20%\",\"total_question_count\": 4,\"process_time\": 20,\"issue_quadrant\": \"不重要不紧急\"}");
            List<JSONObject> result2 = new ArrayList<>();
            result2.add(jsonObject1);
            result2.add(jsonObject2);
            result2.add(jsonObject3);
            result2.add(jsonObject4);
            dataResult.put("management_matrix_info", result2);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]", MultilingualismUtil.getLanguage("queryFail"), e.toString()));
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("querySuccess"), dataResult);
    }


    @Override
    public DWEAIResult getIssueManagementMatrixInfo(Map<String, Object> headers, String messageBody) throws Exception {
        Map<String, Object> dataResult = new HashMap<>(16);
        try {
            //解析数据并返回结果
            //对参数进行解析并返回结果
            JSONObject jsonObject = JSONObject.parseObject(messageBody);
            JSONObject stdData = (JSONObject) jsonObject.get("std_data");
            JSONObject parameter = (JSONObject) stdData.get("parameter");
            JSONArray dataContent = (JSONArray) parameter.get("management_matrix_info");
            //JSONObject result = kanbanInfoDisplayBiz.getIssueManagementMatrixInfo(dataContent);
            JSONObject result2 = JSON.parseObject("{\n" +
                    "            \"unsolved_question_count\": 25,\n" +
                    "            \"management_matrix_info\": [\n" +
                    "                {\n" +
                    "                    \"important\": 1,\n" +
                    "                    \"question_source_name\": null,\n" +
                    "                    \"project_no\": \"123456\",\n" +
                    "                    \"question_classification_name\": null,\n" +
                    "                    \"question_no\": \"XQ_2022012113233689985733\",\n" +
                    "                    \"plan_overdue_days_urgency\": 7,\n" +
                    "                    \"issue_quadrant\": \"重要且紧急\",\n" +
                    "                    \"actual_process_time\": 5,\n" +
                    "                    \"estimate_process_time\": 7\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"important\": 1,\n" +
                    "                    \"question_source_name\": null,\n" +
                    "                    \"project_no\": \"123456\",\n" +
                    "                    \"question_classification_name\": null,\n" +
                    "                    \"question_no\": \"XQ_2022012009130297955536\",\n" +
                    "                    \"plan_overdue_days_urgency\": 8,\n" +
                    "                    \"issue_quadrant\": \"重要且紧急\",\n" +
                    "                    \"actual_process_time\": 6,\n" +
                    "                    \"estimate_process_time\": 11\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"important\": 1,\n" +
                    "                    \"question_source_name\": null,\n" +
                    "                    \"project_no\": \"123456\",\n" +
                    "                    \"question_classification_name\": null,\n" +
                    "                    \"question_no\": \"XQ_2022011909301997286791\",\n" +
                    "                    \"plan_overdue_days_urgency\": 9,\n" +
                    "                    \"issue_quadrant\": \"重要且紧急\",\n" +
                    "                    \"actual_process_time\": 9,\n" +
                    "                    \"estimate_process_time\": 11\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"important\": 1,\n" +
                    "                    \"question_source_name\": null,\n" +
                    "                    \"project_no\": \"123456\",\n" +
                    "                    \"question_classification_name\": null,\n" +
                    "                    \"question_no\": \"XQ_2022012016311700513203\",\n" +
                    "                    \"plan_overdue_days_urgency\": 8,\n" +
                    "                    \"issue_quadrant\": \"重要且紧急\",\n" +
                    "                    \"actual_process_time\": 2,\n" +
                    "                    \"estimate_process_time\": 4\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"important\": 1,\n" +
                    "                    \"question_source_name\": null,\n" +
                    "                    \"project_no\": \"123456\",\n" +
                    "                    \"question_classification_name\": null,\n" +
                    "                    \"question_no\": \"XQ_2022012115142561277044\",\n" +
                    "                    \"plan_overdue_days_urgency\": 7,\n" +
                    "                    \"issue_quadrant\": \"重要且紧急\",\n" +
                    "                    \"actual_process_time\": 4,\n" +
                    "                    \"estimate_process_time\": 5\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"important\": 1,\n" +
                    "                    \"question_source_name\": null,\n" +
                    "                    \"project_no\": \"123456\",\n" +
                    "                    \"question_classification_name\": null,\n" +
                    "                    \"question_no\": \"XQ_2022012509201829796738\",\n" +
                    "                    \"plan_overdue_days_urgency\": 3,\n" +
                    "                    \"issue_quadrant\": \"重要且紧急\",\n" +
                    "                    \"actual_process_time\": 9,\n" +
                    "                    \"estimate_process_time\": 11\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"last_month_process_time_ratio\": \"0%\",\n" +
                    "            \"process_time_rate\": \"20%\",\n" +
                    "            \"total_question_count\": 50,\n" +
                    "            \"estimate_process_time\": 8\n" +
                    "        }");
            dataResult.put("result_info", result2);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DWRuntimeException(String.format("%s by [%s]", MultilingualismUtil.getLanguage("queryFail"), e.toString()));
        }
        return new DWEAIResult("0", "0", MultilingualismUtil.getLanguage("querySuccess"), dataResult);
    }


}
