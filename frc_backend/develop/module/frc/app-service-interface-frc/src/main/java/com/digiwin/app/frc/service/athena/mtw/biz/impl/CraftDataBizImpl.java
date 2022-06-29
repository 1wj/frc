package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.CraftDataBiz;
import com.digiwin.app.frc.service.athena.mtw.common.constants.CraftDataConstant;
import com.digiwin.app.frc.service.athena.mtw.common.enums.EffectiveEnum;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.CraftDataEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.model.CraftDataModel;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.CraftDataVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.CraftDataMapper;
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
 * @Date: 2021/11/16 13:54
 * @Version 1.0
 * @Description  工艺信息处理Biz-impl
 */
@Service
public class CraftDataBizImpl implements CraftDataBiz {

    @Autowired
    private CraftDataMapper craftDataMapper;

    @Override
    public List<JSONObject> addCraftData(JSONArray dataContent) throws IOException, DWArgumentException {
        //添加工艺信息
        CheckFieldValueUtil.validateModels(dataContent,new CraftDataModel());
        //对必传参数进行校验 并进行封装成entity
        List<CraftDataEntity> entities = checkAndHandleData(dataContent, TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName());
        int result = craftDataMapper.addCraftDataInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }


    @Override
    public boolean deleteCraftData(JSONArray dataContent) {
       //删除工艺信息
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(CraftDataConstant.OP_ID);
            oidList.add(oid);
        }
        int result = craftDataMapper.deleteCraftDataInfo(oidList);
        return result > 0;
    }

    @Override
    public boolean updateCraftData(JSONArray dataContent) throws IOException, DWArgumentException {
        //更新工艺信息
        CheckFieldValueUtil.validateModels(dataContent,new CraftDataModel());
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<CraftDataEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            CraftDataEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = craftDataMapper.updateBatch(entities);
        return result > 0;
    }


    @Override
    public List<JSONObject> getCraftData(JSONArray dataContent) throws JsonProcessingException {
        //获取工艺信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<CraftDataEntity> entities;
        if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
            entities = craftDataMapper.getCraftDataInfo(tenantSid, null, null,null);
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
     * @return List<CraftDataEntity> 封装后实体类的集合
     */
    private List<CraftDataEntity> queryByCondition(JSONArray dataContent, Long tenantSid) {
        List<CraftDataEntity> entities;
        //平台查询只支持传一笔数据 规格使用数组传参
        JSONObject jsonObject = dataContent.getJSONObject(0);
        entities = craftDataMapper.getCraftDataInfo(
                tenantSid,
                jsonObject.getString(CraftDataConstant.OP_NO),
                jsonObject.getString(CraftDataConstant.OP_NAME),
                jsonObject.getString(CraftDataConstant.MANAGE_STATUS));
        return entities;
    }


    /**
     * 对必传参数进行校验 并进行封装成entity(修改)
     *
     * @param dataContent 解析后数据
     * @param updateName  修改人姓名
     * @param i           循环遍历的索引值
     * @return CraftDataEntity 封装后的实体类
     * @throws DWArgumentException
     */
    private CraftDataEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException, IOException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(CraftDataConstant.OP_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("opId",
                    MultilingualismUtil.getLanguage("notExist"));
        }
        //对编号进行校验  只能是字母、数字、短横线组合
        if(!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(CraftDataConstant.OP_NO))){
            throw new DWArgumentException("opNo",MultilingualismUtil.getLanguage("NumberRules"));
        }
        CraftDataEntity entity = new CraftDataEntity();
        CraftDataModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), CraftDataModel.class);
        BeanUtils.copyProperties(model,entity);
        entity.setUpdateName(updateName);
        entity.setUpdateTime(new Date());
        entity.setTenantSid(tenantSid);
        return entity;
    }


    /**
     * 将实体类集合转成前端要求的格式集合
     *
     * @param entities 问题分类实体集合
     * @return
     */
    private List<JSONObject> convertData(List<CraftDataEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (CraftDataEntity entity : entities) {
            CraftDataVo vo = new CraftDataVo();
            BeanUtils.copyProperties(entity, vo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            mapList.add(jsonObject);
        }
        return mapList;
    }




    /**
     * 对必传参数进行校验 并进行封装成List<Entity>(新增)
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @param userName    用户姓名
     * @return List<CraftDataEntity> 封装后实体类集合
     * @throws DWArgumentException
     */
    private List<CraftDataEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException, IOException {
        List<CraftDataEntity> entities = new ArrayList<>();
        //查询所有工艺编号信息
        List<String> opNos = craftDataMapper.queryAllOpNos(tenantSid);
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if (!jsonObject.containsKey(CraftDataConstant.OP_NO) || StringUtils.isEmpty(jsonObject.getString(CraftDataConstant.OP_NO))) {
                throw new DWArgumentException("opNo", MultilingualismUtil.getLanguage("notExist"));
            }
            //对编号进行校验  只能是字母、数字、短横线组合
            if(!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(CraftDataConstant.OP_NO))){
              throw new DWArgumentException("opNo",MultilingualismUtil.getLanguage("NumberRules"));
            }
            if(CollUtil.isNotEmpty(opNos) && opNos.contains(jsonObject.getString(CraftDataConstant.OP_NO))){
                throw new DWArgumentException("opNo", MultilingualismUtil.getLanguage("NoAlreadyExist"));
            }
            if (!jsonObject.containsKey(CraftDataConstant.OP_NAME) || StringUtils.isEmpty(jsonObject.getString(CraftDataConstant.OP_NAME))) {
                throw new DWArgumentException("opName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(CraftDataConstant.MANAGE_STATUS) || StringUtils.isEmpty(jsonObject.getString(CraftDataConstant.MANAGE_STATUS))) {
                throw new DWArgumentException("manageStatus", MultilingualismUtil.getLanguage("notExist"));
            }
            String manageStatus = jsonObject.getString(CraftDataConstant.MANAGE_STATUS);
            if(!manageStatus.equals(EffectiveEnum.EFFECTIVE.getCode()) && !manageStatus.equals(EffectiveEnum.INVALID.getCode())){
                throw new DWArgumentException("manageStatus",MultilingualismUtil.getLanguage("parameterError"));
            }
            CraftDataModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), CraftDataModel.class);
            CraftDataEntity entity = new CraftDataEntity();
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
