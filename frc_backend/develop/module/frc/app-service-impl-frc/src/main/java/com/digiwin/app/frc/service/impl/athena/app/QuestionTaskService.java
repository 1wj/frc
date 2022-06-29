package com.digiwin.app.frc.service.impl.athena.app;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.app.biz.IQuestionCardServiceBiz;
import com.digiwin.app.frc.service.athena.app.service.IQuestionTaskService;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class QuestionTaskService implements IQuestionTaskService {
    @Autowired
    private IQuestionCardServiceBiz questionCardServiceBiz;
    @Override
    public DWServiceResult questionCardList(String messageBody) throws Exception {
        JSONObject cardData = questionCardServiceBiz.questionCardList(messageBody);
        return new DWServiceResult(true,cardData);
    }

    @Override
    public DWServiceResult questionCardDetailInfo(String messageBody) throws Exception {
        JSONObject detailInfo = questionCardServiceBiz.questionCardDetailInfo(messageBody);
        return new DWServiceResult(true,detailInfo);
    }
}
