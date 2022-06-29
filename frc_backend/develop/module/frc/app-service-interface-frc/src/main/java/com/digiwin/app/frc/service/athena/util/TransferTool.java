package com.digiwin.app.frc.service.athena.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName TransferUtil
 * @Description string -model -entity转换工具类
 * @Author author
 * @Date 2022/2/10 18:24
 * @Version 1.0
 **/
public class TransferTool {

    public static ObjectMapper objectMapper = new ObjectMapper();


    /**
     * string 转model
     * @param param 原始数据
     * @param clazz 转换的类
     * @param <T>
     * @return
     */
    public static <T> T convertString2Model(String param,Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(param.getBytes(),clazz);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * string 根据 key 转 model
     * @param param 原始数据
     * @param key 要获得的值
     * @param isArray 要获取的值是否是数组
     * @param clazz 目标类
     * @param <T> 返回目标类
     * @return 返回目标类
     */
    public static <T> T convertString2Model(String param,String key,boolean isArray,Class<T> clazz) {
        JSONObject jsonObject = JSONObject.parseObject(param);
        String data = isArray ? jsonObject.getJSONArray(key).getString(0) : jsonObject.getString(key);
        try {
            return new ObjectMapper().readValue(data.getBytes(),clazz);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * String 转model List
     * @param param
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public static  <T> List<T> convertString2List(String param,String key,Class<T> clazz) {
        Map<String, Object> paramMap = str2Map(param);
        return getMapTs(paramMap, key, clazz);
    }

    /**
     * String 转 map
     *
     * @param param
     * @return
     * @throws Exception
     */
    public static Map<String, Object> str2Map(String param) {
        if (StringUtils.isEmpty(param)) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(param, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    /**
     * map 取 model list
     *
     * @param map
     * @return
     * @throws Exception
     */
    public static <T> List<T> getMapTs(Map map, String key, Class<T> clazz) {
        if (!MapUtils.isEmpty(map)) {
            Object obj = map.get(key);
            if (obj != null) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    String objStr = mapper.writeValueAsString(obj);
                    JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
                    return mapper.readValue(objStr, type);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return new ArrayList<>();
                }
            }
        }
        return new ArrayList<>();
    }
}
