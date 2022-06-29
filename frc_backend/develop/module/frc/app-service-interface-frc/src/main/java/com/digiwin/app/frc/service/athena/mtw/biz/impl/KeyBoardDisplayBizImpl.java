package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.KeyBoardDisplayBiz;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionSolutionBiz;
import com.digiwin.app.frc.service.athena.mtw.common.constants.KeyBoardDisplayConstant;
import com.digiwin.app.frc.service.athena.mtw.common.constants.MethodTypeConstant;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.KeyBoardDisplayEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.model.KeyBoardDisplayModel;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.KeyBoardDisplayVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.KeyBoardDisplayMapper;
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
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/23 10:09
 * @Version 1.0
 * @Description  看板模板显示处理Biz-impl
 */
@Service
public class KeyBoardDisplayBizImpl implements KeyBoardDisplayBiz {

    @Autowired
    private KeyBoardDisplayMapper keyBoardDisplayMapper;

    @Override
    public List<JSONObject> addKeyBoardDisplay(JSONArray dataContent) throws DWArgumentException, IOException {
        //新增模板栏位匹配显示信息
        //对必传参数进行校验 并进行封装成entity
        List<KeyBoardDisplayEntity> entities = checkAndHandleData(dataContent, TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName(), MethodTypeConstant.ADD);
        int result = keyBoardDisplayMapper.addKeyBoardDisplayInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }


    @Override
    public boolean deleteKeyBoardDisplay(JSONArray dataContent) {
        //删除模板栏位匹配显示信息
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(KeyBoardDisplayConstant.KANBAN_DISPLAY_ID);
            oidList.add(oid);
        }
        int result = keyBoardDisplayMapper.deleteKeyBoardDisplayInfo(oidList);
        return result > 0;
    }

    @Override
    public boolean updateKeyBoardDisplay(JSONArray dataContent) throws IOException, DWArgumentException {
        //更新模板栏位匹配显示信息
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<KeyBoardDisplayEntity> entities = checkAndHandleData(dataContent, tenantSid, updateName, MethodTypeConstant.UPDATE);
        int result = keyBoardDisplayMapper.updateBatch(entities);
        return result > 0;
    }

    @Autowired
    private QuestionSolutionBiz solutionBiz;

    @Override
    public List<Map<String, Object>> getKeyBoardDisplay(JSONArray dataContent) throws JsonProcessingException, DWArgumentException {
        //平台查询只支持传一笔数据 规格使用数组传参
        JSONObject jsonObject = dataContent.getJSONObject(0);
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        String kanbanTemplateId = jsonObject.getString("kanban_template_id");
        if(StringUtils.isEmpty(kanbanTemplateId)){
            throw new DWArgumentException("kanbanTemplateId", MultilingualismUtil.getLanguage("notExist"));
        }
        //查询所有的解决方案信息
        List<Map<String, Object>> solutionInfo = solutionBiz.getSolutionInfo(new JSONArray());
        for (Map<String, Object> map : solutionInfo) {
            String solutionId = (String) map.get("solution_id");
            List<KeyBoardDisplayEntity> entityList =  keyBoardDisplayMapper.getKeyBoardDisplayInfo(tenantSid,kanbanTemplateId,solutionId);
            List<JSONObject> jsonObjects = convertData(entityList);
            map.put("kanban_display_info",jsonObjects);
        }
        return solutionInfo;
    }


    /**
     * 将实体类集合转成前端要求的格式集合
     *
     * @param entities 看板模板显示实体集合
     * @return List<JSONObject>
     */
    private List<JSONObject> convertData(List<KeyBoardDisplayEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (KeyBoardDisplayEntity entity : entities) {
            KeyBoardDisplayVo vo = new KeyBoardDisplayVo();
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
     * @return List<KeyBoardDisplayEntity> 封装后实体类集合
     * @throws DWArgumentException
     */
    private List<KeyBoardDisplayEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName,String methodType) throws DWArgumentException, IOException {
        List<KeyBoardDisplayEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if(MethodTypeConstant.UPDATE.equals(methodType)){
                String oid = jsonObject.getString(KeyBoardDisplayConstant.KANBAN_DISPLAY_ID);
                if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
                    throw new DWArgumentException("kanbanDisplayId",
                            MultilingualismUtil.getLanguage("notExist"));
                }
            }
            if (!jsonObject.containsKey(KeyBoardDisplayConstant.KANBAN_TEMPLATE_ID) || StringUtils.isEmpty(jsonObject.getString(KeyBoardDisplayConstant.KANBAN_TEMPLATE_ID))) {
                throw new DWArgumentException("kanbanTemplateId", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(KeyBoardDisplayConstant.KANBAN_TEMPLATE_NAME) || StringUtils.isEmpty(jsonObject.getString(KeyBoardDisplayConstant.KANBAN_TEMPLATE_NAME))) {
                throw new DWArgumentException("kanbanTemplateName", MultilingualismUtil.getLanguage("notExist"));
            }

            if (!jsonObject.containsKey(KeyBoardDisplayConstant.SOLUTION_ID) || StringUtils.isEmpty(jsonObject.getString(KeyBoardDisplayConstant.SOLUTION_ID))) {
                throw new DWArgumentException("solutionId", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(KeyBoardDisplayConstant.SOLUTION_NAME) || StringUtils.isEmpty(jsonObject.getString(KeyBoardDisplayConstant.SOLUTION_NAME))) {
                throw new DWArgumentException("solutionName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(KeyBoardDisplayConstant.FIELD_NO) || StringUtils.isEmpty(jsonObject.getString(KeyBoardDisplayConstant.FIELD_NO))) {
                throw new DWArgumentException("fieldNo", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(KeyBoardDisplayConstant.FIELD_NAME) || StringUtils.isEmpty(jsonObject.getString(KeyBoardDisplayConstant.FIELD_NAME))) {
                throw new DWArgumentException("fieldName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(KeyBoardDisplayConstant.STEP_ID) || StringUtils.isEmpty(jsonObject.getString(KeyBoardDisplayConstant.STEP_ID))) {
                throw new DWArgumentException("stepId", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(KeyBoardDisplayConstant.STEP_NAME) || StringUtils.isEmpty(jsonObject.getString(KeyBoardDisplayConstant.STEP_NAME))) {
                throw new DWArgumentException("stepName", MultilingualismUtil.getLanguage("notExist"));
            }
            KeyBoardDisplayModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), KeyBoardDisplayModel.class);
            KeyBoardDisplayEntity entity = new KeyBoardDisplayEntity();
            BeanUtils.copyProperties(model, entity);
            if(MethodTypeConstant.UPDATE.equals(methodType)){
                entity.setUpdateName(userName);
                entity.setUpdateTime(new Date());
                entity.setTenantSid(tenantSid);
                entities.add(entity);
                continue;
            }
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantSid);
            entity.setCreateTime(new Date());
            entity.setCreateName(userName);
            entities.add(entity);
        }
        return entities;
    }


}
