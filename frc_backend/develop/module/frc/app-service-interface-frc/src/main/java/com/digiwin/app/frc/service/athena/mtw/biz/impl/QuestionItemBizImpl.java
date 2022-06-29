package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionItemBiz;
import com.digiwin.app.frc.service.athena.mtw.common.constants.QuestionItemConstant;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionItemEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.model.QuestionItemModel;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionItemVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionItemMapper;
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
import java.util.*;

/**
 * @Author: xieps
 * @Date: 2021/11/11 11:26
 * @Version 1.0
 * @Description 物料信息处理Biz-impl
 */
@Service
public class QuestionItemBizImpl implements QuestionItemBiz {

    @Autowired
    private QuestionItemMapper questionItemMapper;

    @Override
    public List<JSONObject> addQuestionItem(JSONArray dataContent) throws DWArgumentException, IOException {
       //添加物料信息
        CheckFieldValueUtil.validateModels(dataContent,new QuestionItemModel());
        //对必传参数进行校验 并进行封装成entity
        List<QuestionItemEntity> entities = checkAndHandleData(dataContent, TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName());
        int result = questionItemMapper.addQuestionItemInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;

    }

    @Override
    public boolean deleteQuestionItem(JSONArray dataContent) {
        //删除物料信息
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(QuestionItemConstant.ITEM_ID);
            oidList.add(oid);
        }
        int result = questionItemMapper.deleteQuestionItemInfo(oidList);
        return result > 0;

    }


    @Override
    public boolean updateQuestionItem(JSONArray dataContent) throws DWArgumentException, IOException {
        //更新物料信息
        CheckFieldValueUtil.validateModels(dataContent,new QuestionItemModel());
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionItemEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            QuestionItemEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = questionItemMapper.updateBatch(entities);
        return result > 0;
    }


    @Override
    public List<JSONObject> getQuestionItem(JSONArray dataContent) throws JsonProcessingException {
        //获取物料信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionItemEntity> entities;
        if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
            entities = questionItemMapper.getQuestionItemInfo(tenantSid, null, null);
        } else {
            entities = queryByCondition(dataContent, tenantSid);
        }
        //entity转成平台规范带有_格式的前端数据
        return convertData(entities);

    }

    /**
     * 将实体类集合转成前端要求的格式集合
     *
     * @param entities 问题分类实体集合
     * @return
     */
    private List<JSONObject> convertData(List<QuestionItemEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (QuestionItemEntity entity : entities) {
            QuestionItemVo vo = new QuestionItemVo();
            BeanUtils.copyProperties(entity,vo);
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
     * @return List<QuestionItemEntity> 封装后实体类的集合
     */
    private List<QuestionItemEntity> queryByCondition(JSONArray dataContent, Long tenantSid) {
        List<QuestionItemEntity> entities;
        JSONObject jsonObject = dataContent.getJSONObject(0);
        entities = questionItemMapper.getQuestionItemInfo(
                tenantSid,
                jsonObject.getString(QuestionItemConstant.ITEM_NO),
                jsonObject.getString(QuestionItemConstant.ITEM_NAME));
        return entities;
    }


    /**
     * 对必传参数进行校验 并进行封装成entity
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @param userName    用户姓名
     * @return List<QuestionItemEntity> 封装后实体类集合
     * @throws DWArgumentException
     */
    private List<QuestionItemEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException, IOException {
        List<QuestionItemEntity> entities = new ArrayList<>();
        //查询所有物料编号信息
        List<String> itemNos = questionItemMapper.queryAllItemNos(tenantSid);
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if (!jsonObject.containsKey(QuestionItemConstant.ITEM_NO) || StringUtils.isEmpty(jsonObject.getString(QuestionItemConstant.ITEM_NO))) {
                throw new DWArgumentException("itemNo", MultilingualismUtil.getLanguage("notExist"));
            }
            if(CollUtil.isNotEmpty(itemNos) && itemNos.contains(jsonObject.getString(QuestionItemConstant.ITEM_NO))){
                throw new DWArgumentException("itemNo", MultilingualismUtil.getLanguage("NoAlreadyExist"));
            }
            //对编号进行校验  只能数字、字母和短横线组成
            if(!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(QuestionItemConstant.ITEM_NO))){
                throw new DWArgumentException("itemNo",MultilingualismUtil.getLanguage("NumberRules"));
            }
            if (!jsonObject.containsKey(QuestionItemConstant.ITEM_NAME) || StringUtils.isEmpty(jsonObject.getString(QuestionItemConstant.ITEM_NAME))) {
                throw new DWArgumentException("itemName", MultilingualismUtil.getLanguage("notExist"));
            }
            QuestionItemModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), QuestionItemModel.class);
            QuestionItemEntity entity = new QuestionItemEntity();
            BeanUtils.copyProperties(model,entity);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantSid);
            entity.setCreateTime(new Date());
            entity.setCreateName(userName);
            entities.add(entity);
        }
        return entities;
    }




    /**
     * 对必传参数进行校验 并进行封装成entity
     *
     * @param dataContent 解析后数据
     * @param updateName  修改人姓名
     * @param i           循环遍历的索引值
     * @return QuestionItemEntity 封装后的实体类
     * @throws DWArgumentException
     */
    private QuestionItemEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException, IOException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(QuestionItemConstant.ITEM_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("itemId",
                    MultilingualismUtil.getLanguage("notExist"));
        }
        //对编号进行校验  只能数字、字母和短横线组成
        if(!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(QuestionItemConstant.ITEM_NO))){
            throw new DWArgumentException("itemNo",MultilingualismUtil.getLanguage("NumberRules"));
        }
        QuestionItemEntity entity = new QuestionItemEntity();
        QuestionItemModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(),QuestionItemModel.class);
        BeanUtils.copyProperties(model,entity);
        entity.setUpdateName(updateName);
        entity.setUpdateTime(new Date());
        entity.setTenantSid(tenantSid);
        return entity;
    }
}
