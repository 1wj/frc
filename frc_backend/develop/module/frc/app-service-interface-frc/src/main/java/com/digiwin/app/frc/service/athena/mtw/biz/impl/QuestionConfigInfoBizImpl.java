package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionConfigInfoBiz;
import com.digiwin.app.frc.service.athena.mtw.common.enums.QuestionTypeEnum;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xieps
 * @Date: 2022/1/11 11:09
 * @Version 1.0
 * @Description
 */
@Service
public class QuestionConfigInfoBizImpl implements QuestionConfigInfoBiz {

    @Override
    public List<JSONObject> getQuestionLiablePersonConfig(JSONArray dataContent) {
        //返回两组固定的值
        List<JSONObject> list = new ArrayList<>();
        JSONObject object1 = new JSONObject();
        object1.put("question_type_no",QuestionTypeEnum.QualityIssues.getCode());
        object1.put("question_type_name", MultilingualismUtil.getLanguage("quality_issues"));
        list.add(object1);
        return list;
    }
}
