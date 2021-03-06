package com.digiwin.app.frc.service.athena.ppc.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.common.Const.QuestionResponseConst;
import com.digiwin.app.frc.service.athena.common.enums.qdh.QuestionSolveEnum;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.QuestionBackEntity;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionBackMapper;
import com.digiwin.app.frc.service.athena.mtw.mapper.QuestionClassificationMapper;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionProcessConfigBiz;
import com.digiwin.app.frc.service.athena.ppc.constants.QuestionProcessConfigConstant;
import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionProcessConfigEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.model.QuestionProcessConfigModel;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.QuestionProcessConfigVo;
import com.digiwin.app.frc.service.athena.ppc.mapper.QuestionProcessConfigMapper;
import com.digiwin.app.frc.service.athena.ppc.mapper.QuestionRiskLevelMapper;
import com.digiwin.app.frc.service.athena.qdh.biz.IamEocBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.vo.QuestionDetailVo;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.DataInstanceMapper;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * @Author: xieps
 * @Date: 2022/2/17 10:39
 * @Version 1.0
 * @Description
 */
@Service
public class QuestionProcessConfigBizImpl implements QuestionProcessConfigBiz {

    @Autowired
    private QuestionRiskLevelMapper riskLevelMapper;

    @Autowired
    private QuestionClassificationMapper classificationMapper;

    @Autowired
    private QuestionProcessConfigMapper questionProcessConfigMapper;

    @Override
    public List<JSONObject> addQuestionProcessConfig(JSONArray dataContent) throws IOException, DWArgumentException {
        //????????????????????????????????????
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        Long tenantSid = (Long) profile.get("tenantSid");
        String userName = (String) profile.get("userName");
        //??????????????????????????? ??????????????????entity
        List<QuestionProcessConfigEntity> entities = checkAndHandleData(dataContent, tenantSid, userName);
        int result = questionProcessConfigMapper.addQuestionProcessConfigInfo(entities);
        //entity????????????????????????_?????????????????????
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }


    @Override
    public boolean deleteQuestionProcessConfig(JSONArray dataContent) {
        //??????????????????????????????
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(QuestionProcessConfigConstant.PROCESS_CONFIG_ID);
            oidList.add(oid);
        }
        int result = questionProcessConfigMapper.deleteQuestionProcessConfigInfo(oidList,tenantSid);
        classificationMapper.updateProcessConfigIdByDelete(oidList,tenantSid);
        riskLevelMapper.updateProcessConfigIdByDelete(oidList,tenantSid);
        return result > 0;
    }


    @Override
    public boolean updateQuestionProcessConfigInfo(JSONArray dataContent) throws IOException, DWArgumentException {
        //??????????????????????????????
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        List<QuestionProcessConfigEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            QuestionProcessConfigEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = questionProcessConfigMapper.updateBatch(entities);
        return result > 0;
    }


    @Override
    public List<JSONObject> getQuestionProcessConfig(JSONArray dataContent) throws JsonProcessingException {
        //??????????????????????????????
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<QuestionProcessConfigVo> vos;
        if(StringUtils.isEmpty(dataContent) || dataContent.isEmpty()){
            vos = questionProcessConfigMapper.getQuestionProcessConfig(tenantSid, new QuestionProcessConfigModel());
        }else {
            //???????????????????????????????????? ????????????????????????
            JSONObject jsonObject = dataContent.getJSONObject(0);
            //??????????????????????????????model???
            QuestionProcessConfigModel model = JSON.parseObject(jsonObject.toJSONString(), QuestionProcessConfigModel.class);
            vos = questionProcessConfigMapper.getQuestionProcessConfig(tenantSid, model);
        }
        //????????????????????????_?????????????????????
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (QuestionProcessConfigVo vo : vos) {
            JSONObject jsonObjectInfo = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObjectList.add(jsonObjectInfo);
        }
        return jsonObjectList;
    }
    /**
     * ??????????????????????????? ??????????????????entity(??????)
     *
     * @param dataContent ???????????????
     * @param updateName  ???????????????
     * @param i           ????????????????????????
     * @return QuestionProcessConfigEntity ?????????????????????
     * @throws DWArgumentException
     */
    private QuestionProcessConfigEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(QuestionProcessConfigConstant.PROCESS_CONFIG_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("processConfigId",
                    MultilingualismUtil.getLanguage("notExist"));
        }
        QuestionProcessConfigModel model = JSON.parseObject(jsonObject.toJSONString(), QuestionProcessConfigModel.class);
        QuestionProcessConfigEntity entity = new QuestionProcessConfigEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setTenantSid(tenantSid);
        entity.setUpdateName(updateName);
        entity.setUpdateTime(new Date());
        JSONArray levelInfo = jsonObject.getJSONArray("risk_level_info");
        if(!StringUtils.isEmpty(levelInfo) && !levelInfo.isEmpty()){
            List<String> levelIds = new ArrayList<>();
            for (int j = 0; j < levelInfo.size(); j++) {
                JSONObject jsonObject1 = levelInfo.getJSONObject(j);
                String riskLevelId = jsonObject1.getString("risk_level_id");
                levelIds.add(riskLevelId);
            }
            riskLevelMapper.updateProcessConfigIdInfo(entity.getOid(),tenantSid);
            riskLevelMapper.updateProcessConfigId(levelIds,entity.getOid(),tenantSid);
        }else{
            riskLevelMapper.updateProcessConfigIdInfo(entity.getOid(),tenantSid);
        }
        JSONArray classificationInfo = jsonObject.getJSONArray("classification_info");
        if(!StringUtils.isEmpty(classificationInfo) && !classificationInfo.isEmpty()){
            List<String> classificationIds = new ArrayList<>();
            for (int j = 0; j < classificationInfo.size(); j++) {
                JSONObject jsonObject1 = classificationInfo.getJSONObject(j);
                String classificationId = jsonObject1.getString("classification_id");
                classificationIds.add(classificationId);
            }
            classificationMapper.updateProcessConfigIdInfo(entity.getOid(),tenantSid);
            classificationMapper.updateProcessConfigId(classificationIds,entity.getOid(),tenantSid);
        }else{
            classificationMapper.updateProcessConfigIdInfo(entity.getOid(),tenantSid);
        }
        return entity;
    }



    /**
     * ??????????????????????????? ??????????????????List<Entity>(??????)
     *
     * @param dataContent ???????????????
     * @param tenantSid   ??????id
     * @param userName    ????????????
     * @return List<QuestionProcessConfigEntity> ????????????????????????
     * @throws DWArgumentException
     */
    private List<QuestionProcessConfigEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException {
        List<QuestionProcessConfigEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //???????????????????????????
            if (StringUtils.isEmpty(jsonObject.getString(QuestionProcessConfigConstant.ATTRIBUTION_NO))) {
                throw new DWArgumentException("attributionNo", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty( jsonObject.getJSONArray("risk_level_info"))) {
                throw new DWArgumentException("riskLevelInfo", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionProcessConfigConstant.LIABLE_PERSON_NAME))) {
                throw new DWArgumentException("liablePersonName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionProcessConfigConstant.LIABLE_PERSON_ID))) {
                throw new DWArgumentException("liablePersonId", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionProcessConfigConstant.SOLUTION_STEP_ID))) {
                throw new DWArgumentException("solutionStepId", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionProcessConfigConstant.SOURCE_ID))) {
                throw new DWArgumentException("sourceId", MultilingualismUtil.getLanguage("notExist"));
            }

            QuestionProcessConfigModel model = JSON.parseObject(jsonObject.toJSONString(), QuestionProcessConfigModel.class);
            QuestionProcessConfigEntity entity = new QuestionProcessConfigEntity();
            BeanUtils.copyProperties(model, entity);

            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantSid);
            entity.setCreateTime(new Date());
            entity.setCreateName(userName);
            JSONArray levelInfo = jsonObject.getJSONArray("risk_level_info");
            if(!StringUtils.isEmpty(levelInfo)) {
                List<String> levelIds = new ArrayList<>();
                for (int j = 0; j < levelInfo.size(); j++) {
                    JSONObject jsonObject1 = levelInfo.getJSONObject(j);
                    String riskLevelId = jsonObject1.getString("risk_level_id");
                    levelIds.add(riskLevelId);
                }
                riskLevelMapper.updateProcessConfigId(levelIds, entity.getOid(), tenantSid);
            }
            entity.setRiskLevelInfo(JSON.toJSONString(levelInfo));
            JSONArray classificationInfo = jsonObject.getJSONArray("classification_info");
            if(!StringUtils.isEmpty(classificationInfo)) {
                List<String> classificationIds = new ArrayList<>();
                for (int j = 0; j < classificationInfo.size(); j++) {
                    JSONObject jsonObject1 = classificationInfo.getJSONObject(j);
                    String classificationId = jsonObject1.getString("classification_id");
                    classificationIds.add(classificationId);
                }
                classificationMapper.updateProcessConfigId(classificationIds, entity.getOid(), tenantSid);
            }
            entity.setClassificationInfo(JSON.toJSONString(classificationInfo));
            entities.add(entity);
        }
        return entities;
    }


    /**
     * ???????????????????????????????????????????????????
     *
     * @param entities ???????????????????????????
     * @return
     */
    private List<JSONObject> convertData(List<QuestionProcessConfigEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (QuestionProcessConfigEntity entity : entities) {
            QuestionProcessConfigVo vo = new QuestionProcessConfigVo();
            BeanUtils.copyProperties(entity, vo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObject.put("risk_level_info",JSON.parseArray(entity.getRiskLevelInfo()));
            jsonObject.put("classification_info",JSON.parseArray(entity.getClassificationInfo()));
            jsonObject.remove("source_name");
            jsonObject.remove("solution_step_name");
            mapList.add(jsonObject);
        }
        return mapList;
    }

    @Autowired
    QuestionBackMapper questionBackMapper;

    @Autowired
    DataInstanceMapper dataInstanceMapper;

    @Autowired
    ActionTraceMapper actionTraceMapper;

    @Autowired
    IamEocBiz iamEocBiz;

    @Autowired
    QuestionProcessConfigBiz questionProcessConfigBiz;

    @Override
    public JSONObject getQuestionDetail(String questionId) {
        // ??????questionId??????????????????
        QuestionDetailVo questionDetailVo =
                actionTraceMapper.getQuestionTrace(questionId);
        if (null == questionDetailVo) {
            // ??????????????????????????????
            return new JSONObject();
        }
        // ?????????????????????
        JSONObject jsonObject = JSON.parseObject(questionDetailVo.getDataContent());
        // detail???????????????????????????
        JSONObject detail = jsonObject.getJSONObject(QuestionResponseConst.QUESTION_RESULT);

        // ?????????????????????????????????
        JSONArray attachmentInfos = detail.getJSONArray("attachment_info");
        for (Iterator<Object> iterator = attachmentInfos.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            String step = (String) obj.get("attachment_belong_stage");
            obj.put("attachment_belong_stage",MultilingualismUtil.getLanguage(step));
        }
        // ??????question_info
        detail.put(QuestionResponseConst.QUESTION_INFO,packageQuestionInfo(questionId,questionDetailVo));
        // ???????????????????????????????????????
        packageDetail(detail,questionDetailVo);

        return jsonObject;
    }


    /**
     * ??????question_info???
     * @param questionId ?????????
     * @param questionDetailVo ????????????????????????
     * @return JSONArray
     */
    private Object packageQuestionInfo(String questionId,QuestionDetailVo questionDetailVo){
        JSONObject object = new JSONObject();
        object.put(QuestionResponseConst.QUESTION_ID,questionId);
        object.put(QuestionResponseConst.QUESTION_PROCESS_STATUS,String.valueOf(questionDetailVo.getQuestionProcessStatus()));
        object.put(QuestionResponseConst.QUESTION_PROCESS_RESULT,String.valueOf(questionDetailVo.getQuestionProcessResult()));
        object.put(QuestionResponseConst.QUESTION_NO,questionDetailVo.getQuestionNo());
        object.put(QuestionResponseConst.QUESTION_RECORD_ID,questionDetailVo.getQuestionRecordId());
        object.put(QuestionResponseConst.QUESTION_FLAG_ID,questionDetailVo.getReturnFlagId());
        object.put(QuestionResponseConst.QUESTION_FLAG_NAME,questionDetailVo.getReturnFlagName());
        object.put(QuestionResponseConst.QUESTION_DESCRIPTION,questionDetailVo.getQuestionDescription());
        object.put("liable_person_id",questionDetailVo.getLiablePersonId());
        object.put("liable_person_name",questionDetailVo.getLiablePersonName());
        object.put("liable_person_position_id",questionDetailVo.getLiablePersonPositionId());
        object.put("liable_person_position_name",questionDetailVo.getLiablePersonPositionName());
        object.put("return_reason_no",questionDetailVo.getReturnNo());
        object.put("close_reason",questionDetailVo.getCloseReason());
        // null ??? ??????
        String dataContentString = JSON.toJSONString(object, filter);
        // ???????????????object  question_info?????????????????????
        return JSON.parse(dataContentString);
    }

    private ValueFilter filter = (obj, s, v) -> {
        if (null == v) {
            return "";
        }
        return v;
    };

    /**
     * ????????????????????????????????????????????????????????????athena???????????????????????????????????????????????????????????????????????????
     * @param detail data_content??????
     * @param questionDetailVo ??????????????????????????????????????????
     */
    private void packageDetail(JSONObject detail,QuestionDetailVo questionDetailVo) {
        // ????????????????????????
        detail.put("return_reason_no","");
        // ????????????
        detail.put("return_reason","");
        // ?????????id
        detail.put("return_id","");
        // ???????????????
        detail.put("return_name","");
        // ????????????????????????????????????
        detail.put("return_flag_name","");

        if (!StringUtils.isEmpty(questionDetailVo.getReturnNo())) {
            detail.put("return_reason_no",questionDetailVo.getReturnNo());
            List<QuestionBackEntity> questionBackEntities = questionBackMapper.getQuestionBackInfo(TenantTokenUtil.getTenantSid(),questionDetailVo.getReturnNo(),null,null,null);
            detail.put("return_reason",questionBackEntities.get(0).getBackReason());
            // ?????????????????????
            detail.put("return_id",questionDetailVo.getReturnId());
            detail.put("return_name",questionDetailVo.getReturnName());
        }
        // ?????????
        detail.put("question_no",questionDetailVo.getQuestionNo());
        // ????????????
        detail.put("question_description",questionDetailVo.getQuestionDescription());
        if (!StringUtils.isEmpty(questionDetailVo.getReturnFlagName())) {
            detail.put("return_flag_name",questionDetailVo.getReturnFlagName());
        }
        // ?????????????????????
        JSONObject basicInfo = detail.getJSONObject("question_basic_info");
        detail.put("question_proposer_id",basicInfo.get("question_proposer_id"));
        detail.put("question_proposer_name",basicInfo.get("question_proposer_name"));
        // ???????????????
        if ("QF".equals(questionDetailVo.getQuestionProcessStep())) {
            detail.put("risk_level","");
            detail.put("urgency","");
        }else {
            JSONObject identify = detail.getJSONObject("question_identify_info");
            detail.put("risk_level",identify.get("risk_level_name"));
            detail.put("urgency",identify.get("urgency"));
        }
        // ??????????????????????????????
        detail.put("liable_person_id",questionDetailVo.getLiablePersonId());
        detail.put("liable_person_name",questionDetailVo.getLiablePersonName());
        detail.put("liable_person_position_id",questionDetailVo.getLiablePersonPositionId());
        detail.put("liable_person_position_name",questionDetailVo.getLiablePersonPositionName());

        // ???????????????????????????????????????
        try {
            if (QuestionSolveEnum.question_distribution.getCode().equals(questionDetailVo.getQuestionSolveStep()) ||
                    QuestionSolveEnum.question_curb_distribution.getCode().equals(questionDetailVo.getQuestionSolveStep())){
                List<Map<String,Object>> list = iamEocBiz.getUsers();
                JSONArray processPersonInfos = new JSONArray();
                for (Map<String,Object> map : list) {
                    JSONObject personInfo = new JSONObject();
                    personInfo.put("process_person_id",map.get("id"));
                    personInfo.put("process_person_name",map.get("name"));
                    processPersonInfos.add(personInfo);
                }

                detail.put("process_person_info",processPersonInfos);
            }
            if("QIA".equals(questionDetailVo.getQuestionProcessStep()) || "QIR".equals(questionDetailVo.getQuestionProcessStep())) {
                packageQIPerson(detail);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * ???????????????????????????
     * @param detail data_content
     */
    private void packageQIPerson(JSONObject detail) throws JsonProcessingException {
        // ???question_basic_info????????? risk_level_oid???question_source_oid???question_attribution_no???question_attribution_no
        JSONObject basicInfo = detail.getJSONObject("question_basic_info");
        // ??????????????????
        JSONArray processConfigs = new JSONArray();
        JSONObject config = new JSONObject();
        config.put("risk_level_id",basicInfo.getString("risk_level_oid"));
        config.put("source_id",basicInfo.getString("question_source_oid"));
        config.put("attribution_no",basicInfo.getString("question_attribution_no"));
        processConfigs.add(config);
        // 4???1????????????????????????null????????? ??????
        List<JSONObject> resultList = questionProcessConfigBiz.getQuestionProcessConfig(processConfigs);
        if (!CollectionUtils.isEmpty(resultList)) {
            // ?????????????????? question_identify_info?????????????????????????????????
            JSONObject identifyInfo = detail.getJSONObject("question_identify_info");
            identifyInfo.put("liable_person_id",resultList.get(0).getString("liable_person_id"));
            identifyInfo.put("liable_person_name",resultList.get(0).getString("liable_person_name"));
        }
    }

}
