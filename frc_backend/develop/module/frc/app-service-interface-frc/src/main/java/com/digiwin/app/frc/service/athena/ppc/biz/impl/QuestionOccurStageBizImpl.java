package com.digiwin.app.frc.service.athena.ppc.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionClassificationEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionSourceEntity;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionClassificationMapper;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionSourceMapper;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionOccurStageBiz;
import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionOccurStageEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.model.QuestionOccurStageModel;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.QuestionOccurStageVo;
import com.digiwin.app.frc.service.athena.ppc.mapper.QuestionOccurStageMapper;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author:zhangzlz
 * @Date 2022/2/11   10:42
 */
@Service
public class QuestionOccurStageBizImpl implements QuestionOccurStageBiz {

    @Autowired
    QuestionClassificationMapper questionClassificationsMapper;

    @Autowired
    QuestionSourceMapper questionSourceMapper;

    @Autowired
    QuestionOccurStageMapper questionOccurStageMapper;

    @Override
    public Map addQuestionOccurStageInfo(JSONArray jsonArray) throws Exception {
        //校验参数并封装entity
        List<QuestionOccurStageEntity> entities = getCheckHandleParam(jsonArray);
        int i = questionOccurStageMapper.addQuestionOccurStageInfo(entities);
        //将entity转换成前端需要的格式
        Map<String, Object> map = new HashMap<>();
        map.put("occur_stage_info",convert(entities));
        return i > 0 ? map: null ;
    }

    @Override
    public Boolean deleteQuestionOccurStageInfo(JSONArray jsonArray) throws Exception {
        //先解析参数分装为oid主键集合
        List<String> idList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            String occurStageId = jsonArray.getJSONObject(i).getString("occur_stage_id");
            idList.add(occurStageId);
        }
        //传递租户id和oid进行删除
        int i = questionOccurStageMapper.deleteQuestionOccurStageInfo(idList, TenantTokenUtil.getTenantSid());
        //判断是否删除成功
        return i > 0;
    }

    @Override
    public Boolean updateQuestionOccurStageInfo(JSONArray jsonArray)throws Exception {
        //校验参数，设置修改人，修改时间，封装为entity
        List<QuestionOccurStageEntity> entities = updateCheckHandleParam(jsonArray);
        int i = questionOccurStageMapper.updateQuesOccurStageInfo(entities, TenantTokenUtil.getTenantSid());
        return i > 0;
    }

    @Override
    public List<Map<String, Object>> getQuestionOccurStageInfo(JSONArray jsonArray) throws Exception  {
        //判断前端数组是否为空
        if (StringUtils.isEmpty(jsonArray)||jsonArray.isEmpty()){
            List<QuestionOccurStageEntity> entities = questionOccurStageMapper.getQuesOccurStageInfo(null, null, null,null,null,null, TenantTokenUtil.getTenantSid());
            //将查询到的实体转换成前端需要的样式
            return getConvert(entities);
        }
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        //从json对象中获取查询的参数条件
        String occurStageNo = jsonObject.getString("occur_stage_no");
        String occurStageName = jsonObject.getString("occur_stage_name");
        String manageStatus = jsonObject.getString("manage_status");
        String classificationOid = jsonObject.getString("question_classification_id");
        String sourceOid = jsonObject.getString("question_source_id");
        String attributionNo = jsonObject.getString("question_attribution");
        List<QuestionOccurStageEntity> entities = questionOccurStageMapper.getQuesOccurStageInfo(occurStageNo, occurStageName, manageStatus,classificationOid,sourceOid,attributionNo,TenantTokenUtil.getTenantSid());
        //将查询到的实体转换成前端需要的样式
        return getConvert(entities);
    }

    /**
     * 校验参数并封装实体——更新操作
     * @param jsonArray occur_stage_info 数据体
     * @return 返回封装的数据实体
     * @throws Exception
     */
    private List<QuestionOccurStageEntity> updateCheckHandleParam(JSONArray jsonArray)throws Exception{
        List<QuestionOccurStageEntity> questionOccurStageEntityList = new ArrayList<>();
        //查询所有主键
        List<String> ids = questionOccurStageMapper.getAllQesOccurStageIds(TenantTokenUtil.getTenantSid());
        //校验参数,判断主键、名称、编号是否已存在
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (!jsonObject.containsKey("occur_stage_id")||!ids.contains(jsonObject.getString("occur_stage_id"))){
                throw new DWArgumentException("occur_stage_id", MultilingualismUtil.getLanguage("notExist"));
            }
            //分装entity
            QuestionOccurStageModel model = new ObjectMapper().readValue(jsonObject.toJSONString(), QuestionOccurStageModel.class);
            QuestionOccurStageEntity entity = new QuestionOccurStageEntity();
            BeanUtils.copyProperties(model,entity);
            entity.setUpdateName(TenantTokenUtil.getUserName());
            entity.setUpdateTime(new Date());
            questionOccurStageEntityList.add(entity);
        }
        return questionOccurStageEntityList;
    }

    /**
     * 校验参数封装实体——添加操作
     * @param jsonArray occur_stage_info 数据体
     * @return 返回封装的数据实体集合
     * @throws Exception
     */
    private List<QuestionOccurStageEntity> getCheckHandleParam(JSONArray jsonArray)throws Exception{
        List<QuestionOccurStageEntity> questionOccurStageEntityList = new ArrayList<>();
        //查询所有问题发生阶段的编号和名称
        List<String> names = questionOccurStageMapper.getAllQesOccurStageNames(TenantTokenUtil.getTenantSid());
        List<String> nos = questionOccurStageMapper.getAllQesOccurStageNos(TenantTokenUtil.getTenantSid());
        //校验参数
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (StringUtils.isEmpty(jsonObject.getString("occur_stage_no"))){
                throw new DWArgumentException("occur_stage_no", MultilingualismUtil.getLanguage("notExist"));
            }
            if (CollUtil.isNotEmpty(nos)&&nos.contains(jsonObject.getString("occur_stage_no"))){
                throw new DWArgumentException("occur_stage_no", MultilingualismUtil.getLanguage("isExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString("occur_stage_name"))){
                throw new DWArgumentException("occur_stage_name", MultilingualismUtil.getLanguage("notExist"));
            }
            if (CollUtil.isNotEmpty(names)&&names.contains(jsonObject.getString("occur_stage_name"))){
                throw new DWArgumentException("occur_stage_name", MultilingualismUtil.getLanguage("isExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString("manage_status"))){
                throw new DWArgumentException("manage_status", MultilingualismUtil.getLanguage("notExist"));
            }
            QuestionOccurStageModel model = new ObjectMapper().readValue(jsonObject.toJSONString(), QuestionOccurStageModel.class);
            QuestionOccurStageEntity entity = new QuestionOccurStageEntity();
            BeanUtils.copyProperties(model,entity);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(TenantTokenUtil.getTenantSid());
            entity.setCreateName(TenantTokenUtil.getUserName());
            entity.setCreateTime(new Date());
            questionOccurStageEntityList.add(entity);
        }
        return questionOccurStageEntityList;
    }

    /**
     * 查询所有问题分类名称
     * @return  返回map集合  key ：问题分类主键  value：问题分类名称
     */
    private Map<String,Object> findQuestionClassificationNames(){
        List<QuestionClassificationEntity> entities = questionClassificationsMapper.queryAll(TenantTokenUtil.getTenantSid());
        Map<String, Object> map = new HashMap<>();
        for (QuestionClassificationEntity entity : entities) {
            map.put(entity.getOid(),entity.getClassificationName());
        }
        return map;
    }

    /**
     * 查询所有问题来源名称
     * @return 返回map集合  key:问题来源主键 value：问题来源名称
     */
    private Map<String,Object> findQuestionSourceNames(){
        List<QuestionSourceEntity> entities = questionSourceMapper.queryAll(TenantTokenUtil.getTenantSid());
        Map<String, Object> map = new HashMap<>();
        for (QuestionSourceEntity entity : entities) {
            map.put(entity.getOid(),entity.getSourceName());
        }
        return map;
    }

    /**
     * 转换成前端需要的格式——添加操作返回
     * @param entities  需要转换实体集合
     * @return 返回封装jsonObject集合
     * @throws Exception
     */
    private List<JSONObject> convert(List<QuestionOccurStageEntity> entities)throws Exception{
        List<JSONObject> map = new ArrayList<>();
        for (QuestionOccurStageEntity entity : entities) {
            QuestionOccurStageVo questionOccurStageVo = new QuestionOccurStageVo();
            BeanUtils.copyProperties(entity,questionOccurStageVo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(questionOccurStageVo));
            map.add(jsonObject);
        }
        return map;
    }


    /**
     * 转换成前端需要的格式——查询操作返回
     *
     * @param entities  需要转换的实体类集合
     * @return  list集合
     */
    private List<Map<String,Object>> getConvert(List<QuestionOccurStageEntity> entities){
        //查询所有问题分类名称、问题来源名称  用于返回字段  classification_name  source_name
        Map<String, Object> classificationsNames = findQuestionClassificationNames();
        Map<String, Object> sourceNames = findQuestionSourceNames();
        //将查询到的实体转换成前端需要的样式
        return entities.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("occur_stage_id",m.getOid());
            map.put("occur_stage_name",m.getOccurStageName());
            map.put("occur_stage_no",m.getOccurStageNo());
            map.put("question_attribution",m.getAttributionNo());
            map.put("question_source_id",m.getSourceOid());
            map.put("question_source_name",sourceNames.get(m.getSourceOid()));
            map.put("question_classification_id",m.getClassificationOid());
            map.put("question_classification_name",classificationsNames.get(m.getClassificationOid()));
            map.put("remarks",m.getRemarks());
            map.put("manage_status",m.getManageStatus());
            return map;
        }).collect(Collectors.toList());


    }

}
