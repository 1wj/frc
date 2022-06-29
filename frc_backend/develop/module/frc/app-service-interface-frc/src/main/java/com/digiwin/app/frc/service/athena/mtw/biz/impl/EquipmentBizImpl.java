package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.EquipmentBiz;
import com.digiwin.app.frc.service.athena.mtw.common.constants.EquipmentConstant;
import com.digiwin.app.frc.service.athena.mtw.common.enums.EffectiveEnum;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.EquipmentEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.model.EquipmentModel;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.EquipmentVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.EquipmentMapper;
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
 * @Date: 2021/11/12 14:27
 * @Version 1.0
 * @Description 生产线/设备信息处理Biz-impl
 */
@Service
public class EquipmentBizImpl implements EquipmentBiz {

    @Autowired
    private EquipmentMapper equipmentMapper;




    @Override
    public List<JSONObject> addEquipment(JSONArray dataContent) throws DWArgumentException, IOException {
        //添加生产线、设备信息
        CheckFieldValueUtil.validateModels(dataContent,new EquipmentModel());
        //对必传参数进行校验 并进行封装成entity
        List<EquipmentEntity> entities = checkAndHandleData(dataContent, TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName());
        int result = equipmentMapper.addEquipmentInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }


    @Override
    public boolean deleteEquipment(JSONArray dataContent) {
        //删除生产线、设备信息
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(EquipmentConstant.WORKSTATION_KEY_ID);
            oidList.add(oid);
        }
        int result = equipmentMapper.deleteEquipmentInfo(oidList);
        return result > 0;
    }


    @Override
    public boolean updateEquipment(JSONArray dataContent) throws DWArgumentException, IOException {
        //修改生产线、设备信息
        CheckFieldValueUtil.validateModels(dataContent,new EquipmentModel());
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<EquipmentEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            EquipmentEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = equipmentMapper.updateBatch(entities);
        return result > 0;
    }


    @Override
    public List<JSONObject> getEquipment(JSONArray dataContent) throws JsonProcessingException {
        //获取生产线、设备信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<EquipmentEntity> entities;
        if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
            entities = equipmentMapper.getEquipmentInfo(tenantSid, null, null,null);
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
     * @return List<JSONObject>
     */
    private List<JSONObject> convertData(List<EquipmentEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (EquipmentEntity entity : entities) {
            EquipmentVo vo = new EquipmentVo();
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
     * @return List<EquipmentEntity> 封装后实体类的集合
     */
    private List<EquipmentEntity> queryByCondition(JSONArray dataContent, Long tenantSid) {
        List<EquipmentEntity> entities;
        //平台查询只支持传一笔数据 规格使用数组传参
        JSONObject jsonObject = dataContent.getJSONObject(0);
        entities = equipmentMapper.getEquipmentInfo(
                tenantSid,
                jsonObject.getString(EquipmentConstant.WORKSTATION_ID),
                jsonObject.getString(EquipmentConstant.WORKSTATION_NAME),
                jsonObject.getString(EquipmentConstant.MANAGE_STATUS));
        return entities;
    }



    /**
     * 对必传参数进行校验 并进行封装成entity(修改)
     *
     * @param dataContent 解析后数据
     * @param updateName  修改人姓名
     * @param i           循环遍历的索引值
     * @return EquipmentEntity 封装后的实体类
     * @throws DWArgumentException
     */
    private EquipmentEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException, IOException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(EquipmentConstant.WORKSTATION_KEY_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("workStationKeyId",
                    MultilingualismUtil.getLanguage("notExist"));
        }
        //对生产线id进行校验 只能数字、字母和短横线组成
        if(!CheckFieldValueUtil.checkTargetId(jsonObject.getString(EquipmentConstant.WORKSTATION_ID))){
            throw new DWArgumentException("workStationId",MultilingualismUtil.getLanguage("IdRules"));
        }
        EquipmentEntity entity = new EquipmentEntity();
        EquipmentModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(),EquipmentModel.class);
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
     * @return List<EquipmentEntity> 封装后实体类集合
     * @throws DWArgumentException
     */
    private List<EquipmentEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException, IOException {
        List<EquipmentEntity> entities = new ArrayList<>();
        //获取所有生产线、设备编号信息
        List<String> workStationIds = equipmentMapper.queryAllWorkStationIds(tenantSid);
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if (!jsonObject.containsKey(EquipmentConstant.WORKSTATION_ID) || StringUtils.isEmpty(jsonObject.getString(EquipmentConstant.WORKSTATION_ID))) {
                throw new DWArgumentException("workStationId", MultilingualismUtil.getLanguage("notExist"));
            }
            if(CollUtil.isNotEmpty(workStationIds) && workStationIds.contains(jsonObject.getString(EquipmentConstant.WORKSTATION_ID))){
                throw new DWArgumentException("workStationId", MultilingualismUtil.getLanguage("productionLineId"));
            }
            //对生产线id进行校验 只能数字、字母和短横线组成
            if(!CheckFieldValueUtil.checkTargetId(jsonObject.getString(EquipmentConstant.WORKSTATION_ID))){
                throw new DWArgumentException("workStationId",MultilingualismUtil.getLanguage("IdRules"));
            }
            if (!jsonObject.containsKey(EquipmentConstant.WORKSTATION_NAME) || StringUtils.isEmpty(jsonObject.getString(EquipmentConstant.WORKSTATION_NAME))) {
                throw new DWArgumentException("workStationName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(EquipmentConstant.MANAGE_STATUS) || StringUtils.isEmpty(jsonObject.getString(EquipmentConstant.MANAGE_STATUS))) {
                throw new DWArgumentException("manageStatus", MultilingualismUtil.getLanguage("notExist"));
            }
            String manageStatus = jsonObject.getString(EquipmentConstant.MANAGE_STATUS);
            if(!manageStatus.equals(EffectiveEnum.EFFECTIVE.getCode()) && !manageStatus.equals(EffectiveEnum.INVALID.getCode())){
                throw new DWArgumentException("manageStatus",MultilingualismUtil.getLanguage("parameterError"));
            }
            EquipmentModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(),EquipmentModel.class);
            EquipmentEntity entity = new EquipmentEntity();
            BeanUtils.copyProperties(model,entity);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantSid);
            entity.setCreateTime(new Date());
            entity.setCreateName(userName);
            entities.add(entity);
        }
        return entities;
    }

}
