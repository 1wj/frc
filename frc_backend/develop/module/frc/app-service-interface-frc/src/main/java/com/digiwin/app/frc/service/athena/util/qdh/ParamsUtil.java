package com.digiwin.app.frc.service.athena.util.qdh;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.ParamConst;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.TransferTool;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @ClassName ParamsUtil
 * @Description 统一获取请求参数
 * @Author author
 * @Date 2021/11/11 15:01
 * @Version 1.0
 **/
public class ParamsUtil {
    /**
     * API:取得查询问题详情的入参或 API:生成待审核问题数据的入参
     * @param messageBody athena传入标准格式
     * @return JSONObject json格式数据
     */
    public static JSONObject getDetailParams(String messageBody) throws DWException {
        // 获取请求参数
        JSONObject parameter = getAthenaParameter(messageBody);
        // 获取 M层 ： question_info
        JSONArray questionInfo = (JSONArray) parameter.get(ParamConst.QUESTION_INFO);
        if (StringUtils.isEmpty(questionInfo) || questionInfo.size() == 0) {
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("detailGetParamNull"));
        }
        // 获取 MF层 ：question_id ps:获取问题详情入参 默认只有一笔，直接get 0
        JSONObject questionObject = (JSONObject) questionInfo.get(0);
        return questionObject;
    }

    /**
     * 8D string 转 List<Model>
     * @param messageBody 入参
     * @param clazz 泛型
     * @param <T>  泛型
     * @return List
     */
    public static <T> List<T> string2List(String messageBody, Class<T> clazz){
        // 获取请求参数
        JSONObject parameter = ParamsUtil.getAthenaParameter(messageBody);
        return TransferTool.convertString2List(formatString(parameter.toJSONString()),ParamConst.QUESTION_INFO,clazz);
    }

    public static JSONObject getSolutionParams(String messageBody) throws DWException {
        // 获取请求参数
        JSONObject parameter = getAthenaParameter(messageBody);
        // 获取 M层 ： question_info
        JSONArray solutionInfo = (JSONArray) parameter.get("solution_info");
        if (StringUtils.isEmpty(solutionInfo) || solutionInfo.size() == 0) {
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("detailGetParamNull"));
        }
        // 获取 MF层 ：question_id ps:获取问题详情入参 默认只有一笔，直接get 0
        JSONObject solutionObject = (JSONObject) solutionInfo.get(0);
        return solutionObject;
    }

    /**
     * 取得 更新问题处理 or 生成待审核问题 入参
     * @param messageBody  athena传入标准格式
     * @return M层 question_info
     */
    public static String getQuestionUpdateParams(String messageBody) {
        // 获取请求参数
        JSONObject parameter = getAthenaParameter(messageBody);
        // 获取 M层 ： question_info
        JSONArray questionInfo = (JSONArray) parameter.get(ParamConst.QUESTION_RESULT);
        if (StringUtils.isEmpty(questionInfo) || questionInfo.size() == 0) {
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("detailGetParamNull"));
        }
        return formatString(questionInfo.getString(0));
    }

    public static String getQFParams(String messageBody) {
        // 获取请求参数
        JSONObject parameter = getAthenaParameter(messageBody);
        // 获取 M层 ： question_info
        JSONArray questionInfo = parameter.getJSONArray(ParamConst.QUESTION_RESULT);
        if (StringUtils.isEmpty(questionInfo) || questionInfo.size() == 0) {
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("detailGetParamNull"));
        }
        return formatString(questionInfo.getString(0));
    }


    /**
     * 获取问题发起结构
     * @param messageBody
     * @return
     */
    public static String getInitParam(String messageBody) {
        // 获取请求参数
        JSONObject parameter = getAthenaParameter(messageBody);
        // 获取 M层 ： question_info
        JSONArray questionInfo = parameter.getJSONArray(ParamConst.QUESTION_INFO);
        if (StringUtils.isEmpty(questionInfo) || questionInfo.size() == 0) {
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("detailGetParamNull"));
        }
        return formatString(questionInfo.getString(0));
    }

    /**
     * 迭代三调整
     * @param messageBody
     * @return
     */
    public static String getUpdateParams(String messageBody) {
        // 获取请求参数
        JSONObject parameter = getAthenaParameter(messageBody);
        // 获取 M层 ： question_info
        JSONArray questionInfo = parameter.getJSONArray(ParamConst.QUESTION_RESULT);
        if (StringUtils.isEmpty(questionInfo) || questionInfo.size() == 0) {
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("detailGetParamNull"));
        }
        return formatString(questionInfo.getString(0));
    }

    /**
     * 取得 athena传入参数第一层
     * @param messageBody athena传入标准格式
     * @return parameter层
     */
    public static JSONObject getAthenaParameter(String messageBody){
        JSONObject stdData = JSONObject.parseObject(messageBody).getJSONObject(ParamConst.STD_DATA);
        JSONObject parameter = stdData.getJSONObject(ParamConst.PARAMETER);
        return parameter;
    }

    /**
     * 用于测试
     * @param param
     * @return
     */
    public static String getInitParamForTest(String param){
        JSONObject jsonObject = JSONObject.parseObject(param);
        JSONArray questionInfos =jsonObject.getJSONArray("question_info");
        return formatString(questionInfos.getString(0));
    }

    /**
     * 迭代五调整
     * @param messageBody
     * @return
     */
    public static String getUpdateObjectParams(String messageBody) {
        // 获取请求参数
        JSONObject parameter = getAthenaParameter(messageBody);
        // 获取 M层 ： question_info
        JSONObject questionInfo = (JSONObject) parameter.get(ParamConst.QUESTION_RESULT);
        if (StringUtils.isEmpty(questionInfo) || questionInfo.size() == 0) {
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("detailGetParamNull"));
        }
        return formatString(questionInfo.toString());
    }

    public static JSONObject getDetailParamsForUniversal(String messageBody) throws DWException {
        // 获取请求参数
        JSONObject parameter = getAthenaParameter(messageBody);
        // 获取 M层 ： question_info
        JSONObject questionInfo = (JSONObject) parameter.get(ParamConst.QUESTION_INFO);
        if (StringUtils.isEmpty(questionInfo)) {
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("detailGetParamNull"));
        }
        return questionInfo;
    }

    /**
     * 迭代五调整
     * @param messageBody
     * @return
     */
    public static String getUpdateParamsForUniversal(String messageBody) {
        // 获取请求参数
        JSONObject parameter = getAthenaParameter(messageBody);
        // 获取 M层 ： question_info
        JSONObject questionInfo = parameter.getJSONObject(ParamConst.QUESTION_RESULT);
        if (StringUtils.isEmpty(questionInfo)) {
            throw new DWRuntimeException(MultilingualismUtil.getLanguage("detailGetParamNull"));
        }
        return formatString(questionInfo.toString());
    }

    /**
     * 格式化前端传递过来的字符串
     *
     * @param questionInfosString
     * @return String
     */
    private static String formatString(String questionInfosString) {
        String  replaceFlag = String.valueOf(System.currentTimeMillis());
        return questionInfosString.replaceAll("\\{\"","{'") // {"  --> {'
                .replaceAll("\":\"", "':'")     // ":" --> ':'
                .replaceAll("\",\"", "','")     // "," --> ','
                .replaceAll("\":", "':")        // ":  --> ':
                .replaceAll(",\"", ",'")        // ,"  --> ,'
                .replaceAll("\"\\}", "'}")      // "}  --> '}
                .replaceAll("\\\"", replaceFlag)         // " 转换成    --> 空，以去掉字符串中间的引号
                .replaceAll("\"",replaceFlag)  // \" ----> 替换成标识
                .replaceAll("'", "\"")          // '   --> "，所有的单引号替换回双引号
                .replaceAll("<br />", "")       // 去掉字符串中的<br />
                .replaceAll(replaceFlag,"”")   //替换标识 ----> ”
                .replaceAll("\\\\", "");       // 字符串中含\也会导致解析失败
    }

}
