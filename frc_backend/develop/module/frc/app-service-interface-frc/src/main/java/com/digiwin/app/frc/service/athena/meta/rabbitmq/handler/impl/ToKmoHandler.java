package com.digiwin.app.frc.service.athena.meta.rabbitmq.handler.impl;

import com.digiwin.app.frc.service.athena.meta.rabbitmq.consumer.ProcessManualMessageListener;
import com.digiwin.app.frc.service.athena.meta.rabbitmq.handler.AbsProcessMessageHandler;
import com.digiwin.app.frc.service.athena.qdh.biz.QuestionToKnoBaseBiz;
import com.digiwin.app.module.DWModuleConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName ToKmoHandler
 * @Description FRC数据入库
 * @Author HeX
 * @Date 2022/3/20 17:42
 * @Version 1.0
 **/
@Service
public class ToKmoHandler extends AbsProcessMessageHandler {

    private final static String METHOD = DWModuleConfigUtils.getCurrentModuleProperty("frc.to.kmo");

    private final Logger logger = LoggerFactory.getLogger(ToKmoHandler.class);


    @Autowired
    QuestionToKnoBaseBiz questionToKnoBaseBiz;

    public ToKmoHandler() {
        super(METHOD);
    }

    @Override
    public void execute(Map<String, Object> data) throws Exception {
        // 问题主键
        logger.info("进入toKmo服务");
        String questionOid = String.valueOf(data.get("questionOid"));
        logger.info("主键是"+questionOid);
        questionToKnoBaseBiz.dataToKmo(questionOid);
    }
}
