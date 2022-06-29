package com.digiwin.app.frc.service.impl.athena.app;

import com.digiwin.app.schedule.entity.DWJobResult;
import com.digiwin.app.schedule.quartz.job.DWJob;
import com.digiwin.app.service.DWService;

import java.util.Date;
import java.util.Map;

public class TaskTest implements DWService, DWJob {
    @Override
    public DWJobResult executeJob(Map<String, Object> paramMap) throws Exception {
        System.err.println("定时器："+new Date());
        DWJobResult result = new DWJobResult();
        result.setExecuteStatus(DWJobResult.OK);
        return result;
    }
}
