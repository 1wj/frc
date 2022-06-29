package com.digiwin.app.frc.service.athena.util.qdh;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.common.Const.ParamConst;
import com.digiwin.app.frc.service.athena.util.TransferTool;

import java.util.List;

/**
 * @ClassName QuestionUnapprovedUtil
 * @Description 生成待处理问题信息-工具类
 * @Author hexin
 * @Date 2022/3/7 6:39
 * @Version 1.0
 **/
public class GeneratePendingUtil {

    /**
     * string 转 List<Model>
     * @param messageBody 入参
     * @param clazz 泛型
     * @param <T>  泛型
     * @return List
     */
    public static <T> List<T> string2List(String messageBody,Class<T> clazz){
        // 获取请求参数
        JSONObject parameter = ParamsUtil.getAthenaParameter(messageBody);
        return TransferTool.convertString2List(parameter.toJSONString(),ParamConst.QUESTION_INFO,clazz);
    }
}
