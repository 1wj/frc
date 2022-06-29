package com.digiwin.app.frc.service.athena.config.aspect;

import com.digiwin.app.frc.service.athena.qdh.domain.entity.BaseEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.rqi.EocUtils;
import com.digiwin.app.service.DWServiceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

/**
 * @ClassName BaseEntityAspect
 * @Description 基础类切面，平台获取租户、创建人、创建时间
 * @Author author
 * @Date 2021/11/11 19:57
 * @Version 1.0
 **/
@Aspect
@Component
public class BaseEntityAspect {
    /**
     * 用于任务更新-平台获取租户、创建人、创建时间
     * 在mapper层处理最佳
     * @param pjp 切点
     * @return pjp.proceed(args)
     */
    @Around("execution(* com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper.updateDataInstance(..)) )")
    public Object beforeUpdateDataInstance(ProceedingJoinPoint pjp) throws Throwable {
        Object [] args = pjp.getArgs();
        initUpdateValue(args);
        return pjp.proceed(args);
    }

    @Around("execution(* com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper.insertActionTrace(..)) )")
    public Object beforeInsertUnapprovedActionTrace(ProceedingJoinPoint pjp) throws Throwable {
        Object [] args = pjp.getArgs();
        initUnapprovedQuestionTraceValue(args);
        return pjp.proceed(args);
    }


    @Around("execution(* com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper.insertInitActionTrace(..)) )")
    public Object beforeInsertInitUnapprovedActionTrace(ProceedingJoinPoint pjp) throws Throwable {
        Object [] args = pjp.getArgs();
        initInitQuestionTraceValue(args);
        return pjp.proceed(args);
    }

    @Around("execution(* com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper.insertDataInstance(..)) )")
    public Object beforeInsertDataInstance(ProceedingJoinPoint pjp) throws Throwable {
        Object [] args = pjp.getArgs();
        initRecordValue(args);
        return pjp.proceed(args);
    }

    /**
     * insertRecord() 新增问题记录 赋值
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.digiwin.app.frc.service.athena.qdh.mapper.RecordMapper.insertRecord(..)) )")
    public Object beforeInsertRecord(ProceedingJoinPoint pjp) throws Throwable {
        Object [] args = pjp.getArgs();
        initRecordValue(args);
        return pjp.proceed(args);
    }





    private void initUnapprovedQuestionTraceValue(Object[] args) {
        if (args.length == 0) {
            return;
        }
        //继承BaseEntity基础类
        if (args[0] instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity)args[0];
            baseEntity.setTenantsid((Long) DWServiceContext.getContext().getProfile().get("tenantSid"));
            baseEntity.setCreateTime(new Date());
            baseEntity.setCreateName((String) DWServiceContext.getContext().getProfile().get("userName"));
            baseEntity.setUpdateTime(new Date());
            baseEntity.setUpdateName((String) DWServiceContext.getContext().getProfile().get("userName"));
        }
        if (args[0] instanceof QuestionActionTraceEntity) {
            QuestionActionTraceEntity actionTraceEntity = (QuestionActionTraceEntity)args[0];
            actionTraceEntity.setQuestionProcessStatus(2);
            actionTraceEntity.setQuestionProcessResult(1);
            actionTraceEntity.setStartTime(new Date());
        }
    }

    /**
     * 赋值问题处理追踪
     * @param args
     * @throws Exception
     */
    private void initInitQuestionTraceValue(Object[] args) throws Exception {
        if (args.length == 0) {
            return;
        }
        //继承BaseEntity基础类
        if (args[0] instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity)args[0];
            baseEntity.setTenantsid((Long) DWServiceContext.getContext().getProfile().get("tenantSid"));
            baseEntity.setCreateTime(new Date());
            //调整为0开头，不是90开头
            Map user = EocUtils.getEmpIdForMap((String) DWServiceContext.getContext().getProfile().get("userId"));
            baseEntity.setCreateId((String) user.get("id"));
            baseEntity.setCreateName((String) user.get("name"));
            baseEntity.setUpdateTime(new Date());
            baseEntity.setUpdateName((String) user.get("name"));
        }
        if (args[0] instanceof QuestionActionTraceEntity) {
            QuestionActionTraceEntity actionTraceEntity = (QuestionActionTraceEntity)args[0];
            actionTraceEntity.setQuestionProcessStatus(4);
            actionTraceEntity.setQuestionProcessResult(2);
            actionTraceEntity.setActualCompleteDate(new Date());
            actionTraceEntity.setStartTime(new Date());
        }
    }

    private void initUpdateValue(Object[] args) {
        if (args.length == 0) {
            return;
        }
        //继承BaseEntity基础类
        if (args[0] instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity)args[0];
            baseEntity.setTenantsid((Long) DWServiceContext.getContext().getProfile().get("tenantSid"));
            baseEntity.setUpdateTime(new Date());
            baseEntity.setUpdateName((String) DWServiceContext.getContext().getProfile().get("userName"));
        }
    }



    /**
     * 问题发起-初始化record数据
     * @param args
     */
    private void initRecordValue(Object[] args) {
        if (args.length == 0) {
            return;
        }
        //继承BaseEntity基础类
        if (args[0] instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity)args[0];
            baseEntity.setTenantsid(TenantTokenUtil.getTenantSid());
            baseEntity.setCreateTime(new Date());
            baseEntity.setCreateName(TenantTokenUtil.getUserName());
            baseEntity.setUpdateTime(new Date());
            baseEntity.setUpdateName(TenantTokenUtil.getUserName());
        }
    }


}
