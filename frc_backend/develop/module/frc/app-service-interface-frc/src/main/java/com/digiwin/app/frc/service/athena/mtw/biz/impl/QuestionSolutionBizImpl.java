package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionSolutionBiz;
import com.digiwin.app.frc.service.athena.mtw.common.constants.MethodTypeConstant;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionSolutionEditEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionSolutionMeasureEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.model.QuestionSolutionEditModel;
import com.digiwin.app.frc.service.athena.mtw.domain.model.QuestionSolutionMeasureModel;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionSolutionEditVo;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionSolutionMeasureVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.KeyBoardDisplayMapper;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionSolutionEditMapper;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionSolutionMeasureMapper;
import com.digiwin.app.frc.service.athena.util.*;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.beans.BeanUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: xieps
 * @Date: 2021/11/16 15:27
 * @Version 1.0
 * @Description 解决方案处理Biz-impl
 */
@Service
public class QuestionSolutionBizImpl implements QuestionSolutionBiz {

    @Autowired
    private QuestionSolutionEditMapper questionSolutionEditMapper;

    @Autowired
    private QuestionSolutionMeasureMapper questionSolutionMeasureMapper;

    @Autowired
    private KeyBoardDisplayMapper keyBoardDisplayMapper;

    @Override
    public List addQuestionSolution(JSONArray solutionData) throws IOException, DWArgumentException , OperationException {
        //添加解决方案信息
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        Long tenantSid = (Long) profile.get("tenantSid");
        String userName = (String) profile.get("userName");
        List<QuestionSolutionEditEntity> editEntities = new ArrayList<>();
        List mapList = new ArrayList<>();
        //查询所有解决方案名称
        List<String> solutionNames = questionSolutionEditMapper.queryAllSolutionNames(tenantSid);
        for (int i = 0; i < solutionData.size(); i++) {
            JSONObject jsonObject = solutionData.getJSONObject(i);
            JSONArray solutionMeasureData = jsonObject.getJSONArray("solution_step_info");
            //第一种场景： 只添加解决方案信息
            if (StringUtils.isEmpty(solutionMeasureData) || solutionMeasureData.isEmpty()) {
                QuestionSolutionEditEntity editEntity = encapsulateSolutionEntity(tenantSid, userName, jsonObject, MethodTypeConstant.ADD);
                if (CollUtil.isNotEmpty(solutionNames) && solutionNames.contains(editEntity.getSolutionName())) {
                    throw new DWArgumentException("solutionName", MultilingualismUtil.getLanguage("isExist"));
                }
                questionSolutionEditMapper.addQuestionSolutionEditInfo(editEntity);
                editEntities.add(editEntity);
                List<Map<String, Object>> mapList1 = convertDataAll(editEntities);
                mapList = ListUtils.union(mapList, mapList1);
                continue;
            }
            //第二种场景：只添加解决方案配置信息 (前提是 解决方案主键以及解决方案编号 必须存在)
            String solutionKeyId = jsonObject.getString("solution_key_id");
            String solutionId = jsonObject.getString("solution_id");
            if (!StringUtils.isEmpty(solutionKeyId) && !StringUtils.isEmpty(solutionId)) {
                List<Map<String, Object>> mapList2 = convertDataAll(tenantSid, userName, solutionMeasureData, solutionKeyId, solutionId);
                mapList = ListUtils.union(mapList, mapList2);
                continue;
            }
            //第三种场景：添加解决方案和解决方案配置信息
            QuestionSolutionEditEntity editEntity = encapsulateSolutionEntity(tenantSid, userName, jsonObject, MethodTypeConstant.ADD);
            if (CollUtil.isNotEmpty(solutionNames) && solutionNames.contains(editEntity.getSolutionName())) {
                throw new DWArgumentException("solutionName", MultilingualismUtil.getLanguage("isExist"));
            }
            questionSolutionEditMapper.addQuestionSolutionEditInfo(editEntity);
            String oid = editEntity.getOid();
            if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
                throw new DWArgumentException("solutionKeyId",
                        MultilingualismUtil.getLanguage("notExist"));
            }
            for (int j = 0; j < solutionMeasureData.size(); j++) {
                QuestionSolutionMeasureEntity measureEntity = encapsulateSolutionMeasureEntity(tenantSid, userName, solutionMeasureData, editEntity, oid, j, MethodTypeConstant.ADD);
                questionSolutionMeasureMapper.addQuestionMeasureInfo(measureEntity);
            }
            //添加完成后  转成前端指定格式字段进行返回
            editEntities.add(editEntity);
            List<Map<String, Object>> mapList3 = convertDataAll(editEntities);
            mapList = ListUtils.union(mapList, mapList3);
        }
        return mapList;
    }



    @Override
    public void deleteQuestionSolution(JSONArray dataContent) {
        //删除解决方案信息和步骤信息   或者    只删除解决方案步骤信息
        for (int i = 0; i < dataContent.size(); i++) {
            String solutionEditOid = dataContent.getJSONObject(i).getString("solution_key_id");
            JSONArray stepInfo = (JSONArray) dataContent.getJSONObject(i).get("solution_step_info");
            if(!StringUtils.isEmpty(stepInfo) && !stepInfo.isEmpty()){
                for (int j = 0; j < stepInfo.size(); j++) {
                    String stepId = stepInfo.getJSONObject(j).getString("solution_step_id");
                    questionSolutionMeasureMapper.deleteQuestionSolutionMeasureById(stepId);
                }
            }else{
                questionSolutionEditMapper.deleteQuestionSolutionEditById(solutionEditOid);
                questionSolutionMeasureMapper.deleteQuestionSolutionMeasureByEditOid(solutionEditOid);
            }
        }
    }


    @Override
    public void updateQuestionSolution(JSONArray solutionData) throws IOException, DWArgumentException,OperationException {
        //更新解决方案信息
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        for (int i = 0; i < solutionData.size(); i++) {
            JSONObject jsonObject = solutionData.getJSONObject(i);
            String solutionOid = jsonObject.getString("solution_key_id");
            if (StringUtils.isEmpty(solutionOid) || solutionOid.isEmpty()) {
                throw new DWArgumentException("solutionKeyId", MultilingualismUtil.getLanguage("notExist"));
            }
            JSONArray solutionMeasureData = jsonObject.getJSONArray("solution_step_info");
            //第一种场景： 只更新解决方案信息  需要同步更新看板显示相关字段信息
            if (StringUtils.isEmpty(solutionMeasureData) || solutionMeasureData.isEmpty()) {
                QuestionSolutionEditEntity editEntity = encapsulateSolutionEntity(tenantSid, updateName, jsonObject, MethodTypeConstant.UPDATE);
                questionSolutionEditMapper.updateQuestionSolutionEditInfo(editEntity,tenantSid);
                keyBoardDisplayMapper.updateDisplayInfoBySolutionNo(tenantSid,editEntity.getSolutionNo(),editEntity.getSolutionName(),null,null);
                continue;
            }
            //第二种场景：更新解决方案信息及解决方案步骤信息  需要同步更新看板显示相关字段信息
            QuestionSolutionEditEntity editEntity = encapsulateSolutionEntity(tenantSid, updateName, jsonObject, MethodTypeConstant.UPDATE);
            questionSolutionEditMapper.updateQuestionSolutionEditInfo(editEntity,tenantSid);
            for (int j = 0; j < solutionMeasureData.size(); j++) {
                JSONObject measureInfo = solutionMeasureData.getJSONObject(j);
                String solutionMeasureOid = measureInfo.getString("solution_step_id");
                if (StringUtils.isEmpty(solutionMeasureOid) || solutionMeasureOid.isEmpty()) {
                    throw new DWArgumentException("solutionStepId", MultilingualismUtil.getLanguage("notExist"));
                }
                QuestionSolutionMeasureEntity measureEntity = encapsulateSolutionMeasureEntity(tenantSid, updateName, solutionMeasureData, editEntity, solutionOid, j, MethodTypeConstant.UPDATE);
                questionSolutionMeasureMapper.updateQuestionMeasureByEditOid(solutionMeasureOid, solutionOid, measureEntity,tenantSid);
                keyBoardDisplayMapper.updateDisplayInfoBySolutionNo(tenantSid,editEntity.getSolutionNo(),editEntity.getSolutionName(),measureEntity.getMeasureNo(),measureEntity.getMeasureName());
            }

        }
    }


    @Override
    public List<Map<String, Object>> getSolutionInfo(JSONArray solutionData) throws JsonProcessingException {
        //获取解决方案信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<Map<String, Object>> mapList;
        if (StringUtils.isEmpty(solutionData) || solutionData.isEmpty()) {
            List<QuestionSolutionEditEntity> solutionEditEntities = questionSolutionEditMapper.getQuestionSolutionEditInfo(tenantSid, null, null, null, null, null, null);
            mapList = convertDataAll(solutionEditEntities);
        } else {
            List<QuestionSolutionEditEntity> solutionEditEntities = queryByCondition(solutionData, tenantSid);
            mapList = convertDataAll(solutionEditEntities);
        }
        return mapList;
    }

    /**
     * 将实体类集合转成前端要求的格式集合
     *
     * @param tenantSid           租户
     * @param userName            用户名
     * @param solutionMeasureData 解决方案步骤信息
     * @param solutionKeyId       解决方案主键
     * @param solutionId          解决方案编号
     * @return List<Map < String, Object>>
     * @throws IOException
     */
    private List<Map<String, Object>> convertDataAll(Long tenantSid, String userName, JSONArray solutionMeasureData, String solutionKeyId, String solutionId) throws IOException,OperationException {
        List<Map<String, Object>> mapList2 = new ArrayList<>();
        for (int j = 0; j < solutionMeasureData.size(); j++) {
            QuestionSolutionMeasureEntity measureEntity = encapsulateSolutionMeasureEntity(tenantSid, userName, solutionMeasureData, solutionKeyId, solutionId, j);
            //修改预计完成时间小于零问题
            questionSolutionMeasureMapper.addQuestionMeasureInfo(measureEntity);
        }
        QuestionSolutionEditEntity editEntity = questionSolutionEditMapper.querySolutionEditById(solutionKeyId,tenantSid);
        QuestionSolutionEditVo editVo = new QuestionSolutionEditVo();
        BeanUtils.copyProperties(editEntity,editVo);
        JSONObject jo = JSON.parseObject(new ObjectMapper().writeValueAsString(editVo));
        List<JSONObject> listMeasure = new ArrayList<>();
        List<QuestionSolutionMeasureEntity> measureEntityList = questionSolutionMeasureMapper.queryMeasureInfoByEditOid(solutionKeyId,tenantSid);
        for (QuestionSolutionMeasureEntity measureEntity : measureEntityList) {
            QuestionSolutionMeasureVo vo = new QuestionSolutionMeasureVo();
            BeanUtils.copyProperties(measureEntity, vo);
            vo.setExpectCompleteTime(DateUtil.minutes2Hours(measureEntity.getExpectCompleteTime()));
            JSONObject fieldObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            listMeasure.add(fieldObject);
        }
        jo.put("solution_step_info", listMeasure);
        mapList2.add(jo);
        return mapList2;
    }

    /**
     * 封装解决方案步骤实体类
     *
     * @param tenantSid           租户
     * @param userName            用户名
     * @param solutionMeasureData 解决方案步骤信息
     * @param solutionKeyId       解决方案主键
     * @param solutionId          解决方案编号
     * @param j                   循环索引
     * @return QuestionSolutionMeasureEntity
     * @throws IOException
     */
    private QuestionSolutionMeasureEntity encapsulateSolutionMeasureEntity(Long tenantSid, String userName, JSONArray solutionMeasureData, String solutionKeyId, String solutionId, int j) throws IOException, OperationException {
        JSONObject measureData = solutionMeasureData.getJSONObject(j);
        String expectCompleteDays = measureData.getString("expect_complete_days");
        measureData.put("expect_complete_days",DateUtil.hours2Minutes(expectCompleteDays));
        QuestionSolutionMeasureModel measureModel = new ObjectMapper().readValue(measureData.toString().getBytes(), QuestionSolutionMeasureModel.class);
        QuestionSolutionMeasureEntity measureEntity = new QuestionSolutionMeasureEntity();
        BeanUtils.copyProperties(measureModel, measureEntity);
        measureEntity.setOid(IdGenUtil.uuid());
        measureEntity.setTenantSid(tenantSid);
        measureEntity.setCreateTime(new Date());
        measureEntity.setCreateName(userName);
        measureEntity.setSolutionEditOid(solutionKeyId);
        //添加步骤编号 在解决方案编号的基础上再加三位数升序
        List<String> measureNos = questionSolutionMeasureMapper.queryAllMeasureNosByPrefix(solutionId,tenantSid);
        int maxNumber = 0;
        if (CollUtil.isNotEmpty(measureNos)) {
            String maxMeasureNo = measureNos.get(measureNos.size() - 1);
            String lastNumberStr = maxMeasureNo.substring(maxMeasureNo.length() - 3);
            maxNumber = Integer.parseInt(lastNumberStr);
        }
        String serialNumberMeasure = NumberUtil.frontCompWithZore(maxNumber + 1, 3);
        measureEntity.setMeasureNo(solutionId + serialNumberMeasure);
        return measureEntity;
    }


    /**
     * 将实体类集合转成前端要求的格式集合
     *
     * @param solutionEditEntities 解决方案实体类集合
     * @return List<Map < String, Object>>
     * @throws JsonProcessingException
     */
    private List<Map<String, Object>> convertDataAll(List<QuestionSolutionEditEntity> solutionEditEntities) throws JsonProcessingException {
        List<Map<String, Object>> mapList = new ArrayList<>();
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        for (QuestionSolutionEditEntity editEntity : solutionEditEntities) {
            QuestionSolutionEditVo editVo = new QuestionSolutionEditVo();
            BeanUtils.copyProperties(editEntity, editVo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(editVo));
            String editEntityOid = editEntity.getOid();
            List<QuestionSolutionMeasureEntity> measureEntities = questionSolutionMeasureMapper.queryMeasureInfoByEditOid(editEntityOid,tenantSid);
            if (!StringUtils.isEmpty(measureEntities) && !measureEntities.isEmpty()) {
                List<JSONObject> listMeasure = new ArrayList<>();
                for (QuestionSolutionMeasureEntity measureEntity : measureEntities) {
                    QuestionSolutionMeasureVo vo = new QuestionSolutionMeasureVo();
                    BeanUtils.copyProperties(measureEntity, vo);
                    vo.setExpectCompleteTime(DateUtil.minutes2Hours(measureEntity.getExpectCompleteTime()));
                    JSONObject measureObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
                    listMeasure.add(measureObject);
                }
                jsonObject.put("solution_step_info", listMeasure);
            }else{
                jsonObject.put("solution_step_info", new JSONArray());
            }
            mapList.add(jsonObject);
        }

        return mapList;
    }


    /**
     * 对必传参数进行处理 并进行带条件查询
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @return List<QuestionSolutionEditEntity> 封装后实体类的集合
     */
    private List<QuestionSolutionEditEntity> queryByCondition(JSONArray dataContent, Long tenantSid) {
        List<QuestionSolutionEditEntity> entities;
        //平台查询只支持传一笔数据 规格使用数组传参
        JSONObject jsonObject = dataContent.getJSONObject(0);
        entities = questionSolutionEditMapper.getQuestionSolutionEditInfo(
                tenantSid,
                jsonObject.getString("solution_id"),
                jsonObject.getString("solution_name"),
                jsonObject.getString("manage_status"),
                jsonObject.getString("is_default"),
                jsonObject.getString("liable_person_id"),
                jsonObject.getString("liable_person_name"));
        return entities;
    }


    /**
     * 封装解决方案步骤实体类
     *
     * @param tenantSid           租户id
     * @param userName            用户名
     * @param solutionMeasureData 解决方案步骤数据
     * @param editEntity          解决方案实体类
     * @param oid                 解决方案主键
     * @param j                   循环索引
     * @param methodType          方法类型
     * @return QuestionSolutionMeasureEntity
     * @throws IOException
     */
    private QuestionSolutionMeasureEntity encapsulateSolutionMeasureEntity(Long tenantSid, String userName, JSONArray solutionMeasureData, QuestionSolutionEditEntity editEntity, String oid, int j, String methodType) throws IOException, OperationException {
        JSONObject measureData = solutionMeasureData.getJSONObject(j);
        QuestionSolutionMeasureModel measureModel = new ObjectMapper().readValue(measureData.toString().getBytes(), QuestionSolutionMeasureModel.class);
        QuestionSolutionMeasureEntity measureEntity = new QuestionSolutionMeasureEntity();
        org.springframework.beans.BeanUtils.copyProperties(measureModel, measureEntity);
        measureEntity.setExpectCompleteTime(DateUtil.hours2Minutes(measureEntity.getExpectCompleteTime()));
        if (methodType.equals(MethodTypeConstant.UPDATE)) {
            measureEntity.setUpdateName(userName);
            measureEntity.setUpdateTime(new Date());
            return measureEntity;
        }
        measureEntity.setOid(IdGenUtil.uuid());
        measureEntity.setTenantSid(tenantSid);
        measureEntity.setCreateTime(new Date());
        measureEntity.setCreateName(userName);
        measureEntity.setSolutionEditOid(oid);
        //添加编号 在方案编号的基础上再加三位数升序
        List<String> measureNos = questionSolutionMeasureMapper.queryAllMeasureNosByPrefix(editEntity.getSolutionNo(),tenantSid);
        int maxNumber = 0;
        if (CollUtil.isNotEmpty(measureNos)) {
            String maxMeasureNo = measureNos.get(measureNos.size() - 1);
            String lastNumberStr = maxMeasureNo.substring(maxMeasureNo.length() - 3);
            maxNumber = Integer.parseInt(lastNumberStr);
        }
        String serialNumberMeasure = NumberUtil.frontCompWithZore(maxNumber + 1, 3);
        measureEntity.setMeasureNo(editEntity.getSolutionNo() + serialNumberMeasure);
        return measureEntity;
    }

    /**
     * 封装解决方案配置实体类
     *
     * @param tenantSid  租户id
     * @param userName   用户名
     * @param jsonObject
     * @param methodType 方法类型
     * @return QuestionSolutionEditEntity
     * @throws IOException
     */
    private QuestionSolutionEditEntity encapsulateSolutionEntity(Long tenantSid, String userName, JSONObject jsonObject, String methodType) throws IOException {
        jsonObject.remove("solution_step_info");
        QuestionSolutionEditModel editModel = new ObjectMapper().readValue(jsonObject.toString().getBytes(), QuestionSolutionEditModel.class);
        QuestionSolutionEditEntity editEntity = new QuestionSolutionEditEntity();
        BeanUtils.copyProperties(editModel, editEntity);
        if (methodType.equals(MethodTypeConstant.UPDATE)) {
            editEntity.setUpdateName(userName);
            editEntity.setUpdateTime(new Date());
            return editEntity;
        }
        editEntity.setOid(IdGenUtil.uuid());
        editEntity.setTenantSid(tenantSid);
        editEntity.setCreateTime(new Date());
        editEntity.setCreateName(userName);
        //添加方案编号  固定SE+三位数升序
        String prefix = DWApplicationConfigUtils.getProperty("solutionNoPrefix");
        List<String> solutionIds = questionSolutionEditMapper.queryAllSolutionIds(tenantSid);
        int maxNumber = 0;
        if (CollUtil.isNotEmpty(solutionIds)) {
            String maxSolutionId = solutionIds.get(solutionIds.size() - 1);
            String lastNumberStr = maxSolutionId.substring(maxSolutionId.length() - 3);
            maxNumber = Integer.parseInt(lastNumberStr);
        }
        String serialNumber = NumberUtil.frontCompWithZore(maxNumber + 1, 3);
        editEntity.setSolutionNo(prefix + serialNumber);
        return editEntity;
    }


}
