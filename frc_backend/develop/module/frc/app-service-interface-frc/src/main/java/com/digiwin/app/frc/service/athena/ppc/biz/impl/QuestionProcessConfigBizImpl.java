package com.digiwin.app.frc.service.athena.ppc.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionSolveEnum;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionBackEntity;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionBackMapper;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionClassificationMapper;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionProcessConfigBiz;
import com.digiwin.app.frc.service.athena.ppc.constants.QuestionProcessConfigConstant;
import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionProcessConfigEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.model.QuestionProcessConfigModel;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.QuestionProcessConfigVo;
import com.digiwin.app.frc.service.athena.ppc.mapper.QuestionProcessConfigMapper;
import com.digiwin.app.frc.service.athena.ppc.mapper.QuestionRiskLevelMapper;
import com.digiwin.app.frc.service.athena.qdh.biz.IamEocBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.QuestionDetailVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * @Author: xieps
 * @Date: 2022/2/17 10:39
 * @Version 1.0
 * @Description
 */
@Service
public class QuestionProcessConfigBizImpl implements QuestionProcessConfigBiz {

    @Autowired
    private QuestionRiskLevelMapper riskLevelMapper;

    @Autowired
    private QuestionClassificationMapper classificationMapper;

    @Autowired
    private QuestionProcessConfigMapper questionProcessConfigMapper;

    @Override
    public List<JSONObject> addQuestionProcessConfig(JSONArray dataContent) throws IOException, DWArgumentException {
        //添加问题处理阶段配置信息
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        Long tenantSid = (Long) profile.get("tenantSid");
        String userName = (String) profile.get("userName");
        //对必传参数进行校验 并进行封装成entity
        List<QuestionProcessConfigEntity> entities = checkAndHandleData(dataContent, tenantSid, userName);
        int result = questionProcessConfigMapper.addQuestionProcessConfigInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }


    @Override
    public boolean deleteQuestionProcessConfig(JSONArray dataContent) {
        //删除问题处理配置信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(QuestionProcessConfigConstant.PROCESS_CONFIG_ID);
            oidList.add(oid);
        }
        int result = questionProcessConfigMapper.deleteQuestionProcessConfigInfo(oidList,tenantSid);
        classificationMapper.updateProcessConfigIdByDelete(oidList,tenantSid);
        riskLevelMapper.updateProcessConfigIdByDelete(oidList,tenantSid);
        return result > 0;
    }


    @Override
    public boolean updateQuestionProcessConfigInfo(JSONArray dataContent) throws IOException, DWArgumentException {
        //更新问题处理配置信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        List<QuestionProcessConfigEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            QuestionProcessConfigEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = questionProcessConfigMapper.updateBatch(entities);
        return result > 0;
    }


    @Override
    public List<JSONObject> getQuestionProcessConfig(JSONArray dataContent) throws JsonProcessingException {
        //获取问题处理配置信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionProcessConfigVo> vos;
        if(StringUtils.isEmpty(dataContent) || dataContent.isEmpty()){
            vos = questionProcessConfigMapper.getQuestionProcessConfig(tenantSid, new QuestionProcessConfigModel());
        }else {
            //平台查询只支持传一笔数据 规格使用数组传参
            JSONObject jsonObject = dataContent.getJSONObject(0);
            //将查询条件参数映射到model中
            QuestionProcessConfigModel model = JSON.parseObject(jsonObject.toJSONString(), QuestionProcessConfigModel.class);
            vos = questionProcessConfigMapper.getQuestionProcessConfig(tenantSid, model);
        }
        //转成平台规范带有_格式的前端数据
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (QuestionProcessConfigVo vo : vos) {
            JSONObject jsonObjectInfo = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObjectList.add(jsonObjectInfo);
        }
        return jsonObjectList;
    }
    /**
     * 对必传参数进行校验 并进行封装成entity(修改)
     *
     * @param dataContent 解析后数据
     * @param updateName  修改人姓名
     * @param i           循环遍历的索引值
     * @return QuestionProcessConfigEntity 封装后的实体类
     * @throws DWArgumentException
     */
    private QuestionProcessConfigEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(QuestionProcessConfigConstant.PROCESS_CONFIG_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("processConfigId",
                    MultilingualismUtil.getLanguage("notExist"));
        }
        QuestionProcessConfigModel model = JSON.parseObject(jsonObject.toJSONString(), QuestionProcessConfigModel.class);
        QuestionProcessConfigEntity entity = new QuestionProcessConfigEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setTenantSid(tenantSid);
        entity.setUpdateName(updateName);
        entity.setUpdateTime(new Date());
        JSONArray levelInfo = jsonObject.getJSONArray("risk_level_info");
        if(!StringUtils.isEmpty(levelInfo) && !levelInfo.isEmpty()){
            List<String> levelIds = new ArrayList<>();
            for (int j = 0; j < levelInfo.size(); j++) {
                JSONObject jsonObject1 = levelInfo.getJSONObject(j);
                String riskLevelId = jsonObject1.getString("risk_level_id");
                levelIds.add(riskLevelId);
            }
            riskLevelMapper.updateProcessConfigIdInfo(entity.getOid(),tenantSid);
            riskLevelMapper.updateProcessConfigId(levelIds,entity.getOid(),tenantSid);
        }else{
            riskLevelMapper.updateProcessConfigIdInfo(entity.getOid(),tenantSid);
        }
        JSONArray classificationInfo = jsonObject.getJSONArray("classification_info");
        if(!StringUtils.isEmpty(classificationInfo) && !classificationInfo.isEmpty()){
            List<String> classificationIds = new ArrayList<>();
            for (int j = 0; j < classificationInfo.size(); j++) {
                JSONObject jsonObject1 = classificationInfo.getJSONObject(j);
                String classificationId = jsonObject1.getString("classification_id");
                classificationIds.add(classificationId);
            }
            classificationMapper.updateProcessConfigIdInfo(entity.getOid(),tenantSid);
            classificationMapper.updateProcessConfigId(classificationIds,entity.getOid(),tenantSid);
        }else{
            classificationMapper.updateProcessConfigIdInfo(entity.getOid(),tenantSid);
        }
        return entity;
    }



    /**
     * 对必传参数进行校验 并进行封装成List<Entity>(新增)
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @param userName    用户姓名
     * @return List<QuestionProcessConfigEntity> 封装后实体类集合
     * @throws DWArgumentException
     */
    private List<QuestionProcessConfigEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException {
        List<QuestionProcessConfigEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if (StringUtils.isEmpty(jsonObject.getString(QuestionProcessConfigConstant.ATTRIBUTION_NO))) {
                throw new DWArgumentException("attributionNo", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty( jsonObject.getJSONArray("risk_level_info"))) {
                throw new DWArgumentException("riskLevelInfo", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionProcessConfigConstant.LIABLE_PERSON_NAME))) {
                throw new DWArgumentException("liablePersonName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionProcessConfigConstant.LIABLE_PERSON_ID))) {
                throw new DWArgumentException("liablePersonId", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionProcessConfigConstant.SOLUTION_STEP_ID))) {
                throw new DWArgumentException("solutionStepId", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionProcessConfigConstant.SOURCE_ID))) {
                throw new DWArgumentException("sourceId", MultilingualismUtil.getLanguage("notExist"));
            }

            QuestionProcessConfigModel model = JSON.parseObject(jsonObject.toJSONString(), QuestionProcessConfigModel.class);
            QuestionProcessConfigEntity entity = new QuestionProcessConfigEntity();
            BeanUtils.copyProperties(model, entity);

            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantSid);
            entity.setCreateTime(new Date());
            entity.setCreateName(userName);
            JSONArray levelInfo = jsonObject.getJSONArray("risk_level_info");
            if(!StringUtils.isEmpty(levelInfo)) {
                List<String> levelIds = new ArrayList<>();
                for (int j = 0; j < levelInfo.size(); j++) {
                    JSONObject jsonObject1 = levelInfo.getJSONObject(j);
                    String riskLevelId = jsonObject1.getString("risk_level_id");
                    levelIds.add(riskLevelId);
                }
                riskLevelMapper.updateProcessConfigId(levelIds, entity.getOid(), tenantSid);
            }
            entity.setRiskLevelInfo(JSON.toJSONString(levelInfo));
            JSONArray classificationInfo = jsonObject.getJSONArray("classification_info");
            if(!StringUtils.isEmpty(classificationInfo)) {
                List<String> classificationIds = new ArrayList<>();
                for (int j = 0; j < classificationInfo.size(); j++) {
                    JSONObject jsonObject1 = classificationInfo.getJSONObject(j);
                    String classificationId = jsonObject1.getString("classification_id");
                    classificationIds.add(classificationId);
                }
                classificationMapper.updateProcessConfigId(classificationIds, entity.getOid(), tenantSid);
            }
            entity.setClassificationInfo(JSON.toJSONString(classificationInfo));
            entities.add(entity);
        }
        return entities;
    }


    /**
     * 将实体类集合转成前端要求的格式集合
     *
     * @param entities 风险等级实体类集合
     * @return
     */
    private List<JSONObject> convertData(List<QuestionProcessConfigEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (QuestionProcessConfigEntity entity : entities) {
            QuestionProcessConfigVo vo = new QuestionProcessConfigVo();
            BeanUtils.copyProperties(entity, vo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObject.put("risk_level_info",JSON.parseArray(entity.getRiskLevelInfo()));
            jsonObject.put("classification_info",JSON.parseArray(entity.getClassificationInfo()));
            jsonObject.remove("source_name");
            jsonObject.remove("solution_step_name");
            mapList.add(jsonObject);
        }
        return mapList;
    }

    @Autowired
    QuestionBackMapper questionBackMapper;

    @Autowired
    DataInstanceMapper dataInstanceMapper;

    @Autowired
    ActionTraceMapper actionTraceMapper;

    @Autowired
    IamEocBiz iamEocBiz;

    @Autowired
    QuestionProcessConfigBiz questionProcessConfigBiz;

    @Override
    public JSONObject getQuestionDetail(String questionId) {
        // 根据questionId查询问题详情
        QuestionDetailVo questionDetailVo =
                actionTraceMapper.getQuestionTrace(questionId);
        if (null == questionDetailVo) {
            // 查询为空，返回空对象
            return new JSONObject();
        }
        // 解析最外层结构
        JSONObject jsonObject = JSON.parseObject(questionDetailVo.getDataContent());
        // detail为即将返回数据格式
        JSONObject detail = jsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);

        // 附件所属阶段加入多语系
        JSONArray attachmentInfos = detail.getJSONArray("attachment_info");
        for (Iterator<Object> iterator = attachmentInfos.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            String step = (String) obj.get("attachment_belong_stage");
            obj.put("attachment_belong_stage",MultilingualismUtil.getLanguage(step));
        }
        // 封装question_info
        detail.put(QuestionResponseConst.QUESTION_INFO,packageQuestionInfo(questionId,questionDetailVo));
        // 封装最外层，用于任务卡展示
        packageDetail(detail,questionDetailVo);

        return jsonObject;
    }


    /**
     * 封装question_info层
     * @param questionId 问题号
     * @param questionDetailVo 查询出的问题数据
     * @return JSONArray
     */
    private Object packageQuestionInfo(String questionId,QuestionDetailVo questionDetailVo){
        JSONObject object = new JSONObject();
        object.put(QuestionResponseConst.QUESTION_ID,questionId);
        object.put(QuestionResponseConst.QUESTION_PROCESS_STATUS,String.valueOf(questionDetailVo.getQuestionProcessStatus()));
        object.put(QuestionResponseConst.QUESTION_PROCESS_RESULT,String.valueOf(questionDetailVo.getQuestionProcessResult()));
        object.put(QuestionResponseConst.QUESTION_NO,questionDetailVo.getQuestionNo());
        object.put(QuestionResponseConst.QUESTION_RECORD_ID,questionDetailVo.getQuestionRecordId());
        object.put(QuestionResponseConst.QUESTION_FLAG_ID,questionDetailVo.getReturnFlagId());
        object.put(QuestionResponseConst.QUESTION_FLAG_NAME,questionDetailVo.getReturnFlagName());
        object.put(QuestionResponseConst.QUESTION_DESCRIPTION,questionDetailVo.getQuestionDescription());
        object.put("liable_person_id",questionDetailVo.getLiablePersonId());
        object.put("liable_person_name",questionDetailVo.getLiablePersonName());
        object.put("liable_person_position_id",questionDetailVo.getLiablePersonPositionId());
        object.put("liable_person_position_name",questionDetailVo.getLiablePersonPositionName());
        object.put("return_reason_no",questionDetailVo.getReturnNo());
        object.put("close_reason",questionDetailVo.getCloseReason());
        // null 转 “”
        String dataContentString = JSON.toJSONString(object, filter);
        // 按规格封装object  question_info层组成集合形式
        return JSON.parse(dataContentString);
    }

    private ValueFilter filter = (obj, s, v) -> {
        if (null == v) {
            return "";
        }
        return v;
    };

    /**
     * 封装最外层，用于任务卡展示，只是为了配合athena页面展示，一定要放在最外层，所以有很多冗余字段返回
     * @param detail data_content内容
     * @param questionDetailVo 根据主键查询出的问题详细信息
     */
    private void packageDetail(JSONObject detail,QuestionDetailVo questionDetailVo) {
        // 加入退回原因编号
        detail.put("return_reason_no","");
        // 退回原因
        detail.put("return_reason","");
        // 退回人id
        detail.put("return_id","");
        // 退回人姓名
        detail.put("return_name","");
        // 退回标识：由哪个关卡退回
        detail.put("return_flag_name","");

        if (!StringUtils.isEmpty(questionDetailVo.getReturnNo())) {
            detail.put("return_reason_no",questionDetailVo.getReturnNo());
            List<QuestionBackEntity> questionBackEntities = questionBackMapper.getQuestionBackInfo(TenantTokenUtil.getTenantSid(),questionDetailVo.getReturnNo(),null,null,null);
            detail.put("return_reason",questionBackEntities.get(0).getBackReason());
            // 加入问题退回人
            detail.put("return_id",questionDetailVo.getReturnId());
            detail.put("return_name",questionDetailVo.getReturnName());
        }
        // 问题号
        detail.put("question_no",questionDetailVo.getQuestionNo());
        // 问题描述
        detail.put("question_description",questionDetailVo.getQuestionDescription());
        if (!StringUtils.isEmpty(questionDetailVo.getReturnFlagName())) {
            detail.put("return_flag_name",questionDetailVo.getReturnFlagName());
        }
        // 加入问题提出人
        JSONObject basicInfo = detail.getJSONObject("question_basic_info");
        detail.put("question_proposer_id",basicInfo.get("question_proposer_id"));
        detail.put("question_proposer_name",basicInfo.get("question_proposer_name"));
        // 迭代三内容
        if ("QF".equals(questionDetailVo.getQuestionProcessStep())) {
            detail.put("risk_level","");
            detail.put("urgency","");
        }else {
            JSONObject identify = detail.getJSONObject("question_identify_info");
            detail.put("risk_level",identify.get("risk_level_name"));
            detail.put("urgency",identify.get("urgency"));
        }
        // 最外层返回负责人信息
        detail.put("liable_person_id",questionDetailVo.getLiablePersonId());
        detail.put("liable_person_name",questionDetailVo.getLiablePersonName());
        detail.put("liable_person_position_id",questionDetailVo.getLiablePersonPositionId());
        detail.put("liable_person_position_name",questionDetailVo.getLiablePersonPositionName());

        // 获取问题分析任务卡的负责人
        try {
            if (QuestionSolveEnum.question_distribution.getCode().equals(questionDetailVo.getQuestionSolveStep()) ||
                    QuestionSolveEnum.question_curb_distribution.getCode().equals(questionDetailVo.getQuestionSolveStep())){
                List<Map<String,Object>> list = iamEocBiz.getUsers();
                JSONArray processPersonInfos = new JSONArray();
                for (Map<String,Object> map : list) {
                    JSONObject personInfo = new JSONObject();
                    personInfo.put("process_person_id",map.get("id"));
                    personInfo.put("process_person_name",map.get("name"));
                    processPersonInfos.add(personInfo);
                }

                detail.put("process_person_info",processPersonInfos);
            }
            if("QIA".equals(questionDetailVo.getQuestionProcessStep()) || "QIR".equals(questionDetailVo.getQuestionProcessStep())) {
                packageQIPerson(detail);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取问题分析负责人
     * @param detail data_content
     */
    private void packageQIPerson(JSONObject detail) throws JsonProcessingException {
        // 从question_basic_info里获取 risk_level_oid、question_source_oid、question_attribution_no、question_attribution_no
        JSONObject basicInfo = detail.getJSONObject("question_basic_info");
        // 封装请求入参
        JSONArray processConfigs = new JSONArray();
        JSONObject config = new JSONObject();
        config.put("risk_level_id",basicInfo.getString("risk_level_oid"));
        config.put("source_id",basicInfo.getString("question_source_oid"));
        config.put("attribution_no",basicInfo.getString("question_attribution_no"));
        processConfigs.add(config);
        // 4配1获取负责人，若为null，返回 “”
        List<JSONObject> resultList = questionProcessConfigBiz.getQuestionProcessConfig(processConfigs);
        if (!CollectionUtils.isEmpty(resultList)) {
            // 获取问题分析 question_identify_info结构，将负责人信息放入
            JSONObject identifyInfo = detail.getJSONObject("question_identify_info");
            identifyInfo.put("liable_person_id",resultList.get(0).getString("liable_person_id"));
            identifyInfo.put("liable_person_name",resultList.get(0).getString("liable_person_name"));
        }
    }

}
