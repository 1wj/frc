package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.KeyBoardTemplateBiz;
import com.digiwin.app.frc.service.athena.mtw.common.constants.MethodTypeConstant;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.KeyBoardFieldEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.KeyBoardTemplateEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.model.KeyBoardFieldModel;
import com.digiwin.app.frc.service.athena.mtw.domain.model.KeyBoardTemplateModel;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.KeyBoardFieldVo;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.KeyBoardTemplateVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.KeyBoardAuthorityMapper;
import com.digiwin.app.frc.service.athena.mtw.mapper.KeyBoardDisplayMapper;
import com.digiwin.app.frc.service.athena.mtw.mapper.KeyBoardFieldMapper;
import com.digiwin.app.frc.service.athena.mtw.mapper.KeyBoardTemplateMapper;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.NumberUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.ListUtils;
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
 * @Date: 2021/11/22 10:26
 * @Version 1.0
 * @Description ??????????????????Biz-impl
 */
@Service
public class KeyBoardTemplateBizImpl implements KeyBoardTemplateBiz {

    @Autowired
    private KeyBoardTemplateMapper keyBoardTemplateMapper;

    @Autowired
    private KeyBoardFieldMapper keyBoardFieldMapper;

    @Autowired
    private KeyBoardDisplayMapper keyBoardDisplayMapper;

    @Autowired
    private KeyBoardAuthorityMapper keyBoardAuthorityMapper;

    @Override
    public List addKeyBoardTemplate(JSONArray keyBoardTemplateData) throws DWArgumentException, IOException {
        //????????????????????????
        List<KeyBoardTemplateEntity> templateEntities = new ArrayList<>();
        List mapList = new ArrayList<>();
        for (int i = 0; i < keyBoardTemplateData.size(); i++) {
            JSONObject jsonObject = keyBoardTemplateData.getJSONObject(i);
            JSONArray keyBoardFieldData = jsonObject.getJSONArray("kanban_field_info");
            //?????????????????? ???????????????????????????
            if (StringUtils.isEmpty(keyBoardFieldData) || keyBoardFieldData.isEmpty()) {
                KeyBoardTemplateEntity templateEntity = encapsulateSolutionEntity(TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName(), jsonObject, MethodTypeConstant.ADD);
                keyBoardTemplateMapper.addKeyBoardTemplateInfo(templateEntity);
                templateEntities.add(templateEntity);
                //???????????????  ??????????????????????????????????????????
                List<Map<String, Object>> mapList1 = convertDataAll(templateEntities, null);
                mapList = ListUtils.union(mapList, mapList1);
                continue;
            }
            //?????????????????? ??????????????????????????? (????????? ????????????id?????????????????????????????? ????????????)
            String kanbanTemplateId = jsonObject.getString("kanban_template_id");
            String templateNo = jsonObject.getString("template_no");
            if (!StringUtils.isEmpty(kanbanTemplateId) && !StringUtils.isEmpty(templateNo)) {
                List<Map<String, Object>> mapList2 = convertDataAll(TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName(), keyBoardFieldData, kanbanTemplateId, templateNo);
                mapList = ListUtils.union(mapList, mapList2);
                continue;
            }
            //???????????????????????????????????????????????????????????????
            KeyBoardTemplateEntity templateEntity = encapsulateSolutionEntity(TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName(), jsonObject, MethodTypeConstant.ADD);
            keyBoardTemplateMapper.addKeyBoardTemplateInfo(templateEntity);
            String oid = templateEntity.getOid();
            if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
                throw new DWArgumentException("kanBanTemplateId",
                        MultilingualismUtil.getLanguage("notExist"));
            }
            for (int j = 0; j < keyBoardFieldData.size(); j++) {
                KeyBoardFieldEntity measureEntity = encapsulateKeyBoardFieldEntity(TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName(), keyBoardFieldData, templateEntity, oid, j, MethodTypeConstant.ADD);
                keyBoardFieldMapper.addKeyBoardFieldInfo(measureEntity);
            }
            templateEntities.add(templateEntity);
            //???????????????  ??????????????????????????????????????????
            List<Map<String, Object>> mapList3 = convertDataAll(templateEntities, null);
            mapList = ListUtils.union(mapList, mapList3);

        }

        return mapList;
    }


    @Override
    public void deleteKeyBoardTemplate(JSONArray dataContent) {
        //????????????????????????????????????????????????   ??????  ???????????????????????????
        for (int i = 0; i < dataContent.size(); i++) {
            String keyboardTemplateOid = dataContent.getJSONObject(i).getString("kanban_template_id");
            JSONArray kanbanFieldInfo = (JSONArray) dataContent.getJSONObject(i).get("kanban_field_info");
            if(!StringUtils.isEmpty(kanbanFieldInfo) && !kanbanFieldInfo.isEmpty()){
                for (int j = 0; j < kanbanFieldInfo.size(); j++) {
                    String fieldOid = kanbanFieldInfo.getJSONObject(j).getString("kanban_field_id");
                    keyBoardFieldMapper.deleteKeyBoardFieldById(fieldOid);
                }
            }else{
                keyBoardTemplateMapper.deleteKeyBoardTemplateById(keyboardTemplateOid);
                keyBoardFieldMapper.deleteKeyBoardFieldByTemplateOid(keyboardTemplateOid);
                //???????????????????????? ?????????????????????????????????????????????
                keyBoardAuthorityMapper.deleteKeyBoardAuthorityInfoByModelOid(keyboardTemplateOid);
            }
        }
    }


    @Override
    public void updateKeyBoardTemplate(JSONArray keyboardModelData) throws DWArgumentException, IOException {
        //????????????????????????
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        for (int i = 0; i < keyboardModelData.size(); i++) {
            JSONObject jsonObject = keyboardModelData.getJSONObject(i);
            String keyboardTemplateOid = jsonObject.getString("kanban_template_id");
            if (StringUtils.isEmpty(keyboardTemplateOid) || keyboardTemplateOid.isEmpty()) {
                throw new DWArgumentException("kanBanTemplateId", MultilingualismUtil.getLanguage("notExist"));
            }
            JSONArray keyboardFieldData = jsonObject.getJSONArray("kanban_field_info");
            //?????????????????? ???????????????????????????  ????????????????????????????????????????????????   ?????????????????????????????????????????????
            if (StringUtils.isEmpty(keyboardFieldData) || keyboardFieldData.isEmpty()) {
                KeyBoardTemplateEntity templateEntity = encapsulateSolutionEntity(tenantSid, updateName, jsonObject, MethodTypeConstant.UPDATE);
                keyBoardTemplateMapper.updateKeyBoardTemplateInfo(templateEntity,tenantSid);
                keyBoardDisplayMapper.updateDisplayInfoByModelOid(tenantSid,templateEntity.getOid(),templateEntity.getModelName(),null,null);
                keyBoardAuthorityMapper.updateRelatedKanbanInfoByModelOid(templateEntity.getOid(),tenantSid,templateEntity.getModelName());
                continue;
            }
            //?????????????????????????????????????????????????????????????????????    ????????????????????????????????????????????????   ?????????????????????????????????????????????
            KeyBoardTemplateEntity templateEntity = encapsulateSolutionEntity(tenantSid, updateName, jsonObject, MethodTypeConstant.UPDATE);
            keyBoardTemplateMapper.updateKeyBoardTemplateInfo(templateEntity,tenantSid);
            keyBoardAuthorityMapper.updateRelatedKanbanInfoByModelOid(templateEntity.getOid(),tenantSid,templateEntity.getModelName());
            for (int j = 0; j < keyboardFieldData.size(); j++) {
                JSONObject fieldInfo = keyboardFieldData.getJSONObject(j);
                String keyboardFieldOid = fieldInfo.getString("kanban_field_id");
                if (StringUtils.isEmpty(keyboardFieldOid) || keyboardFieldOid.isEmpty()) {
                    throw new DWArgumentException("kanBanFieldId", MultilingualismUtil.getLanguage("notExist"));
                }
                KeyBoardFieldEntity fieldEntity = encapsulateKeyBoardFieldEntity(tenantSid, updateName, keyboardFieldData, templateEntity, keyboardTemplateOid, j, MethodTypeConstant.UPDATE);
                keyBoardFieldMapper.updateKeyBoardFieldByEditOid(keyboardFieldOid, keyboardTemplateOid, fieldEntity,tenantSid);
                keyBoardDisplayMapper.updateDisplayInfoByModelOid(tenantSid,templateEntity.getOid(),templateEntity.getModelName(),fieldEntity.getFieldName(),fieldEntity.getFieldId());
            }

        }
    }


    @Override
    public List<Map<String, Object>> getKeyBoardTemplate(JSONArray keyboardModelData) throws JsonProcessingException {
        //????????????????????????
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<Map<String, Object>> mapList;
        if (StringUtils.isEmpty(keyboardModelData) || keyboardModelData.isEmpty()) {
            List<KeyBoardTemplateEntity> templateEntities = keyBoardTemplateMapper.getKeyBoardTemplateInfo(tenantSid, null, null, null, null);
            mapList = convertDataAll(templateEntities, null);
        } else {
            List<KeyBoardTemplateEntity> templateEntities = queryByCondition(keyboardModelData, tenantSid);
            mapList = convertDataAll(templateEntities, keyboardModelData);
        }
        return mapList;
    }


    /**
     * ???????????????????????????????????????????????????(??????)
     *
     * @param tenantSid         ??????id
     * @param userName          ?????????
     * @param keyBoardFieldData ??????????????????
     * @param kanbanTemplateId  ??????????????????
     * @param templateNo        ??????????????????
     * @return List<Map < String, Object>>
     * @throws IOException
     */
    private List<Map<String, Object>> convertDataAll(Long tenantSid, String userName, JSONArray keyBoardFieldData, String kanbanTemplateId, String templateNo) throws IOException {
        List<Map<String, Object>> mapList2 = new ArrayList<>();
        for (int j = 0; j < keyBoardFieldData.size(); j++) {
            KeyBoardFieldEntity measureEntity = encapsulateKeyBoardFieldEntity(tenantSid, userName, keyBoardFieldData, kanbanTemplateId, templateNo, j);
            keyBoardFieldMapper.addKeyBoardFieldInfo(measureEntity);
        }
        KeyBoardTemplateEntity templateEntity = keyBoardTemplateMapper.queryTemplateById(kanbanTemplateId,tenantSid);
        KeyBoardTemplateVo templateVo = new KeyBoardTemplateVo();
        BeanUtils.copyProperties(templateEntity, templateVo);
        JSONObject jo = JSON.parseObject(new ObjectMapper().writeValueAsString(templateVo));
        List<JSONObject> listField = new ArrayList<>();
        List<KeyBoardFieldEntity> fieldEntities = keyBoardFieldMapper.queryFieldInfoByTemplateId(kanbanTemplateId,tenantSid);
        for (KeyBoardFieldEntity fieldEntity : fieldEntities) {
            KeyBoardFieldVo vo = new KeyBoardFieldVo();
            BeanUtils.copyProperties(fieldEntity, vo);
            JSONObject fieldObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            listField.add(fieldObject);
        }
        jo.put("kanban_field_info", listField);
        mapList2.add(jo);
        return mapList2;
    }


    /**
     * ???????????????????????????????????????????????????(????????????????????????????????????)
     *
     * @param templateEntities  ???????????????????????????
     * @param keyboardModelData ??????????????????
     * @return List<Map < String, Object>>
     * @throws JsonProcessingException
     */
    private List<Map<String, Object>> convertDataAll(List<KeyBoardTemplateEntity> templateEntities, JSONArray keyboardModelData) throws JsonProcessingException {
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (KeyBoardTemplateEntity templateEntity : templateEntities) {
            KeyBoardTemplateVo templateVo = new KeyBoardTemplateVo();
            BeanUtils.copyProperties(templateEntity, templateVo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(templateVo));
            String templateEntityOid = templateEntity.getOid();
            List<KeyBoardFieldEntity> fieldEntities;
            //???????????????  ???????????????????????????????????????
            // ????????????????????????id?????????????????????id???????????????????????????  ???????????????
            if (!StringUtils.isEmpty(keyboardModelData) && !keyboardModelData.isEmpty()) {
                JSONObject templateInfo = keyboardModelData.getJSONObject(0);
                String keyboardTemplateOid = templateInfo.getString("kanban_template_id");
                if (!StringUtils.isEmpty(keyboardTemplateOid) && !keyboardTemplateOid.isEmpty() && keyboardTemplateOid.equals(templateEntityOid)) {
                    JSONArray fieldData = (JSONArray) templateInfo.get("kanban_field_info");
                    fieldEntities = keyBoardFieldMapper.queryFieldInfoByConditions(keyboardTemplateOid,
                            fieldData.getJSONObject(0).getString("field_name"),
                            fieldData.getJSONObject(0).getString("manage_status"),
                            fieldData.getJSONObject(0).getString("remarks"));
                } else {
                    fieldEntities = keyBoardFieldMapper.queryFieldInfoByConditions(templateEntityOid, null, null, null);
                }
            } else {
                //????????????  ?????????????????????  ????????????
                //????????????  ?????????????????????????????????????????????
                fieldEntities = keyBoardFieldMapper.queryFieldInfoByConditions(templateEntityOid, null, null, null);
            }
            if (!StringUtils.isEmpty(fieldEntities) && !fieldEntities.isEmpty()) {
                List<JSONObject> listField = new ArrayList<>();
                for (KeyBoardFieldEntity fieldEntity : fieldEntities) {
                    KeyBoardFieldVo vo = new KeyBoardFieldVo();
                    BeanUtils.copyProperties(fieldEntity, vo);
                    JSONObject fieldObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
                    listField.add(fieldObject);
                }
                jsonObject.put("kanban_field_info", listField);
            }
            mapList.add(jsonObject);
        }
        return mapList;
    }


    /**
     * ??????????????????????????? ????????????????????????
     *
     * @param dataContent ???????????????
     * @param tenantSid   ??????id
     * @return List<KeyBoardTemplateEntity> ???????????????????????????
     */
    private List<KeyBoardTemplateEntity> queryByCondition(JSONArray dataContent, Long tenantSid) {
        List<KeyBoardTemplateEntity> entities;
        //???????????????????????????????????? ????????????????????????
        JSONObject jsonObject = dataContent.getJSONObject(0);
        entities = keyBoardTemplateMapper.getKeyBoardTemplateInfo(
                tenantSid,
                jsonObject.getString("template_name"),
                jsonObject.getString("manage_status"),
                jsonObject.getInteger("is_default"),
                jsonObject.getString("remarks"));
        return entities;
    }


    /**
     * ???????????????????????????
     *
     * @param tenantSid         ??????id
     * @param userName          ?????????
     * @param keyBoardFieldData ??????????????????
     * @param templateEntity    ?????????????????????
     * @param oid               ??????????????????
     * @param j                 ????????????
     * @param methodType        ????????????
     * @return KeyBoardFieldEntity ?????????????????????
     * @throws IOException
     */
    private KeyBoardFieldEntity encapsulateKeyBoardFieldEntity(Long tenantSid, String userName, JSONArray keyBoardFieldData, KeyBoardTemplateEntity templateEntity, String oid, int j, String methodType) throws IOException {
        JSONObject fieldData = keyBoardFieldData.getJSONObject(j);
        KeyBoardFieldModel fieldModel = new ObjectMapper().readValue(fieldData.toString().getBytes(), KeyBoardFieldModel.class);
        KeyBoardFieldEntity fieldEntity = new KeyBoardFieldEntity();
        BeanUtils.copyProperties(fieldModel, fieldEntity);
        if (methodType.equals(MethodTypeConstant.UPDATE)) {
            fieldEntity.setUpdateName(userName);
            fieldEntity.setUpdateTime(new Date());
            return fieldEntity;
        }
        fieldEntity.setOid(IdGenUtil.uuid());
        fieldEntity.setTenantSid(tenantSid);
        fieldEntity.setCreateTime(new Date());
        fieldEntity.setCreateName(userName);
        fieldEntity.setKeyBoardTemplateOid(oid);
        //???????????? ??????????????????????????????????????????????????????
        List<String>  fieldNos = keyBoardFieldMapper.queryAllFieldNosByPrefix(templateEntity.getModelNo(),tenantSid);
        int maxNumber = 0;
        if(CollUtil.isNotEmpty(fieldNos)){
            String maxFieldNo = fieldNos.get(fieldNos.size() - 1);
            String lastNumberStr = maxFieldNo.substring(maxFieldNo.length() - 3);
            maxNumber = Integer.parseInt(lastNumberStr);
        }
        String serialNumberMeasure = NumberUtil.frontCompWithZore(maxNumber + 1, 3);
        fieldEntity.setFieldId(templateEntity.getModelNo() + serialNumberMeasure);
        return fieldEntity;
    }


    /**
     * ???????????????????????????
     *
     * @param tenantSid         ??????id
     * @param userName          ?????????
     * @param keyBoardFieldData ??????????????????
     * @param kanbanTemplateId  ??????????????????
     * @param templateNo        ??????????????????
     * @param j                 ????????????
     * @return KeyBoardFieldEntity ?????????????????????
     * @throws IOException
     */
    private KeyBoardFieldEntity encapsulateKeyBoardFieldEntity(Long tenantSid, String userName, JSONArray keyBoardFieldData, String kanbanTemplateId, String templateNo, int j) throws IOException {
        JSONObject fieldData = keyBoardFieldData.getJSONObject(j);
        KeyBoardFieldModel fieldModel = new ObjectMapper().readValue(fieldData.toString().getBytes(), KeyBoardFieldModel.class);
        KeyBoardFieldEntity fieldEntity = new KeyBoardFieldEntity();
        BeanUtils.copyProperties(fieldModel, fieldEntity);
        fieldEntity.setOid(IdGenUtil.uuid());
        fieldEntity.setTenantSid(tenantSid);
        fieldEntity.setCreateTime(new Date());
        fieldEntity.setCreateName(userName);
        fieldEntity.setKeyBoardTemplateOid(kanbanTemplateId);
        //???????????? ??????????????????????????????????????????????????????
        List<String>  fieldNos = keyBoardFieldMapper.queryAllFieldNosByPrefix(templateNo,tenantSid);
        int maxNumber = 0;
        if(CollUtil.isNotEmpty(fieldNos)){
            String maxFieldNo = fieldNos.get(fieldNos.size() - 1);
            String lastNumberStr = maxFieldNo.substring(maxFieldNo.length() - 3);
            maxNumber = Integer.parseInt(lastNumberStr);
        }
        String serialNumberMeasure = NumberUtil.frontCompWithZore(maxNumber + 1, 3);
        fieldEntity.setFieldId(templateNo + serialNumberMeasure);
        return fieldEntity;
    }


    /**
     * ???????????????????????????
     *
     * @param tenantSid  ??????id
     * @param userName   ?????????
     * @param jsonObject
     * @param methodType ????????????
     * @return KeyBoardTemplateEntity
     * @throws IOException
     */
    private KeyBoardTemplateEntity encapsulateSolutionEntity(Long tenantSid, String userName, JSONObject jsonObject, String methodType) throws IOException {
        jsonObject.remove("kanban_field_info");
        KeyBoardTemplateModel templateModel = new ObjectMapper().readValue(jsonObject.toString().getBytes(), KeyBoardTemplateModel.class);
        KeyBoardTemplateEntity templateEntity = new KeyBoardTemplateEntity();
        BeanUtils.copyProperties(templateModel, templateEntity);
        if (methodType.equals(MethodTypeConstant.UPDATE)) {
            templateEntity.setUpdateName(userName);
            templateEntity.setUpdateTime(new Date());
            return templateEntity;
        }
        templateEntity.setOid(IdGenUtil.uuid());
        templateEntity.setTenantSid(tenantSid);
        templateEntity.setCreateTime(new Date());
        templateEntity.setCreateName(userName);
        //??????????????????  ??????KM+???????????????
        String prefix = DWApplicationConfigUtils.getProperty("keyboardTemplateNoPrefix");
        List<String> templateNos = keyBoardTemplateMapper.queryAllTemplateNos(tenantSid);
        int maxNumber = 0;
        if(CollUtil.isNotEmpty(templateNos)){
            String maxTemplateNo = templateNos.get(templateNos.size() - 1);
            String lastNumberStr = maxTemplateNo.substring(maxTemplateNo.length() - 3);
            maxNumber = Integer.parseInt(lastNumberStr);
        }
        String serialNumber = NumberUtil.frontCompWithZore(maxNumber + 1, 3);
        templateEntity.setModelNo(prefix + serialNumber);
        return templateEntity;
    }

}
