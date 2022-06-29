package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionIdentifyConfigBiz;
import com.digiwin.app.frc.service.athena.mtw.common.constants.QuestionIdentifyConfigConstant;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionIdentifyConfigEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.model.QuestionIdentifyConfigModel;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionIdentifyConfigVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionIdentifyConfigMapper;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.mtw.CheckFieldValueUtil;
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

/**
 * @Author: xieps
 * @Date: 2022/1/4 11:47
 * @Version 1.0
 * @Description 问题识别配置处理Biz-impl
 */
@Service
public class QuestionIdentifyConfigBizImpl implements QuestionIdentifyConfigBiz {

    @Autowired
    private QuestionIdentifyConfigMapper questionIdentifyConfigMapper;


    @Override
    public List<JSONObject> addQuestionIdentifyConfig(JSONArray dataContent) throws DWArgumentException, IOException {
        //添加问题识别配置信息
        CheckFieldValueUtil.validateModels(dataContent, new QuestionIdentifyConfigModel());
        //对必传参数进行校验 并进行封装成entity
        List<QuestionIdentifyConfigEntity> entities = checkAndHandleData(dataContent, TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName());
        int result = questionIdentifyConfigMapper.addQuestionIdentifyConfigInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }

    @Override
    public boolean deleteQuestionIdentifyConfig(JSONArray dataContent) {
        //删除问题识别配置信息
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(QuestionIdentifyConfigConstant.IDENTIFY_CONFIG_ID);
            oidList.add(oid);
        }
        int result = questionIdentifyConfigMapper.deleteQuestionIdentifyConfigInfo(oidList);
        return result > 0;
    }

    @Override
    public boolean updateQuestionIdentifyConfig(JSONArray dataContent) throws DWArgumentException, IOException {
        //更新问题识别配置信息
        CheckFieldValueUtil.validateModels(dataContent, new QuestionIdentifyConfigModel());
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionIdentifyConfigEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            QuestionIdentifyConfigEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = questionIdentifyConfigMapper.updateBatch(entities);
        return result > 0;
    }

    @Override
    public List<JSONObject> getQuestionIdentifyConfig(JSONObject dataContent) throws JsonProcessingException {
        //获取问题识别配置信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionIdentifyConfigEntity> entities;
        if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
            entities = questionIdentifyConfigMapper.getQuestionIdentifyConfigInfo(tenantSid, null, null, null);
        } else {
            entities = checkAndHandleData(dataContent, tenantSid);
        }
        //entity转成平台规范带有_格式的前端数据
        return convertData(entities);
    }


    /**
     * 将实体类集合转成前端要求的格式集合
     *
     * @param entities 问题分类实体集合
     * @return List<JSONObject>
     */
    private List<JSONObject> convertData(List<QuestionIdentifyConfigEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (QuestionIdentifyConfigEntity entity : entities) {
            QuestionIdentifyConfigVo vo = new QuestionIdentifyConfigVo();
            BeanUtils.copyProperties(entity, vo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            mapList.add(jsonObject);
        }
        return mapList;
    }


    /**
     * 对必传参数进行处理 并进行带条件查询
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @return List<QuestionIdentifyConfigEntity> 封装后实体类的集合
     */
    private List<QuestionIdentifyConfigEntity> checkAndHandleData(JSONObject dataContent, Long tenantSid) {
        List<QuestionIdentifyConfigEntity> entities;
        entities = questionIdentifyConfigMapper.getQuestionIdentifyConfigInfo(
                tenantSid,
                dataContent.getString(QuestionIdentifyConfigConstant.CLASSIFICATION_NO),
                dataContent.getString(QuestionIdentifyConfigConstant.CLASSIFICATION_NAME),
                dataContent.getString(QuestionIdentifyConfigConstant.LIABLE_PERSON_NAME));
        return entities;
    }


    /**
     * 对必传参数进行校验 并进行封装成entity
     *
     * @param dataContent 解析后数据
     * @param updateName  修改人姓名
     * @param i           循环遍历的索引值
     * @return QuestionIdentifyConfigEntity 封装后的实体类
     * @throws DWArgumentException
     */
    private QuestionIdentifyConfigEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException, IOException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(QuestionIdentifyConfigConstant.IDENTIFY_CONFIG_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("identifyConfigId", MultilingualismUtil.getLanguage("notExist"));
        }
        //对编号进行校验  只能数字、字母和短横线组成
        if (!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(QuestionIdentifyConfigConstant.CLASSIFICATION_NO))) {
            throw new DWArgumentException("classificationNo", MultilingualismUtil.getLanguage("NumberRules"));
        }
        QuestionIdentifyConfigEntity entity = new QuestionIdentifyConfigEntity();
        QuestionIdentifyConfigModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), QuestionIdentifyConfigModel.class);
        BeanUtils.copyProperties(model, entity);
        entity.setUpdateName(updateName);
        entity.setUpdateTime(new Date());
        entity.setTenantSid(tenantSid);
        return entity;
    }


    /**
     * 对必传参数进行校验 并进行封装成entity
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @param userName    用户姓名
     * @return List<QuestionIdentifyConfigEntity> 封装后实体类集合
     * @throws DWArgumentException IOException
     */
    private List<QuestionIdentifyConfigEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException,  IOException {
        List<QuestionIdentifyConfigEntity> entities = new ArrayList<>();
        //查询所有问题分类编号信息
        List<String> classificationNos = questionIdentifyConfigMapper.queryAllClassificationNos(tenantSid);
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if (!jsonObject.containsKey(QuestionIdentifyConfigConstant.CLASSIFICATION_NO) || StringUtils.isEmpty(jsonObject.getString(QuestionIdentifyConfigConstant.CLASSIFICATION_NO))) {
                throw new DWArgumentException("classificationNo", MultilingualismUtil.getLanguage("notExist"));
            }
            if (CollUtil.isNotEmpty(classificationNos) && classificationNos.contains(jsonObject.getString(QuestionIdentifyConfigConstant.CLASSIFICATION_NO))) {
                throw new DWArgumentException("classificationNo", MultilingualismUtil.getLanguage("NoAlreadyExist"));
            }
            //对编号进行校验  只能数字、字母和短横线组成
            if (!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(QuestionIdentifyConfigConstant.CLASSIFICATION_NO))) {
                throw new DWArgumentException("classificationNo", MultilingualismUtil.getLanguage("NumberRules"));
            }
            if (!jsonObject.containsKey(QuestionIdentifyConfigConstant.CLASSIFICATION_NAME) || StringUtils.isEmpty(jsonObject.getString(QuestionIdentifyConfigConstant.CLASSIFICATION_NAME))) {
                throw new DWArgumentException("classificationName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(QuestionIdentifyConfigConstant.LIABLE_PERSON_ID) || StringUtils.isEmpty(jsonObject.getString(QuestionIdentifyConfigConstant.LIABLE_PERSON_ID))) {
                throw new DWArgumentException("liablePersonId", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(QuestionIdentifyConfigConstant.LIABLE_PERSON_NAME) || StringUtils.isEmpty(jsonObject.getString(QuestionIdentifyConfigConstant.LIABLE_PERSON_NAME))) {
                throw new DWArgumentException("liablePersonName", MultilingualismUtil.getLanguage("notExist"));
            }

            QuestionIdentifyConfigModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), QuestionIdentifyConfigModel.class);
            QuestionIdentifyConfigEntity entity = new QuestionIdentifyConfigEntity();
            BeanUtils.copyProperties(model, entity);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantSid);
            entity.setCreateTime(new Date());
            entity.setCreateName(userName);
            entities.add(entity);
        }
        return entities;
    }


}
