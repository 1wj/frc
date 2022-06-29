package com.digiwin.app.frc.service.athena.mtw.biz;

import com.alibaba.fastjson.JSONArray;
import com.digiwin.app.container.exceptions.DWArgumentException;

import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/1/10 13:13
 * @Version 1.0
 * @Description 看板防呆校验查询biz
 */
public interface KanbanFoolproofBiz {

    /**
     * 看板防呆校验查询信息
     *
     * @param dataContent 解析后数据
     * @return List<String>
     * @throws DWArgumentException
     */
    List<Object> getKanbanFoolproofInfo(JSONArray dataContent) throws DWArgumentException;

}
