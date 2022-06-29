package com.digiwin.app.frc.service.athena.ppc.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.ppc.biz.QuestionLiablePersonDepartmentLatitudeConfigBiz;
import com.digiwin.app.frc.service.athena.ppc.constants.QuestionLiablePersonDepartmentLatitudeConfigConstant;
import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionLiablePersonDepartmentLatitudeConfigEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.model.QuestionLiablePersonDepartmentLatitudeConfigModel;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.FeedbackDepartmentVo;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.QuestionLiablePersonDepartmentLatitudeConfigVo;
import com.digiwin.app.frc.service.athena.ppc.mapper.QuestionLiablePersonDepartmentLatitudeConfigMapper;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWServiceContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
*@Author Jiangyw
*@Date 2022/3/11
*@Time 16:47
*@Version
*/
@Service
public class QuestionLiablePersonDepartmentLatitudeConfigBizImpl implements QuestionLiablePersonDepartmentLatitudeConfigBiz {

    @Autowired
    private QuestionLiablePersonDepartmentLatitudeConfigMapper questionLiablePersonDepartmentLatitudeConfigMapper;


    @Override
    public List<JSONObject> addQuestionLiablePersonDepartmentLatitudeConfig(JSONArray dataContent) throws DWArgumentException, IOException {
        //添加租户信息
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        Long tenantSid = (Long)profile.get("tenantSid");
        String userName = (String)profile.get("userName");
        //对必传参数进行校验 并封装成entity
        List<QuestionLiablePersonDepartmentLatitudeConfigEntity> entities = addCheckAndHandleDataContent(dataContent,tenantSid,userName);
        questionLiablePersonDepartmentLatitudeConfigMapper.addQuestionLiablePersonDepartmentLatitudeConfig(entities);
        return convertData(entities);
    }

    @Override
    public Boolean deleteQuestionLiablePersonDepartmentLatitudeConfig(JSONArray dataContent) {
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        Long tenantSid = (Long)profile.get("tenantSid");
        List<String> oidList = new ArrayList<>();
        for(int i = 0; i < dataContent.size(); i++){
            JSONObject jsonObject = dataContent.getJSONObject(i);
            oidList.add(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.CONFIG_ID));
        }
        return questionLiablePersonDepartmentLatitudeConfigMapper
                .deleteQuestionLiablePersonDepartmentLatitudeConfig(oidList,tenantSid)
                > 0;
    }

    @Override
    public Boolean updateQuestionLiablePersonDepartmentLatitudeConfig(JSONArray dataContent) throws DWArgumentException, IOException {
        //添加租户信息
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        Long tenantSid = (Long)profile.get("tenantSid");
        String userName = (String)profile.get("userName");
        //对必传参数进行校验 并封装成entity
        List<QuestionLiablePersonDepartmentLatitudeConfigEntity> entities = updateCheckAndHandleDataContent(dataContent,tenantSid,userName);
        int result = questionLiablePersonDepartmentLatitudeConfigMapper.updateQuestionLiablePersonDepartmentLatitudeConfig(entities);
        return result > 0;
    }



    @Override
    public List<JSONObject> getQuestionLiablePersonDepartmentLatitudeConfig(JSONArray dataContent) throws DWArgumentException, JsonProcessingException {
        //添加租户信息
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        Long tenantSid = (Long)profile.get("tenantSid");

        JSONObject jsonObject = dataContent.getJSONObject(0);

        //对数据进行校验
        if (!jsonObject.containsKey(QuestionLiablePersonDepartmentLatitudeConfigConstant.CONFIG_FLAG)|| StringUtils.isEmpty(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.CONFIG_FLAG))) {
            throw new DWArgumentException("configFlag", MultilingualismUtil.getLanguage("notExist"));
        }
        String configFlag = jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.CONFIG_FLAG);
        if(!configFlag.equals("QC")&&!configFlag.equals("QAN")&&!configFlag.equals("QH")&&!configFlag.equals("QAC")){
            throw new DWArgumentException("configFlag",MultilingualismUtil.getLanguage("parameterError"));
        }
        String attributionNo = jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.ATTRIBUTION_NO);
        if (!StringUtils.isEmpty(attributionNo) && !attributionNo.equals("1")&&!attributionNo.equals("2")) {
            throw new DWArgumentException("attributionNo",MultilingualismUtil.getLanguage("parameterError"));
        }
        String acceptanceRole = jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.ACCEPTANCE_ROLE);
        if (!StringUtils.isEmpty(acceptanceRole) && !attributionNo.equals("1")&&!attributionNo.equals("2")&&!attributionNo.equals("3")) {
            throw new DWArgumentException("acceptanceRole",MultilingualismUtil.getLanguage("parameterError"));
        }

        String riskLevelId = jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.RISK_LEVEL_ID);
        String feedbackDepartmentId = jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.FEEDBACK_DEPARTMENT_ID);

        List<QuestionLiablePersonDepartmentLatitudeConfigEntity> entities = questionLiablePersonDepartmentLatitudeConfigMapper.getQuestionLiablePersonDepartmentLatitudeConfig(configFlag,attributionNo,riskLevelId,feedbackDepartmentId,acceptanceRole,tenantSid);
        return convertData(entities);
    }


    @Override
    public Map<String, Object> getLiablePersonMessage(String attributionNo, String riskLevelOid, String feedbackDepartmentOid, String sourceOid, String classificationOid,String solutionOid) {
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        Long tenantSid = (Long)profile.get("tenantSid");
        List<Map<String,Object>> result;
        if (StringUtils.isEmpty(solutionOid)){
            result = questionLiablePersonDepartmentLatitudeConfigMapper.getLiablePersonDepartmentLatitudeMessage(attributionNo,riskLevelOid,feedbackDepartmentOid,tenantSid);
            if (!result.isEmpty()) {
                return result.get(0);
            }
        }
        result = questionLiablePersonDepartmentLatitudeConfigMapper.getLiablePersonMessage(attributionNo,riskLevelOid,sourceOid,classificationOid,solutionOid,tenantSid);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return new HashMap<>();
    }



    /**
     * 新增 将数据转换为前端格式
     * @param entities 实体类
     * @return JSONObject list数组
     */
    private List<JSONObject> convertData(List<QuestionLiablePersonDepartmentLatitudeConfigEntity> entities) throws JsonProcessingException {
        List<JSONObject> map =  new ArrayList<>();
        for(int i = 0; i < entities.size(); i++){
            QuestionLiablePersonDepartmentLatitudeConfigEntity entity = entities.get(i);
            QuestionLiablePersonDepartmentLatitudeConfigVo vo = new QuestionLiablePersonDepartmentLatitudeConfigVo();
            BeanUtils.copyProperties(entity,vo);
            vo.setFeedbackDepartmentMessage(splitFeedbackDepartments(entity.getFeedbackDepartments()));
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            map.add(jsonObject);
        }
        return map;
    }

    /**
     * 将部门信息分割为数组vo类
     * @param feedbackDepartmentMessage 部门信息字符串
     * @return 部门信息vo类 list数组
     */
    private List<FeedbackDepartmentVo> splitFeedbackDepartments(String feedbackDepartmentMessage){
         List<FeedbackDepartmentVo> feedbackDepartmentVos = new ArrayList<>();
         String[] feedbackDepartments = feedbackDepartmentMessage.split(";");
         for (String feedbackDepartment : feedbackDepartments) {
             String[] messages = feedbackDepartment.split("_");
             if (messages.length == 2) {
                 FeedbackDepartmentVo vo = new FeedbackDepartmentVo();
                 vo.setFeedbackDepartmentId(messages[0]);
                 vo.setFeedbackDepartmentName(messages[1]);
                 feedbackDepartmentVos.add(vo);
             }
         }
         return feedbackDepartmentVos;
    }

    /**
     * 将部门信息校验并组合封装为字符串
     * @param feedbackDepartmentMessages 部门信息JSON数组对象
     * @return 部门信息字符串
     */
    private String handleFeedbackDepartments(JSONArray feedbackDepartmentMessages) throws DWArgumentException {
        StringBuilder feedbackDepartments = new StringBuilder();
        for ( int k = 0; k < feedbackDepartmentMessages.size(); k++){
            JSONObject feedbackDepartment = feedbackDepartmentMessages.getJSONObject(k);
            if (!feedbackDepartment.containsKey(QuestionLiablePersonDepartmentLatitudeConfigConstant.FEEDBACK_DEPARTMENT_ID) || StringUtils.isEmpty(feedbackDepartment.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.FEEDBACK_DEPARTMENT_ID))) {
                throw new DWArgumentException("feedbackDepartmentId", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!feedbackDepartment.containsKey(QuestionLiablePersonDepartmentLatitudeConfigConstant.FEEDBACK_DEPARTMENT_NAME) || StringUtils.isEmpty(feedbackDepartment.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.FEEDBACK_DEPARTMENT_NAME))) {
                throw new DWArgumentException("feedbackDepartmentName", MultilingualismUtil.getLanguage("notExist"));
            }
            feedbackDepartments.append(feedbackDepartment.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.FEEDBACK_DEPARTMENT_ID));
            feedbackDepartments.append("_");
            feedbackDepartments.append(feedbackDepartment.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.FEEDBACK_DEPARTMENT_NAME));
            feedbackDepartments.append(";");
        }
        return feedbackDepartments.toString();
    }

    /**
     * 新增 数据校验和封装
     * @param dataContent 数据 JSON数组
     * @param tenantSid 租户id
     * @param userName 用户名
     * @return 封装成实体类的list数组
     * @throws DWArgumentException
     * @throws IOException
     */
    private List<QuestionLiablePersonDepartmentLatitudeConfigEntity> addCheckAndHandleDataContent(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException {
        List<QuestionLiablePersonDepartmentLatitudeConfigModel> models = new ArrayList<>();

        for(int i = 0; i <dataContent.size(); i++){
            JSONObject jsonObject = dataContent.getJSONObject(i);

            //对数据进行校验
            if (StringUtils.isEmpty(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.CONFIG_FLAG))) {
                throw new DWArgumentException("configFlag", MultilingualismUtil.getLanguage("notExist"));
            }
            String configFlag = jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.CONFIG_FLAG);
            if (!configFlag.equals("QC")&&!configFlag.equals("QAN")&&!configFlag.equals("QH")&&!configFlag.equals("QAC")){
                throw new DWArgumentException("configFlag",MultilingualismUtil.getLanguage("parameterError"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.ATTRIBUTION_NO))) {
                throw new DWArgumentException("attributionNo",MultilingualismUtil.getLanguage("notExist"));
            }
            String attributionNo = jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.ATTRIBUTION_NO);
            if (!attributionNo.equals("1")&&!attributionNo.equals("2")){
                throw new DWArgumentException("attributionNo",MultilingualismUtil.getLanguage("parameterError"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.RISK_LEVEL_ID))) {
                throw new DWArgumentException("riskLevelId",MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.FEEDBACK_DEPARTMENT_MESSAGE))) {
                throw new DWArgumentException("feedbackDepartmentMessage",MultilingualismUtil.getLanguage("notExist"));
            }
            String feedbackDepartmentMessages = handleFeedbackDepartments(jsonObject.getJSONArray(QuestionLiablePersonDepartmentLatitudeConfigConstant.FEEDBACK_DEPARTMENT_MESSAGE));

            if (configFlag.equals("QAC")) {
                if (StringUtils.isEmpty(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.ACCEPTANCE_ROLE))) {
                    throw new DWArgumentException("acceptanceRole",MultilingualismUtil.getLanguage("notExist"));
                }
                String acceptanceRole = jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.ACCEPTANCE_ROLE);
                if (!acceptanceRole.equals("1")&&!acceptanceRole.equals("2")&&!acceptanceRole.equals("3")) {
                    throw new DWArgumentException("acceptanceRole",MultilingualismUtil.getLanguage("parameterError"));
                }
            }else {
                if (StringUtils.isEmpty(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.LIABLE_PERSON_NAME))) {
                    throw new DWArgumentException("liablePersonName",MultilingualismUtil.getLanguage("notExist"));
                }
                if (StringUtils.isEmpty(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.LIABLE_PERSON_ID))) {
                    throw new DWArgumentException("liablePersonId",MultilingualismUtil.getLanguage("notExist"));
                }
                if (jsonObject.containsKey("acceptanceRole")){
                    jsonObject.remove("acceptanceRole");
                }
            }

            //封装数据
            QuestionLiablePersonDepartmentLatitudeConfigModel model = JSON.parseObject(jsonObject.toJSONString(), QuestionLiablePersonDepartmentLatitudeConfigModel.class);

            //单独组合封装部门相关信息
            model.setFeedbackDepartments(feedbackDepartmentMessages);

            models.add(model);
        }

        addCheckFeedbackDepartment(models.get(0),tenantSid);

        return models.stream().map(m -> {
            QuestionLiablePersonDepartmentLatitudeConfigEntity entity = new QuestionLiablePersonDepartmentLatitudeConfigEntity();
            BeanUtils.copyProperties(m, entity);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantsid(tenantSid);
            entity.setCreateName(userName);
            entity.setCreateTime(new Date());
            return entity;
        }).collect(Collectors.toList());
    }




    /**
     * 更新 数据校验和封装
     * @param dataContent 数据 JSON数组
     * @param tenantSid 租户id
     * @param userName 用户名
     * @return 封装成实体类的list数组
     * @throws DWArgumentException
     * @throws IOException
     */
    private List<QuestionLiablePersonDepartmentLatitudeConfigEntity> updateCheckAndHandleDataContent(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException{
        List<QuestionLiablePersonDepartmentLatitudeConfigModel> models = new ArrayList<>();

        for(int i = 0; i <dataContent.size(); i++){
            JSONObject jsonObject = dataContent.getJSONObject(i);

            //对数据进行校验
            if (StringUtils.isEmpty(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.CONFIG_FLAG))) {
                throw new DWArgumentException("configFlag", MultilingualismUtil.getLanguage("notExist"));
            }
            String configFlag = jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.CONFIG_FLAG);
            if(!configFlag.equals("QC")&&!configFlag.equals("QAN")&&!configFlag.equals("QH")&&!configFlag.equals("QAC")){
                throw new DWArgumentException("configFlag",MultilingualismUtil.getLanguage("parameterError"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.ATTRIBUTION_NO))) {
                throw new DWArgumentException("attributionNo",MultilingualismUtil.getLanguage("notExist"));
            }
            String attributionNo = jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.ATTRIBUTION_NO);
            if(!attributionNo.equals("1")&&!attributionNo.equals("2")){
                throw new DWArgumentException("attributionNo",MultilingualismUtil.getLanguage("parameterError"));
            }
            if (StringUtils.isEmpty(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.RISK_LEVEL_ID))) {
                throw new DWArgumentException("riskLevelId",MultilingualismUtil.getLanguage("notExist"));
            }
            JSONArray feedbackDepartmentMessages = null;
            if (!StringUtils.isEmpty(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.FEEDBACK_DEPARTMENT_MESSAGE))) {
                feedbackDepartmentMessages = jsonObject.getJSONArray(QuestionLiablePersonDepartmentLatitudeConfigConstant.FEEDBACK_DEPARTMENT_MESSAGE);
            }

            if (configFlag.equals("QAC")) {
                if (StringUtils.isEmpty(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.ACCEPTANCE_ROLE))) {
                    throw new DWArgumentException("acceptanceRole",MultilingualismUtil.getLanguage("notExist"));
                }
                String acceptanceRole = jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.ACCEPTANCE_ROLE);
                if (!acceptanceRole.equals("1")&&!acceptanceRole.equals("2")&&!acceptanceRole.equals("3")) {
                    throw new DWArgumentException("acceptanceRole",MultilingualismUtil.getLanguage("parameterError"));
                }
            }else {
                if (StringUtils.isEmpty(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.LIABLE_PERSON_NAME))) {
                    throw new DWArgumentException("liablePersonName",MultilingualismUtil.getLanguage("notExist"));
                }
                if (StringUtils.isEmpty(jsonObject.getString(QuestionLiablePersonDepartmentLatitudeConfigConstant.LIABLE_PERSON_ID))) {
                    throw new DWArgumentException("liablePersonId",MultilingualismUtil.getLanguage("notExist"));
                }
                jsonObject.remove("acceptanceRole");
            }

            //封装数据
            QuestionLiablePersonDepartmentLatitudeConfigModel model = JSON.parseObject(jsonObject.toJSONString(), QuestionLiablePersonDepartmentLatitudeConfigModel.class);

            //单独组合封装部门相关信息
            if(feedbackDepartmentMessages != null) {
                model.setFeedbackDepartments(handleFeedbackDepartments(feedbackDepartmentMessages));
            }
            models.add(model);
        }


        updataCheckFeedbackDepartment(models,tenantSid);

        return models.stream().map(m -> {
            QuestionLiablePersonDepartmentLatitudeConfigEntity entity = new QuestionLiablePersonDepartmentLatitudeConfigEntity();
            BeanUtils.copyProperties(m, entity);
            entity.setTenantsid(tenantSid);
            entity.setUpdateName(userName);
            entity.setUpdateTime(new Date());
            return entity;
        }).collect(Collectors.toList());
    }

    /**
     * @Description 新增的部门字段重复校验
     * 注意 ：该方面的需求存在逻辑缺陷，导致时间复杂度的问题非常棘手
     * 前端传回的问题责任人配置数据为一个数组，后端查询的问题配置责任人数据也为一个数组，数组和数组依次比较的时间复杂度比较为n²
     * 两个数组中需要比较的问题类别信息也是一个数组，也就是说在n²之中还要再进行一次n²的数组依次比较，总时间复杂度为n^4
     *
     * 服务器存储的类别信息和前端传回的类别信息皆为乱序。
     * 如果先排序再比较，会稍微降低时间复杂度，但因为需要排序的数组过多，结果会收效甚微，
     * 考虑到实际场景下，n的数量级在1-10之间，实际上的时间成本可能反而会增加，暴力比较反而是最优解。
     *
     * 如果数量级扩大，需要重新考虑整个更新和新增的代码逻辑，以及前端传回的逻辑，
     * 使其在存入和前端查询的数据为有序数据，最后在这里使用双指针算法进行比较
     *
     * @param model 校验对象
     * @param tenantsid 租户id
     * @return JSONArray
     * @author Jiangyw
     * @Date 2022/3/30
     */
    private void addCheckFeedbackDepartment(QuestionLiablePersonDepartmentLatitudeConfigModel model, Long tenantsid) throws DWArgumentException {
        List<QuestionLiablePersonDepartmentLatitudeConfigEntity> entities = questionLiablePersonDepartmentLatitudeConfigMapper.getQuestionLiablePersonDepartmentLatitudeConfig(
                model.getConfigFlag(),
                null, null, null, null, tenantsid);
        List<QuestionLiablePersonDepartmentLatitudeConfigModel> models = new ArrayList<>();
        models.add(model);
        checkFeedbackDepartment(models,entities);
    }

    /**
     * @Description 更新的部门字段重复校验
     * @param models
     * @param tenantsid
     * @return void
     * @author Jiangyw
     * @Date 2022/4/24
     */
    private void updataCheckFeedbackDepartment(List<QuestionLiablePersonDepartmentLatitudeConfigModel> models,Long tenantsid) throws DWArgumentException {
        List<QuestionLiablePersonDepartmentLatitudeConfigEntity> entities = questionLiablePersonDepartmentLatitudeConfigMapper.getQuestionLiablePersonDepartmentLatitudeConfig(
                models.get(0).getConfigFlag(),
                null, null, null, null, tenantsid);
        for (QuestionLiablePersonDepartmentLatitudeConfigEntity entity : entities) {
            for (QuestionLiablePersonDepartmentLatitudeConfigModel model : models) {
                if (model.getOid().equals(entity.getOid())) {
                    entity.setRiskLevelId(model.getRiskLevelId());
                    entity.setAttributionNo(model.getAttributionNo());
                    entity.setFeedbackDepartments(model.getFeedbackDepartments());
                }
            }
        }
        checkFeedbackDepartment(models, entities);
    }



    private void checkFeedbackDepartment(List<QuestionLiablePersonDepartmentLatitudeConfigModel> models, List<QuestionLiablePersonDepartmentLatitudeConfigEntity> entities) throws DWArgumentException {
        for (int i = 0; i < entities.size(); i++) {
            QuestionLiablePersonDepartmentLatitudeConfigEntity entity = entities.get(i);
            for(int j = 0 ; j < models.size(); j++) {
                QuestionLiablePersonDepartmentLatitudeConfigModel model = models.get(j);
                if (model.getOid() !=null  && model.getOid().equals(entity.getOid())) {
                    continue;
                }
                if(model.getRiskLevelId().equals(entity.getRiskLevelId())&&model.getAttributionNo().equals(entity.getAttributionNo())){
                    List<FeedbackDepartmentVo> vos1 = splitFeedbackDepartments(entity.getFeedbackDepartments());
                    List<FeedbackDepartmentVo> vos2 = splitFeedbackDepartments(model.getFeedbackDepartments());
                    for (FeedbackDepartmentVo vo1 : vos1) {
                        for (FeedbackDepartmentVo vo2 : vos2) {
                            if (vo1.getFeedbackDepartmentId().equals(vo2.getFeedbackDepartmentId())) {
                                String row = String.valueOf(i + 1);
                                throw new DWArgumentException("feedbackDepartmentMessage", MultilingualismUtil.getLanguage("DepartmentDuplicateData") + row);
                            }
                        }
                    }
                }
            }
        }
    }
}
