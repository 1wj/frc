package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.eightD;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.RoleEightDEnum;
import com.digiwin.app.frc.service.athena.config.annotation.ParamValidationHandler;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionSolutionEditEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionSolutionMeasureEntity;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionSolutionEditMapper;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionSolutionMeasureMapper;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.EightDQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.QuestionInfo8DModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.TeamBuilderModel;
import com.digiwin.app.frc.service.athena.solutions.domain.vo.eightD.PendingQuestionVo;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.DateUtil;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.TransferTool;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.frc.service.athena.util.rqi.EocUtils;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName QuestionFormTeam
 * @Description 组建团队逻辑处理
 * @Author HeX
 * @Date 2022/3/8 3:13
 * @Version 1.0
 **/
public class QuestionFormTeam implements QuestionHandlerStrategy {
    /**
     * 手动注入(工厂new出的对象)
     */
    @Autowired
    private QuestionSolutionMeasureMapper questionSolutionMeasureMapper =  SpringContextHolder.getBean(QuestionSolutionMeasureMapper.class);

    @Autowired
    private QuestionSolutionEditMapper questionSolutionEditMapper = SpringContextHolder.getBean(QuestionSolutionEditMapper.class);

    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    EightDQuestionBiz eightDQuestionBiz =  SpringContextHolder.getBean(EightDQuestionBiz.class);



    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {

        JSONObject resultJsonObject = JSON.parseObject(parameters);
        //1.string转model，将待更新的字段转为model
        TeamBuilderModel teamBuilderModel = TransferTool.convertString2Model(parameters, TeamBuilderModel.class);

        //2.参数校验
        if (teamBuilderModel!=null){
            ParamCheckUtil.checkTeamBuilderParams(teamBuilderModel);
        }else {
            throw new DWRuntimeException("参数为空");
        }
        //3.更新任务卡状态
        QuestionInfo8DModel questionInfoModel = teamBuilderModel.getQuestionInfos().get(0);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONArray teamMemberInfo = resultJsonObject.getJSONArray("team_member_info");
        JSONArray planManagementInfo = resultJsonObject.getJSONArray("plan_arrange");
        JSONArray attachmentInfos =  resultJsonObject.getJSONArray("attachment_info");
        //4.处理更新数据

        return updateDetailInfo(entity,teamMemberInfo,planManagementInfo,attachmentInfos,questionInfoModel.getOid());
    }



    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 1-获取当前处理步骤前一步骤
            List<BeforeQuestionVo> beforeQuestionVos;
            // 校验skip
            ParamValidationHandler.validateParams(entity);
            if ("2".equals(entity.getSkip())) {
                beforeQuestionVos = actionTraceMapper.getBeforeQuestionTraceForIdentity(TenantTokenUtil.getTenantSid(),entity.getQuestionNo(),QuestionUpdateEnum.question_identification.getCode(), null);
            }else {
                // 获取当前处理步骤的前一节点(获取问题识别审核数据)
                beforeQuestionVos = actionTraceMapper.getBeforeQuestionTraceForIdentity(TenantTokenUtil.getTenantSid(),entity.getQuestionNo(),QuestionUpdateEnum.question_identification_review.getCode(), null);
            }
            // 2-新增待审核问题追踪
            String dataInstanceOid = IdGenUtil.uuid();
            String oid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            entity.setOid(oid);

            // 3-获取处理顺序
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStepForIdentity(TenantTokenUtil.getTenantSid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

            // 4-表单数据流转
            DataInstanceEntity dataInstanceEntity = createDataInstance(dataInstanceOid,oid,beforeQuestionVos,entity);
            actionTraceBiz.insertActionTrace(entity,dataInstanceEntity);

            // 5-封装response数据
            PendingQuestionVo vo = new PendingQuestionVo();
            BeanUtils.copyProperties(entity, vo);
            String userId = TenantTokenUtil.getUserId();
            vo.setEmpId(EocUtils.getEmpId(userId));
            vo.setEmpName(EocUtils.getEmpName(userId));
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            responseParam.add(jsonObject);
        }
        responseObject.put("return_data",responseParam);
        return responseObject;
    }





    @Override
    public JSONObject handleBack(List<QuestionActionTraceEntity> actionTraceEntityList)  {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 获取退回的节点的数据
            List<BeforeQuestionVo> beforeQuestionVo = actionTraceMapper.getBeforeQuestionTraceForIdentity((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionNo(), entity.getQuestionProcessStep(),
                    entity.getQuestionSolveStep());
            // 新增待审核问题追踪
            String dataInstanceOid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            String oid = IdGenUtil.uuid();
            entity.setOid(oid);
            entity.setTenantsid((Long) DWServiceContext.getContext().getProfile().get("tenantSid"));
            // 获取处理顺序
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);
            // 加入退回人id、name
            entity.setReturnId((String) DWServiceContext.getContext().getProfile().get("userId"));
            entity.setReturnName((String) DWServiceContext.getContext().getProfile().get("userName"));

            DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
            dataInstanceEntity.setOid(dataInstanceOid);
            dataInstanceEntity.setDataContent(beforeQuestionVo.get(0).getDataContent());
            dataInstanceEntity.setQuestionTraceOid(oid);

            actionTraceMapper.insertActionTrace(entity);
            dataInstanceMapper.insertDataInstance(dataInstanceEntity);

            JSONObject object = new JSONObject();
            object.put("pending_approve_question_id",oid);
            responseParam.add(object);


        }
        responseObject.put("return_data",responseParam);
        return responseObject;
    }



    /**
     * 生成待审核的表单内容
     * @param dataInstanceOid   待生成的当前处理步骤的问题实例表 主键
     * @param oid 当前节点的问题处理追踪表 主键
     * @param beforeQuestionVos 问题分析审核 表单数据
     * @return
     */
    private DataInstanceEntity createDataInstance(String dataInstanceOid, String oid, List<BeforeQuestionVo> beforeQuestionVos,QuestionActionTraceEntity  traceEntity) throws Exception {
        String dataContent = beforeQuestionVos.get(0).getDataContent();

        //获取前一个节点问题分析审核的表单数据
        JSONObject resultJsonObject = JSON.parseObject(dataContent);

        //获取最外层 question_result[].get(0)
        JSONObject questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT).getJSONObject(0);

        //添加  team_member 和  plan_management
        JSONArray teamMember = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        String userId = beforeQuestionVos.get(0).getLiablePersonId();
        Map<String,Object> map = EocUtils.getEmpIdForMap2(userId);
        JSONArray depts = (JSONArray) map.get("depts");
        JSONObject obj = (JSONObject) depts.get(0);
        jsonObject.put("member_id",traceEntity.getLiablePersonId());
        jsonObject.put("member_name",traceEntity.getLiablePersonName());
        jsonObject.put("department_id",obj.get("id"));
        jsonObject.put("department_name",obj.get("name"));
        jsonObject.put("duty_no",obj.get("levelId"));
        jsonObject.put("duty_name",obj.get("levelName"));
        jsonObject.put("role_no", RoleEightDEnum.GROUP_LEADER.getCode());
        jsonObject.put("role_name",RoleEightDEnum.GROUP_LEADER.getMessage());
        jsonObject.put("remark","");
        teamMember.add(jsonObject);
        questionResult.put("team_member_info",teamMember);
        //获取8D解决方案步骤中的信息
        JSONArray planManagement = new JSONArray();
        QuestionSolutionEditEntity editEntity = questionSolutionEditMapper.getQuestionSolutionEditInfoByEditNo(TenantTokenUtil.getTenantSid(), "SE001");
        List<QuestionSolutionMeasureEntity> measureEntities = questionSolutionMeasureMapper.queryMeasureInfoByEditOid(editEntity.getOid(), TenantTokenUtil.getTenantSid());
        List<QuestionSolutionMeasureEntity> entityList = measureEntities.stream().filter(item -> !"SE001001".equals(item.getMeasureNo())).collect(Collectors.toList());
        for (QuestionSolutionMeasureEntity measureEntity : entityList) {
            JSONObject object = new JSONObject();
            object.put("step_no",measureEntity.getMeasureNo());
            object.put("step_name",measureEntity.getMeasureName());
            object.put("liable_person_id","");
            object.put("liable_person_name","");
            //D3围堵措施时间不得超过期望关闭时间
            if("SE001002".equals(measureEntity.getMeasureNo())){
                String expectDate = DateUtil.getExpectDateWithHourMinute(measureEntity.getExpectCompleteTime());
                JSONObject questionBasicInfo = (JSONObject) questionResult.getJSONArray("question_basic_info").get(0);
                String expectSolveDate = questionBasicInfo.getString("expect_solve_date");
                long expectSolveDateTime = new SimpleDateFormat("yyyy-MM-dd").parse(expectSolveDate).getTime();
                object.put("expect_solve_date",expectSolveDateTime >
                        System.currentTimeMillis() ? expectDate : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(expectSolveDateTime)));
            }else{
                object.put("expect_solve_date", DateUtil.getExpectDateWithHourMinute(measureEntity.getExpectCompleteTime()));
            }
            object.put("attachment_upload_flag","N");
            object.put("human_bottleneck_analysis","");
            planManagement.add(object);
        }
        questionResult.put("plan_arrange",planManagement);
        //添加第一关卡的预计完成时间  yyyy-MM-dd HH:mm:ss
        Optional<QuestionSolutionMeasureEntity> firstMeasureEntity = measureEntities.stream().filter(item -> "SE001001".equals(item.getMeasureNo())).findFirst();
        if(firstMeasureEntity.isPresent()){
            traceEntity.setExpectCompleteDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(DateUtil.getExpectDateWithHourMinute(firstMeasureEntity.get().getExpectCompleteTime())));
        }
        // null 转 “”
        String dataContentString = JSON.toJSONString(resultJsonObject, filter);
        // 落存问题描述和组建团队的详细数据
        DataInstanceEntity entity = new DataInstanceEntity();
        entity.setOid(dataInstanceOid);
        entity.setDataContent(dataContentString);
        entity.setQuestionTraceOid(oid);
        return entity;
    }

    /**
     * null转“” 过滤
     */
    private ValueFilter filter = (obj, s, v) -> {
        if (null == v) {
            return "";
        }
        return v;
    };


    private JSONObject updateDetailInfo(QuestionActionTraceEntity entity, JSONArray teamMemberInfo, JSONArray planManagementInfo,JSONArray attachmentModels, String oid) {
        // 获取需更新的表单数据，并解析结构，获取核心数据
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        JSONObject dataDetail = getDataDetail(resultJsonObject);

        //处理团队成员信息
        dataDetail.remove("team_member_info");
        dataDetail.put("team_member_info",teamMemberInfo);

        //处理计划安排信息  时间处理 更新时 添加时分秒
        JSONArray beforePlanArrange = dataDetail.getJSONArray("plan_arrange");
        for (Iterator<Object> iterator = beforePlanArrange.iterator(); iterator.hasNext();) {
            JSONObject beforeObj = (JSONObject)iterator.next();
            for (Iterator<Object> iteratorNew = planManagementInfo.iterator(); iteratorNew.hasNext();) {
                JSONObject newObj = (JSONObject)iteratorNew.next();
                if(beforeObj.get("step_no").equals(newObj.get("step_no"))){
                    String beforeExpectSolveDate = (String)beforeObj.get("expect_solve_date");
                    String expectSolveDate = (String) newObj.get("expect_solve_date");
                    String substring = beforeExpectSolveDate.substring(10);
                    expectSolveDate = expectSolveDate+substring;
                    newObj.put("expect_solve_date", expectSolveDate);
                    break;
                }
            }
        }
        //处理计划安排信息
        dataDetail.remove("plan_arrange");
        dataDetail.put("plan_arrange",planManagementInfo);


        // 处理附件
        JSONArray attachmentInfos = (JSONArray) dataDetail.get("attachment_info");
        JSONArray mustUploadAttachments = new JSONArray();

        for (Iterator<Object> iterator = attachmentModels.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            boolean status = true;
            for (Iterator<Object> it = attachmentInfos.iterator();it.hasNext();) {
                JSONObject attach = (JSONObject)it.next();
                if (attach.get("attachment_id").equals(obj.get("attachment_id"))) {
                    status =false;
                    break;
                }
            }
            if (status) {
                mustUploadAttachments.add(obj);
            }
        }

        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE001001");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());

        // 初始实体
        DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,dataInstanceEntity);

        return eightDQuestionBiz.handleUpdateForTeamBuilder(entity,attachmentEntities,dataInstanceEntity);
    }

    /**
     * 获取需更新的表单数据，并解析结构，获取核心数据
     * @param resultJsonObject
     * @return
     */
    private JSONObject getDataDetail(JSONObject resultJsonObject) {
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        return questionResult.getJSONObject(0);
    }

}
