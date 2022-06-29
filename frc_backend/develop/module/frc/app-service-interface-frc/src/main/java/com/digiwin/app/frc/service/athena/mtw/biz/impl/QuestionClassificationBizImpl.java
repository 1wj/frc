package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionClassificationBiz;
import com.digiwin.app.frc.service.athena.mtw.common.constants.MethodTypeConstant;
import com.digiwin.app.frc.service.athena.mtw.common.constants.QuestionClassificationConstant;
import com.digiwin.app.frc.service.athena.mtw.common.enums.EffectiveEnum;
import com.digiwin.app.frc.service.athena.mtw.common.enums.QuestionAttributionEnum;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.ClassificationSourceMidEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionClassificationEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.model.QuestionClassificationModel;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionClassificationQueryVo;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionClassificationVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionClassificationMapper;
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
 * @Date: 2021/11/5 15:44
 * @Version 1.0
 * @Description 问题分类实体处理Biz-impl
 */
@Service
public class QuestionClassificationBizImpl implements QuestionClassificationBiz {


    @Autowired
    private QuestionClassificationMapper questionClassificationMapper;


    @Override
    public List<JSONObject> addQuestionClassification(JSONArray dataContent) throws DWArgumentException, IOException {
        //添加问题分类信息
        CheckFieldValueUtil.validateModels(dataContent,new QuestionClassificationModel());
        //对必传参数进行校验 并进行封装成entity
        List<QuestionClassificationEntity> entities = checkAndHandleData(dataContent, TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName());
        int result = questionClassificationMapper.addQuestionClassificationInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities, MethodTypeConstant.ADD);
        return result > 0 ? mapList : null;
    }


    @Override
    public boolean deleteQuestionClassification(JSONArray dataContent) throws DWArgumentException {
        //删除问题分类信息
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(QuestionClassificationConstant.CLASSIFICATION_ID);
            oidList.add(oid);
        }
        int result = questionClassificationMapper.deleteQuestionClassificationInfo(oidList);
        questionClassificationMapper.deleteClassificationSourceInfo(oidList,TenantTokenUtil.getTenantSid());
        return result > 0;
    }


    @Override
    public boolean updateQuestionClassification(JSONArray dataContent) throws DWArgumentException, IOException {
        //更新问题分类信息
        CheckFieldValueUtil.validateModels(dataContent,new QuestionClassificationModel());
        List<QuestionClassificationEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            QuestionClassificationEntity entity = checkAndHandleData(dataContent, TenantTokenUtil.getUserName(), i,TenantTokenUtil.getTenantSid());
            entities.add(entity);
        }
        int result = questionClassificationMapper.updateBatch(entities);
        return result > 0;
    }


    @Override
    public List<JSONObject> getQuestionClassification(JSONArray dataContent) throws JsonProcessingException {
        //获取问题分类信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionClassificationQueryVo> vos;
        if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
            vos = questionClassificationMapper.getQuestionClassificationInfo(tenantSid, new QuestionClassificationVo());
        } else {
            //平台查询只支持传一笔数据 规格使用数组传参
            JSONObject jsonObject = dataContent.getJSONObject(0);
            //将查询条件参数映射到model中
            QuestionClassificationVo vo = JSON.parseObject(jsonObject.toJSONString(), QuestionClassificationVo.class);

            String questionAttributions = "";
            String attribution = jsonObject.getString(QuestionClassificationConstant.QUESTIONATTRIBUTION);
            if(!StringUtils.isEmpty(attribution) && !attribution.isEmpty()){
                if(QuestionAttributionEnum.INTERNAL.getCode().equals(attribution)) {
                    questionAttributions = QuestionAttributionEnum.INTERNAL.getCode()+ "," + QuestionAttributionEnum.ALL.getCode();
                }else if(QuestionAttributionEnum.EXTERNAL.getCode().equals(attribution)){
                    questionAttributions = QuestionAttributionEnum.EXTERNAL.getCode()+","+QuestionAttributionEnum.ALL.getCode();
                }else if(QuestionAttributionEnum.ALL.getCode().equals(attribution)){
                    questionAttributions = QuestionAttributionEnum.INTERNAL.getCode()+","+QuestionAttributionEnum.EXTERNAL.getCode()+","+QuestionAttributionEnum.ALL.getCode();
                }else{
                    questionAttributions = "";
                }
            }
            vo.setQuestionAttribution(questionAttributions);

            vos = questionClassificationMapper.getQuestionClassificationInfo(tenantSid, vo);
        }
        //vo转成平台规范带有_格式的前端数据
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (QuestionClassificationQueryVo vo : vos) {
            JSONObject jsonObjectInfo = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObjectList.add(jsonObjectInfo);
        }
        return jsonObjectList;
    }


    /**
     * 将实体类集合转成前端要求的格式集合
     *
     * @param entities 问题分类实体集合
     * @param method   区别方法类型常量
     * @return
     */
    private List<JSONObject> convertData(List<QuestionClassificationEntity> entities, String method) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (QuestionClassificationEntity entity : entities) {
            QuestionClassificationVo vo = new QuestionClassificationVo();
            BeanUtils.copyProperties(entity, vo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObject.put("question_source_info",JSON.parseArray(entity.getSourceInfo()));
            jsonObject.remove("source_name");
            mapList.add(jsonObject);
        }
        return mapList;
    }



    /**
     * 对必传参数进行校验 并进行封装成entity
     *
     * @param dataContent 解析后数据
     * @param updateName  修改人姓名
     * @param i           循环遍历的索引值
     * @return QuestionClassificationEntity 封装后的实体类
     * @throws DWArgumentException
     */
    private QuestionClassificationEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException, IOException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(QuestionClassificationConstant.CLASSIFICATION_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("classificationId", MultilingualismUtil.getLanguage("notExist"));
        }
        //对编号进行校验  只能数字、字母和短横线组成
        if(!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(QuestionClassificationConstant.CLASSIFICATION_NO))){
            throw new DWArgumentException("classificationNo",MultilingualismUtil.getLanguage("NumberRules"));
        }
        QuestionClassificationEntity entity = new QuestionClassificationEntity();
        QuestionClassificationModel model = JSON.parseObject(jsonObject.toJSONString(),QuestionClassificationModel.class);
        BeanUtils.copyProperties(model,entity);
        entity.setUpdateName(updateName);
        entity.setUpdateTime(new Date());
        entity.setTenantSid(tenantSid);
        JSONArray questionSourceInfo = jsonObject.getJSONArray("question_source_info");
        if(!StringUtils.isEmpty(questionSourceInfo) && !questionSourceInfo.isEmpty()){
            List<ClassificationSourceMidEntity> midList = new ArrayList<>();
            for (int j = 0; j < questionSourceInfo.size(); j++) {
                JSONObject jsonObject1 = questionSourceInfo.getJSONObject(j);
                String sourceId = jsonObject1.getString("question_source_id");
                midList.add(new ClassificationSourceMidEntity(
                        IdGenUtil.uuid(),tenantSid,entity.getOid(),sourceId,new Date(),updateName,null,null
                ));
            }
            questionClassificationMapper.deleteClassificationSourceInfoByClassificationId(tenantSid,entity.getOid());
            questionClassificationMapper.addClassificationSourceMidInfo(midList);
        }else{
            questionClassificationMapper.deleteClassificationSourceInfoByClassificationId(tenantSid,entity.getOid());
        }
        return entity;
    }


    /**
     * 对必传参数进行校验 并进行封装成entity
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @param userName    用户姓名
     * @return List<QuestionClassificationEntity> 封装后实体类集合
     * @throws DWArgumentException IOException
     */
    private List<QuestionClassificationEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException, IOException {
        List<QuestionClassificationEntity> entities = new ArrayList<>();
        //查询所有问题分类编号信息
        List<String> classificationNos = questionClassificationMapper.queryAllClassificationNos(tenantSid);
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if (!jsonObject.containsKey(QuestionClassificationConstant.CLASSIFICATION_NO) || StringUtils.isEmpty(jsonObject.getString(QuestionClassificationConstant.CLASSIFICATION_NO))) {
                throw new DWArgumentException("classificationNo", MultilingualismUtil.getLanguage("notExist"));
            }
            if(CollUtil.isNotEmpty(classificationNos) && classificationNos.contains(jsonObject.getString(QuestionClassificationConstant.CLASSIFICATION_NO))){
                throw new DWArgumentException("classificationNo", MultilingualismUtil.getLanguage("NoAlreadyExist"));
            }
            //对编号进行校验  只能数字、字母和短横线组成
            if(!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(QuestionClassificationConstant.CLASSIFICATION_NO))){
                throw new DWArgumentException("classificationNo",MultilingualismUtil.getLanguage("NumberRules"));
            }
            if (!jsonObject.containsKey(QuestionClassificationConstant.CLASSIFICATION_NAME) || StringUtils.isEmpty(jsonObject.getString(QuestionClassificationConstant.CLASSIFICATION_NAME))) {
                throw new DWArgumentException("classificationName", MultilingualismUtil.getLanguage("notExist"));
            }
            //对问题归属  进行校验
            if (!jsonObject.containsKey(QuestionClassificationConstant.QUESTIONATTRIBUTION) || StringUtils.isEmpty(jsonObject.getString(QuestionClassificationConstant.QUESTIONATTRIBUTION))) {
                throw new DWArgumentException("questionAttribution", MultilingualismUtil.getLanguage("notExist"));
            }
            String questionAttribution = jsonObject.getString(QuestionClassificationConstant.QUESTIONATTRIBUTION);
            if(!QuestionAttributionEnum.EXTERNAL.getCode().equals(questionAttribution) && !QuestionAttributionEnum.INTERNAL.getCode().equals(questionAttribution) && !QuestionAttributionEnum.ALL.getCode().equals(questionAttribution)){
                throw new DWArgumentException("questionAttribution", MultilingualismUtil.getLanguage("parameterError"));
            }
            if (!jsonObject.containsKey(QuestionClassificationConstant.MANAGE_STATUS) || StringUtils.isEmpty(jsonObject.getString(QuestionClassificationConstant.MANAGE_STATUS))) {
                throw new DWArgumentException("manageStatus", MultilingualismUtil.getLanguage("notExist"));
            }
            String manageStatus = jsonObject.getString(QuestionClassificationConstant.MANAGE_STATUS);
            if(!manageStatus.equals(EffectiveEnum.EFFECTIVE.getCode()) && !manageStatus.equals(EffectiveEnum.INVALID.getCode())){
                throw new DWArgumentException("manageStatus", MultilingualismUtil.getLanguage("parameterError"));
            }
            QuestionClassificationModel model = JSON.parseObject(jsonObject.toJSONString(),QuestionClassificationModel.class);
            QuestionClassificationEntity entity = new QuestionClassificationEntity();
            BeanUtils.copyProperties(model, entity);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantSid);
            entity.setCreateTime(new Date());
            entity.setCreateName(userName);
            //问题来源 添加关联表数据
            List<ClassificationSourceMidEntity> midList = new ArrayList<>();
            JSONArray questionSourceInfo = jsonObject.getJSONArray("question_source_info");
            if(!StringUtils.isEmpty(questionSourceInfo) && !questionSourceInfo.isEmpty()){
                for (int j = 0; j < questionSourceInfo.size(); j++) {
                    JSONObject jsonObject1 = questionSourceInfo.getJSONObject(j);
                    String sourceId = jsonObject1.getString("question_source_id");
                    midList.add(new ClassificationSourceMidEntity(
                            IdGenUtil.uuid(),tenantSid,entity.getOid(),sourceId,new Date(),userName,null,null
                    ));
                }
                questionClassificationMapper.addClassificationSourceMidInfo(midList);
                entity.setSourceInfo(JSON.toJSONString(questionSourceInfo));
            }

            entities.add(entity);
        }
        return entities;
    }


}
