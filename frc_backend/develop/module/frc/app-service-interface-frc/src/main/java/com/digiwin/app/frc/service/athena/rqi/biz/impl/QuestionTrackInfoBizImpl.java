package com.digiwin.app.frc.service.athena.rqi.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionRecordEntity;
import com.digiwin.app.frc.service.athena.qdh.mapper.RecordMapper;
import com.digiwin.app.frc.service.athena.rqi.biz.QuestionTrackInfoBiz;
import com.digiwin.app.frc.service.athena.rqi.constants.TaskCodeConstants;
import com.digiwin.app.frc.service.athena.rqi.domain.entity.QuestionTrackResponsibleEntity;
import com.digiwin.app.frc.service.athena.rqi.domain.model.QuestionTrackProcessorModel;
import com.digiwin.app.frc.service.athena.rqi.domain.model.QuestionTrackProposerModel;
import com.digiwin.app.frc.service.athena.rqi.domain.model.QuestionTrackResponsibleModel;
import com.digiwin.app.frc.service.athena.rqi.domain.vo.QuestionTrackProcessorVo;
import com.digiwin.app.frc.service.athena.rqi.domain.vo.QuestionTrackProposerVo;
import com.digiwin.app.frc.service.athena.rqi.domain.vo.QuestionTrackResponsibleVo;
import com.digiwin.app.frc.service.athena.rqi.mapper.ActionTraceInfoMapper;

import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.rqi.EocUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


import static java.util.stream.Collectors.toList;

/**
 * @Author: xieps
 * @Date: 2021/12/31 14:11
 * @Version 1.0
 * @Description 获取问题追踪信息Biz-impl
 */
@Service
public class QuestionTrackInfoBizImpl implements QuestionTrackInfoBiz {


    @Autowired
    private ActionTraceInfoMapper actionTraceInfoMapper;

    @Autowired
    private RecordMapper recordMapper;

    @Override
    public List<JSONObject> getQuestionTrackProposerInfo(JSONArray dataContent) throws Exception {
        //获取提出者问题追踪数据信息   由于只有一笔取get(0)
        JSONObject jsonObject = dataContent.getJSONObject(0);
        //通过上下文获取提出者id
        String empId = EocUtils.getEmpId(TenantTokenUtil.getUserId());
        //将参数映射到实体类中
        QuestionTrackProposerModel proposerModel = JSON.parseObject(jsonObject.toJSONString(), QuestionTrackProposerModel.class);
        //对查询条件进行处理
        handleData(proposerModel);
        //进行查询操作
        //根据时间范围以及问题反馈确认单的标识QFL、问题提出者  查询该范围内的问题单号、和问题发起时间 并根据问题单号进行分组
        List<Map<String, Object>> mapList = actionTraceInfoMapper.queryQuestionNoByProposerId(proposerModel.getFeedbackStartDate(), proposerModel.getFeedbackEndDate(),empId,proposerModel.getQuestionNo(),TenantTokenUtil.getTenantSid());
        //取出所有符合上述条件的问题单号
        List<String> questionNos = mapList.stream().map(e->(String)e.get("question_no")).collect(toList());

        List<QuestionTrackResponsibleEntity> entities;
        if (!StringUtils.isEmpty(questionNos) && !questionNos.isEmpty()) {
            entities = getQuestionEntities(questionNos, proposerModel.getQuestionStatus(), proposerModel.getQuestionDescription(), proposerModel.getQuestionProcessStage(),null, TenantTokenUtil.getTenantSid());
        }else {
            return new ArrayList<>();
        }
        //迭代六：反馈者和当责者将8D解决方案中的已完成的短期结案验收一并加入
        addShotTerm(TenantTokenUtil.getTenantSid(), entities, proposerModel.getQuestionStatus());
        //迭代六：已在SQL中加入了问题状态筛选
        //根据单据状态进行筛选
        //根据来源编号 和 分类编号进行筛选
        entities = filterBySourceNoAndClassificationNo(proposerModel, entities);
        //根据SN号进行筛选
        String sn = proposerModel.getSn();
        if (!StringUtils.isEmpty(sn)){
            entities = entities.stream().filter(e->snFilter(e,sn)).collect(toList());
        }
        if (entities.isEmpty()){
            return new ArrayList<>();
        }
        //把实体类转成前端回传要求的格式
        return convertData(entities, mapList);
    }



    @Override
    public List<JSONObject> getQuestionTrackProcessorInfo(JSONArray dataContent) throws Exception {
        //获取处理者问题追踪数据  由于只有一笔取get(0)
        JSONObject jsonObject = dataContent.getJSONObject(0);
        //通过上下文获取处理者ID
        String empId = EocUtils.getEmpId(TenantTokenUtil.getUserId());
        //将参数映射到实体类中
        QuestionTrackProcessorModel processorModel = JSON.parseObject(jsonObject.toJSONString(), QuestionTrackProcessorModel.class);
        processorModel.setProcessorId(empId);
        //对查询条件进行处理
        handleData(processorModel);
        //首先根据处理人Id 查找到对应的问题单号  并进行分组
        //通过问题单号 再查询 根据时间范围 和  QFL  查询出 问题的发起时间 create_date 和 问题单号 / 反馈者iD、名称 以及问题反馈者（筛选了一次 ）
        //如果有反馈者信息  获取反馈者 所能看到的问题单号
        List<String> feedbackIds = getProposerQuestionNos(jsonObject);
        List<Map<String, Object>> processorMapList = actionTraceInfoMapper.queryQuestionNoByProcessorId(processorModel.getProcessorId(),processorModel.getFeedbackStartDate(), processorModel.getFeedbackEndDate(),feedbackIds,processorModel.getQuestionNo(),TenantTokenUtil.getTenantSid());
        //取出所有符合上述条件的问题单号
        List<String> processorQuestionNos = new ArrayList<>();
        for (Map<String, Object> map : processorMapList) {
            processorQuestionNos.add((String) map.get("question_no"));
        }
        //根据问题单号查询 待处理的数据 （代表该条数据）
        List<QuestionTrackResponsibleEntity> entities;
        if (!StringUtils.isEmpty(processorQuestionNos) && !processorQuestionNos.isEmpty()) {
            entities = getQuestionEntities(processorQuestionNos, processorModel.getQuestionStatus(),processorModel.getQuestionDescription(),processorModel.getQuestionProcessStage(),empId,TenantTokenUtil.getTenantSid());
        }else {
            return new ArrayList<>();
        }
        //根据单据状态进行筛选
        //根据SN号进行筛选
        String sn = processorModel.getSn();
        if (!StringUtils.isEmpty(sn)){
            entities = entities.stream().filter(e->snFilter(e,sn)).collect(toList());
        }
        if (entities.isEmpty()){
            return new ArrayList<>();
        }
        //把实体类转成前端回传要求的格式
        return convertData2(entities, processorMapList);
    }

    @Override
    public List<JSONObject> getQuestionTrackResponsibleInfo(JSONArray dataContent) throws Exception {
        //获取项目当责者问题追踪信息  由于只有一笔取get(0)
        JSONObject jsonObject = dataContent.getJSONObject(0);
        //通过上下文获取当前登录者id
        String empId = EocUtils.getEmpId(TenantTokenUtil.getUserId());
        QuestionTrackResponsibleModel responsibleModel = JSON.parseObject(jsonObject.toJSONString(), QuestionTrackResponsibleModel.class);
        responsibleModel.setResponsibleId(empId);
        //对查询条件进行处理
        responsibleModel = handleData(responsibleModel);
        //根据当责者id 查询问题单号并进行分组  (QF问题确认责任人即为当责者 进行判断当前登录人是否属于当责者)
        List<Map<String, Object>> responsibleMapList = actionTraceInfoMapper.queryQuestionNoByTime(responsibleModel.getFeedbackStartDate(), responsibleModel.getFeedbackEndDate(), empId,TenantTokenUtil.getTenantSid(),responsibleModel.getQuestionNo());
        if(responsibleMapList.isEmpty()){
            return new ArrayList<>();
        }
        //取出所有符合上述条件的问题单号
        List<String> responsibleQuestionNos = responsibleMapList.stream().map(e->(String)e.get("question_no")).collect(toList());
        //如果有反馈者信息  获取反馈者  所能看到的问题单号
        List<String> proposerNos = getProposerQuestionNos2(jsonObject, responsibleMapList);
        //对当责者所能看到的单号对应   和   反馈者能看到的单号  做一个交集操作得到两者能共同看到的单号
        if (!proposerNos.isEmpty()) {
            responsibleQuestionNos = responsibleQuestionNos.stream().filter(proposerNos::contains).collect(Collectors.toList());
        }
        List<Map<String, Object>> mapListInfo = getMapInfo(TenantTokenUtil.getTenantSid(), responsibleModel, responsibleQuestionNos);
        if(mapListInfo.isEmpty()){
            return new ArrayList<>();
        }


        //取出符合条件单号的数据主键
        List<String> oids = mapListInfo.stream().map(item -> (String)item.get("oid")).collect(toList());
        //获取处理者所能看到的问题
        List<String> processorOidList = getProcessorOids(jsonObject, mapListInfo);
        //根据当责者和反馈者所能看到的问题单号为基础    对处理者所能看到的问题单  做一个交集操作
        if (!processorOidList.isEmpty()) {
            oids = oids.stream().filter(processorOidList::contains).collect(Collectors.toList());
        }
        //带着符合时间范围的问题单号数据 进行进一步细致查询
        List<QuestionTrackResponsibleEntity> entities = actionTraceInfoMapper.queryQuestionTrackResponsibleInfo(oids, responsibleModel,TenantTokenUtil.getTenantSid());
        //迭代六：反馈者和当责者将8D解决方案中的已完成的短期结案验收一并加入
        addShotTerm(TenantTokenUtil.getTenantSid(), entities, responsibleModel.getQuestionStatus());
        //根据SN号进行筛选
        String sn = responsibleModel.getSn();
        if (!StringUtils.isEmpty(sn)){
            entities = entities.stream().filter(e->snFilter(e,sn)).collect(toList());
        }
        if (entities.isEmpty()){
            return new ArrayList<>();
        }
        //把实体类转成前端回传要求的格式
        return convertResponsibleData(entities, responsibleMapList);
    }

    /**
     * 实体类：找出短期结案数据并添加入数组
     * @param tenantSid
     * @param entities
     * @param questionStatus
     */
    private void addShotTerm(Long tenantSid, List<QuestionTrackResponsibleEntity> entities, String questionStatus) {
        if(!"1".equals(questionStatus)){
            Set<String> questionNos2 = new HashSet<>();
            for (QuestionTrackResponsibleEntity entity : entities){
                String questionSolveStep = entity.getQuestionSolveStep();
                if(questionSolveStep!=null && (questionSolveStep.startsWith("SE001") || questionSolveStep.startsWith("SE003"))){
                    questionNos2.add(entity.getQuestionNo());
                }
            }
            for (QuestionTrackResponsibleEntity entity : entities){
                String questionSolveStep = entity.getQuestionSolveStep();
                if(questionSolveStep != null && (questionSolveStep.equals("SE001014") || questionSolveStep.equals("SE001013") || questionSolveStep.equals("SE003005") || questionSolveStep.equals("SE003009"))){
                    questionNos2.remove(entity.getQuestionNo());
                }
            }
            if(!questionNos2.isEmpty()){
                entities.addAll(actionTraceInfoMapper.queryShotTermByQuestionNo(questionNos2,tenantSid));
            }
        }
    }

    private List<Map<String, Object>> getMapInfo(Long tenantSid, QuestionTrackResponsibleModel responsibleModel, List<String> responsibleQuestionNos) {
        List<Map<String,Object>> mapListProcessing = new ArrayList<>();
        List<Map<String,Object>> mapListCompleted = new ArrayList<>();
        if (!StringUtils.isEmpty(responsibleQuestionNos) && !responsibleQuestionNos.isEmpty()) {
            //根据问题单号查询具体数据
            List<QuestionRecordEntity> records = recordMapper.getQuestionRecordByNos(tenantSid,responsibleQuestionNos);
            //根据问题记录的状态进行分组
            //已完成的，根据问题单号查询 该单号对应的最后一笔数据的主键 （代表该条数据）
            if(!"1".equals(responsibleModel.getQuestionStatus())){
                List<String>completeNos = records.stream().filter(e->2 == e.getCurrentQuestionProcessStatus()).map(QuestionRecordEntity::getQuestionNo).collect(toList());
                if(!StringUtils.isEmpty(completeNos) && !completeNos.isEmpty()){
                    mapListCompleted = actionTraceInfoMapper.queryOidAndLiableInfoByNos(completeNos,tenantSid);
                }
            }
            //进行中的，根据问题单号查询 该单号对应的所有待处理数据的主键
            if(!"2".equals(responsibleModel.getQuestionStatus())){
                List<String>underwayNos = records.stream().filter(e->2 !=e.getCurrentQuestionProcessStatus()).map(QuestionRecordEntity::getQuestionNo).collect(toList());
                if(!StringUtils.isEmpty(underwayNos) && !underwayNos.isEmpty()){
                    mapListProcessing = actionTraceInfoMapper.queryOidAndLiableInfoByNos2(underwayNos,tenantSid);
                }
            }
            //数据合并
            mapListProcessing.addAll(mapListCompleted);
        }
        return mapListProcessing;
    }


    private boolean snFilter(QuestionTrackResponsibleEntity e, String sn){
        JSONObject jsonObject = JSON.parseObject(e.getDataContent());
        JSONObject detailInfo;
        if("QS".equals(e.getQuestionProcessStep()) && !StringUtils.isEmpty(e.getQuestionSolveStep()) && "SE003".equals(e.getQuestionSolveStep().substring(0,5))){
            JSONObject result =  jsonObject.getJSONObject("question_result");
            detailInfo = result.getJSONObject("question_detail_info");
        }else{
            JSONArray result =  jsonObject.getJSONArray("question_result");
            detailInfo = result.getJSONObject(0).getJSONArray("question_detail_info").getJSONObject(0);
        }
        String entitySn = detailInfo.getString("sn");
        return !StringUtils.isEmpty(entitySn) && entitySn.contains(sn);
    }

    private List<QuestionTrackResponsibleEntity> getQuestionEntities(List<String> questionNos, String questionStatus, String questionDescription, String questionProcessStage,String liabliePersonId,Long tenantSid){
        List<QuestionTrackResponsibleEntity> entities1 = new ArrayList<>();
        List<QuestionTrackResponsibleEntity> entities2 = new ArrayList<>();
        //根据问题单号查询具体数据
        List<QuestionRecordEntity> records = recordMapper.getQuestionRecordByNos(tenantSid,questionNos);
        //根据问题记录的状态进行分组
        //已完成的，根据问题单号查询 该单号对应的最后一笔数据的主键 （代表该条数据）
        if(!"1".equals(questionStatus)){
            List<String> completeNos = records.stream().filter(e->2 == e.getCurrentQuestionProcessStatus()).map(QuestionRecordEntity::getQuestionNo).collect(toList());
            if(!StringUtils.isEmpty(completeNos) && !completeNos.isEmpty()){
                entities1 = actionTraceInfoMapper.queryCompleteOidByNos(completeNos,questionDescription,questionProcessStage,liabliePersonId,tenantSid);
            }
        }
        //进行中的，根据问题单号查询 该单号对应的所有待处理数据的主键
        if(!"2".equals(questionStatus)){
            List<String> underwayNos = records.stream().filter(e->1 ==e.getCurrentQuestionProcessStatus()||0==e.getCurrentQuestionProcessStatus()).map(QuestionRecordEntity::getQuestionNo).collect(toList());
            if(!StringUtils.isEmpty(underwayNos) && !underwayNos.isEmpty()){
                entities2 = actionTraceInfoMapper.queryUnderwayOidByNos(underwayNos,questionDescription,questionProcessStage,liabliePersonId, tenantSid);
            }
        }
        //数据合并
        entities2.addAll(entities1);
        return entities2;
    }


    private List<String> getProcessorOids(JSONObject jsonObject, List<Map<String, Object>> mapList) {
        // 如果传值有处理者对应的信息 对应最后一笔数据中 处理者id
        //获取处理者所能看到的问题单号     获取处理者信息  处理者id
        List<String> processPersonIds = new ArrayList<>();
        JSONArray processPersonInfo = jsonObject.getJSONArray("process_person_info");
        for(Iterator<Object> iterator = processPersonInfo.iterator();iterator.hasNext();){
            JSONObject item = (JSONObject) iterator.next();
            processPersonIds.add(item.getString("process_person_id"));
        }
        List<String> processorOids = new ArrayList<>();
        if (!StringUtils.isEmpty(processPersonIds) && !processPersonIds.isEmpty()) {
            for (Map<String, Object> objectMap : mapList) {
                String liablePersonId = (String) objectMap.get("liable_person_id");
                for (String processPersonId : processPersonIds) {
                    if(liablePersonId.equals(processPersonId)){
                        String processorOid = (String) objectMap.get("oid");
                        processorOids.add(processorOid);
                    }
                }
            }
        }
        return processorOids;
    }

    private List<String>  getProposerQuestionNos(JSONObject jsonObject) {
        JSONArray feedbackPersonInfo = jsonObject.getJSONArray("feedback_person_info");
        List<String> feedbackPersonIds = new ArrayList<>();
        if (!StringUtils.isEmpty(feedbackPersonInfo) && !feedbackPersonInfo.isEmpty()) {
            for (int i = 0; i < feedbackPersonInfo.size(); i++) {
                JSONObject jsonObjectNew = feedbackPersonInfo.getJSONObject(i);
                String feedbackPersonId = jsonObjectNew.getString("feedback_person_id");
                feedbackPersonIds.add(feedbackPersonId);
            }
        }
        return feedbackPersonIds;
    }



    private List<String> getProposerQuestionNos2(JSONObject jsonObject, List<Map<String, Object>> processorMapList) {
        JSONArray feedbackPersonInfo = jsonObject.getJSONArray("feedback_person_info");
        List<String> feedbackPersonIds = new ArrayList<>();
        for(Iterator<Object> iterator = feedbackPersonInfo.iterator();iterator.hasNext();){
            JSONObject item = (JSONObject) iterator.next();
            feedbackPersonIds.add(item.getString("feedback_person_id"));
        }
        //标识区分 反馈者信息是否是为空
        List<String> proposerNos = new ArrayList<>();
        if (!StringUtils.isEmpty(feedbackPersonIds) && !feedbackPersonIds.isEmpty()) {
            for (String feedbackPersonId : feedbackPersonIds) {
                for (Map<String, Object> map1 : processorMapList) {
                    String createId = (String) map1.get("create_id");
                    if(feedbackPersonId.equals(createId)){
                        proposerNos.add((String) map1.get("question_no"));
                    }
                }
            }
        }
        return proposerNos;
    }



    /**
     * 把实体类转成前端回传要求的格式
     *
     * @param entities           实体类
     * @param responsibleMapList
     * @return List<JSONObject>
     */
    private List<JSONObject> convertResponsibleData(List<QuestionTrackResponsibleEntity> entities, List<Map<String, Object>> responsibleMapList) throws JsonProcessingException {
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (QuestionTrackResponsibleEntity entity : entities) {
            //获取问题单号的发起时间
            String questionNo = entity.getQuestionNo();
            for (Map<String, Object> map : responsibleMapList) {
                String questionNoBefore = (String) map.get("question_no");
                if (questionNo.equals(questionNoBefore)) {
                    Date createData = (Date) map.get("create_date");
                    String feedbackName = (String) map.get("create_name");
                    String createId = (String) map.get("create_id");
                    entity.setQuestionInitiateDate(createData);
                    entity.setProposerPersonName(feedbackName);
                    entity.setProposerPersonId(createId);
                }
            }
            QuestionTrackResponsibleVo vo = new QuestionTrackResponsibleVo();
            BeanUtils.copyProperties(entity, vo);
            //从json中取出问题来源 问题分类名称 赋值给相应的实体类
            if (entity.getDataContent() != null) {
                String dataContent = entity.getDataContent();
                JSONObject object = JSON.parseObject(dataContent);
                String questionClassificationName;
                String questionSourceName;
                Integer important;
                Integer urgency ;
                String questionSolveStep = entity.getQuestionSolveStep();
                if(!StringUtils.isEmpty(questionSolveStep) && "SE003".equals(questionSolveStep.substring(0,5))){
                    JSONObject questionResult = object.getJSONObject("question_result");
                    JSONObject questionBasicInfo = questionResult.getJSONObject("question_basic_info");
                    questionClassificationName = questionBasicInfo.getString("question_classification_name");
                    questionSourceName = questionBasicInfo.getString("question_source_name");
                    important = questionBasicInfo.getInteger("important");
                    urgency = questionBasicInfo.getInteger("urgency");
                }else{
                    JSONArray questionResult = object.getJSONArray("question_result");
                    JSONArray questionBasicInfo = questionResult.getJSONObject(0).getJSONArray("question_basic_info");
                    questionClassificationName = questionBasicInfo.getJSONObject(0).getString("question_classification_name");
                    questionSourceName = questionBasicInfo.getJSONObject(0).getString("question_source_name");
                    important = questionBasicInfo.getJSONObject(0).getInteger("important");
                    urgency = questionBasicInfo.getJSONObject(0).getInteger("urgency");
                }
                vo.setImportant(important);
                vo.setUrgency(urgency);
                vo.setClassificationName(questionClassificationName);
                vo.setSourceName(questionSourceName);
            }

            //问题处理状态及处理状态名称赋值  根据question_process_status question_process_result return_flag_id
            if (2 == entity.getQuestionProcessStatus() && 1 == entity.getQuestionProcessResult() && entity.getReturnFlagId() == null) {
                vo.setQuestionProcessStatus(0);
                vo.setQuestionProcessStatusName("处理中");
            }
            if (2 == entity.getQuestionProcessStatus() && 1 == entity.getQuestionProcessResult() && entity.getReturnFlagId() != null) {
                vo.setQuestionProcessStatus(1);
                vo.setQuestionProcessStatusName("退回处理");
            }
            if (5 == entity.getQuestionProcessStatus() && 3 == entity.getQuestionProcessResult()) {
                vo.setQuestionProcessStatus(2);
                vo.setQuestionProcessStatusName("终止结案");
            }
            if (8 == entity.getQuestionProcessStatus() && 2 == entity.getQuestionProcessResult()) {
                vo.setQuestionProcessStatus(3);
                vo.setQuestionProcessStatusName("正常结案");
            }
            //处理 问题处理阶段字段值信息  并处理是否逾期提示
            if ("QS".equals(entity.getQuestionProcessStep()) && entity.getQuestionSolveStep() != null) {
                vo.setQuestionProcessStage(entity.getQuestionProcessStep());
                vo.setQuestionProcessStep(entity.getQuestionSolveStep());
                //判断是否逾期  Y 逾期  N 未逾期
                String isOverDue = "N";
                if(!StringUtils.isEmpty(entity.getActualCompleteDate()) && !StringUtils.isEmpty(entity.getExpectCompleteTime())) {
                    isOverDue = entity.getActualCompleteDate().getTime() - entity.getExpectCompleteTime().getTime() > 0 ? "Y" : "N";
                }else if(!StringUtils.isEmpty(entity.getExpectCompleteTime())){
                    isOverDue = System.currentTimeMillis() - entity.getExpectCompleteTime().getTime() > 0 ? "Y" : "N";
                }
                vo.setIsOverDue(isOverDue);
                //迭代六：特殊的，短期结案验收的已完成阶段，处理状态为4-短期结案
                if((vo.getQuestionProcessStage().equals("SE001014")|| vo.getQuestionProcessStage().equals("SE003005") ) && (4 == entity.getQuestionProcessStatus() && 2 == entity.getQuestionProcessResult())) {
                    vo.setQuestionProcessStatus(4);
                    vo.setQuestionProcessStatusName("短期结案");
                }
            } else {
                vo.setQuestionProcessStage(entity.getQuestionProcessStep());
                vo.setQuestionProcessStep(null);
                //主项目卡 当前系统时间 - （开始时间+默认1）
                String isOverDue = (System.currentTimeMillis()-(entity.getStartTime().getTime()+ 24 * 3600 * 1000))>0 ? "Y":"N";
                vo.setIsOverDue(isOverDue);
            }
            //时间装换   问题发起时间   问题接收时间/问题结案时间
            String initiateDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entity.getQuestionInitiateDate());
            vo.setQuestionInitiateDate(initiateDate);
            String receiveDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entity.getQuestionReceiveDate());
            vo.setQuestionReceiveDate(receiveDate);

            //反馈者名称赋值
            vo.setProposerPersonName(entity.getProposerPersonName());
            vo.setProposerPersonId(entity.getProposerPersonId());

            //为每一条记录增加taskCode和appCode字段信息
            vo.setAppCode("FRC");
            vo.setTaskCode(getTaskCode(entity));

            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObjectList.add(jsonObject);
        }
        return jsonObjectList;
    }

    private String getTaskCode(QuestionTrackResponsibleEntity entity) {

        String processStep = entity.getQuestionProcessStep();
        String solveStep = entity.getQuestionSolveStep();
        if ("QS".equals(processStep) || !StringUtils.isEmpty(solveStep)){
            return TaskCodeConstants.TASK_CODE_MAP.get(solveStep);
        }else if(!StringUtils.isEmpty(processStep)){
            return TaskCodeConstants.TASK_CODE_MAP.get(processStep);
        }
        return "";
    }


    /**
     * 根据来源编号和分类编号进行过滤筛选
     *
     * @param proposerModel 提出者model
     * @param entities      提出者信息实体类
     * @return List<QuestionTrackProposerEntity>
     */
    private List<QuestionTrackResponsibleEntity> filterBySourceNoAndClassificationNo(QuestionTrackProposerModel proposerModel, List<QuestionTrackResponsibleEntity> entities) {
        List<QuestionTrackResponsibleEntity> newEntities = new ArrayList<>();
        for (QuestionTrackResponsibleEntity entity : entities) {
            if (entity.getDataContent() != null) {
                String questionSolveStep = entity.getQuestionSolveStep();
                String questionClassificationNo;
                String questionSourceNo;
                JSONObject questionBasicInfo;
                if(!StringUtils.isEmpty(questionSolveStep) && "SE003".equals(questionSolveStep.substring(0,5))){
                    JSONObject jsonObject = JSON.parseObject(entity.getDataContent());
                    JSONObject questionResult = jsonObject.getJSONObject("question_result");
                    questionBasicInfo = questionResult.getJSONObject("question_basic_info");
                }else{
                    JSONObject jsonObject = JSON.parseObject(entity.getDataContent());
                    JSONObject questionResult = jsonObject.getJSONArray("question_result").getJSONObject(0);
                    questionBasicInfo = questionResult.getJSONArray("question_basic_info").getJSONObject(0);
                }
                questionClassificationNo = questionBasicInfo.getString("question_classification_no");
                questionSourceNo = questionBasicInfo.getString("question_source_no");
                if(!StringUtils.isEmpty(proposerModel.getSourceNo()) && !proposerModel.getSourceNo().equals(questionSourceNo)){
                    continue;
                }
                if(!StringUtils.isEmpty(proposerModel.getClassificationNo()) && !proposerModel.getClassificationNo().equals(questionClassificationNo) ){
                    continue;
                }
                newEntities.add(entity);
            }
        }
        return newEntities;
    }


    /**
     * 将实体类集合转成前端要求的格式集合  提出者信息转换
     *
     * @param entities 实体类集合
     * @return List<JSONObject>
     */
    private List<JSONObject> convertData(List<QuestionTrackResponsibleEntity> entities, List<Map<String, Object>> mapList) throws JsonProcessingException{
        List<JSONObject> jsonObjectList = new ArrayList<>();
        Map<String,Date> createDateMap = new HashMap<>();
        for(Map<String, Object> map : mapList){
            createDateMap.put((String) map.get("question_no"),(Date)map.get("create_date"));
        }
        for (QuestionTrackResponsibleEntity entity : entities) {
            //获取问题单号的发起时间
            String questionNo = entity.getQuestionNo();
            entity.setQuestionInitiateDate(createDateMap.get(questionNo));
            QuestionTrackProposerVo vo = new QuestionTrackProposerVo();
            BeanUtils.copyProperties(entity, vo);
            //从json中取出问题来源 问题分类名称 赋值给相应的实体类
            if (entity.getDataContent() != null) {
                String dataContent = entity.getDataContent();
                JSONObject object = JSON.parseObject(dataContent);
                String questionClassificationName;
                String questionSourceName;
                Integer important;
                Integer urgency ;
                String questionSolveStep = entity.getQuestionSolveStep();
                if(!StringUtils.isEmpty(questionSolveStep) && "SE003".equals(questionSolveStep.substring(0,5))){
                    JSONObject questionResult = object.getJSONObject("question_result");
                    JSONObject questionBasicInfo = questionResult.getJSONObject("question_basic_info");
                    questionClassificationName = questionBasicInfo.getString("question_classification_name");
                    questionSourceName = questionBasicInfo.getString("question_source_name");
                    important = questionBasicInfo.getInteger("important");
                    urgency = questionBasicInfo.getInteger("urgency");
                }else{
                    JSONArray questionResult = object.getJSONArray("question_result");
                    JSONArray questionBasicInfo = questionResult.getJSONObject(0).getJSONArray("question_basic_info");
                    questionClassificationName = questionBasicInfo.getJSONObject(0).getString("question_classification_name");
                    questionSourceName = questionBasicInfo.getJSONObject(0).getString("question_source_name");
                    important = questionBasicInfo.getJSONObject(0).getInteger("important");
                    urgency = questionBasicInfo.getJSONObject(0).getInteger("urgency");
                }
                vo.setImportant(important);
                vo.setUrgency(urgency);
                vo.setClassificationName(questionClassificationName);
                vo.setSourceName(questionSourceName);
            }

            //问题处理状态及处理状态名称赋值  根据question_process_status question_process_result return_flag_id
            if (2 == entity.getQuestionProcessStatus() && 1 == entity.getQuestionProcessResult() && entity.getReturnFlagId() == null) {
                vo.setQuestionProcessStatus(0);
                vo.setQuestionProcessStatusName("处理中");
            }else if (2 == entity.getQuestionProcessStatus() && 1 == entity.getQuestionProcessResult() && entity.getReturnFlagId() != null) {
                vo.setQuestionProcessStatus(1);
                vo.setQuestionProcessStatusName("退回处理");
            }else if (5 == entity.getQuestionProcessStatus() && 3 == entity.getQuestionProcessResult()) {
                vo.setQuestionProcessStatus(2);
                vo.setQuestionProcessStatusName("终止结案");
            }else if (8 == entity.getQuestionProcessStatus() && 2 == entity.getQuestionProcessResult()) {
                vo.setQuestionProcessStatus(3);
                vo.setQuestionProcessStatusName("正常结案");
            }
            //处理 问题处理阶段字段值信息  并处理是否逾期提示
            if ("QS".equals(entity.getQuestionProcessStep()) && entity.getQuestionSolveStep() != null) {
                vo.setQuestionProcessStage(entity.getQuestionSolveStep());
                //判断是否逾期  Y 逾期  N 未逾期
                String isOverDue = "N";
                if(!StringUtils.isEmpty(entity.getActualCompleteDate()) && !StringUtils.isEmpty(entity.getExpectCompleteTime())) {
                    isOverDue = entity.getActualCompleteDate().getTime() - entity.getExpectCompleteTime().getTime() > 0 ? "Y" : "N";
                }else if(!StringUtils.isEmpty(entity.getExpectCompleteTime())){
                    isOverDue = System.currentTimeMillis() - entity.getExpectCompleteTime().getTime() > 0 ? "Y" : "N";
                }
                vo.setIsOverDue(isOverDue);
                //迭代六：特殊的，短期结案验收的已完成阶段，处理状态为4-短期结案
                if((vo.getQuestionProcessStage().equals("SE001014")|| vo.getQuestionProcessStage().equals("SE003005") ) && (4 == entity.getQuestionProcessStatus() && 2 == entity.getQuestionProcessResult())) {
                    vo.setQuestionProcessStatus(4);
                    vo.setQuestionProcessStatusName("短期结案");
                }
            } else {
                vo.setQuestionProcessStage(entity.getQuestionProcessStep());
                //主项目卡 当前系统时间 - （开始时间+默认1）
                String isOverDue = (System.currentTimeMillis()-(entity.getStartTime().getTime()+ 24 * 3600 * 1000))>0 ? "Y":"N";
                vo.setIsOverDue(isOverDue);
            }
            //时间装换   问题发起时间   问题接收时间/问题结案时间
            String initiateDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entity.getQuestionInitiateDate());
            vo.setQuestionInitiateDate(initiateDate);
            String receiveDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entity.getQuestionReceiveDate());
            vo.setQuestionReceiveDate(receiveDate);

            //为每一条记录增加taskCode和appCode字段信息
            vo.setAppCode("FRC");
            vo.setTaskCode(getTaskCode(entity));

            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObjectList.add(jsonObject);
        }
        return jsonObjectList;
    }



    /**
     * 对查询条件进行处理   单据的状态 和  反馈的开始时间和结束时间进行处理
     *
     * @param proposerModel 提出者model
     * @return QuestionTrackProposerModel
     * @throws ParseException
     */
    private QuestionTrackProposerModel handleData(QuestionTrackProposerModel proposerModel) {
        //单据状态 0：全部   1：处理中（包括退回和待审核）    2：已处理（包括终止和结案）
        String questionStatus = proposerModel.getQuestionStatus();
        if (StringUtils.isEmpty(questionStatus)) {
            questionStatus = "0";
        }
        proposerModel.setQuestionStatus(questionStatus);
        //反馈开始时间和结束时间处理
        String startDate = proposerModel.getFeedbackStartDate();
        String endDate = proposerModel.getFeedbackEndDate();
        String compareEndTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (compareEndTime.equals(endDate)) {
            startDate += " 00:00:00";
            endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        } else {
            startDate += " 00:00:00";
            endDate += " 23:59:59";
        }
        proposerModel.setFeedbackStartDate(startDate);
        proposerModel.setFeedbackEndDate(endDate);
        return proposerModel;
    }


    /**
     * 对查询条件进行处理   单据的状态 和  反馈的开始时间和结束时间进行处理
     *
     * @param responsibleModel 当责者model
     * @return QuestionTrackResponsibleModel
     * @throws ParseException
     */
    private QuestionTrackResponsibleModel handleData(QuestionTrackResponsibleModel responsibleModel) {
        //单据状态 0：全部   1：处理中（包括退回和待审核）    2：已处理（包括终止和结案）
        String questionStatus = responsibleModel.getQuestionStatus();
        if (StringUtils.isEmpty(questionStatus)) {
            questionStatus = "0";
        }
        responsibleModel.setQuestionStatus(questionStatus);
        //反馈开始时间和结束时间处理
        String startDate = responsibleModel.getFeedbackStartDate();
        String endDate = responsibleModel.getFeedbackEndDate();
        String compareEndTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (compareEndTime.equals(endDate)) {
            startDate += " 00:00:00";
            endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        } else {
            startDate += " 00:00:00";
            endDate += " 23:59:59";
        }
        responsibleModel.setFeedbackStartDate(startDate);
        responsibleModel.setFeedbackEndDate(endDate);
        return responsibleModel;
    }


    /**
     * 对查询条件进行处理   单据的状态 和  反馈的开始时间和结束时间进行处理
     *
     * @param processorModel 处理者model
     * @return QuestionTrackProcessorModel
     * @throws ParseException
     */
    private QuestionTrackProcessorModel handleData(QuestionTrackProcessorModel processorModel) {
        //单据状态 0：全部   1：处理中（包括退回和待审核）    2：已处理（包括终止和结案）
        String questionStatus = processorModel.getQuestionStatus();
        if (StringUtils.isEmpty(questionStatus)) {
            questionStatus = "0";
        }
        processorModel.setQuestionStatus(questionStatus);
        //反馈开始时间和结束时间处理
        String startDate = processorModel.getFeedbackStartDate();
        String endDate = processorModel.getFeedbackEndDate();
        String compareEndTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (compareEndTime.equals(endDate)) {
            startDate += " 00:00:00";
            endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        } else {
            startDate += " 00:00:00";
            endDate += " 23:59:59";
        }
        processorModel.setFeedbackStartDate(startDate);
        processorModel.setFeedbackEndDate(endDate);
        return processorModel;
    }


    /**
     * 将实体类集合转成前端要求的格式集合  提出者信息转换
     *
     * @param entities 实体类集合
     * @return List<JSONObject>
     */
    private List<JSONObject> convertData2(List<QuestionTrackResponsibleEntity> entities, List<Map<String, Object>> mapList) throws JsonProcessingException{
        List<JSONObject> jsonObjectList = new ArrayList<>();
        HashMap<String, Map<String, Object>> beforeMap = new HashMap<>();
        for(Map<String, Object> map : mapList){
            beforeMap.put((String) map.get("question_no"),map);
        }
        for (QuestionTrackResponsibleEntity entity : entities) {
            //获取问题单号的发起时间
            String questionNo = entity.getQuestionNo();
            Map<String,Object> map = beforeMap.get(questionNo);
            Date createData = (Date) map.get("create_date");
            String createName = (String) map.get("create_name");
            String createId = (String) map.get("create_id");
            entity.setQuestionInitiateDate(createData);
            entity.setProposerPersonName(createName);
            entity.setProposerPersonId(createId);

            QuestionTrackProcessorVo vo = new QuestionTrackProcessorVo();
            BeanUtils.copyProperties(entity, vo);
            //从json中取出问题来源 问题分类名称 赋值给相应的实体类
            if (entity.getDataContent() != null) {
                String dataContent = entity.getDataContent();
                JSONObject object = JSON.parseObject(dataContent);
                String questionClassificationName;
                String questionSourceName;
                Integer important;
                Integer urgency ;
                String questionSolveStep = entity.getQuestionSolveStep();
                if(!StringUtils.isEmpty(questionSolveStep) && "SE003".equals(questionSolveStep.substring(0,5))){
                    JSONObject questionResult = object.getJSONObject("question_result");
                    JSONObject questionBasicInfo = questionResult.getJSONObject("question_basic_info");
                    questionClassificationName = questionBasicInfo.getString("question_classification_name");
                    questionSourceName = questionBasicInfo.getString("question_source_name");
                    important = questionBasicInfo.getInteger("important");
                    urgency = questionBasicInfo.getInteger("urgency");
                }else{
                    JSONArray questionResult = object.getJSONArray("question_result");
                    JSONArray questionBasicInfo = questionResult.getJSONObject(0).getJSONArray("question_basic_info");
                    questionClassificationName = questionBasicInfo.getJSONObject(0).getString("question_classification_name");
                    questionSourceName = questionBasicInfo.getJSONObject(0).getString("question_source_name");
                    important = questionBasicInfo.getJSONObject(0).getInteger("important");
                    urgency = questionBasicInfo.getJSONObject(0).getInteger("urgency");
                }
                vo.setImportant(important);
                vo.setUrgency(urgency);
                vo.setClassificationName(questionClassificationName);
                vo.setSourceName(questionSourceName);
            }

            //问题处理状态及处理状态名称赋值  根据question_process_status question_process_result return_flag_id
            if (2 == entity.getQuestionProcessStatus() && 1 == entity.getQuestionProcessResult() && entity.getReturnFlagId() == null) {
                vo.setQuestionProcessStatus(0);
                vo.setQuestionProcessStatusName("处理中");
            } else if (2 == entity.getQuestionProcessStatus() && 1 == entity.getQuestionProcessResult() && entity.getReturnFlagId() != null) {
                vo.setQuestionProcessStatus(1);
                vo.setQuestionProcessStatusName("退回处理");
            } else if (5 == entity.getQuestionProcessStatus() && 3 == entity.getQuestionProcessResult()) {
                vo.setQuestionProcessStatus(2);
                vo.setQuestionProcessStatusName("终止结案");
            } else if (8 == entity.getQuestionProcessStatus() && 2 == entity.getQuestionProcessResult()) {
                vo.setQuestionProcessStatus(3);
                vo.setQuestionProcessStatusName("正常结案");
            }
            //处理 问题处理阶段字段值信息  并处理是否逾期提示
            if ("QS".equals(entity.getQuestionProcessStep()) && entity.getQuestionSolveStep() != null) {
                vo.setQuestionProcessStage(entity.getQuestionSolveStep());
                //判断是否逾期  Y 逾期  N 未逾期
                String isOverDue = "N";
                if(!StringUtils.isEmpty(entity.getActualCompleteDate()) && !StringUtils.isEmpty(entity.getExpectCompleteTime())) {
                    isOverDue = entity.getActualCompleteDate().getTime() - entity.getExpectCompleteTime().getTime() > 0 ? "Y" : "N";
                }else if(!StringUtils.isEmpty(entity.getExpectCompleteTime())){
                    isOverDue = System.currentTimeMillis() - entity.getExpectCompleteTime().getTime() > 0 ? "Y" : "N";
                }
                vo.setIsOverDue(isOverDue);
            } else {
                vo.setQuestionProcessStage(entity.getQuestionProcessStep());
                //主项目卡 当前系统时间 - （开始时间+默认1）
                String isOverDue = (System.currentTimeMillis()-(entity.getStartTime().getTime()+ 24 * 3600 * 1000))>0 ? "Y":"N";
                vo.setIsOverDue(isOverDue);
            }
            //时间装换   问题发起时间   问题接收时间/问题结案时间
            String initiateDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entity.getQuestionInitiateDate());
            vo.setQuestionInitiateDate(initiateDate);
            String receiveDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entity.getQuestionReceiveDate());
            vo.setQuestionReceiveDate(receiveDate);

            //为每一条记录增加taskCode和appCode字段信息
            vo.setAppCode("FRC");
            vo.setTaskCode(getTaskCode(entity));

            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObjectList.add(jsonObject);
        }
        return jsonObjectList;
    }


}
