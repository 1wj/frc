package com.digiwin.app.frc.service.athena.config.aspect;

import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionProcessResultEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionProcessStatusEnum;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.BaseEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.rqi.EocUtils;
import com.digiwin.app.service.DWServiceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName QuestionInitAspect
 * @Description TODO
 * @Author author
 * @Date 2022/2/11 11:10
 * @Version 1.0
 **/
@Aspect
@Component
public class QuestionInitAspect {


    /**
     * 问题发起-批量新增任务卡切面
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper.insertBatchActionTrace(..)) )")
    public Object insertBatchActionTrace(ProceedingJoinPoint pjp) throws Throwable {
        Object [] args = pjp.getArgs();
        initBatchActionTrace(args);
        return pjp.proceed(args);
    }

    /**
     * 问题发起-批量新增数据实例切面
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper.insertBatchDataInstance(..)) )")
    public Object insertBatchDataInstance(ProceedingJoinPoint pjp) throws Throwable {
        Object [] args = pjp.getArgs();
        initBatchDataInstance(args);
        return pjp.proceed(args);
    }

    /**
     * 问题发起-批量新增附件赋值
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper.insertBatchAttachment(..)) )")
    public Object insertBatchAttachment(ProceedingJoinPoint pjp) throws Throwable {
        Object [] args = pjp.getArgs();
        initAttachmentData(args);
        return pjp.proceed(args);
    }

    /**
     * 问题发起-批量新增任务卡
     * @param args
     * @throws Exception
     */
    private void initBatchActionTrace(Object[] args) throws Exception {
        if (args.length == 0) {
            return;
        }
        List<QuestionActionTraceEntity> lists = (List<QuestionActionTraceEntity>) args[0];
        lists.stream().forEach(e -> {
            if (e instanceof BaseEntity) {
                e.setTenantsid(TenantTokenUtil.getTenantSid());
                e.setCreateTime(new Date());
                Map user = null;
                try {
                    user = EocUtils.getEmpIdForMap(TenantTokenUtil.getUserId());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
               if(user != null){
                   e.setCreateId((String) user.get("id"));
                   e.setCreateName((String) user.get("name"));
                   e.setUpdateTime(new Date());
                   e.setUpdateName((String) user.get("name"));
               }
            }
            if (e instanceof QuestionActionTraceEntity) {
                e.setActualCompleteDate(new Date());
                e.setStartTime(new Date());
            }
        });
    }

    /**
     * 问题发起-批量新增数据实例赋值
     * @param args
     * @throws Exception
     */
    private void initBatchDataInstance(Object[] args){
        if (args.length == 0) {
            return;
        }
        List<DataInstanceEntity> lists = (List<DataInstanceEntity>) args[0];
        lists.stream().forEach(e -> {
            e.setTenantsid(TenantTokenUtil.getTenantSid());
            e.setCreateTime(new Date());
            e.setCreateName(TenantTokenUtil.getUserName());
            e.setUpdateTime(new Date());
            e.setUpdateName(TenantTokenUtil.getUserName());
        });
    }

    /**
     * 问题发起-初始化附件信息
     * @param args
     */
    private  void initAttachmentData(Object[] args){
        if (args.length == 0) {
            return;
        }
        List<AttachmentEntity> lists = (List<AttachmentEntity>) args[0];
        lists.stream().forEach(e -> {
            e.setTenantsid(TenantTokenUtil.getTenantSid());
            e.setCreateTime(new Date());
            e.setCreateName(TenantTokenUtil.getUserName());
            e.setUpdateTime(new Date());
            e.setUpdateName(TenantTokenUtil.getUserName());
        });
        return;
    }


}
