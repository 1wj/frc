package com.digiwin.app.frc.service.impl.athena.ppc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.ppc.biz.LiablePersonConfigBiz;
import com.digiwin.app.frc.service.athena.ppc.service.ILiablePersonConfigService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:zhangzlz
 * @Date 2022/3/11   9:58
 */
public class LiablePersonConfigService implements ILiablePersonConfigService {

    @Autowired
    private LiablePersonConfigBiz liablePersonConfigBiz;

    @Override
    public DWServiceResult addLiablePersonConfigInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //解析校验前端消息体
            JSONArray dataContent = parseData(messageBody, true);
            //新增数据
            List<Map<String, Object>> maps = liablePersonConfigBiz.addLiablePersonConfig(dataContent);
            Map<String, Object> map = new HashMap<>();
            map.put("config_info",maps);
            //设置返回结果
            result.setData(map);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("addSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult deleteLiablePersonConfigInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //解析校验前端消息体
            JSONArray dataContent = parseData(messageBody, true);
            //删除数据
            boolean resultInfo = liablePersonConfigBiz.deleteLiablePersonConfig(dataContent);
            //判断删除结果
            if (resultInfo) {
                result.setSuccess(true);
                result.setMessage(MultilingualismUtil.getLanguage("deleteSuccess"));
            } else {
                result.setSuccess(false);
                result.setMessage(MultilingualismUtil.getLanguage("deleteFail"));
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult updateLiablePersonConfigInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //解析校验前端消息体
            JSONArray dataContent = parseData(messageBody, true);
            //修改数据
            boolean resultInfo = liablePersonConfigBiz.updateLiablePersonConfig(dataContent);
            //判断修改结果
            if (resultInfo) {
                result.setSuccess(true);
                result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
            } else {
                result.setSuccess(false);
                result.setMessage(MultilingualismUtil.getLanguage("updateFail"));
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;    }

    @Override
    public DWServiceResult getLiablePersonConfigInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //解析校验前端消息体
            JSONArray dataContent = parseData(messageBody, true );
            //查询数据
            List<JSONObject> resultInfo = liablePersonConfigBiz.getLiablePersonConfig(dataContent);
            //设置返回结果
            Map<String, Object> map = new HashMap<>();
            map.put("config_info",resultInfo);
            result.setData(map);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;    }


    /**
     * 解析前端参数信息
     *
     * @param messageBody 消息体
     * @param check       是否进行校验
     * @return JSONArray
     */
    private JSONArray parseData(String messageBody, boolean check) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray dataContent = (JSONArray) parameter.get("config_info");
        if (check) {
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return dataContent;
    }
}
