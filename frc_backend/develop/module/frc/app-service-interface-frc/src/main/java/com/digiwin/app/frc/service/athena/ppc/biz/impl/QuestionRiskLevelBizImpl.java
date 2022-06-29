package com.digiwin.app.frc.service.athena.ppc.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.common.enums.EffectiveEnum;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionRiskLevelBiz;

import com.digiwin.app.frc.service.athena.ppc.constants.QuestionRiskLevelConstant;
import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionRiskLevelEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.model.QuestionRiskLevelModel;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.QuestionRiskLevelVo;
import com.digiwin.app.frc.service.athena.ppc.mapper.QuestionRiskLevelMapper;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
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
 * @Date: 2022/2/10 11:44
 * @Version 1.0
 * @Description  风险等级信息处理Biz-impl
 */
@Service
public class QuestionRiskLevelBizImpl implements QuestionRiskLevelBiz {

    @Autowired
    private QuestionRiskLevelMapper questionRiskLevelMapper;

    @Override
    public List<JSONObject> addQuestionRiskLevel(JSONArray dataContent) throws IOException, DWArgumentException {
        //添加风险等级信息
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        Long tenantSid = (Long) profile.get("tenantSid");
        String userName = (String) profile.get("userName");
        //对必传参数进行校验 并进行封装成entity
        List<QuestionRiskLevelEntity> entities = checkAndHandleData(dataContent, tenantSid, userName);
        int result = questionRiskLevelMapper.addQuestionRiskLevelInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }

    @Override
    public boolean deleteQuestionRiskLevel(JSONArray dataContent) {
        //删除风险等级信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(QuestionRiskLevelConstant.RISK_LEVEL_ID);
            oidList.add(oid);
        }
        int result = questionRiskLevelMapper.deleteQuestionRiskLevelInfo(oidList,tenantSid);
        return result > 0;
    }

    @Override
    public boolean updateQuestionRiskLevel(JSONArray dataContent) throws DWArgumentException, IOException {
        //更新风险等级信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        List<QuestionRiskLevelEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            QuestionRiskLevelEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = questionRiskLevelMapper.updateBatch(entities);
        return result > 0;
    }

    @Override
    public List<JSONObject> getQuestionRiskLevel(JSONArray dataContent) throws JsonProcessingException {
        //获取工艺信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionRiskLevelEntity> entities;
        if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
            entities = questionRiskLevelMapper.getQuestionRiskLevelInfo(tenantSid, null, null,null,null,null);
        } else {
            entities = queryByCondition(dataContent, tenantSid);
        }
        //entity转成平台规范带有_格式的前端数据
        return convertData(entities);
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
    private List<QuestionRiskLevelEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException, IOException {
        List<QuestionRiskLevelEntity> entities = new ArrayList<>();
        //查询所有风险等级编号信息
        List<String> riskLevelNos = questionRiskLevelMapper.queryAllRiskLevelNos(tenantSid);
        //查询所有风险等级名称信息
        List<String> riskLevelNames = questionRiskLevelMapper.queryAllRiskLevelNames(tenantSid);
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if (StringUtils.isEmpty(jsonObject.getString(QuestionRiskLevelConstant.RISK_LEVEL_NO))) {
                throw new DWArgumentException("riskLevelNo", MultilingualismUtil.getLanguage("notExist"));
            }
            if(CollUtil.isNotEmpty(riskLevelNos) && riskLevelNos.contains(jsonObject.getString(QuestionRiskLevelConstant.RISK_LEVEL_NO))){
                throw new DWArgumentException("riskLevelNo", MultilingualismUtil.getLanguage("NoAlreadyExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionRiskLevelConstant.RISK_LEVEL_NAME))) {
                throw new DWArgumentException("riskLevelName", MultilingualismUtil.getLanguage("notExist"));
            }
            if(CollUtil.isNotEmpty(riskLevelNames) && riskLevelNames.contains(jsonObject.getString(QuestionRiskLevelConstant.RISK_LEVEL_NAME))){
                throw new DWArgumentException("riskLevelName", MultilingualismUtil.getLanguage("NameAlreadyExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionRiskLevelConstant.MANAGE_STATUS))) {
                throw new DWArgumentException("manageStatus", MultilingualismUtil.getLanguage("notExist"));
            }
            String manageStatus = jsonObject.getString(QuestionRiskLevelConstant.MANAGE_STATUS);
            if(!manageStatus.equals(EffectiveEnum.EFFECTIVE.getCode()) && !manageStatus.equals(EffectiveEnum.INVALID.getCode())){
                throw new DWArgumentException("manageStatus",MultilingualismUtil.getLanguage("parameterError"));
            }
            if (jsonObject.containsKey(QuestionRiskLevelConstant.IMPORTANT) && jsonObject.getInteger(QuestionRiskLevelConstant.IMPORTANT) != 1 && jsonObject.getInteger(QuestionRiskLevelConstant.IMPORTANT) != 2) {
                throw new DWArgumentException("important", MultilingualismUtil.getLanguage("parameterError"));
            }
            if (jsonObject.containsKey(QuestionRiskLevelConstant.URGENCY) && jsonObject.getInteger(QuestionRiskLevelConstant.URGENCY) != 1 && jsonObject.getInteger(QuestionRiskLevelConstant.URGENCY) != 2) {
                throw new DWArgumentException("urgency", MultilingualismUtil.getLanguage("parameterError"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionRiskLevelConstant.IS_EDIT))) {
                throw new DWArgumentException("isEdit", MultilingualismUtil.getLanguage("notExist"));
            }
            String isEdit = jsonObject.getString(QuestionRiskLevelConstant.IS_EDIT);
            if (!"Y".equals(isEdit) && !"N".equals(isEdit)) {
                throw new DWArgumentException("isEdit", MultilingualismUtil.getLanguage("parameterError"));
            }
            if(jsonObject.containsKey(QuestionRiskLevelConstant.IS_UPLOAD)){
                String isUpload = jsonObject.getString(QuestionRiskLevelConstant.IS_UPLOAD);
                if (!"1".equals(isUpload) && !"0".equals(isUpload)) {
                    throw new DWArgumentException("isUpload", MultilingualismUtil.getLanguage("parameterError"));
                }
            }else {
                jsonObject.put(QuestionRiskLevelConstant.IS_UPLOAD,"N");
            }
            QuestionRiskLevelModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), QuestionRiskLevelModel.class);
            QuestionRiskLevelEntity entity = new QuestionRiskLevelEntity();
            BeanUtils.copyProperties(model, entity);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantSid);
            entity.setCreateTime(new Date());
            entity.setCreateName(userName);
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
    private List<JSONObject> convertData(List<QuestionRiskLevelEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (QuestionRiskLevelEntity entity : entities) {
            QuestionRiskLevelVo vo = new QuestionRiskLevelVo();
            BeanUtils.copyProperties(entity, vo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            mapList.add(jsonObject);
        }
        return mapList;
    }


    /**
     * 对必传参数进行校验 并进行封装成entity(修改)
     *
     * @param dataContent 解析后数据
     * @param updateName  修改人姓名
     * @param i           循环遍历的索引值
     * @return QuestionRiskLevelEntity 封装后的实体类
     * @throws DWArgumentException
     */
    private QuestionRiskLevelEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException, IOException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(QuestionRiskLevelConstant.RISK_LEVEL_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("riskLevelId",
                    MultilingualismUtil.getLanguage("notExist"));
        }
        QuestionRiskLevelEntity entity = new QuestionRiskLevelEntity();
        QuestionRiskLevelModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), QuestionRiskLevelModel.class);
        BeanUtils.copyProperties(model,entity);
        entity.setUpdateName(updateName);
        entity.setUpdateTime(new Date());
        entity.setTenantSid(tenantSid);
        return entity;
    }



    /**
     * 对必传参数进行处理 并进行带条件查询
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @return List<QuestionRiskLevelEntity> 封装后实体类的集合
     */
    private List<QuestionRiskLevelEntity> queryByCondition(JSONArray dataContent, Long tenantSid) {
        List<QuestionRiskLevelEntity> entities;
        //平台查询只支持传一笔数据 规格使用数组传参
        JSONObject jsonObject = dataContent.getJSONObject(0);
        entities = questionRiskLevelMapper.getQuestionRiskLevelInfo(
                tenantSid,
                jsonObject.getString(QuestionRiskLevelConstant.RISK_LEVEL_NO),
                jsonObject.getString(QuestionRiskLevelConstant.RISK_LEVEL_NAME),
                jsonObject.getString(QuestionRiskLevelConstant.IS_EDIT),
                jsonObject.getString(QuestionRiskLevelConstant.MANAGE_STATUS),
                jsonObject.getString(QuestionRiskLevelConstant.IS_UPLOAD));
        return entities;
    }

}
