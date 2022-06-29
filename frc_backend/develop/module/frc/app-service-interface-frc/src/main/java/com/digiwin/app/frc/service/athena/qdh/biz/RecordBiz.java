package com.digiwin.app.frc.service.athena.qdh.biz;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionRecordEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.QuestionRecordInfoModel;

import java.util.List;
import java.util.Map;

/**
 * @ClassName RecordBiz
 * @Description 问题记录Biz
 * @Author author
 * @Date 2021/11/15 22:18
 * @Version 1.0
 **/
public interface RecordBiz {
    /**
     * 更新问题记录
     * @param recordEntity 问题记录实体
     * @return 成功与否
     */
    int updateRecord(QuestionRecordEntity recordEntity);

    /**
     * 新增问题记录
     * @param recordEntity 问题记录实体
     * @return 成功与否
     */
    JSONObject insertRecord(QuestionRecordEntity recordEntity);

    /**
     * 获取问题项目详情V2
     * @param recordInfoModel
     * @return
     */
    List getRecordNew(List<QuestionRecordInfoModel> recordInfoModel);
}
