package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.eightD;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.Question8DSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.EightDQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.IamEocBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.QuestionDetailVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.ContainmentMeasureExecuteInfoModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.eightD.QuestionInfo8DModel;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName QuestionContainmentMeasureExecute
 * @Description 围堵措施执行
 * @Author HeX
 * @Date 2022/3/8 11:08
 * @Version 1.0
 **/
public class QuestionContainmentMeasureExecute implements QuestionHandlerStrategy {
    @Autowired
    AttachmentMapper attachmentMapper  =  SpringContextHolder.getBean(AttachmentMapper.class);

    /**
     * 手动注入(工厂new出的对象)
     */
    @Autowired
    ActionTraceMapper actionTraceMapper =  SpringContextHolder.getBean(ActionTraceMapper.class);

    @Autowired
    DataInstanceMapper dataInstanceMapper =  SpringContextHolder.getBean(DataInstanceMapper.class);

    @Autowired
    ActionTraceBiz actionTraceBiz =  SpringContextHolder.getBean(ActionTraceBiz.class);

    @Autowired
    EightDQuestionBiz eightDQuestionBiz =  SpringContextHolder.getBean(EightDQuestionBiz.class);


    @Autowired
    IamEocBiz iamEocBiz =  SpringContextHolder.getBean(IamEocBiz.class);

    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        JSONObject resultJsonObject = JSON.parseObject(parameters);

        //1.string转model，将待更新的字段转为model
        ContainmentMeasureExecuteInfoModel measureExecuteInfoModel = TransferTool.convertString2Model(parameters, ContainmentMeasureExecuteInfoModel.class);
        //2. 参数校验
        ParamCheckUtil.checkMeasureExecuteParams(measureExecuteInfoModel);

        //3.更新任务卡状态
        QuestionInfo8DModel questionInfoModel = measureExecuteInfoModel.getQuestionInfos().get(0);
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONArray containmentMeasuresExecute = resultJsonObject.getJSONArray("containment_measure_execute");
        JSONArray attachmentInfos = resultJsonObject.getJSONArray("attachment_info");
        return handleDetail(entity,containmentMeasuresExecute,attachmentInfos,questionInfoModel.getOid());
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 1-获取当前处理步骤前一步骤
            List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), Question8DSolveEnum.containment_measure.getCode());

            if (CollectionUtils.isEmpty(beforeQuestionVos)) {
                throw new DWRuntimeException( MultilingualismUtil.getLanguage("beforeStepNull"));
            }

            createDataInstance1(entity, beforeQuestionVos.get(0), responseParam);
        }
        responseObject.put("return_data",responseParam);
        return responseObject;
    }


    private JSONArray createDataInstance1(QuestionActionTraceEntity entity,BeforeQuestionVo beforeQuestionVo,JSONArray responseParam) throws Exception {

        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(beforeQuestionVo.getDataContent());
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray(QuestionResponseConst.QUESTION_RESULT);
        JSONObject dataDetail = questionResult.getJSONObject(0);

        //+ 预计完成时间
        DateUtil.assignValueForExpectCompleteTime(entity,dataDetail,Question8DSolveEnum.containment_measure.getCode());

        // containment_measure 处理循环复用问题
        JSONArray containmentMeasure = dataDetail.getJSONArray("containment_measure");
        dataDetail.remove("containment_measure");
        String textNew = JSON.toJSONString(containmentMeasure, SerializerFeature.DisableCircularReferenceDetect);
        JSONArray jsonArray = JSON.parseArray(textNew);
        dataDetail.put("containment_measure",jsonArray);

        String dataInstanceOid;
        String oid;
        Map<String, List<Map<String, Object>>> collect = handleMeasureExecute(containmentMeasure);
        for (Map.Entry<String, List<Map<String, Object>>> m : collect.entrySet()) {
            List<Map<String, Object>> re = collect.get(m.getKey());
            // 初始化-待审核问题-数据 entity
            dataInstanceOid = IdGenUtil.uuid();
            oid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            entity.setOid(oid);
            entity.setLiablePersonId((String) re.get(0).get("liable_person_id"));
            entity.setLiablePersonName((String) re.get(0).get("liable_person_name"));
            // 获取执行顺序
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);


            dataDetail.put("containment_measure_execute",re);

            // 保留value为null的数据
            JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
            // jsonObject转string
            String dataContentString = JSON.toJSONString(resultJsonObject);

            // 落存围堵措施执行信息
            DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
            dataInstanceEntity.setOid(dataInstanceOid);
            dataInstanceEntity.setDataContent(dataContentString);
            dataInstanceEntity.setQuestionTraceOid(oid);
            actionTraceBiz.insertActionTraceForCurb(entity,dataInstanceEntity);

            dataDetail.remove("containment_measure_execute",re);


            JSONObject responseObject = new JSONObject();
            responseObject.put("pending_approve_question_id",oid);
            responseObject.put("question_no",entity.getQuestionNo());
            responseObject.put("question_description",entity.getQuestionDescription());
            responseObject.put("return_flag_id",entity.getReturnFlagId());
            responseObject.put("return_flag_name",entity.getReturnFlagName());
            //生成待审核的任务卡取最大的预计完成时间
            Date maxDate = new SimpleDateFormat("yyyy-MM-dd").parse((String) re.get(0).get("expect_solve_date"));
            for (int i = 1; i < re.size(); i++) {
                maxDate = new SimpleDateFormat("yyyy-MM-dd").parse((String) re.get(i).get("expect_solve_date")).getTime() > maxDate.getTime() ?
                        new SimpleDateFormat("yyyy-MM-dd").parse((String) re.get(i).get("expect_solve_date")) : maxDate;
            }
            responseObject.put("expect_solve_date",maxDate);

            Map<String,Object> user = iamEocBiz.getEmpUserId((String) re.get(0).get("liable_person_id"));
            responseObject.put("liable_person_id",user.get("id"));
            responseObject.put("liable_person_name",user.get("name"));
            responseObject.put("employee_id",re.get(0).get("liable_person_id"));
            responseObject.put("employee_name", re.get(0).get("liable_person_name"));

            responseParam.add(responseObject);

        }


        return responseParam;
    }



    @Override
    public JSONObject handleBack(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        return null;
    }




    private Map<String, List<Map<String, Object>>> handleMeasureExecute(JSONArray containmentMeasure){
        List<Map<String, Object>> containmentMeasureDetails = new ArrayList<>();
        containmentMeasure.stream().forEach(pb -> {
            Map<String, Object> rightMap = (Map<String, Object>) pb;
            rightMap.put("containment_status","1");
            rightMap.remove("attachment_upload_flag");
            rightMap.put("containment_illustrate","");
            rightMap.put("actual_finish_date","");
            rightMap.put("process_work_hours","");
            containmentMeasureDetails.add(rightMap);
        });

        // 将集合list按照liable_person_id进行分组
        Map<String, List<Map<String, Object>>> collect = containmentMeasureDetails.stream().collect(Collectors.groupingBy(this::customKey));
        collect.forEach((k, v) -> {
            System.out.println(k + " -> " + v);
        });

        return collect;
    }

    private  String customKey(Map<String,Object> map){
        return map.get("liable_person_id").toString();
    }


    /**
     * 更新表单详情数据
     * @param containmentMeasuresExecute 入参 需更新字段信息
     * @param oid 问题处理追踪主键
     * @throws JsonProcessingException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONArray containmentMeasuresExecute,JSONArray attachmentModels,  String oid) throws IOException {
        // 获取入参 需更新表单
        // 获取反馈单表单信息
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        // 获取最外层 question_result
        JSONArray questionResult = resultJsonObject.getJSONArray("question_result");
        JSONObject dataDetail = (JSONObject) questionResult.get(0);

        JSONArray containmentMeasure = dataDetail.getJSONArray("containment_measure");

        //处理预计完成时间年月日时分秒
        DateUtil.measuresExecute(containmentMeasuresExecute);
        for( Iterator<Object> iterator1 = containmentMeasuresExecute.iterator();iterator1.hasNext();){
            JSONObject item = (JSONObject) iterator1.next();
            String processWorkHours = item.getString("process_work_hours");
            //验证处理工时字段为非负数
            if (NumberUtil.isNumericByRegEx(processWorkHours)){
                double num=Double.parseDouble(processWorkHours);
                if (num<0){
                    throw new DWRuntimeException("处理工时请输入非负数");
                }
            }else {
                throw new DWRuntimeException("处理工时请输入非负数");
            }
        }

        // 将containment_measures_execute信息加入
        dataDetail.remove("containment_measure_execute");
        dataDetail.put("containment_measure_execute",containmentMeasuresExecute);

        JSONArray attachmentInfos = (JSONArray) dataDetail.get("attachment_info");

        //本地上传的附件信息
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

        //校验附件是否必传
        //获取当前任务卡的负责人信息
        QuestionDetailVo questionTrace = actionTraceMapper.getQuestionTrace(questionActionTraceEntity.getOid());
        String currentLiablePersonId = questionTrace.getLiablePersonId();
        //围堵场所根据负责人id进行分组
        Map<String, List<Map<String, Object>>> listMap = processContainmentMeasureInfos(containmentMeasure);
//Map.Entry<String, List<Map<String, Object>>> key : listMap.entrySet()
        for (Map.Entry<String, List<Map<String, Object>>> m : listMap.entrySet()) {
            String key=m.getKey();
            List<Map<String, Object>> mapList = listMap.get(key);
            //负责人id相同  判断附件是否必传
            if(currentLiablePersonId.equals(key)){
                boolean flag = mapList.stream().anyMatch(item -> !StringUtils.isEmpty(item.get("attachment_upload_flag")) && item.get("attachment_upload_flag") == "Y");
                if (flag) {
                    if (Collections.isEmpty(mustUploadAttachments)) {
                        // 查询数据库
                        List<AttachmentEntity> attachmentEntities = attachmentMapper.getAttachments((Long) DWServiceContext.getContext().getProfile().get("tenantSid"), dataInstanceVo.getOid());
                        if (Collections.isEmpty(attachmentEntities)) {
                            throw new DWRuntimeException("attachment_upload_flag is Y, so attachment must be uploaded ! ");
                        }
                        break;
                    }
                }
            }

        }

        // 处理附件
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE001003");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // 初始实体
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // 落存数据
        return eightDQuestionBiz.handleUpdateForContainmentMeasureExecute(questionActionTraceEntity,attachmentEntities,entity);
    }

    private Map<String, List<Map<String, Object>>> processContainmentMeasureInfos(JSONArray containmentMeasure) {
        List<Map<String, Object>> containmentMeasureDetails = new ArrayList<>();
        containmentMeasure.stream().forEach(pb -> {
            Map<String, Object> rightMap = (Map<String, Object>) pb;
            containmentMeasureDetails.add(rightMap);
        });

        // 将集合list按照liable_person_id进行分组
        Map<String, List<Map<String, Object>>> collect = containmentMeasureDetails.stream().collect(Collectors.groupingBy(this::customKey));
        collect.forEach((k, v) -> {
            System.out.println(k + " -> " + v);
        });
        return collect;
    }
}
