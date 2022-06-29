package com.digiwin.app.frc.service.athena.app.biz;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface IQuestionCardServiceBiz {

    public JSONObject questionCardList(String messageBody);

    public JSONObject questionCardDetailInfo(String messageBody);
}
