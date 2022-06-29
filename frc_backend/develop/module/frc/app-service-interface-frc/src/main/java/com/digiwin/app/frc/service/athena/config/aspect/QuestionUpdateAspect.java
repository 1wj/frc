package com.digiwin.app.frc.service.athena.config.aspect;

import com.digiwin.app.frc.service.athena.qdh.domain.entity.BaseEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.service.DWServiceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @ClassName QuestionUpdateAspect
 * @Description 更新切面
 * @Author HeX
 * @Date 2022/2/16 11:29
 * @Version 1.0
 **/
@Aspect
@Component
public class QuestionUpdateAspect {

    /**
     * 更新问题识别-切面
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper.updateActionTrace(..)) )")
    public Object beforeUpdateActionTrace(ProceedingJoinPoint pjp) throws Throwable {
        Object [] args = pjp.getArgs();
        updateValue(args);
        return pjp.proceed(args);
    }

    /**
     * 更新问题实例-切面
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper.updateDataInstance(..)) )")
    public Object beforeUpdateDataInstance(ProceedingJoinPoint pjp) throws Throwable {
        Object [] args = pjp.getArgs();
        updateValue(args);
        return pjp.proceed(args);
    }

    private void updateValue(Object[] args) {
        if (args.length == 0) {
            return;
        }
        //继承BaseEntity基础类
        if (args[0] instanceof BaseEntity) {
            BaseEntity baseEntity = (BaseEntity)args[0];
            baseEntity.setTenantsid((Long) DWServiceContext.getContext().getProfile().get("tenantSid"));
            baseEntity.setUpdateName((String) DWServiceContext.getContext().getProfile().get("userName"));
            baseEntity.setUpdateTime(new Date());
            baseEntity.setUpdateId(TenantTokenUtil.getUserId());
        }
        if (args[0] instanceof QuestionActionTraceEntity) {
            QuestionActionTraceEntity actionTraceEntity = (QuestionActionTraceEntity)args[0];
            actionTraceEntity.setActualCompleteDate(new Date());
        }
    }

}
