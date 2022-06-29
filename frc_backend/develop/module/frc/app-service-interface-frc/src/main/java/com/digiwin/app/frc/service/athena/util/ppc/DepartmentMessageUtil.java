package com.digiwin.app.frc.service.athena.util.ppc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
*@ClassName: departmentMessageUtil
*@Description 部门信息转换工具类
*@Author Jiangyw
*@Date 2022/6/8
*@Time 15:09
*@Version
*/
public class DepartmentMessageUtil {
    /**
     * @Description 组装部门信息为字符串
     * @param departmentMessage
     * @return java.lang.String
     * @author Jiangyw
     * @Date 2022/6/8
     */
    public static String handleDepartmentMessageInfo(JSONArray departmentMessage) {
        StringBuilder departmentInfo = new StringBuilder();
        for (int j = 0; j < departmentMessage.size(); j++) {
            JSONObject jsonObject1 = departmentMessage.getJSONObject(j);
            String departmentId = jsonObject1.getString("feedback_department_id");
            String departmentName = jsonObject1.getString("feedback_department_name");
            departmentInfo.append(departmentId).append("_").append(departmentName).append(",");
        }
        return departmentInfo.substring(0,departmentInfo.toString().lastIndexOf(','));
    }

    public static List<JSONObject> splitDepartmentMessage(String departmentMessage ){
        List<JSONObject> objectList = new ArrayList<>();
        if(!StringUtils.isEmpty(departmentMessage)) {
            String[] departmentMessageInfo = departmentMessage.split(",");
            for (String department : departmentMessageInfo) {
                JSONObject object = new JSONObject();
                String[] split = department.split("_");
                object.put("feedback_department_id", split[0]);
                object.put("feedback_department_name", split[1]);
                objectList.add(object);
            }
        }
        return objectList;
    }
}
