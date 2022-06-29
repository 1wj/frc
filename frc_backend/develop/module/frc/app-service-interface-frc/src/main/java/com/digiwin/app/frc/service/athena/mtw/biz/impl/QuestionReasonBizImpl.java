package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionReasonBiz;
import com.digiwin.app.frc.service.athena.mtw.common.constants.QuestionReasonConstant;
import com.digiwin.app.frc.service.athena.mtw.common.enums.EffectiveEnum;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionReasonEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.model.QuestionReasonModel;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionReasonVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionReasonMapper;
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
 * @Date: 2021/11/16 17:08
 * @Version 1.0
 * @Description 处理原因代码Biz-impl
 */
@Service
public class QuestionReasonBizImpl implements QuestionReasonBiz {

    @Autowired
    private QuestionReasonMapper questionReasonMapper;

    @Override
    public List<JSONObject> addQuestionReason(JSONArray dataContent) throws IOException, DWArgumentException {
        //添加原因代码
        CheckFieldValueUtil.validateModels(dataContent,new QuestionReasonModel());
        //对必传参数进行校验 并进行封装成entity
        List<QuestionReasonEntity> entities = checkAndHandleData(dataContent, TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName());
        int result = questionReasonMapper.addQuestionReasonInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }

    @Override
    public boolean deleteQuestionReason(JSONArray dataContent) {
        //删除原因代码信息
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(QuestionReasonConstant.REASON_ID);
            oidList.add(oid);
        }
        int result = questionReasonMapper.deleteQuestionReasonInfo(oidList);
        return result > 0;
    }


    @Override
    public boolean updateQuestionReason(JSONArray dataContent) throws IOException, DWArgumentException {
        //更新原因代码
        CheckFieldValueUtil.validateModels(dataContent,new QuestionReasonModel());
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionReasonEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            QuestionReasonEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = questionReasonMapper.updateBatch(entities);
        return result > 0;
    }

    @Override
    public List<JSONObject> getQuestionReason(JSONArray dataContent) throws JsonProcessingException {
        //查询原因代码
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionReasonEntity> entities;
        if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
            entities = questionReasonMapper.getQuestionReasonInfo(tenantSid, null, null, null,null,null, null);
        } else {
            entities = queryByCondition(dataContent, tenantSid);
        }
        //entity转成平台规范带有_格式的前端数据
        return convertData(entities);
    }


    /**
     * 对必传参数进行处理 并进行带条件查询
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @return List<QuestionReasonEntity> 封装后实体类的集合
     */
    private List<QuestionReasonEntity> queryByCondition(JSONArray dataContent, Long tenantSid) {
        List<QuestionReasonEntity> entities;
        //平台查询只支持传一笔数据 规格使用数组传参
        JSONObject jsonObject = dataContent.getJSONObject(0);
        entities = questionReasonMapper.getQuestionReasonInfo(
                tenantSid,
                jsonObject.getString(QuestionReasonConstant.CATEGORY_NO),
                jsonObject.getString(QuestionReasonConstant.CATEGORY_NAME),
                jsonObject.getString(QuestionReasonConstant.REASON_CODE),
                jsonObject.getString(QuestionReasonConstant.REASON_NAME),
                jsonObject.getString(QuestionReasonConstant.MANAGE_STATUS),
                jsonObject.getString(QuestionReasonConstant.REMARKS));
        return entities;
    }


    /**
     * 将实体类集合转成前端要求的格式集合
     *
     * @param entities 问题分类实体集合
     * @return List<JSONObject>
     */
    private List<JSONObject> convertData(List<QuestionReasonEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (QuestionReasonEntity entity : entities) {
            QuestionReasonVo vo = new QuestionReasonVo();
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
     * @return QuestionReasonEntity 封装后的实体类
     * @throws DWArgumentException
     */
    private QuestionReasonEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException, IOException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(QuestionReasonConstant.REASON_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("reasonId",
                    MultilingualismUtil.getLanguage("notExist"));
        }
        //对编号进行校验  只能数字、字母和短横线组成
        if(!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(QuestionReasonConstant.CATEGORY_NO))){
            throw new DWArgumentException("categoryNo",MultilingualismUtil.getLanguage("NumberRules"));
        }
        QuestionReasonEntity entity = new QuestionReasonEntity();
        QuestionReasonModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), QuestionReasonModel.class);
        BeanUtils.copyProperties(model,entity);
        entity.setUpdateName(updateName);
        entity.setUpdateTime(new Date());
        entity.setTenantSid(tenantSid);
        return entity;
    }




    /**
     * 对必传参数进行校验 并进行封装成List<Entity>(新增)
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @param userName    用户姓名
     * @return List<QuestionReasonEntity> 封装后实体类集合
     * @throws DWArgumentException
     */
    private List<QuestionReasonEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException, IOException {
        List<QuestionReasonEntity> entities = new ArrayList<>();
        //查询所有类别编号信息
        List<String> categoryNos = questionReasonMapper.queryAllCategoryNos(tenantSid);
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if (!jsonObject.containsKey(QuestionReasonConstant.CATEGORY_NO) || StringUtils.isEmpty(jsonObject.getString(QuestionReasonConstant.CATEGORY_NO))) {
                throw new DWArgumentException("categoryNo", MultilingualismUtil.getLanguage("notExist"));
            }
            if(CollUtil.isNotEmpty(categoryNos) && categoryNos.contains(jsonObject.getString(QuestionReasonConstant.CATEGORY_NO))){
                throw new DWArgumentException("categoryNo", MultilingualismUtil.getLanguage("NoAlreadyExist"));
            }
            //对编号进行校验  只能数字、字母和短横线组成
            if(!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(QuestionReasonConstant.CATEGORY_NO))){
                throw new DWArgumentException("categoryNo",MultilingualismUtil.getLanguage("NumberRules"));
            }
            if (!jsonObject.containsKey(QuestionReasonConstant.CATEGORY_NAME) || StringUtils.isEmpty(jsonObject.getString(QuestionReasonConstant.CATEGORY_NAME))) {
                throw new DWArgumentException("categoryName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(QuestionReasonConstant.REASON_CODE) || StringUtils.isEmpty(jsonObject.getString(QuestionReasonConstant.REASON_CODE))) {
                throw new DWArgumentException("reasonCode", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(QuestionReasonConstant.REASON_NAME) || StringUtils.isEmpty(jsonObject.getString(QuestionReasonConstant.REASON_NAME))) {
                throw new DWArgumentException("reasonName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(QuestionReasonConstant.MANAGE_STATUS) || StringUtils.isEmpty(jsonObject.getString(QuestionReasonConstant.MANAGE_STATUS))) {
                throw new DWArgumentException("manageStatus", MultilingualismUtil.getLanguage("notExist"));
            }
            String manageStatus = jsonObject.getString(QuestionReasonConstant.MANAGE_STATUS);
            if(!manageStatus.equals(EffectiveEnum.EFFECTIVE.getCode()) && !manageStatus.equals(EffectiveEnum.INVALID.getCode())){
                throw new DWArgumentException("manageStatus", MultilingualismUtil.getLanguage("parameterError"));
            }
            QuestionReasonModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), QuestionReasonModel.class);
            QuestionReasonEntity entity = new QuestionReasonEntity();
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
