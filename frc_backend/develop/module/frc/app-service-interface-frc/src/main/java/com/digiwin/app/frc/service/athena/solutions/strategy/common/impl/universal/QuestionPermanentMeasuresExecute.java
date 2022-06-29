package com.digiwin.app.frc.service.athena.solutions.strategy.common.impl.universal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUniversalSolveEnum;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionUpdateEnum;
import com.digiwin.app.frc.service.athena.qdh.biz.ActionTraceBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.IamEocBiz;
import com.digiwin.app.frc.service.athena.qdh.biz.UniversalQuestionBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.DataInstanceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.BeforeQuestionVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.DataInstanceVo;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.QuestionDetailVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.PermanentMeasureExecuteInfoModel;
import com.digiwin.app.frc.service.athena.solutions.domain.model.universal.QuestionInfoUniversalModel;
import com.digiwin.app.frc.service.athena.solutions.strategy.common.QuestionHandlerStrategy;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.frc.service.athena.util.qdh.AttachmentUtils;
import com.digiwin.app.frc.service.athena.util.qdh.ParamCheckUtil;
import com.digiwin.app.frc.service.athena.util.qdh.SpringContextHolder;
import com.digiwin.app.service.DWServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: xieps
 * @Date: 2022/4/11 9:55
 * @Version 1.0
 * @Description D5-1恒久措施执行
 */
public class QuestionPermanentMeasuresExecute implements QuestionHandlerStrategy {

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
    UniversalQuestionBiz universalQuestionBiz =  SpringContextHolder.getBean(UniversalQuestionBiz.class);


    @Autowired
    IamEocBiz iamEocBiz =  SpringContextHolder.getBean(IamEocBiz.class);


    Logger logger = LoggerFactory.getLogger(QuestionPermanentMeasuresExecute.class);

    @Override
    public JSONObject updateQuestion(String parameters) throws Exception {
        JSONObject resultJsonObject = JSON.parseObject(parameters);

        //1.string转model，将待更新的字段转为model
        PermanentMeasureExecuteInfoModel measureExecuteInfoModel = TransferTool.convertString2Model(parameters, PermanentMeasureExecuteInfoModel.class);
        //2. 参数校验
        ParamCheckUtil.checkPermanentMeasureExecuteParams(measureExecuteInfoModel);

        //3.更新任务卡状态
        QuestionInfoUniversalModel questionInfoModel = measureExecuteInfoModel.getQuestionInfo();
        QuestionActionTraceEntity entity = new QuestionActionTraceEntity();
        BeanUtils.copyProperties(questionInfoModel,entity);

        JSONArray lastingMeasureExecute = resultJsonObject.getJSONArray("lasting_measure_execute");
        JSONArray attachmentInfos = resultJsonObject.getJSONArray("attachment_info");
        JSONObject questionConfirm = resultJsonObject.getJSONObject("question_confirm");

        return handleDetail(entity,lastingMeasureExecute,attachmentInfos,questionConfirm,questionInfoModel.getOid());
    }

    @Override
    public JSONObject generatePendingQuestion(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        JSONObject responseObject = new JSONObject();
        JSONArray responseParam = new JSONArray();
        for (QuestionActionTraceEntity entity : actionTraceEntityList) {
            // 1-获取当前处理步骤前一步骤
            List<BeforeQuestionVo> beforeQuestionVos = actionTraceMapper.getBeforeQuestionTrace(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionUniversalSolveEnum.permanent_measures.getCode());

            if (CollectionUtils.isEmpty(beforeQuestionVos)) {
                throw new DWRuntimeException( MultilingualismUtil.getLanguage("beforeStepNull"));
            }

            createDataInstance(entity, beforeQuestionVos.get(0), responseParam);
        }
        responseObject.put("return_data",responseParam);
        return responseObject;
    }

    @Override
    public JSONObject handleBack(List<QuestionActionTraceEntity> actionTraceEntityList) throws Exception {
        return null;
    }



    private JSONArray createDataInstance(QuestionActionTraceEntity entity,BeforeQuestionVo beforeQuestionVo,JSONArray responseParam) throws Exception {

        // 获取前一结点的表单数据 string转json
        JSONObject resultJsonObject = JSON.parseObject(beforeQuestionVo.getDataContent());
        // 获取最外层 question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);

        //追踪表实体类设置预期完成时间
        DateUtil.assignValueForExpectCompleteTime(entity,dataDetail, QuestionUniversalSolveEnum.permanent_measures.getCode());

        // lasting_measure 处理循环复用问题
        JSONArray lastingMeasure = dataDetail.getJSONArray("lasting_measure");
        dataDetail.remove("lasting_measure");
        String textNew = JSON.toJSONString(lastingMeasure, SerializerFeature.DisableCircularReferenceDetect);
        JSONArray jsonArray = JSON.parseArray(textNew);
        dataDetail.put("lasting_measure",jsonArray);

        String dataInstanceOid;
        String oid;
        Map<String, List<Map<String, Object>>> collect = handleMeasureExecute(lastingMeasure);
        for (String key : collect.keySet()) {
            List<Map<String, Object>> re = collect.get(key);
            // 初始化-待审核问题-数据 entity
            String liablePersonId = (String) re.get(0).get("liable_person_id");
            dataInstanceOid = IdGenUtil.uuid();
            oid = IdGenUtil.uuid();
            entity.setDataInstanceOid(dataInstanceOid);
            entity.setOid(oid);
            entity.setLiablePersonId(liablePersonId);
            entity.setLiablePersonName((String) re.get(0).get("liable_person_name"));
            // 获取执行顺序
            List<QuestionActionTraceEntity> questionActionTraceEntities = actionTraceMapper.getLastStep((Long) DWServiceContext.getContext().getProfile().get("tenantSid"),entity.getQuestionRecordOid(),entity.getQuestionNo());
            entity.setPrincipalStep(questionActionTraceEntities.get(0).getPrincipalStep()+1);

            //增加恒久措施执行历史数据置灰操作 is_history_date为Y
            //为组装恒久措施执行数据 获取恒久措施验收历史数据
            List<BeforeQuestionVo> historicalDataList = actionTraceMapper.getHistoricalData(TenantTokenUtil.getTenantSid(),entity.getQuestionRecordOid(),
                    entity.getQuestionNo(), QuestionUpdateEnum.question_solve.getCode(), QuestionUniversalSolveEnum.permanent_measures_execute_verify.getCode(),questionActionTraceEntities.get(0).getPrincipalStep()+1);
            logger.info("#####################################获取历史数据"+historicalDataList.toString());
            if(!historicalDataList.isEmpty()){
                logger.info("不为空");
                for (BeforeQuestionVo historicalData : historicalDataList) {
                    JSONObject parseObject = JSON.parseObject( historicalData.getDataContent());
                    JSONObject questionResult = parseObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);
                    JSONArray measureExecuteVerify = questionResult.getJSONArray("lasting_measure_execute_verify");
                    for ( Iterator<Object> iterator = measureExecuteVerify.iterator();iterator.hasNext();){
                        JSONObject obj = (JSONObject) iterator.next();
                        if(liablePersonId.equals(obj.getString("liable_person_id"))){
                            obj.put("is_history_data","Y");
                            obj.remove("verify_illustrate");
                            obj.remove("verify_status");
                            obj.remove("verify_date");
                            re.add(obj);
                        }
                    }
                }
            }
            Collections.reverse(re);
            dataDetail.put("lasting_measure_execute",re);

            // 保留value为null的数据
            JSON.toJSONString(dataDetail, SerializerFeature.WriteMapNullValue);
            // jsonObject转string
            String dataContentString = JSON.toJSONString(resultJsonObject);

            // 落存恒久措施执行信息
            DataInstanceEntity dataInstanceEntity = new DataInstanceEntity();
            dataInstanceEntity.setOid(dataInstanceOid);
            dataInstanceEntity.setDataContent(dataContentString);
            dataInstanceEntity.setQuestionTraceOid(oid);
            actionTraceBiz.insertActionTraceForCurb(entity,dataInstanceEntity);

            dataDetail.remove("lasting_measure_execute",re);


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
            // 獲取員工userID 0开头
            Map<String,Object> user = iamEocBiz.getEmpUserId((String) re.get(0).get("liable_person_id"));
            responseObject.put("liable_person_id",user.get("id"));
            responseObject.put("liable_person_name",user.get("name"));
            responseObject.put("employee_id",re.get(0).get("liable_person_id"));
            responseObject.put("employee_name", re.get(0).get("liable_person_name"));

            responseParam.add(responseObject);

        }


        return responseParam;
    }


    private Map<String, List<Map<String, Object>>> handleMeasureExecute(JSONArray lastingMeasure){
        List<Map<String, Object>> permanentMeasureDetails = new ArrayList<>();
        lastingMeasure.stream().forEach(pb -> {
            Map<String, Object> rightMap = (Map<String, Object>) pb;
            boolean flag = rightMap.containsKey("is_history_data") && "Y".equals(rightMap.get("is_history_data"));
            if(!flag) {
                rightMap.remove("attachment_upload_flag");
                rightMap.put("execute_illustrate", "");
                rightMap.put("actual_finish_date", "");
                rightMap.put("process_work_hours", "");
                rightMap.put("execute_status", "1");
                rightMap.put("is_history_data", "N");
                permanentMeasureDetails.add(rightMap);
            }
        });

        // 将集合list按照liable_person_id进行分组
        Map<String, List<Map<String, Object>>> collect = permanentMeasureDetails.stream().collect(Collectors.groupingBy(this::customKey));

        return collect;
    }

    private  String customKey(Map<String,Object> map){
        return map.get("liable_person_id").toString();
    }


    /**
     * 更新表单详情数据
     * @param lastingMeasureExecute 入参 需更新字段信息
     * @param oid 问题处理追踪主键
     * @throws IOException
     */
    private JSONObject handleDetail(QuestionActionTraceEntity questionActionTraceEntity,JSONArray lastingMeasureExecute,JSONArray attachmentModels,JSONObject questionConfirm ,String oid) throws IOException {
        // 获取入参 需更新表单
        // 获取反馈单表单信息
        DataInstanceVo dataInstanceVo = dataInstanceMapper.getQuestionDetailForQuestionNo(oid);
        JSONObject resultJsonObject = JSON.parseObject(dataInstanceVo.getDataContent());
        // 获取最外层 question_result
        JSONObject dataDetail = resultJsonObject.getJSONObject("question_result");

        JSONArray lastingMeasure = dataDetail.getJSONArray("lasting_measure");

        // 将lasting_measure_execute信息加入
        dataDetail.remove("lasting_measure_execute");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String hourMinuteSecond = currentDate.substring(10);
        for(Iterator<Object> iterator = lastingMeasureExecute.iterator(); iterator.hasNext();){
            JSONObject object = (JSONObject) iterator.next();
            String expectSolveDate = object.getString("expect_solve_date");
            String actualFinish = object.getString("actual_finish_date");
            String[] words = actualFinish.trim().split(" ");
            object.put("actual_finish_date",words[0]);
            expectSolveDate+=hourMinuteSecond;
            object.remove("expect_solve_date");
            object.put("expect_solve_date",expectSolveDate);

        }
        dataDetail.put("lasting_measure_execute",lastingMeasureExecute);
        // 将question_confirm信息加入
        dataDetail.remove("question_confirm");
        dataDetail.put("question_confirm",questionConfirm);

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
        //恒久措施据负责人id进行分组
        Map<String, List<Map<String, Object>>> listMap = processContainmentMeasureInfos(lastingMeasure);

        for (String key : listMap.keySet()) {
            List<Map<String, Object>> mapList = listMap.get(key);
            //负责人id相同  判断附件是否必传
            if(currentLiablePersonId.equals(key)){
                boolean flag = mapList.stream().anyMatch(item -> !StringUtils.isEmpty(item.get("attachment_upload_flag")) && item.get("attachment_upload_flag") == "Y");
                if (flag) {
                    if (mustUploadAttachments.size() == 0) {
                        // 查询数据库
                        List<AttachmentEntity> attachmentEntities = attachmentMapper.getAttachments((Long) DWServiceContext.getContext().getProfile().get("tenantSid"), dataInstanceVo.getOid());
                        if (attachmentEntities.size() == 0) {
                            throw new DWRuntimeException("attachment_upload_flag is Y, so attachment must be uploaded ! ");
                        }
                        break;
                    }
                }
            }

        }

        // 处理附件
        List<AttachmentEntity> attachmentEntities = AttachmentUtils.handleMustAttachments(attachmentInfos,mustUploadAttachments,dataInstanceVo.getQuestionNo(),dataInstanceVo.getOid(),"SE003007");
        dataInstanceVo.setDataContent(resultJsonObject.toJSONString());
        // 初始实体
        DataInstanceEntity entity = new DataInstanceEntity();
        BeanUtils.copyProperties(dataInstanceVo,entity);
        // 落存数据
        return universalQuestionBiz.handleUpdateForPermanentMeasureExecute(questionActionTraceEntity,attachmentEntities,entity);
    }

    private Map<String, List<Map<String, Object>>> processContainmentMeasureInfos(JSONArray lastingMeasure) {
        List<Map<String, Object>> lastMeasureDetails = new ArrayList<>();
        lastingMeasure.stream().forEach(pb -> {
            Map<String, Object> rightMap = (Map<String, Object>) pb;
            lastMeasureDetails.add(rightMap);
        });

        // 将集合list按照liable_person_id进行分组
        Map<String, List<Map<String, Object>>> collect = lastMeasureDetails.stream().collect(Collectors.groupingBy(this::customKey));
        return collect;
    }
}
