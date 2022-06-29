package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.DefectCodeBiz;
import com.digiwin.app.frc.service.athena.mtw.common.constants.DefectCodeConstant;
import com.digiwin.app.frc.service.athena.mtw.common.enums.EffectiveEnum;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.DefectCodeEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.model.DefectCodeModel;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.DefectCodeVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.DefectCodeMapper;
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
 * @Date: 2021/11/9 17:22
 * @Version 1.0
 * @Description 缺陷代码信息处理Biz-impl
 */
@Service
public class DefectCodeBizImpl implements DefectCodeBiz  {

    @Autowired
    private DefectCodeMapper defectCodeMapper;

    @Override
    public List<JSONObject> addDefectCodeInfo(JSONArray dataContent) throws DWArgumentException, IOException {
        //添加缺陷代码信息
        CheckFieldValueUtil.validateModels(dataContent,new DefectCodeModel());
        //对必传参数进行校验 并进行封装成entity
        List<DefectCodeEntity> entities = checkAndHandleData(dataContent, TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName());
        int result = defectCodeMapper.addDefectCodeInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }


    @Override
    public boolean deleteDefectCode(JSONArray dataContent) {
        //删除缺陷代码信息
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(DefectCodeConstant.DEFECT_ID);
            oidList.add(oid);
        }
        int result = defectCodeMapper.deleteDefectCodeInfo(oidList);
        return result > 0;
    }


    @Override
    public boolean updateDefectCode(JSONArray dataContent) throws DWArgumentException, IOException {
        //更新缺陷代码信息
        CheckFieldValueUtil.validateModels(dataContent,new DefectCodeModel());
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<DefectCodeEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            DefectCodeEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = defectCodeMapper.updateBatch(entities);
        return result > 0;
    }


    @Override
    public List<JSONObject> getDefectCode(JSONArray dataContent) throws JsonProcessingException {
        //获取缺陷代码信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<DefectCodeEntity> entities;
        if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
            entities = defectCodeMapper.getDefectCodeInfo(tenantSid, null, null, null, null, null, null, null);
        } else {
            entities = queryByCondition(dataContent, tenantSid);
        }
        //entity转成平台规范带有_格式的前端数据
        return  convertData(entities);
    }

    /**
     * 将实体类集合转成前端要求的格式集合
     *
     * @param entities 缺陷代码实体类集合
     * @return List<JSONObject>
     */
    private List<JSONObject> convertData(List<DefectCodeEntity> entities) throws JsonProcessingException {

        List<JSONObject> mapList = new ArrayList<>();
        for (DefectCodeEntity entity : entities) {
            DefectCodeVo vo = new DefectCodeVo();
            String imageDmcId = entity.getImageDmcId();
            BeanUtils.copyProperties(entity, vo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObject.put("defect_attachment", JSON.parseObject(imageDmcId));
            mapList.add(jsonObject);
        }
        return mapList;
    }


    /**
     * 对必传参数进行校验 并进行封装成entity
     *
     * @param dataContent 解析后数据
     * @param updateName  修改人姓名
     * @param i           循环索引
     * @return DefectCodeEntity
     * @throws DWArgumentException
     */
    private DefectCodeEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException, IOException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(DefectCodeConstant.DEFECT_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("defectId",
                    MultilingualismUtil.getLanguage("notExist"));
        }
        //对编号进行校验  只能是字母、数字、短横线组合
        if (!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(DefectCodeConstant.CATEGORY_NO))) {
            throw new DWArgumentException("categoryNo", MultilingualismUtil.getLanguage("NumberRules"));
        }
        if (!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(DefectCodeConstant.DEFECT_NO))) {
            throw new DWArgumentException("defectNo", MultilingualismUtil.getLanguage("NumberRules"));
        }
        JSONObject attachment = jsonObject.getJSONObject("defect_attachment");
        jsonObject.remove("defect_attachment");
        DefectCodeEntity entity = new DefectCodeEntity();
        DefectCodeModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), DefectCodeModel.class);
        BeanUtils.copyProperties(model, entity);
        entity.setUpdateName(updateName);
        entity.setUpdateTime(new Date());
        entity.setImageDmcId(JSON.toJSONString(attachment));
        entity.setTenantSid(tenantSid);
        return entity;
    }


    /**
     * 对必传参数进行处理 并进行带条件查询
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @return List<DefectCodeEntity>
     */
    private List<DefectCodeEntity> queryByCondition(JSONArray dataContent, Long tenantSid) {
        List<DefectCodeEntity> entities;
        JSONObject jsonObject = dataContent.getJSONObject(0);
        entities = defectCodeMapper.getDefectCodeInfo(
                tenantSid,
                jsonObject.getString(DefectCodeConstant.CATEGORY_NO),
                jsonObject.getString(DefectCodeConstant.CATEGORY_NAME),
                jsonObject.getString(DefectCodeConstant.DEFECT_NO),
                jsonObject.getString(DefectCodeConstant.DEFECT_NAME),
                jsonObject.getString(DefectCodeConstant.DEFECT_GRADE),
                jsonObject.getString(DefectCodeConstant.MANAGE_STATUS),
                jsonObject.getString(DefectCodeConstant.REMARKS));
        return entities;
    }


    /**
     * 对必传参数进行校验 并进行封装成entity
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @param userName    用户名
     * @return List<DefectCodeEntity>
     * @throws DWArgumentException
     */
    private List<DefectCodeEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException, IOException {
        List<DefectCodeEntity> entities = new ArrayList<>();
        //查询所有缺陷代码编号
        List<String> categoryNos = defectCodeMapper.queryAllCategoryNo(tenantSid);
        List<String> defectNos = defectCodeMapper.queryAllDefectNos(tenantSid);
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if (!jsonObject.containsKey(DefectCodeConstant.CATEGORY_NO) || StringUtils.isEmpty(jsonObject.getString(DefectCodeConstant.CATEGORY_NO))) {
                throw new DWArgumentException("categoryNo", MultilingualismUtil.getLanguage("notExist"));
            }
            //对编号进行校验  只能是字母、数字、短横线组合
            if (!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(DefectCodeConstant.CATEGORY_NO))) {
                throw new DWArgumentException("categoryNo", MultilingualismUtil.getLanguage("NumberRules"));
            }
            if (CollUtil.isNotEmpty(categoryNos) && categoryNos.contains(jsonObject.getString(DefectCodeConstant.CATEGORY_NO))) {
                throw new DWArgumentException("categoryNo", MultilingualismUtil.getLanguage("NoAlreadyExist"));
            }
            if (!jsonObject.containsKey(DefectCodeConstant.CATEGORY_NAME) || StringUtils.isEmpty(jsonObject.getString(DefectCodeConstant.CATEGORY_NAME))) {
                throw new DWArgumentException("categoryName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(DefectCodeConstant.DEFECT_NO) || StringUtils.isEmpty(jsonObject.getString(DefectCodeConstant.DEFECT_NO))) {
                throw new DWArgumentException("defectNo", MultilingualismUtil.getLanguage("notExist"));
            }
            if (CollUtil.isNotEmpty(defectNos) && defectNos.contains(jsonObject.getString(DefectCodeConstant.DEFECT_NO))) {
                throw new DWArgumentException("defectNo", MultilingualismUtil.getLanguage("isExist"));
            }
            //对编号进行校验  只能是字母、数字、短横线组合
            if (!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(DefectCodeConstant.DEFECT_NO))) {
                throw new DWArgumentException("defectNo", MultilingualismUtil.getLanguage("NumberRules"));
            }
            if (!jsonObject.containsKey(DefectCodeConstant.DEFECT_NAME) || StringUtils.isEmpty(jsonObject.getString(DefectCodeConstant.DEFECT_NAME))) {
                throw new DWArgumentException("defectName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(DefectCodeConstant.DEFECT_GRADE) || StringUtils.isEmpty(jsonObject.getString(DefectCodeConstant.DEFECT_GRADE))) {
                throw new DWArgumentException("defectGrade", MultilingualismUtil.getLanguage("notExist"));
            }

            if (!jsonObject.containsKey(DefectCodeConstant.MANAGE_STATUS) || StringUtils.isEmpty(jsonObject.getString(DefectCodeConstant.MANAGE_STATUS))) {
                throw new DWArgumentException("manageStatus", MultilingualismUtil.getLanguage("notExist"));
            }
            String manageStatus = jsonObject.getString(DefectCodeConstant.MANAGE_STATUS);
            if (!manageStatus.equals(EffectiveEnum.EFFECTIVE.getCode()) && !manageStatus.equals(EffectiveEnum.INVALID.getCode())) {
                throw new DWArgumentException("manageStatus", MultilingualismUtil.getLanguage("parameterError"));
            }

            JSONObject attachment = jsonObject.getJSONObject("defect_attachment");
            jsonObject.remove("defect_attachment");

            DefectCodeModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), DefectCodeModel.class);
            DefectCodeEntity entity = new DefectCodeEntity();
            BeanUtils.copyProperties(model, entity);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantSid);
            entity.setCreateTime(new Date());
            entity.setCreateName(userName);

            entity.setImageDmcId(JSON.toJSONString(attachment));

            entities.add(entity);
        }
        return entities;
    }


}
