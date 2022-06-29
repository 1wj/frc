package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionSourceBiz;
import com.digiwin.app.frc.service.athena.mtw.common.constants.QuestionSourceConstant;
import com.digiwin.app.frc.service.athena.mtw.common.enums.EffectiveEnum;
import com.digiwin.app.frc.service.athena.mtw.common.enums.QuestionAttributionEnum;
import com.digiwin.app.frc.service.athena.mtw.common.enums.SourceClassificationNoEnum;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.ClassificationSourceMidEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionSourceEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.model.QuestionSourceModel;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionSourceQueryVo;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionSourceVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionClassificationMapper;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionSourceMapper;
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
 * @Date: 2021/11/11 15:12
 * @Version 1.0
 * @Description 问题来源处理Biz-impl
 */
@Service
public class QusetionSourceBizImpl implements QuestionSourceBiz {

    @Autowired
    private QuestionSourceMapper questionSourceMapper;

    @Autowired
    private QuestionClassificationMapper questionClassificationMapper;

    @Override
    public List<JSONObject> addQuestionSource(JSONArray dataContent) throws DWArgumentException, IOException {
        //添加问题来源信息
        CheckFieldValueUtil.validateModels(dataContent,new QuestionSourceModel());
        //对必传参数进行校验 并进行封装成entity
        List<QuestionSourceEntity> entities = checkAndHandleData(dataContent, TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName());
        int result = questionSourceMapper.addQuestionSourceInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }


    @Override
    public boolean deleteQuestionSource(JSONArray dataContent) {
        //删除问题来源信息
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(QuestionSourceConstant.SOURCE_ID);
            oidList.add(oid);
        }
        int result = questionSourceMapper.deleteQuestionSourceInfo(oidList);
        questionClassificationMapper.deleteClassificationSourceInfoBySourceIds(oidList,TenantTokenUtil.getTenantSid());
        return result > 0;
    }


    @Override
    public boolean updateQuestionSource(JSONArray dataContent) throws DWArgumentException, IOException {
        //更新问题来源信息
        CheckFieldValueUtil.validateModels(dataContent,new QuestionSourceModel());
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionSourceEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            QuestionSourceEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = questionSourceMapper.updateBatch(entities);
        return result > 0;
    }


    @Override
    public List<JSONObject> getQuestionSource(JSONArray dataContent) throws JsonProcessingException {
        //获取问题来源信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionSourceQueryVo> vos;
        if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
            vos = questionSourceMapper.getQuestionSourceInfo(tenantSid, new QuestionSourceVo());
        } else {
            //平台查询只支持传一笔数据 规格使用数组传参
            JSONObject jsonObject = dataContent.getJSONObject(0);
            //将查询条件参数映射到model中
            QuestionSourceVo vo = JSON.parseObject(jsonObject.toJSONString(), QuestionSourceVo.class);

            String questionAttributions = "";
            String attribution = jsonObject.getString("source_classification_no");
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
            vo.setSourceCategory(questionAttributions);

            vos = questionSourceMapper.getQuestionSourceInfo(tenantSid,vo);
        }
        //vo转成平台规范带有_格式的前端数据
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (QuestionSourceQueryVo vo : vos) {
            JSONObject jsonObjectInfo = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObjectList.add(jsonObjectInfo);
        }
        return jsonObjectList;
    }

    /**
     * 将实体类集合转成前端要求的格式集合
     *
     * @param entities 问题分类实体集合
     * @return List<JSONObject>
     */
    private List<JSONObject> convertData(List<QuestionSourceEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (QuestionSourceEntity entity : entities) {
            QuestionSourceVo vo = new QuestionSourceVo();
            BeanUtils.copyProperties(entity, vo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObject.put("question_classification_info",JSON.parseArray(entity.getClassificationInfo()));
            jsonObject.remove("classification_name");
            mapList.add(jsonObject);
        }
        return mapList;
    }


    /**
     * 对必传参数进行校验 并进行封装成entity
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @param userName    用户姓名
     * @return List<QuestionSourceEntity> 封装后实体类集合
     * @throws DWArgumentException
     */
    private List<QuestionSourceEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException, IOException {
        List<QuestionSourceEntity> entities = new ArrayList<>();
        //查询所有问题来源编号信息
        List<String> sourceNos = questionSourceMapper.queryAllSourceNos(tenantSid);
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if (!jsonObject.containsKey(QuestionSourceConstant.SOURCE_NO) || StringUtils.isEmpty(jsonObject.getString(QuestionSourceConstant.SOURCE_NO))) {
                throw new DWArgumentException("sourceNo", MultilingualismUtil.getLanguage("notExist"));
            }
            if(CollUtil.isNotEmpty(sourceNos) && sourceNos.contains(jsonObject.getString(QuestionSourceConstant.SOURCE_NO))){
                throw new DWArgumentException("sourceNo", MultilingualismUtil.getLanguage("NoAlreadyExist"));
            }
            if (!jsonObject.containsKey(QuestionSourceConstant.SOURCE_NAME) || StringUtils.isEmpty(jsonObject.getString(QuestionSourceConstant.SOURCE_NAME))) {
                throw new DWArgumentException("sourceName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(QuestionSourceConstant.SOURCE_CLASSIFICATION_NO) || StringUtils.isEmpty(jsonObject.getString(QuestionSourceConstant.SOURCE_CLASSIFICATION_NO))) {
                throw new DWArgumentException("sourceClassificationNo", MultilingualismUtil.getLanguage("notExist"));
            }
            String sourceClassificationNo = jsonObject.getString(QuestionSourceConstant.SOURCE_CLASSIFICATION_NO);
            if(!SourceClassificationNoEnum.EXTERNAL.getCode().equals(sourceClassificationNo) && !SourceClassificationNoEnum.INTERNAL.getCode().equals(sourceClassificationNo) && !SourceClassificationNoEnum.OTHER.getCode().equals(sourceClassificationNo)){
                throw new DWArgumentException("sourceClassificationNo", MultilingualismUtil.getLanguage("parameterError"));
            }
            if (!jsonObject.containsKey(QuestionSourceConstant.MANAGE_STATUS) || StringUtils.isEmpty(jsonObject.getString(QuestionSourceConstant.MANAGE_STATUS))) {
                throw new DWArgumentException("manageStatus", MultilingualismUtil.getLanguage("notExist"));
            }
            String manageStatus = jsonObject.getString(QuestionSourceConstant.MANAGE_STATUS);
            if (!manageStatus.equals(EffectiveEnum.EFFECTIVE.getCode()) && !manageStatus.equals(EffectiveEnum.INVALID.getCode())) {
                throw new DWArgumentException("manageStatus", MultilingualismUtil.getLanguage("parameterError"));
            }
            QuestionSourceModel model =  JSON.parseObject(jsonObject.toJSONString(), QuestionSourceModel.class);
            QuestionSourceEntity entity = new QuestionSourceEntity();
            BeanUtils.copyProperties(model, entity);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantSid);
            entity.setCreateTime(new Date());
            entity.setCreateName(userName);
            //问题来源 添加关联表数据
            List<ClassificationSourceMidEntity> midList = new ArrayList<>();
            JSONArray questionClassificationInfo = jsonObject.getJSONArray("question_classification_info");
            if(!StringUtils.isEmpty(questionClassificationInfo) && !questionClassificationInfo.isEmpty()){
                for (int j = 0; j < questionClassificationInfo.size(); j++) {
                    JSONObject jsonObject1 = questionClassificationInfo.getJSONObject(j);
                    String classificationId = jsonObject1.getString("question_classification_id");
                    midList.add(new ClassificationSourceMidEntity(
                            IdGenUtil.uuid(),tenantSid,classificationId,entity.getOid(),new Date(),userName,null,null
                    ));
                }
                questionClassificationMapper.addClassificationSourceMidInfo(midList);
                entity.setClassificationInfo(JSON.toJSONString(questionClassificationInfo));
            }


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
     * @return QuestionSourceEntity 封装后的实体类
     * @throws DWArgumentException
     */
    private QuestionSourceEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException, IOException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(QuestionSourceConstant.SOURCE_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("sourceId",
                    MultilingualismUtil.getLanguage("notExist"));
        }
        QuestionSourceModel model = JSON.parseObject(jsonObject.toJSONString(), QuestionSourceModel.class);
        QuestionSourceEntity entity = new QuestionSourceEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setUpdateName(updateName);
        entity.setUpdateTime(new Date());
        entity.setTenantSid(tenantSid);
        JSONArray questionClassificationInfo = jsonObject.getJSONArray("question_classification_info");
        if(!StringUtils.isEmpty(questionClassificationInfo) && !questionClassificationInfo.isEmpty()){
            List<ClassificationSourceMidEntity> midList = new ArrayList<>();
            for (int j = 0; j < questionClassificationInfo.size(); j++) {
                JSONObject jsonObject1 = questionClassificationInfo.getJSONObject(j);
                String classificationId = jsonObject1.getString("question_classification_id");
                midList.add(new ClassificationSourceMidEntity(
                        IdGenUtil.uuid(),tenantSid,classificationId,entity.getOid(),new Date(),updateName,null,null
                ));
            }
            questionClassificationMapper.deleteClassificationSourceInfoBySourceId(tenantSid,entity.getOid());
            questionClassificationMapper.addClassificationSourceMidInfo(midList);
        }else{
            questionClassificationMapper.deleteClassificationSourceInfoBySourceId(tenantSid,entity.getOid());
        }
        return entity;
    }

}
