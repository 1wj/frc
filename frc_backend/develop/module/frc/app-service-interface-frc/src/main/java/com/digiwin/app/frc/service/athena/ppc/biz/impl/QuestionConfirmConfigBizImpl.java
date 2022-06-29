package com.digiwin.app.frc.service.athena.ppc.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionConfirmConfigBiz;
import com.digiwin.app.frc.service.athena.ppc.constants.QuestionConfirmConfigConstant;
import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionConfirmConfigEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.model.QuestionConfirmConfigModel;
import com.digiwin.app.frc.service.athena.ppc.domain.model.QuestionConfirmConfigQueryModel;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.QuestionConfirmConfigVo;
import com.digiwin.app.frc.service.athena.ppc.mapper.QuestionConfirmConfigMapper;
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
 * @Date: 2022/2/15 14:52
 * @Version 1.0
 * @Description  问题确认配置处理Biz-impl
 */
@Service
public class QuestionConfirmConfigBizImpl implements QuestionConfirmConfigBiz {

    @Autowired
    private QuestionConfirmConfigMapper questionConfirmConfigMapper;

    @Autowired
    private QuestionRiskLevelMapper riskLevelMapper;

    @Override
    public List<JSONObject> addQuestionConfirmConfig(JSONArray dataContent) throws IOException, DWArgumentException {
        //添加问题确认配置信息
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        Long tenantSid = (Long) profile.get("tenantSid");
        String userName = (String) profile.get("userName");
        //对必传参数进行校验 并进行封装成entity
        List<QuestionConfirmConfigEntity> entities = checkAndHandleData(dataContent, tenantSid, userName);
        int result = questionConfirmConfigMapper.addQuestionConfirmConfigInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }

    @Override
    public boolean deleteQuestionConfirmConfig(JSONArray dataContent) {
        //删除问题确认配置信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(QuestionConfirmConfigConstant.CONFIRM_CONFIG_ID);
            oidList.add(oid);
        }
        int result = questionConfirmConfigMapper.deleteQuestionConfirmConfigInfo(oidList,tenantSid);
        riskLevelMapper.deleteConfirmConfigIds(oidList,tenantSid);
        return result > 0;
    }


    @Override
    public boolean updateQuestionConfirmConfigInfo(JSONArray dataContent) throws IOException, DWArgumentException {
        //更新风险等级信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        List<QuestionConfirmConfigEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            QuestionConfirmConfigEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = questionConfirmConfigMapper.updateBatch(entities);
        return result > 0;
    }


    @Override
    public List<JSONObject> getQuestionConfirmConfig(JSONArray dataContent) throws JsonProcessingException {
        //获取问题确认配置信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionConfirmConfigVo> vos;
        if(StringUtils.isEmpty(dataContent) || dataContent.isEmpty()){
            vos = questionConfirmConfigMapper.getQuestionConfirmConfig(tenantSid,new QuestionConfirmConfigQueryModel(),null);
        }else{
            //平台查询只支持传一笔数据 规格使用数组传参
            JSONObject jsonObject = dataContent.getJSONObject(0);
            //将查询条件参数映射到model中
            QuestionConfirmConfigQueryModel model = JSON.parseObject(jsonObject.toJSONString(), QuestionConfirmConfigQueryModel.class);
            String feedBackInfo = jsonObject.getString("feedback_department_message");
            vos = questionConfirmConfigMapper.getQuestionConfirmConfig(tenantSid,model,feedBackInfo);
        }
        //转成平台规范带有_格式的前端数据
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (QuestionConfirmConfigVo vo : vos) {
            List<JSONObject> objectList = DepartmentMessageUtil.splitDepartmentMessage(vo.getFeedbackDepartmentMessage());
            JSONObject jsonObjectInfo = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObjectInfo.put("feedback_department_message",objectList);
            jsonObjectInfo.remove("feedbackDepartmentMessage");
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
     * @return QuestionConfirmConfigEntity 封装后的实体类
     * @throws DWArgumentException
     */
    private QuestionConfirmConfigEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(QuestionConfirmConfigConstant.CONFIRM_CONFIG_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("confirmConfigId",
                    MultilingualismUtil.getLanguage("notExist"));
        }
        QuestionConfirmConfigModel model = JSON.parseObject(jsonObject.toJSONString(), QuestionConfirmConfigModel.class);
        QuestionConfirmConfigEntity entity = new QuestionConfirmConfigEntity();
        BeanUtils.copyProperties(model, entity);
        JSONArray departmentMessage = jsonObject.getJSONArray("feedback_department_message");
        if(!StringUtils.isEmpty(departmentMessage)) {
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
            riskLevelMapper.updateConfirmConfigIdInfo(entity.getOid(),tenantSid);
            riskLevelMapper.updateConfirmConfigId(levelIds,entity.getOid(),tenantSid);
        }else{
            riskLevelMapper.updateConfirmConfigIdInfo(entity.getOid(),tenantSid);
        }
        return entity;
    }

    /**
     * 对必传参数进行校验 并进行封装成List<Entity>(新增)
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @param userName    用户姓名
     * @return List<QuestionRiskLevelEntity> 封装后实体类集合
     * @throws DWArgumentException
     */
    private List<QuestionConfirmConfigEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException {
        List<QuestionConfirmConfigEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if (StringUtils.isEmpty(jsonObject.getString(QuestionConfirmConfigConstant.ATTRIBUTION_NO))) {
                throw new DWArgumentException("attributionNo", MultilingualismUtil.getLanguage("notExist"));
            }
            JSONArray riskLevelInfo = jsonObject.getJSONArray("risk_level_info");
            if (StringUtils.isEmpty(riskLevelInfo) ) {
                throw new DWArgumentException("riskLevelInfo", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionConfirmConfigConstant.LIABLE_PERSON_NAME))) {
                throw new DWArgumentException("liablePersonName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionConfirmConfigConstant.LIABLE_PERSON_ID))) {
                throw new DWArgumentException("liablePersonId", MultilingualismUtil.getLanguage("notExist"));
            }

            QuestionConfirmConfigModel model = JSON.parseObject(jsonObject.toJSONString(), QuestionConfirmConfigModel.class);
            QuestionConfirmConfigEntity entity = new QuestionConfirmConfigEntity();
            BeanUtils.copyProperties(model, entity);
            JSONArray departmentMessage = jsonObject.getJSONArray("feedback_department_message");
            if(!StringUtils.isEmpty(departmentMessage)) {
                entity.setFeedbackDepartmentMessage(DepartmentMessageUtil.handleDepartmentMessageInfo(departmentMessage));
            }
            entity.setFeedBackDepartmentInfo(JSON.toJSONString(departmentMessage));

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
                riskLevelMapper.updateConfirmConfigId(levelIds, entity.getOid(), tenantSid);
            }
            entity.setRiskLevelInfo(JSON.toJSONString(levelInfo));
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
    private List<JSONObject> convertData(List<QuestionConfirmConfigEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (QuestionConfirmConfigEntity entity : entities) {
            QuestionConfirmConfigVo vo = new QuestionConfirmConfigVo();
            BeanUtils.copyProperties(entity, vo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObject.put("risk_level_info",JSON.parseArray(entity.getRiskLevelInfo()));
            jsonObject.put("feedback_department_message",JSON.parseArray(entity.getFeedBackDepartmentInfo()));
            jsonObject.remove("source_name");
            jsonObject.remove("feedbackDepartmentMessage");
            jsonObject.remove("classification_name");
            mapList.add(jsonObject);
        }
        return mapList;
    }
}
