package com.digiwin.app.frc.service.athena.defaultAdd;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;

import java.io.IOException;
import java.util.List;

/**
 * @version: 1.0
 * @Description: 默认基础资料入库
 * @Author: Author
 * @Date: 2022/6/22 15:09
 */
public interface DefaultAddBiz {

    /**
     * 添加基础资料
     * @param dataContent 解析后的数据
     * @return int
     * @throws IOException
     * @throws DWArgumentException
     */
    int addCraftData(JSONArray dataContent) throws IOException, DWArgumentException;

}
