package com.digiwin.app.frc.service.athena.ppc.biz;

import com.alibaba.fastjson.JSONArray;
import com.digiwin.app.container.exceptions.DWArgumentException;

import java.util.List;
import java.util.Map;

/**
 * @Author:zhangzlz
 * @Date 2022/2/11   10:40
 */
public interface QuestionOccurStageBiz {
    /**
     * 添加问题发生阶段
     *
     * @param jsonArray
     * @return 返回封装前端需要的数据
     * @throws Exception
     * @throws DWArgumentException
     */
    Map addQuestionOccurStageInfo(JSONArray jsonArray) throws Exception, DWArgumentException;

    /**
     * 删除问题发生阶段
     *
     * @param jsonArray 消息体
     * @throws Exception
     */
    Boolean deleteQuestionOccurStageInfo(JSONArray jsonArray)throws Exception;

    /**
     * 更新问题发生阶段
     * @param jsonArray
     * @throws Exception
     */
    Boolean updateQuestionOccurStageInfo(JSONArray jsonArray) throws Exception;

    /**
     * 查询问题发生阶段
     * @param jsonArray 消息体
     * @return  返回封装的数据
     * @throws Exception
     */
    List<Map<String,Object>> getQuestionOccurStageInfo(JSONArray jsonArray) throws Exception;
}
