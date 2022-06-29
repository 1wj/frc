package com.digiwin.app.frc.service.athena.util;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.service.DWEAIResult;
import com.digiwin.app.service.DWServiceResult;

/**
 * @ClassName ResultTool
 * @Description 统一返参工具类
 * @Author HeX
 * @Date 2022/3/7 10:20
 * @Version 1.0
 **/
public class ResultTool {

    /**
     * 返回成功
     * @return
     */
    public static DWEAIResult success(){
        return new DWEAIResult();
    }

    /**
     * 返回成功
     * @param message 返回信息
     * @param data 返回数据
     * @return DWEAIResult
     */
    public static DWEAIResult success(String message,JSONObject data) {
        return new DWEAIResult("0", "0",message,data);
    }

    /**
     * 返回失败
     * @param language
     * @param data
     * @return
     */
    public static DWEAIResult fail(String language,JSONObject data){
        return new DWEAIResult("-1", "0",language,data);
    }




}
