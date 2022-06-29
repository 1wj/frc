package com.digiwin.app.frc.service.athena.rqi.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.*;
import com.digiwin.app.frc.service.athena.mtw.mapper.*;
import com.digiwin.app.frc.service.athena.rqi.biz.KanbanInfoDisplayBiz;
import com.digiwin.app.frc.service.athena.rqi.constants.TaskCodeConstants;
import com.digiwin.app.frc.service.athena.rqi.domain.entity.IssueManagementDetailEntity;
import com.digiwin.app.frc.service.athena.rqi.domain.entity.IssueManagementEntity;
import com.digiwin.app.frc.service.athena.rqi.domain.entity.KanbanInfoEntity;
import com.digiwin.app.frc.service.athena.rqi.domain.entity.QuestionTrackResponsibleEntity;
import com.digiwin.app.frc.service.athena.rqi.domain.model.IssueManagementDetailModel;
import com.digiwin.app.frc.service.athena.rqi.domain.model.KanbanInfoModel;
import com.digiwin.app.frc.service.athena.rqi.domain.vo.KeyBoardTemplateInfoVo;
import com.digiwin.app.frc.service.athena.rqi.mapper.KanbanInfoMapper;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.rqi.EocUtils;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: xieps
 * @Date: 2022/1/25 10:03
 * @Version 1.0
 * @Description 看板信息展示Biz-impl
 */
@Service
public class KanbanInfoDisplayBizImpl implements KanbanInfoDisplayBiz {

    @Autowired
    private KeyBoardTemplateMapper keyBoardTemplateMapper;

    @Autowired
    private KanbanInfoMapper kanbanInfoMapper;

    @Autowired
    private KeyBoardDisplayMapper keyBoardDisplayMapper;

    @Autowired
    private QuestionSolutionEditMapper editMapper;

    @Autowired
    private QuestionSolutionMeasureMapper measureMapper;

    @Autowired
    private KeyBoardAuthorityMapper keyBoardAuthorityMapper;

    @Override
    public JSONObject getKanbanSearchFieldInfo() throws JsonProcessingException {
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        JSONObject object = new JSONObject();
        //获取看板模板信息
        List<KeyBoardTemplateEntity> templateInfoList = keyBoardTemplateMapper.getKeyBoardTemplateInfo(tenantSid, null, null, null, null);
        List<JSONObject> mapList = new ArrayList<>();
        for (KeyBoardTemplateEntity entity : templateInfoList) {
            KeyBoardTemplateInfoVo vo = new KeyBoardTemplateInfoVo();
            BeanUtils.copyProperties(entity, vo);
            JSONObject jsonObject = JSONObject.parseObject(new ObjectMapper().writeValueAsString(vo));
            mapList.add(jsonObject);
        }
        object.put("kanban_template_info", mapList);
        //组装整体状态信息
        List<JSONObject> mapList2 = new ArrayList<>();
        JSONObject object1 = new JSONObject();
        object1.put("overall_status_id", "R");
        object1.put("overall_status_name", "已逾期");
        JSONObject object2 = new JSONObject();
        object2.put("overall_status_id", "Y");
        object2.put("overall_status_name", "进行中");
        JSONObject object3 = new JSONObject();
        object3.put("overall_status_id", "G");
        object3.put("overall_status_name", "已完成");
        mapList2.add(object1);
        mapList2.add(object2);
        mapList2.add(object3);
        object.put("overall_status_info", mapList2);
        return object;
    }

    @Override
    public List<JSONObject> getKanbanInfo() throws Exception {
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        //查询出所有的看板信息实体类集合
        List<KanbanInfoEntity> entities = kanbanInfoMapper.getKanbanInfo(tenantSid);
        //根据问题单号进行分组
        Map<String, List<KanbanInfoEntity>> kanbanResult;
        kanbanResult = entities.stream().collect(Collectors.groupingBy(KanbanInfoEntity::getQuestionNo));
        Set<String> questionNosKeys = kanbanResult.keySet();

        //  查询看板显示配置维护作业  该租户下的所有信息
        List<KeyBoardDisplayEntity> displayEntities = keyBoardDisplayMapper.getAllKeyBoardDisplayInfo(tenantSid);
        Map<String, List<KeyBoardDisplayEntity>> resultInfo;
        resultInfo = displayEntities.stream().collect(Collectors.groupingBy(KeyBoardDisplayEntity::getModelOid));
        Set<String> modelOids = resultInfo.keySet();

        List<JSONObject> listMax = new ArrayList<>();
        for (String modelOid : modelOids) {
            List<KeyBoardDisplayEntity> entityList2 = resultInfo.get(modelOid);
            //根据解决方案的编号进行分组
            Map<String, List<KeyBoardDisplayEntity>> result;
            result = entityList2.stream().collect(Collectors.groupingBy(KeyBoardDisplayEntity::getSolutionNo));
            Set<String> solutionNosKeys = result.keySet();

            for (String solutionNo : solutionNosKeys) {
                List<KeyBoardDisplayEntity> entityList = result.get(solutionNo);

                //根据解决方案编号 查询出相应方案主键 再根据方案主键查询出相应的步骤栏位匹配信息
                List<QuestionSolutionEditEntity> questionSolutionEditInfo = editMapper.getQuestionSolutionEditInfo(tenantSid, solutionNo, null, null, null, null, null);
                if(StringUtils.isEmpty(questionSolutionEditInfo) || questionSolutionEditInfo.isEmpty()){
                    break;
                }
                QuestionSolutionEditEntity editEntity = questionSolutionEditInfo.get(0);
                String editEntityOid = editEntity.getOid();

                List<QuestionSolutionMeasureEntity> measureEntities = measureMapper.queryMeasureInfoByEditOid(editEntityOid,tenantSid);
                if( StringUtils.isEmpty(measureEntities) || measureEntities.isEmpty()){
                    break;
                }
                ArrayList<QuestionSolutionMeasureEntity> sorftMeasureEntities = measureEntities.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(QuestionSolutionMeasureEntity::getMeasureNo))), ArrayList::new));
                JSONObject objectMeasureInfo = new JSONObject();
                int sumDay = 0;
                for (QuestionSolutionMeasureEntity measureEntity : sorftMeasureEntities) {
                    String measureNo = measureEntity.getMeasureNo();
                   // Integer expectCompleteTime = measureEntity.getExpectCompleteTime();
                    Integer expectCompleteTime = 1;
                    sumDay+=expectCompleteTime;
                    objectMeasureInfo.put(measureNo,sumDay);
                }


                //同一个解决方案 按照模板栏位id进行排序
                ArrayList<KeyBoardDisplayEntity> sortAfterDisplayEntities = entityList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(KeyBoardDisplayEntity::getFieldId))), ArrayList::new));

                //根据问题单号  遍历
                for (String questionNo : questionNosKeys) {
                    //获取该问题号下相应的解决步骤信息 并按照进行的步骤顺序进行排序   todo
                    ArrayList<KanbanInfoEntity> sortAfterKanbanEntities = kanbanResult.get(questionNo).stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(KanbanInfoEntity::getPrincipalStep))), ArrayList::new));

                    //获取每个解决方案步骤的第一步的开始时间
                    Date startTime = sortAfterDisplayEntities.get(0).getCreateTime();

                    //场景  涉及退回的情况根据步骤排序好的看板实体集合  筛选出可能涉及退回操作 的returnFlagId 不为null  并且 principalStep 最大
                    KanbanInfoEntity returnKanbanInfoEntity = null;
                    List<KanbanInfoEntity> newSorAfterKanbanEntities = sortAfterKanbanEntities.stream().filter(item -> item.getReturnFlagId() != null).collect(Collectors.toList());
                    if(!StringUtils.isEmpty(newSorAfterKanbanEntities) && !newSorAfterKanbanEntities.isEmpty()){
                        //取最大步骤 表明这是最后退回的步骤
                        returnKanbanInfoEntity = newSorAfterKanbanEntities.stream().max(Comparator.comparing(KanbanInfoEntity::getPrincipalStep)).get();
                        //sortAfterKanbanEntities = (ArrayList<KanbanInfoEntity>) sortAfterKanbanEntities.stream().filter(item -> item.getPrincipalStep() > kanbanInfoEntity.getPrincipalStep()).collect(Collectors.toList());
                    }

                    if(sortAfterKanbanEntities.isEmpty()){
                        break;
                    }

                    //遍历该问题号下  解决方案步骤solveStep的编号集合
                    List<String> solveStepList = new ArrayList<>();
                    for (KanbanInfoEntity sortAfterKanbanEntity : sortAfterKanbanEntities) {
                        solveStepList.add(sortAfterKanbanEntity.getQuestionSolveStep());
                    }

                    JSONObject currentStepObject = new JSONObject(new LinkedHashMap<>());

                    List<JSONObject> listMin = new ArrayList<>();
                    Long consumeTime = 0L;
                    String kanbanTemplateId = null;
                    String kanbanTemplateName = null;
                    String measureName = null;
                    boolean flag = false;
                    boolean flag2 = false;


                    label:
                    for (KeyBoardDisplayEntity displayEntity : sortAfterDisplayEntities) {

                        JSONObject jsonObject1 = new JSONObject();
                        //获取模板栏位名称
                        String fieldName = displayEntity.getFieldName();
                        //获取解决方案编号
                        String measureNo = displayEntity.getMeasureNo();

                        kanbanTemplateId = displayEntity.getModelOid();
                        kanbanTemplateName = displayEntity.getModelName();
                        Calendar calendar = Calendar.getInstance();
                        Integer expectConsumeDay = objectMeasureInfo.getInteger(measureNo);
                        if(expectConsumeDay == null){
                            expectConsumeDay = 0;
                        }

                        //判断解决方案的步骤编号是否包含
                        if(solveStepList.contains(measureNo)){

                            List<KanbanInfoEntity> entityList1 = sortAfterKanbanEntities.stream().filter(item -> measureNo.equals(item.getQuestionSolveStep())).collect(Collectors.toList());
//                            KanbanInfoEntity kanbanEntity = entityList1.get(0);
                            //获取相应步骤的排序的最大值
                            KanbanInfoEntity kanbanEntity = entityList1.stream().max(Comparator.comparing(KanbanInfoEntity::getPrincipalStep)).get();
                            //KanbanInfoEntity kanbanEntity = entityList1.stream().sorted(Comparator.comparing(KanbanInfoEntity::getPrincipalStep)).collect(Collectors.toList()).get(entityList1.size()-1);
                            //判断是否是相同解决方案编号  如果不是结束循环
                            if (!solutionNo.equals(kanbanEntity.getQuestionSolveStep().substring(0, 5))) {
                                break label;
                            }


                            //returnFlagId不为NULL 出现了退回情况  那么 记录principalStep 与后续步骤比较
//                            if(kanbanEntity.getReturnFlagId() != null){
//                                principalStep = kanbanEntity.getPrincipalStep();
//                            }
                            //表明有退回操作
                            if(returnKanbanInfoEntity != null){

                            }

                            //如果principalStep 不为null  表明有退回重走    为null表明没有退回操作
                            if(returnKanbanInfoEntity != null ){
                                //记录退回进行步骤小于当前步骤的序号 表明是退回后 进行的步骤
                                if(returnKanbanInfoEntity.getPrincipalStep() < kanbanEntity.getPrincipalStep()){
                                    flag = true;
                                    flag2 = true;
                                    measureName = displayEntity.getMeasureName();
                                    //步骤名column 完成时间value (只取月份和日期) 状态status
                                    jsonObject1.put("step_name", fieldName);
                                    Date actualCompleteDate = kanbanEntity.getActualCompleteDate();

                                    if (!StringUtils.isEmpty(kanbanEntity.getActualCompleteDate())) {
                                        jsonObject1.put("status", "G");
                                        currentStepObject.put("G",measureName);
                                        if(actualCompleteDate != null){
                                            calendar.setTime(actualCompleteDate);
                                            int month = calendar.get(Calendar.MONTH) + 1;
                                            int day = calendar.get(Calendar.DATE);
                                            jsonObject1.put("complete_date", month + "/" + day);
                                        }
                                    }else if (!StringUtils.isEmpty(kanbanEntity.getExceptCompleteDate()) && new Date().after(kanbanEntity.getExceptCompleteDate())) {
                                        jsonObject1.put("status", "R");
                                        currentStepObject.put("R",measureName);

                                        calendar.setTime(startTime);
                                        calendar.add(Calendar.DATE,expectConsumeDay);
                                        int month = calendar.get(Calendar.MONTH) + 1;
                                        int day = calendar.get(Calendar.DATE);
                                        jsonObject1.put("complete_date", month + "/" + day);
                                    }else {
                                        jsonObject1.put("status", "Y");

                                        currentStepObject.put("Y",measureName);

                                        calendar.setTime(startTime);
                                        calendar.add(Calendar.DATE,expectConsumeDay);
                                        int month = calendar.get(Calendar.MONTH) + 1;
                                        int day = calendar.get(Calendar.DATE);
                                        jsonObject1.put("complete_date", month + "/" + day);

                                    }

                                    if (!StringUtils.isEmpty(kanbanEntity.getUpdateDate()) && !StringUtils.isEmpty(kanbanEntity.getCreateDate())) {
                                        consumeTime += kanbanEntity.getUpdateDate().getTime() - kanbanEntity.getCreateDate().getTime();
                                    }
                                    listMin.add(jsonObject1);
                                }
                                else{
                                    //如果记录的退回步骤大于当前进行的步骤  那么表明 是已经走过的步骤
                                    JSONObject obj2 = new JSONObject();
                                    measureName = displayEntity.getMeasureName();
                                    obj2.put("step_name",fieldName);
                                    Date actualCompleteDate = kanbanEntity.getActualCompleteDate();
                                    if(actualCompleteDate != null){
                                        calendar.setTime(actualCompleteDate);
                                        int month = calendar.get(Calendar.MONTH) + 1;
                                        int day = calendar.get(Calendar.DATE);
                                        obj2.put("complete_date", month + "/" + day);
                                    }

//                                    calendar.setTime(startTime);
//                                    calendar.add(Calendar.DATE,expectConsumeDay);
//                                    int month = calendar.get(Calendar.MONTH) + 1;
//                                    int day = calendar.get(Calendar.DATE);
//                                    obj2.put("complete_date", month + "/" + day);

                                    obj2.put("status","G");
                                    currentStepObject.put("G",measureName);
                                    listMin.add(obj2);
                                }

                            }else{

                                flag = true;
                                flag2 = true;
                                measureName = displayEntity.getMeasureName();
                                //步骤名column 完成时间value (只取月份和日期) 状态status
                                jsonObject1.put("step_name", fieldName);
                                Date actualCompleteDate = kanbanEntity.getActualCompleteDate();

                                // Date expectDate =  kanbanEntity.getExceptCompleteDate();
                                if (!StringUtils.isEmpty(kanbanEntity.getActualCompleteDate())) {
                                    jsonObject1.put("status", "G");

                                    currentStepObject.put("G",measureName);

                                    if(actualCompleteDate != null){
                                        calendar.setTime(actualCompleteDate);
                                        int month = calendar.get(Calendar.MONTH) + 1;
                                        int day = calendar.get(Calendar.DATE);
                                        jsonObject1.put("complete_date", month + "/" + day);
                                    }
                                }else if (!StringUtils.isEmpty(kanbanEntity.getExceptCompleteDate()) && new Date().after(kanbanEntity.getExceptCompleteDate())) {
                                    jsonObject1.put("status", "R");

                                    currentStepObject.put("R",measureName);

                                    calendar.setTime(startTime);
                                    calendar.add(Calendar.DATE,expectConsumeDay);
                                    int month = calendar.get(Calendar.MONTH) + 1;
                                    int day = calendar.get(Calendar.DATE);
                                    jsonObject1.put("complete_date", month + "/" + day);
                                }else {
                                    jsonObject1.put("status", "Y");

                                    currentStepObject.put("Y",measureName);

                                    calendar.setTime(startTime);
                                    calendar.add(Calendar.DATE,expectConsumeDay);
                                    int month = calendar.get(Calendar.MONTH) + 1;
                                    int day = calendar.get(Calendar.DATE);
                                    jsonObject1.put("complete_date", month + "/" + day);

                                }

                                if (!StringUtils.isEmpty(kanbanEntity.getUpdateDate()) && !StringUtils.isEmpty(kanbanEntity.getCreateDate())) {
                                    consumeTime += kanbanEntity.getUpdateDate().getTime() - kanbanEntity.getCreateDate().getTime();
                                }
                                listMin.add(jsonObject1);
                            }


                        }else{
                            JSONObject obj2 = new JSONObject();
                            obj2.put("step_name",fieldName);
                            Calendar calendar1 = Calendar.getInstance();
                            calendar1.setTime(startTime);
                            calendar1.add(Calendar.DATE,expectConsumeDay);
                            int month = calendar1.get(Calendar.MONTH) + 1;
                            int day = calendar1.get(Calendar.DATE);
                            obj2.put("complete_date", month + "/" + day);
                            if(flag2){
                                obj2.put("status","R");
                                currentStepObject.put("R",measureName);
                            }else{
                                obj2.put("status", null);
                            }
                            listMin.add(obj2);
                        }


                    }
                    //封装数据
                    if(flag){
                        encapsulatedData(tenantSid, kanbanResult, listMax, measureEntities, kanbanTemplateId,kanbanTemplateName, questionNo, sortAfterKanbanEntities, currentStepObject, listMin, consumeTime);
                    }
                }

            }


        }
        return listMax;
    }

    /**
     * 对数据进行封装
     *
     *
     * @param tenantSid
     * @param kanbanResult
     * @param listMax
     * @param questionNo
     * @param sortAfterKanbanEntities
     * @param listMin
     * @param consumeTime
     */
    private void encapsulatedData(Long tenantSid, Map<String, List<KanbanInfoEntity>> kanbanResult, List<JSONObject> listMax, List<QuestionSolutionMeasureEntity> measureEntities, String kanbanTemplateId,String kanbanTemplateName, String questionNo, ArrayList<KanbanInfoEntity> sortAfterKanbanEntities, JSONObject currentStep, List<JSONObject> listMin, Long consumeTime) {
        JSONObject obj = new JSONObject();
        obj.put("step_info", listMin);
        //****存储时间相关信息
        //存储实际关闭时间取问题关闭的更新时间
        Optional<KanbanInfoEntity> infoEntity = kanbanResult.get(questionNo).stream().max(Comparator.comparing(KanbanInfoEntity::getQuestionSolveStep));
        Date updateDate = infoEntity.get().getUpdateDate();
        if(updateDate != null){
            obj.put("actual_close_date", updateDate);
        }else {
            obj.put("actual_close_date", "");
        }

        //存储实际耗时  取各步骤耗时累加和
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(consumeTime));
        obj.put("actual_consume_days", cal.get(Calendar.DATE));
        //存储 问题解决预计总用时(天)  维护作业中解决方案配置中每个步骤预计完成天数之和
        int expectAllDate = 1;
        //expectAllDate = measureEntities.stream().mapToInt(Integer.parseInt(QuestionSolutionMeasureEntity::getExpectCompleteTime)).sum();
        obj.put("question_solve_total_plan_hours", "");
        //存储预计关闭时间
        cal.setTime(infoEntity.get().getCreateDate());
        cal.add(Calendar.DATE,expectAllDate);
        obj.put("plan_close_date",cal.getTime());

        //发生时间  取的是问题确认的create_date
        Date happenTime = kanbanInfoMapper.getHappenTimeByQuestionNo(tenantSid,questionNo);
        obj.put("happen_date",happenTime);

        //存储问题单号
        obj.put("question_no", questionNo);
        //看板模板主键
        obj.put("kanban_template_id", kanbanTemplateId);
        //看板模板名称
        obj.put("kanban_template_name", kanbanTemplateName);

        //存储  根据每个步骤的状态设置 整体状态  allMatch判断集合中是否所有元素都满足条件
        if (listMin.stream().allMatch(item -> !StringUtils.isEmpty(item.get("status")) && item.get("status") == "G")) {
            obj.put("overall_status", "G");
            obj.put("measure_name",currentStep.get("G"));
        } else if (listMin.stream().anyMatch(item ->  !StringUtils.isEmpty(item.get("status")) && item.get("status") == "Y")) {
            obj.put("overall_status", "Y");
            obj.put("measure_name",currentStep.get("Y"));
        } else if (listMin.stream().anyMatch(item -> !StringUtils.isEmpty(item.get("status")) &&  item.get("status") == "R")) {
            obj.put("overall_status", "R");
            obj.put("measure_name",currentStep.get("R"));
        }

        //****存储相应的其他讯息
        String content = sortAfterKanbanEntities.get(0).getDataContent();
        JSONObject object = JSONObject.parseObject(content);
        Object choiceMethod = object.get(QuestionResponseConst.QUESTION_RESULT);
        String repeatTimes;
        String liablePersonId;
        String liablePersonName;
        String solutionName;
        String solutionId;

        String defectNo;
        String defectName;
        String riskLevel;
        String urgency;
        String important;
        String proposerName;
        String proposerId;
        String questionDescription;
        String classificationName;
        String sourceName;

        if(choiceMethod instanceof JSONObject){
            JSONObject questionResult = object.getJSONObject("question_result");

            JSONObject identifyInfo = questionResult.getJSONObject("question_identify_info");

            repeatTimes = identifyInfo.getString("repeat_times");
            liablePersonId = identifyInfo.getString("liable_person_id");
            liablePersonName = identifyInfo.getString("liable_person_name");
            solutionName = identifyInfo.getString("solution_name");
            solutionId = identifyInfo.getString("solution_id");

            JSONObject detailInfo = questionResult.getJSONObject("question_detail_info");
            defectNo = detailInfo.getString("defect_no");
            defectName = detailInfo.getString("defect_name");

            JSONObject basicInfo = questionResult.getJSONObject("question_basic_info");
            riskLevel = basicInfo.getString("risk_level_name");
            urgency = basicInfo.getString("urgency");
            important = basicInfo.getString("important");
            proposerName = basicInfo.getString("question_proposer_name");
            proposerId = basicInfo.getString("question_proposer_id");
            questionDescription = basicInfo.getString("question_description");
            classificationName = basicInfo.getString("question_classification_name");
            sourceName = basicInfo.getString("question_source_name");

        }else{
            JSONArray questionResult = object.getJSONArray("question_result");

            JSONArray identifyInfo = questionResult.getJSONObject(0).getJSONArray("question_identify_info");

             repeatTimes = identifyInfo.getJSONObject(0).getString("repeat_times");
             liablePersonId = identifyInfo.getJSONObject(0).getString("liable_person_id");
             liablePersonName = identifyInfo.getJSONObject(0).getString("liable_person_name");
             solutionName = identifyInfo.getJSONObject(0).getString("solution_name");
             solutionId = identifyInfo.getJSONObject(0).getString("solution_id");

            JSONArray detailInfo = questionResult.getJSONObject(0).getJSONArray("question_detail_info");
             defectNo = detailInfo.getJSONObject(0).getString("defect_no");
             defectName = detailInfo.getJSONObject(0).getString("defect_name");

            JSONArray basicInfo = questionResult.getJSONObject(0).getJSONArray("question_basic_info");
             riskLevel = basicInfo.getJSONObject(0).getString("risk_level_name");
             urgency = basicInfo.getJSONObject(0).getString("urgency");
             important = basicInfo.getJSONObject(0).getString("important");
             proposerName = basicInfo.getJSONObject(0).getString("question_proposer_name");
             proposerId = basicInfo.getJSONObject(0).getString("question_proposer_id");
             questionDescription = basicInfo.getJSONObject(0).getString("question_description");
             classificationName = basicInfo.getJSONObject(0).getString("question_classification_name");
             sourceName = basicInfo.getJSONObject(0).getString("question_source_name");

        }

        obj.put("important","1".equals(important)?MultilingualismUtil.getLanguage("important"):MultilingualismUtil.getLanguage("not_important"));
        obj.put("risk_level", riskLevel);
        obj.put("urgency", "1".equals(urgency)? MultilingualismUtil.getLanguage("urgency"):MultilingualismUtil.getLanguage("not_urgency"));
        obj.put("repeat_times", repeatTimes);
        obj.put("liable_person_id", liablePersonId);
        obj.put("liable_person_name", liablePersonName);
        obj.put("solution_name", solutionName);
        obj.put("solution_no", solutionId);
        obj.put("defect_no", defectNo);
        obj.put("defect_name", defectName);
        obj.put("question_proposer_name", proposerName);
        obj.put("question_proposer_id", proposerId);
        obj.put("question_description", questionDescription);
        obj.put("classification_name", classificationName);
        obj.put("source_name", sourceName);
        obj.put("question_id",sortAfterKanbanEntities.get(0).getQuestionId());
        //为每一条记录增加taskCode和appCode字段信息
        obj.put("app_no", "FRC");
        obj.put("task_no",getTaskCode1(sortAfterKanbanEntities.get(0)));
        listMax.add(obj);
    }

    private String getTaskCode1(KanbanInfoEntity entity) {
        String processStep = entity.getQuestionProcessStep();
        String solveStep = entity.getQuestionSolveStep();
        if ("QS".equals(processStep) || !StringUtils.isEmpty(solveStep)){
            return TaskCodeConstants.TASK_CODE_MAP.get(solveStep);
        }else if(!StringUtils.isEmpty(processStep)){
            return TaskCodeConstants.TASK_CODE_MAP.get(processStep);
        }
        return "";
    }

    @Override
    public List<JSONObject> getKanbanInfoTest(){

        //1.查询所有问题数据
        List<KanbanInfoEntity> entities = kanbanInfoMapper.getKanbanInfo(TenantTokenUtil.getTenantSid());
        //2.根据问题单号进行分组
        Map<String, List<KanbanInfoEntity>> kanbanResult = entities.stream().collect(Collectors.groupingBy(KanbanInfoEntity::getQuestionNo));
        //3.获取问题单号List
        Set<String> questionNosKeys = kanbanResult.keySet();
        // 4.获取每条数据对应的看板显示栏位
        //List<KeyBoardDisplayEntity> displayEntities = keyBoardDisplayMapper.getAllKeyBoardDisplayInfo(TenantTokenUtil.getTenantSid(),"SE002");
        List<KeyBoardDisplayEntity> displayEntities = keyBoardDisplayMapper.getAllKeyBoardDisplayInfo(TenantTokenUtil.getTenantSid());
        // todo 封装每一条问题数据
        return null;
    }

    /**
     * 封装每一条问题数据
     * @param questionNosKeys 问题号集合
     * @param kanbanResult 每个问题号对应的各步骤数据
     * @param displayEntities 显示栏位列表
     * @return
     */
    private List<JSONObject> processQuestionDetail(Set<String> questionNosKeys,Map<String, List<KanbanInfoEntity>> kanbanResult,List<KeyBoardDisplayEntity> displayEntities){
        return questionNosKeys.stream().map(s ->{
            JSONObject question = new JSONObject();
            // 封装栏位信息
            question.put("measure_info", processDynamicData(s,kanbanResult,displayEntities));
            // 得到每个问题号详细数据
            List<KanbanInfoEntity> questionInfos = kanbanResult.get(s).stream()
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(KanbanInfoEntity::getQuestionSolveStep))), ArrayList::new));
            // todo
            question.put("overall_status","");
            question.put("happen_date", "");
            question.put("solution_no","");

            return question;
        }).collect(Collectors.toList());
    }

    /**
     * 封装动态列
     * @param key 问题号
     * @param kanbanResult 每个问题对应的值
     * @param displayEntities 看板栏位信息
     */
    private List<JSONObject> processDynamicData(String key,Map<String, List<KanbanInfoEntity>> kanbanResult,List<KeyBoardDisplayEntity> displayEntities){
        return displayEntities.stream().map(d->{
            JSONObject row = new JSONObject();
            // 得到每个问题号详细数据
            List<KanbanInfoEntity> questionInfos = kanbanResult.get(key).stream()
                    .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(KanbanInfoEntity::getQuestionSolveStep))), ArrayList::new));
            // 列名
            row.put("column",d.getFieldName());
            // 完成时间
            row.put("value","");
            // 状态
            row.put("status","");
            processValueAndDate(key,questionInfos,row);
            return row;
        }).collect(Collectors.toList());

    }

    private void processValueAndDate(String measureNo,List<KanbanInfoEntity> questionInfos,JSONObject row){
        List<KanbanInfoEntity> data = questionInfos.stream().filter(a -> a.getQuestionSolveStep().equals(measureNo))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(data)) {
            return ;
        }
        // 单字段排序，根据id排序
        List<KanbanInfoEntity> sortList = data.stream().sorted(Comparator.comparing(KanbanInfoEntity::getPrincipalStep)).collect(Collectors.toList());
        // 获取完成时间
        Date finishDate = sortList.get(0).getActualCompleteDate();
        row.put("value",finishDate);
        row.put("status",sortList.get(0));

    }

    private String checkStatus(KanbanInfoEntity entity){
        // 已完成
        if (!StringUtils.isEmpty(entity.getActualCompleteDate())) {
            return "G";
        }
        if (new Date().after(entity.getExceptCompleteDate())) {
            return "R";
        }
        return "Y";
    }





    @Override
    public List<JSONObject> getIssueManagementMatrixOverviewInfo(JSONArray dataContent) throws ParseException {
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        //获取议题矩阵管理的查询条件 由于只有一笔 取get(0)
        JSONObject jsonObject = dataContent.getJSONObject(0);
        String issueDate = jsonObject.getString("issue_year_month");
        String projectNo = jsonObject.getString("project_no");
        //根据月份获取天数 取得当月的时间范围
        String startTime = null;
        String endTime =  null;
        if(!StringUtils.isEmpty(issueDate)){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM").parse(issueDate));
            int dayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            startTime = issueDate+"-"+"01";
            endTime = issueDate+"-"+dayOfMonth;
        }
        List<JSONObject> obj = new ArrayList<>();
        //查询所有四象限符合条件的处理时间总数  时间范围内 以及 项目号 满足
        List<IssueManagementEntity> quadrantList = kanbanInfoMapper.queryHandleDateAndIssueCountByTimeAndDemand(tenantSid,startTime,endTime,projectNo);
        //组装第一象限的数据  重要(1)且紧急(1)
        List<IssueManagementEntity> firstList = quadrantList.stream().filter(item -> item.getImportant() == 1 && item.getUrgency() == 1).collect(Collectors.toList());
        int firstIsssueCount = firstList.size();
        int firstHandDate = getHandleDateHour(firstList);
        long firstUnsolvedCount = quadrantList.stream().filter(item -> item.getImportant() == 1 && item.getUrgency() == 1 && item.getProcessStatus() == 1 ).count();
        //组装第二象限的数据  重要(1)且不紧急(2)
        List<IssueManagementEntity> secondList = quadrantList.stream().filter(item -> item.getImportant() == 1 && item.getUrgency() == 2).collect(Collectors.toList());
        int secondIsssueCount = secondList.size();
        int secondHandDate = getHandleDateHour(secondList);
        long secondUnsolvedCount = quadrantList.stream().filter(item -> item.getImportant() == 1 && item.getUrgency() == 2 && item.getProcessStatus() == 1).count();
        //组装第三象限的数据  不重要(2)且紧急(1)
        List<IssueManagementEntity> thirdList = quadrantList.stream().filter(item -> item.getImportant() == 2 && item.getUrgency() == 1).collect(Collectors.toList());
        int thirdIsssueCount = thirdList.size();
        int thirdHandDate = getHandleDateHour(thirdList);
        long thirdUnsolvedCount = quadrantList.stream().filter(item -> item.getImportant() == 2 && item.getUrgency() == 1 && item.getProcessStatus() == 1).count();
        //组装第四象限的数据  重要(2)且不紧急(2)
        List<IssueManagementEntity> fourdList = quadrantList.stream().filter(item -> item.getImportant() == 2 && item.getUrgency() == 2).collect(Collectors.toList());
        int fourIsssueCount = fourdList.size();
        int fourHandDate = getHandleDateHour(fourdList);
        long fourUnsolvedCount = quadrantList.stream().filter(item -> item.getImportant() == 2 && item.getUrgency() == 2 && item.getProcessStatus() == 1).count();

        int sumHandDate = firstHandDate+secondHandDate+thirdHandDate+fourHandDate;
        long sumIssueCount = firstIsssueCount + secondIsssueCount + thirdIsssueCount + fourIsssueCount;
        JSONObject firstObj = assembleData(firstIsssueCount, firstHandDate, firstUnsolvedCount, sumHandDate,"重要且紧急",sumIssueCount);
        JSONObject secondObj = assembleData(secondIsssueCount, secondHandDate, secondUnsolvedCount, sumHandDate,"重要但不紧急",sumIssueCount);
        JSONObject thirdObj = assembleData(thirdIsssueCount, thirdHandDate, thirdUnsolvedCount, sumHandDate,"不重要但紧急",sumIssueCount);
        JSONObject fourObj = assembleData(fourIsssueCount, fourHandDate, fourUnsolvedCount, sumHandDate,"不重要不紧急",sumIssueCount);
        obj.add(firstObj);
        obj.add(secondObj);
        obj.add(thirdObj);
        obj.add(fourObj);
        return obj;
    }

    private JSONObject assembleData(int issueCount, int HandDate, long unsolvedCount, int sumHandDate,String issueQuardant,long sumIssueCount) {
        JSONObject obj = new JSONObject();
        obj.put("process_time",HandDate);
        obj.put("process_time_rate",String.format("%.0f", (float) HandDate / (float) sumHandDate * 100)+"%");
        obj.put("total_question_count",issueCount);
        obj.put("unsolved_question_count",unsolvedCount);
        obj.put("issue_quadrant",issueQuardant);
        obj.put("proportion",String.format("%.0f", (float) issueCount / (float) sumIssueCount * 100)+"%");
        return obj;
    }


    @Override
    public JSONObject getIssueManagementMatrixInfo(JSONArray dataContent) throws DWArgumentException, ParseException {
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        JSONObject jsonObject = dataContent.getJSONObject(0);
        //将传参映射到相应的实体类中
        IssueManagementDetailModel detailModel = JSON.parseObject(jsonObject.toJSONString(), IssueManagementDetailModel.class);
        //对必传参数进行校验
        validateFieldValue(detailModel);
        //根据月份获取天数 取得当月的时间范围
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new SimpleDateFormat("yyyy-MM").parse(detailModel.getIssueDate()));
        int dayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String startTime = detailModel.getIssueDate()+"-"+"01";
        String endTime = detailModel.getIssueDate()+"-"+dayOfMonth;
        List<IssueManagementDetailEntity> detailEntities = kanbanInfoMapper.queryIssueManageDetailInfoByCondition(tenantSid,detailModel.getProjectNo(),detailModel.getIssueStatus(),detailModel.getImportantFlag(),detailModel.getUrgencyFlag(),startTime,endTime);

        //组装数据
        JSONObject resultObject = new JSONObject();
        //上月处理时间占比
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        String yearLastMonth = calendar.get(Calendar.YEAR) +"-"+ (calendar.get(Calendar.MONTH)+1);
        int dayOfLastMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        String lastStartTime = yearLastMonth+"-"+"01";
        String lastEndTime = yearLastMonth+"-"+dayOfLastMonth;
        List<IssueManagementEntity> quadrantListOfLastMonth = kanbanInfoMapper.queryHandleDateAndIssueCountByTimeAndDemand(tenantSid,lastStartTime,lastEndTime,detailModel.getProjectNo());
        List<IssueManagementEntity> firstList = quadrantListOfLastMonth.stream().filter(item -> item.getImportant() == 1 && item.getUrgency() == 1).collect(Collectors.toList());
        List<IssueManagementEntity> secondList = quadrantListOfLastMonth.stream().filter(item -> item.getImportant() == 1 && item.getUrgency() == 2).collect(Collectors.toList());
        List<IssueManagementEntity> thirdList = quadrantListOfLastMonth.stream().filter(item -> item.getImportant() == 2 && item.getUrgency() == 1).collect(Collectors.toList());
        List<IssueManagementEntity> fourList = quadrantListOfLastMonth.stream().filter(item -> item.getImportant() == 2 && item.getUrgency() == 2).collect(Collectors.toList());
        int firstHandleHour = getHandleDateHour(firstList);
        int secondHandleHour = getHandleDateHour(secondList);
        int thirdHandleHour = getHandleDateHour(thirdList);
        int fourHandleHour = getHandleDateHour(fourList);
        //根据重要性和紧急度判断属于哪个象限 并存储上月处理时间占比
        Integer urgencyFlag = detailModel.getUrgencyFlag();
        Integer importantFlag = detailModel.getImportantFlag();
        String quardantNoInfo = "";
        if(importantFlag == 1 && urgencyFlag == 1){
            quardantNoInfo += "重要且紧急";
            if(firstHandleHour+secondHandleHour+thirdHandleHour+fourHandleHour != 0){
                resultObject.put("last_month_process_time_rate",String.format("%.0f", (float) firstHandleHour / (float) (firstHandleHour+secondHandleHour+thirdHandleHour+fourHandleHour) * 100)+"%");
            }else{
                resultObject.put("last_month_process_time_ratio",0+"%");
            }
        }else if(importantFlag == 1 && urgencyFlag == 2){
            quardantNoInfo += "重要但不紧急";
            if(firstHandleHour+secondHandleHour+thirdHandleHour+fourHandleHour != 0){
                resultObject.put("last_month_process_time_rate",String.format("%.0f", (float) firstHandleHour / (float) (secondHandleHour+secondHandleHour+thirdHandleHour+fourHandleHour) * 100)+"%");
            }else{
                resultObject.put("last_month_process_time_ratio",0+"%");
            }
        }else if(importantFlag == 2 && urgencyFlag == 1){
            quardantNoInfo += "不重要但紧急";
            if(firstHandleHour+secondHandleHour+thirdHandleHour+fourHandleHour != 0){
                resultObject.put("last_month_process_time_rate",String.format("%.0f", (float) firstHandleHour / (float) (thirdHandleHour+secondHandleHour+thirdHandleHour+fourHandleHour) * 100)+"%");
            }else{
                resultObject.put("last_month_process_time_ratio",0+"%");
            }
        }else {
            quardantNoInfo += "不重要不紧急";
            if(firstHandleHour+secondHandleHour+thirdHandleHour+fourHandleHour != 0){
                resultObject.put("last_month_process_time_rate",String.format("%.0f", (float) firstHandleHour / (float) (fourHandleHour+secondHandleHour+thirdHandleHour+fourHandleHour) * 100)+"%");
            }else{
                resultObject.put("last_month_process_time_ratio",0+"%");
            }
        }

        resultObject.put("process_time_rate",detailModel.getProcessTimeRatio());
        resultObject.put("total_question_count",detailModel.getAllQuestionQty());
        resultObject.put("unsolved_question_count",detailModel.getUnsolveQuestionQty());
        //预计处理时间  期望时间完成时间- 实际开始时间
        long processTime = detailEntities.stream().mapToLong(item -> item.getExpectFinishTime().getTime() - item.getActualEndDate().getTime()).sum();
        calendar.setTime(new Date(processTime));
        int processTimeHours = calendar.get(Calendar.HOUR);
        resultObject.put("estimate_process_time",processTimeHours);
        List<JSONObject> objectList = new ArrayList<>();
        for (IssueManagementDetailEntity detailEntity : detailEntities) {
            JSONObject object = new JSONObject();
            object.put("project_no",detailEntity.getProjectNo());
            object.put("question_source_name",detailEntity.getSourceName());
            object.put("question_classification_name",detailEntity.getClassificationName());
            object.put("question_no",detailEntity.getQuestionNo());
            object.put("issue_quadrant",quardantNoInfo);
            object.put("important",detailEntity.getImportant());
            long estimatedProcessingTime = detailEntity.getExpectFinishTime().getTime() - detailEntity.getCreateDate().getTime();
            calendar.setTime(new Date(estimatedProcessingTime));
            int date = calendar.get(Calendar.DATE);
            object.put("estimate_process_time",calendar.get(Calendar.HOUR));
            long actualProcessingTime = detailEntity.getActualEndDate().getTime() - detailEntity.getCreateDate().getTime();
            calendar.setTime(new Date(actualProcessingTime));
            object.put("actual_process_time",calendar.get(Calendar.HOUR));

            Integer taskFinishTime = detailEntity.getTaskFinishTime();
            object.put("plan_overdue_days_urgency",date-taskFinishTime);
            objectList.add(object);

        }
        resultObject.put("management_matrix_info",objectList);
        return resultObject;
    }

    private int getHandleDateHour(List<IssueManagementEntity> list) {
        int handHourDate = 0 ;
        for (IssueManagementEntity managementEntity : list) {
            Date actualEndDate = managementEntity.getActualEndDate();
            Date createDate = managementEntity.getCreateDate();
            Long time =  actualEndDate.getTime()-createDate.getTime();
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date(time));
            int hour = cal.get(Calendar.HOUR);
            handHourDate += hour;
        }
        return handHourDate;
    }

    /**
     * 对必传参数进行校验
     *
     * @param detailModel
     * @throws DWArgumentException
     */
    private void validateFieldValue(IssueManagementDetailModel detailModel) throws DWArgumentException {
        Integer importantFlag = detailModel.getImportantFlag();
        Integer urgencyFlag = detailModel.getUrgencyFlag();
        if(importantFlag == null || (importantFlag != 1 && importantFlag != 2)){
            throw new DWArgumentException("importantFlag", MultilingualismUtil.getLanguage("parameterError"));
        }
        if(urgencyFlag == null || (urgencyFlag != 1 && urgencyFlag != 2)){
            throw new DWArgumentException("urgencyFlag", MultilingualismUtil.getLanguage("parameterError"));
        }
        Integer unsolveQuestionQty = detailModel.getUnsolveQuestionQty();
        if(unsolveQuestionQty == null){
            throw new DWArgumentException("unsolveQuestionQty",MultilingualismUtil.getLanguage("notExist"));
        }
        Integer allQuestionQty = detailModel.getAllQuestionQty();
        if(allQuestionQty == null){
            throw new DWArgumentException("allQuestionQty",MultilingualismUtil.getLanguage("notExist"));
        }
        String processTimeRatio = detailModel.getProcessTimeRatio();
        if(StringUtils.isEmpty(processTimeRatio)){
            throw new DWArgumentException("processTimeRatio",MultilingualismUtil.getLanguage("notExist"));
        }
    }

    /**
     * 判断登录用户是否有该看板模板的查看权限
     *
     * @param tenantSid  租户id
     * @param empId     用户id
     * @param kanbanInfoModel 看板模板model
     * @return boolean
     */
    private boolean determineUserPermissions(Long tenantSid, String empId, KanbanInfoModel kanbanInfoModel) {
        //只有看板权限维护作业 对应模板维护的指定查看人才可以看到相应的模板
        String templateId = kanbanInfoModel.getKanbanTemplateId();
        String specifyViews = keyBoardAuthorityMapper.querySpecifyViewerByTemplateId(templateId,tenantSid);
        List<String> specifyViewInfo = new ArrayList<>();
        String[] specifyViewers = specifyViews.split(",");
        for (String view : specifyViewers) {
            String[] info = view.split("_");
            specifyViewInfo.add(info[0]);
        }
        if(specifyViewInfo.isEmpty() || !specifyViewInfo.contains(empId)){
            return true;
        }
        return false;
    }



}
