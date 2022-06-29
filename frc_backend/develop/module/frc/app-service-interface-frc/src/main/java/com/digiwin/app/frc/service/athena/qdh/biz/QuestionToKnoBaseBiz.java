package com.digiwin.app.frc.service.athena.qdh.biz;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @ClassName QuestionToKnoBase
 * @Description 问题快反入库服务
 * @Author author
 * @Date 2022/1/21 10:50
 * @Version 1.0
 **/
public interface QuestionToKnoBaseBiz {
    /**
     * 问题快反数据入库
     * @param dataDetail 问题详情
     * @param questionNo 问题号
     */
    JSONObject questionToBase(String questionNo,JSONObject dataDetail) throws JsonProcessingException;


    /**
     * 问题验收 正常结案 -> 经验抽取，入kmo
     * @param oid question_action_trace主键
     * @return 负责人
     */
    void dataToKmo(String oid) throws JsonProcessingException;
}
