package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.KeyBoardAuthorityBiz;
import com.digiwin.app.frc.service.athena.mtw.common.constants.KeyBoardAuthorityConstant;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.KeyBoardAuthorityEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.KeyBoardAuthorityVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.KeyBoardAuthorityMapper;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
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
 * @Date: 2021/11/24 10:11
 * @Version 1.0
 * @Description 看板权限配置处理Biz-impl
 */
@Service
public class KeyBoardAuthorityBizImpl implements KeyBoardAuthorityBiz {

    @Autowired
    private KeyBoardAuthorityMapper keyBoardAuthorityMapper;

    @Override
    public List<JSONObject> addKeyBoardAuthority(JSONArray dataContent) throws DWArgumentException, IOException {
        //添加看板权限配置信息
        //对必传参数进行校验 并进行封装成entity
        List<KeyBoardAuthorityEntity> entities = checkAndHandleData(dataContent, TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName());
        int result = keyBoardAuthorityMapper.addKeyBoardAuthorityInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }


    @Override
    public boolean deleteKeyBoardAuthority(JSONArray dataContent) {
        //删除看板权限配置信息
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(KeyBoardAuthorityConstant.KANBAN_PERMISSIONS_ID);
            oidList.add(oid);
        }
        int result = keyBoardAuthorityMapper.deleteKeyBoardAuthorityInfo(oidList);
        return result > 0;
    }


    @Override
    public boolean updateKeyBoardAuthority(JSONArray dataContent) throws IOException, DWArgumentException {
        //修改看板权限配置信息   根据看板模板id先删除  再进行新增操作
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<KeyBoardAuthorityEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            KeyBoardAuthorityEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = keyBoardAuthorityMapper.updateBatch(entities);
        return result > 0;
    }


    @Override
    public List<JSONObject> getKeyBoardAuthority(JSONArray dataContent) throws JsonProcessingException {
        //获取看板权限配置信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<KeyBoardAuthorityEntity> entities;
        if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
            entities = keyBoardAuthorityMapper.getKeyBoardAuthorityInfo(tenantSid, null, null);
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
    private List<KeyBoardAuthorityEntity> queryByCondition(JSONArray dataContent, Long tenantSid) {
        List<KeyBoardAuthorityEntity> entities;
        //平台查询只支持传一笔数据 规格使用数组传参
        JSONObject jsonObject = dataContent.getJSONObject(0);
        entities = keyBoardAuthorityMapper.getKeyBoardAuthorityInfo(
                tenantSid,
                jsonObject.getString(KeyBoardAuthorityConstant.KANBAN_TEMPLATE_NAME),
                jsonObject.getString(KeyBoardAuthorityConstant.DESIGNATION_INSPECTOR));
        return entities;
    }


    /**
     * 对必传参数进行校验 并进行封装成entity(修改)
     *
     * @param dataContent 解析后数据
     * @param updateName  修改人姓名
     * @param i           循环遍历的索引值
     * @return KeyBoardAuthorityEntity 封装后的实体类
     * @throws DWArgumentException
     */
    private KeyBoardAuthorityEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException, IOException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        //对必传参数进行校验
        if (!jsonObject.containsKey(KeyBoardAuthorityConstant.KANBAN_PERMISSIONS_ID) || StringUtils.isEmpty(jsonObject.getString(KeyBoardAuthorityConstant.KANBAN_PERMISSIONS_ID))) {
            throw new DWArgumentException("kanBanPermissionsId", MultilingualismUtil.getLanguage("notExist"));
        }
        KeyBoardAuthorityEntity entity = new KeyBoardAuthorityEntity();
        JSONArray inspectorInfos = jsonObject.getJSONArray("designation_inspector");
        StringBuilder specifyViewers = new StringBuilder();
        if(!CollUtil.isEmpty(inspectorInfos)){
            for (int j = 0; j < inspectorInfos.size(); j++) {
                JSONObject jsonObject1 = inspectorInfos.getJSONObject(j);
                String inspectorId = jsonObject1.getString("inspector_id");
                String inspectorName = jsonObject1.getString("inspector_name");
                specifyViewers.append(inspectorId).append("_").append(inspectorName).append(",");
            }
            String strViewers = specifyViewers.toString().substring(0,specifyViewers.toString().lastIndexOf(','));
            entity.setSpecifyViewer(strViewers);
        }
        // 看板模板id不为null,赋值
        if(!StringUtils.isEmpty(jsonObject.getString(KeyBoardAuthorityConstant.KANBAN_TEMPLATE_ID))){
            entity.setModelOid(jsonObject.getString("kanban_template_id"));
        }
        // 看板模板名称不为null,赋值
        if(!StringUtils.isEmpty(jsonObject.getString(KeyBoardAuthorityConstant.KANBAN_TEMPLATE_NAME))){
            entity.setModelName(jsonObject.getString("kanban_template_name"));
        }
        entity.setOid(jsonObject.getString("kanban_permissions_id"));
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
    private List<JSONObject> convertData(List<KeyBoardAuthorityEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (KeyBoardAuthorityEntity entity : entities) {
            KeyBoardAuthorityVo vo = new KeyBoardAuthorityVo();
            BeanUtils.copyProperties(entity, vo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            JSONArray  designationInspector = new JSONArray();
            String[] specifyViews = entity.getSpecifyViewer().split(",");
            for (String view : specifyViews) {
                String[] info = view.split("_");
                JSONObject object = new JSONObject();
                object.put("inspector_id",info[0]);
                object.put("inspector_name",info[1]);
                designationInspector.add(object);
            }
            jsonObject.put("designation_inspector",designationInspector);
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
     * @return List<KeyBoardAuthorityEntity> 封装后实体类集合
     * @throws DWArgumentException
     */
    private List<KeyBoardAuthorityEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException, IOException {
        List<KeyBoardAuthorityEntity> entities = new ArrayList<>();
        //查询所有的看板模板主键  防止看板模板名称重复
        List<String> templateIds = keyBoardAuthorityMapper.queryAllTemplateIds(tenantSid);
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if (!jsonObject.containsKey(KeyBoardAuthorityConstant.KANBAN_TEMPLATE_ID) || StringUtils.isEmpty(jsonObject.getString(KeyBoardAuthorityConstant.KANBAN_TEMPLATE_ID))) {
                throw new DWArgumentException("kanBanTemplateId", MultilingualismUtil.getLanguage("notExist"));
            }
            if(CollUtil.isNotEmpty(templateIds) && templateIds.contains(jsonObject.getString(KeyBoardAuthorityConstant.KANBAN_TEMPLATE_ID))){
                throw new DWArgumentException( MultilingualismUtil.getLanguage("kanbanTemplateName"), MultilingualismUtil.getLanguage("kanbanTemplateName"));
            }
            if (!jsonObject.containsKey(KeyBoardAuthorityConstant.KANBAN_TEMPLATE_NAME) || StringUtils.isEmpty(jsonObject.getString(KeyBoardAuthorityConstant.KANBAN_TEMPLATE_NAME))) {
                throw new DWArgumentException("kanBanTemplateName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.getJSONArray("designation_inspector").getJSONObject(i).containsKey(KeyBoardAuthorityConstant.INSPECTOR_ID) || StringUtils.isEmpty(jsonObject.getJSONArray("designation_inspector").getJSONObject(i).getString(KeyBoardAuthorityConstant.INSPECTOR_ID))) {
                throw new DWArgumentException("inspectorId", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.getJSONArray("designation_inspector").getJSONObject(i).containsKey(KeyBoardAuthorityConstant.INSPECTOR_NAME) || StringUtils.isEmpty(jsonObject.getJSONArray("designation_inspector").getJSONObject(i).getString(KeyBoardAuthorityConstant.INSPECTOR_NAME))) {
                throw new DWArgumentException("inspectorName", MultilingualismUtil.getLanguage("notExist"));
            }
            KeyBoardAuthorityEntity entity = new KeyBoardAuthorityEntity();
            JSONArray inspectorInfos = jsonObject.getJSONArray("designation_inspector");
            StringBuilder specifyViewers = new StringBuilder();
            for (int j = 0; j < inspectorInfos.size(); j++) {
                JSONObject jsonObject1 = inspectorInfos.getJSONObject(j);
                String inspectorId = jsonObject1.getString("inspector_id");
                String inspectorName = jsonObject1.getString("inspector_name");
                specifyViewers.append(inspectorId).append("_").append(inspectorName).append(",");
            }
            String strViewers = specifyViewers.toString().substring(0,specifyViewers.toString().lastIndexOf(','));
            entity.setModelOid(jsonObject.getString("kanban_template_id"));
            entity.setModelName(jsonObject.getString("kanban_template_name"));
            entity.setSpecifyViewer(strViewers);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantSid);
            entity.setCreateTime(new Date());
            entity.setCreateName(userName);
            entities.add(entity);
        }
        return entities;
    }


}
