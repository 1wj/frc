package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.QuestionBackBiz;
import com.digiwin.app.frc.service.athena.mtw.common.constants.QuestionBackConstant;
import com.digiwin.app.frc.service.athena.mtw.common.enums.QuestionBackNodeNoEnum;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionBackEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.model.QuestionBackModel;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.QuestionBackVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionBackMapper;
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
 * @Date: 2021/11/15 17:10
 * @Version 1.0
 * @Description 问题退回处理Biz-impl
 */
@Service
public class QuestionBackBizImpl implements QuestionBackBiz {

    @Autowired
    private QuestionBackMapper questionBackMapper;

    @Override
    public List<JSONObject> addQuestionBack(JSONArray dataContent) throws IOException, DWArgumentException {
        //添加问题退回信息
        CheckFieldValueUtil.validateModels(dataContent,new QuestionBackModel());
        //对必传参数进行校验 并进行封装成entity
        List<QuestionBackEntity> entities = checkAndHandleData(dataContent, TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName());
        int result = questionBackMapper.addQuestionBackInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }


    @Override
    public boolean deleteQuestionBack(JSONArray dataContent) {
        //删除问题退回信息
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(QuestionBackConstant.RETURN_ID);
            oidList.add(oid);
        }
        int result = questionBackMapper.deleteQuestionBackInfo(oidList);
        return result > 0;
    }


    @Override
    public boolean updateQuestionBack(JSONArray dataContent) throws IOException, DWArgumentException {
        //修改问题退回信息
        CheckFieldValueUtil.validateModels(dataContent,new QuestionBackModel());
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionBackEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            QuestionBackEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = questionBackMapper.updateBatch(entities);
        return result > 0;
    }

    @Override
    public List<JSONObject> getQuestionBack(JSONArray dataContent) throws JsonProcessingException {
        //获取问题退回信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionBackEntity> entities;
        if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
            entities = questionBackMapper.getQuestionBackInfo(tenantSid, null, null, null,null);
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
     * @return List<QuestionBackEntity> 封装后实体类的集合
     */
    private List<QuestionBackEntity> queryByCondition(JSONArray dataContent, Long tenantSid) {
        List<QuestionBackEntity> entities;
        //平台查询只支持传一笔数据 规格使用数组传参
        JSONObject jsonObject = dataContent.getJSONObject(0);
        String nodeNoCompose = "";
        String nodeName = jsonObject.getString(QuestionBackConstant.NODE_NO);
        if(!StringUtils.isEmpty(nodeName) && !nodeName.isEmpty()){
            if(QuestionBackNodeNoEnum.CLOSE.getCode().equals(nodeName)) {
                nodeNoCompose = QuestionBackNodeNoEnum.DISTRIBUTION.getCode()+ "," + QuestionBackNodeNoEnum.CONTAIN.getCode();
            }else if(QuestionBackNodeNoEnum.DISCERN.getCode().equals(nodeName)){
                nodeNoCompose = QuestionBackNodeNoEnum.FEEDBACK.getCode();
            }else if(QuestionBackNodeNoEnum.DISTRIBUTION.getCode().equals(nodeName)){
               nodeNoCompose = QuestionBackNodeNoEnum.FEEDBACK.getCode()+","+QuestionBackNodeNoEnum.DISCERN.getCode();
            }else if(QuestionBackNodeNoEnum.ACCEPTANCE.getCode().equals(nodeName)){
                nodeNoCompose = QuestionBackNodeNoEnum.FEEDBACK.getCode()+","+QuestionBackNodeNoEnum.DISCERN.getCode()+","+QuestionBackNodeNoEnum.DISTRIBUTION.getCode();
            }else if(QuestionBackNodeNoEnum.IDENTIFYVIEW.getCode().equals(nodeName)){
                nodeNoCompose = QuestionBackNodeNoEnum.FEEDBACK.getCode()+","+QuestionBackNodeNoEnum.DISCERN.getCode();
            }else if(QuestionBackNodeNoEnum.EightD1ANDD2.getCode().equals(nodeName)){
                nodeNoCompose = QuestionBackNodeNoEnum.DISCERN.getCode();
            } else{
                nodeNoCompose = "";
            }
        }
        entities = questionBackMapper.getQuestionBackInfo(
                tenantSid,
                jsonObject.getString(QuestionBackConstant.RETURN_NO),
                jsonObject.getString(QuestionBackConstant.RETURN_REASON),
                nodeNoCompose,
                nodeNoCompose);
        return entities;
    }


    /**
     * 对必传参数进行校验 并进行封装成entity
     *
     * @param dataContent 解析后数据
     * @param updateName  修改人姓名
     * @param i           循环遍历的索引值
     * @return QuestionBackEntity 封装后的实体类
     * @throws DWArgumentException
     */
    private QuestionBackEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException, IOException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(QuestionBackConstant.RETURN_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("returnId",
                    MultilingualismUtil.getLanguage("notExist"));
        }
        //对编号进行校验  只能数字、字母和短横线组成
        if(!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(QuestionBackConstant.RETURN_NO))){
            throw new DWArgumentException("returnNo",MultilingualismUtil.getLanguage("NumberRules"));
        }
        QuestionBackEntity entity = new QuestionBackEntity();
        QuestionBackModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), QuestionBackModel.class);
        BeanUtils.copyProperties(model, entity);
        if(!StringUtils.isEmpty(jsonObject.getString(QuestionBackConstant.NODE_NAME))){
            entity.setNodeId(jsonObject.getString(QuestionBackConstant.NODE_NAME));
        }
        entity.setUpdateName(updateName);
        entity.setUpdateTime(new Date());
        entity.setTenantSid(tenantSid);
        return entity;
    }


    /**
     * 将实体类集合转成前端要求的格式集合
     *
     * @param entities 问题分类实体集合
     * @return List<JSONObject> 返回前端数据类型
     */
    private List<JSONObject> convertData(List<QuestionBackEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (QuestionBackEntity entity : entities) {
            QuestionBackVo vo = new QuestionBackVo();
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
     * @return List<QuestionBackEntity> 封装后实体类集合
     * @throws DWArgumentException
     */
    private List<QuestionBackEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException, IOException {
        List<QuestionBackEntity> entities = new ArrayList<>();
        //查询所有问题编号信息
        List<String> returnNos = questionBackMapper.queryAllReturnNos(tenantSid);
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if (!jsonObject.containsKey(QuestionBackConstant.RETURN_NO) || StringUtils.isEmpty(jsonObject.getString(QuestionBackConstant.RETURN_NO))) {
                throw new DWArgumentException("returnNo", MultilingualismUtil.getLanguage("notExist"));
            }
            if(CollUtil.isNotEmpty(returnNos) && returnNos.contains(jsonObject.getString(QuestionBackConstant.RETURN_NO))){
                throw new DWArgumentException("returnNo", MultilingualismUtil.getLanguage("NoAlreadyExist"));
            }
            //对编号进行校验  只能数字、字母和短横线组成
            if(!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(QuestionBackConstant.RETURN_NO))){
                throw new DWArgumentException("returnNo",MultilingualismUtil.getLanguage("NumberRules"));
            }
            if (!jsonObject.containsKey(QuestionBackConstant.RETURN_REASON) || StringUtils.isEmpty(jsonObject.getString(QuestionBackConstant.RETURN_REASON))) {
                throw new DWArgumentException("returnReason", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(QuestionBackConstant.NODE_NAME) || StringUtils.isEmpty(jsonObject.getString(QuestionBackConstant.NODE_NAME))) {
                throw new DWArgumentException("nodeName", MultilingualismUtil.getLanguage("notExist"));
            }
            QuestionBackModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), QuestionBackModel.class);
            QuestionBackEntity entity = new QuestionBackEntity();
            BeanUtils.copyProperties(model, entity);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantSid);
            entity.setCreateTime(new Date());
            entity.setCreateName(userName);
            entity.setNodeId(jsonObject.getString(QuestionBackConstant.NODE_NAME));
            entities.add(entity);
        }
        return entities;
    }
}
