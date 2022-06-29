package com.digiwin.app.frc.service.athena.ppc.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author:zhangzlz
 * @Date 2022/3/11   10:06
 */
public interface LiablePersonConfigBiz {

    /**
     * 添加责任人配置信息
     *
     * @param dataContent 消息体
     * @return 返回Map类型集合
     * @throws IOException
     * @throws DWArgumentException
     */
    List<Map<String, Object>> addLiablePersonConfig(JSONArray dataContent) throws IOException, DWArgumentException;

    /**
     * 删除责任人配置信息
     *
     * @param dataContent 消息体
     * @return 返回 true：删除成功 false：删除失败
     * @throws Exception
     */
    boolean deleteLiablePersonConfig(JSONArray dataContent)throws Exception;

    /**
     * 修改责任人配置信息
     *
     * @param dataContent 消息体
     * @return 返回 true:修改成功 false：修改失败
     * @throws IOException
     * @throws DWArgumentException
     */
    boolean updateLiablePersonConfig(JSONArray dataContent) throws IOException, DWArgumentException;

    /**
     * 查询责任人配置信息
     *
     * @param dataContent 消息体
     * @return 返回 JSONObject类型集合
     * @throws Exception
     */
    List<JSONObject> getLiablePersonConfig(JSONArray dataContent) throws Exception;
}
