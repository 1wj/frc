package com.digiwin.app.frc.service.athena.ppc.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.common.enums.EffectiveEnum;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionClassificationMapper;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionAnalysisConfigBiz;
import com.digiwin.app.frc.service.athena.ppc.constants.QuestionAnalysisConfigConstant;
import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionAnalysisConfigEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.model.QuestionAnalysisConfigModel;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.QuestionAnalysisConfigVo;
import com.digiwin.app.frc.service.athena.ppc.mapper.QuestionAnalysisConfigMapper;
import com.digiwin.app.frc.service.athena.ppc.mapper.QuestionRiskLevelMapper;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.ppc.DepartmentMessageUtil;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2022/2/17 14:45
 * @Version 1.0
 * @Description
 */
@Service
public class QuestionAnalysisConfigBizImpl implements QuestionAnalysisConfigBiz {

    @Autowired
    private QuestionAnalysisConfigMapper questionAnalysisConfigMapper;

    @Autowired
    private QuestionRiskLevelMapper riskLevelMapper;

    @Autowired
    private QuestionClassificationMapper classificationMapper;

    @Override
    public List<JSONObject> addQuestionAnalysisConfig(JSONArray dataContent) throws IOException, DWArgumentException {
        //??????????????????????????????
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        Long tenantSid = (Long) profile.get("tenantSid");
        String userName = (String) profile.get("userName");
        //??????????????????????????? ??????????????????entity
        List<QuestionAnalysisConfigEntity> entities = checkAndHandleData(dataContent, tenantSid, userName);
        int result = questionAnalysisConfigMapper.addQuestionAnalysisConfigInfo(entities);
        //entity????????????????????????_?????????????????????
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }


    @Override
    public boolean deleteQuestionAnalysisConfig(JSONArray dataContent) {
        //??????????????????????????????
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(QuestionAnalysisConfigConstant.ANALYSIS_CONFIG_ID);
            oidList.add(oid);
        }
        int result = questionAnalysisConfigMapper.deleteQuestionAnalysisConfigInfo(oidList,tenantSid);
        riskLevelMapper.deleteAnalysisConfigIds(oidList,tenantSid);
        classificationMapper.deleteAnalysisConfigIds(oidList,tenantSid);
        return result > 0;
    }


    @Override
    public boolean updateQuestionAnalysisConfigInfo(JSONArray dataContent) throws IOException, DWArgumentException {
        //??????????????????????????????
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        List<QuestionAnalysisConfigEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            QuestionAnalysisConfigEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = questionAnalysisConfigMapper.updateBatch(entities);
        return result > 0;
    }

    @Override
    public List<JSONObject> getQuestionAnalysisConfig(JSONArray dataContent) throws JsonProcessingException {
        //??????????????????????????????
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionAnalysisConfigVo> vos;
        if(StringUtils.isEmpty(dataContent) || dataContent.isEmpty()){
            vos = questionAnalysisConfigMapper.getQuestionAnalysisConfig(tenantSid,new QuestionAnalysisConfigModel(),null);
        }else{
            //???????????????????????????????????? ????????????????????????
            JSONObject jsonObject = dataContent.getJSONObject(0);
            //??????????????????????????????model???
            QuestionAnalysisConfigModel model = JSON.parseObject(jsonObject.toJSONString(), QuestionAnalysisConfigModel.class);
            String feedbackInfo = jsonObject.getString("feedback_department_message");
           vos = questionAnalysisConfigMapper.getQuestionAnalysisConfig(tenantSid,model,feedbackInfo);
        }
        //????????????????????????_?????????????????????
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (QuestionAnalysisConfigVo vo : vos) {
            List<JSONObject> objectList = DepartmentMessageUtil.splitDepartmentMessage(vo.getFeedbackDepartmentMessage());
            JSONObject jsonObjectInfo = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObjectInfo.put("feedback_department_message",objectList);
            jsonObjectInfo.remove("feedbackDepartmentMessage");
            jsonObjectList.add(jsonObjectInfo);
        }
        return jsonObjectList;
    }



    /**
     * ??????????????????????????? ??????????????????entity(??????)
     *
     * @param dataContent ???????????????
     * @param updateName  ???????????????
     * @param i           ????????????????????????
     * @return QuestionConfirmConfigEntity ?????????????????????
     * @throws DWArgumentException
     */
    private QuestionAnalysisConfigEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException, IOException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(QuestionAnalysisConfigConstant.ANALYSIS_CONFIG_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("analysisConfigId",
                    MultilingualismUtil.getLanguage("notExist"));
        }
        QuestionAnalysisConfigModel model = JSON.parseObject(jsonObject.toJSONString(), QuestionAnalysisConfigModel.class);
        QuestionAnalysisConfigEntity entity = new QuestionAnalysisConfigEntity();
        BeanUtils.copyProperties(model, entity);
        JSONArray departmentMessage = jsonObject.getJSONArray("feedback_department_message");
        if(!StringUtils.isEmpty(departmentMessage)){
            entity.setFeedbackDepartmentMessage(DepartmentMessageUtil.handleDepartmentMessageInfo(departmentMessage));
        }
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
            riskLevelMapper.updateAnalysisConfigIdInfo(entity.getOid(),tenantSid);
            riskLevelMapper.updateAnalysisConfigId(levelIds,entity.getOid(),tenantSid);
        }else{
            riskLevelMapper.updateAnalysisConfigIdInfo(entity.getOid(),tenantSid);
        }

        JSONArray classificationInfo = jsonObject.getJSONArray("classification_info");
        if(!StringUtils.isEmpty(classificationInfo) && !classificationInfo.isEmpty()) {
            List<String> classificationIds = new ArrayList<>();
            for (int j = 0; j < classificationInfo.size(); j++) {
                JSONObject jsonObject1 = classificationInfo.getJSONObject(j);
                String classificationId = jsonObject1.getString("classification_id");
                classificationIds.add(classificationId);
            }
            classificationMapper.updateAnalysisConfigIdInfo(entity.getOid(),tenantSid);
            classificationMapper.updateAnalysisConfigId(classificationIds, entity.getOid(), tenantSid);
        }else{
            classificationMapper.updateAnalysisConfigIdInfo(entity.getOid(),tenantSid);
        }

        return entity;
    }





    /**
     * ??????????????????????????? ??????????????????List<Entity>(??????)
     *
     * @param dataContent ???????????????
     * @param tenantSid   ??????id
     * @param userName    ????????????
     * @return List<QuestionAnalysisConfigEntity> ????????????????????????
     * @throws DWArgumentException
     */
    private List<QuestionAnalysisConfigEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException {
        List<QuestionAnalysisConfigEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //???????????????????????????
            if ( StringUtils.isEmpty(jsonObject.getString(QuestionAnalysisConfigConstant.ATTRIBUTION_NO))) {
                throw new DWArgumentException("attributionNo", MultilingualismUtil.getLanguage("notExist"));
            }
            JSONArray riskLevelInfo = jsonObject.getJSONArray("risk_level_info");
            if (StringUtils.isEmpty(riskLevelInfo) ) {
                throw new DWArgumentException("riskLevelInfo", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionAnalysisConfigConstant.LIABLE_PERSON_NAME))) {
                throw new DWArgumentException("liablePersonName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionAnalysisConfigConstant.LIABLE_PERSON_ID))) {
                throw new DWArgumentException("liablePersonId", MultilingualismUtil.getLanguage("notExist"));
            }
            QuestionAnalysisConfigModel model = JSON.parseObject(jsonObject.toJSONString(), QuestionAnalysisConfigModel.class);
            QuestionAnalysisConfigEntity entity = new QuestionAnalysisConfigEntity();
            BeanUtils.copyProperties(model, entity);
            JSONArray departmentMessage = jsonObject.getJSONArray("feedback_department_message");
            if(!StringUtils.isEmpty(departmentMessage)){
                entity.setFeedbackDepartmentMessage(DepartmentMessageUtil.handleDepartmentMessageInfo(departmentMessage));
            }
            entity.setFeedBackDepartmentInfo(JSON.toJSONString(departmentMessage));
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantSid);
            entity.setCreateTime(new Date());
            entity.setCreateName(userName);
            JSONArray levelInfo = jsonObject.getJSONArray("risk_level_info");
            if(!StringUtils.isEmpty(levelInfo) && !levelInfo.isEmpty()) {
                List<String> levelIds = new ArrayList<>();
                for (int j = 0; j < levelInfo.size(); j++) {
                    JSONObject jsonObject1 = levelInfo.getJSONObject(j);
                    String riskLevelId = jsonObject1.getString("risk_level_id");
                    levelIds.add(riskLevelId);
                }
                riskLevelMapper.updateAnalysisConfigId(levelIds, entity.getOid(), tenantSid);
            }
            entity.setRiskLevelInfo(JSON.toJSONString(levelInfo));
            JSONArray classificationInfo = jsonObject.getJSONArray("classification_info");
            if(!StringUtils.isEmpty(classificationInfo) ) {
                List<String> classificationIds = new ArrayList<>();
                for (int j = 0; j < classificationInfo.size(); j++) {
                    JSONObject jsonObject1 = classificationInfo.getJSONObject(j);
                    String classificationId = jsonObject1.getString("classification_id");
                    classificationIds.add(classificationId);
                }
                classificationMapper.updateAnalysisConfigId(classificationIds, entity.getOid(), tenantSid);
            }
            entity.setClassificationInfo(JSON.toJSONString(classificationInfo));
            entities.add(entity);
        }
        return entities;
    }



    /**
     * ???????????????????????????????????????????????????
     *
     * @param entities ???????????????????????????
     * @return
     */
    private List<JSONObject> convertData(List<QuestionAnalysisConfigEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (QuestionAnalysisConfigEntity entity : entities) {
            QuestionAnalysisConfigVo vo = new QuestionAnalysisConfigVo();
            BeanUtils.copyProperties(entity, vo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObject.put("risk_level_info",JSON.parseArray(entity.getRiskLevelInfo()));
            jsonObject.put("feedback_department_message",JSON.parseArray(entity.getFeedBackDepartmentInfo()));
            jsonObject.put("classification_info",JSON.parseArray(entity.getClassificationInfo()));
            jsonObject.remove("source_name");
            jsonObject.remove("feedbackDepartmentMessage");
            jsonObject.remove("classificationInfo");
            mapList.add(jsonObject);
        }
        return mapList;
    }



}
