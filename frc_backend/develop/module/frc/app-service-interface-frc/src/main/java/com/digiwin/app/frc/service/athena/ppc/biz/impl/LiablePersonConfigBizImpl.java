package com.digiwin.app.frc.service.athena.ppc.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.ppc.biz.LiablePersonConfigBiz;
import com.digiwin.app.frc.service.athena.ppc.domain.entity.LiablePersonConfigEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.entity.QuestionClassificationLiablePersonConfigEntity;
import com.digiwin.app.frc.service.athena.ppc.domain.model.LiablePersonConfigModel;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.ClassificationVo;
import com.digiwin.app.frc.service.athena.ppc.domain.vo.LiablePersonConfigVo;
import com.digiwin.app.frc.service.athena.ppc.mapper.LiablePersonConfigMapper;
import com.digiwin.app.frc.service.athena.ppc.mapper.QuestionClassificationLiablePersonConfigMapper;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author:zhangzlz
 * @Date 2022/3/11   10:08
 */
@Service
public class LiablePersonConfigBizImpl implements LiablePersonConfigBiz {


    @Autowired
    private QuestionClassificationLiablePersonConfigMapper questionClassificationLiablePersonConfigMapper;

    @Autowired
    private LiablePersonConfigMapper liablePersonConfigMapper;

    @Override
    public List<Map<String, Object>> addLiablePersonConfig(JSONArray dataContent) throws IOException, DWArgumentException {
        //判断并校验参数
        List<LiablePersonConfigEntity> entities = addCheckAndHandleData(dataContent);
        //添加数据 判断返回结果
        int i = liablePersonConfigMapper.addLiablePersonConfigInfo(entities);
        if (i == 0){
            return Collections.emptyList();
        }
        //将数据转换成前端需要的格式进行返回
        List<Map<String, Object>> maps = addConvertData(entities);
        //对classification_info进行处理
        for (int i1 = 0; i1 < dataContent.size(); i1++) {
            JSONArray classificationInfo = dataContent.getJSONObject(i1).getJSONArray("classification_info");
            maps.get(i1).put("classification_info",classificationInfo);
        }
        return maps;
    }

    @Override
    public boolean deleteLiablePersonConfig(JSONArray dataContent) {
        List<String> configIds = new ArrayList<>();
        //将前端传来的数据转换成主键集合
        for (int i = 0; i < dataContent.size(); i++) {
            String configId = dataContent.getJSONObject(i).getString("config_id");
            configIds.add(configId);
        }
        //删除配置人主键的信息
        int i = liablePersonConfigMapper.deleteLiablePersonConfigInfo(configIds, TenantTokenUtil.getTenantSid());

        //更新问题分类表中的关于问题责任人主键信息
        questionClassificationLiablePersonConfigMapper.deleteQuestionClassificationLiablePersonConfigByLiablePersonId(configIds,TenantTokenUtil.getTenantSid());

        return i > 0;
    }

    @Override
    public boolean updateLiablePersonConfig(JSONArray dataContent) throws DWArgumentException {
        List<LiablePersonConfigEntity> entities = updateCheckAndHandleData(dataContent);
        int i = liablePersonConfigMapper.updateBatch(entities);
        return i > 0;
    }

    @Override
    public List<JSONObject> getLiablePersonConfig(JSONArray dataContent) throws Exception {
        JSONObject jsonObject = dataContent.getJSONObject(0);
        List<LiablePersonConfigVo> vos;
        //对必传参数进行判空 同时判断是基础资料页面传参还是项目卡页面
        if (Boolean.TRUE.equals(getCheckAndHandleData(jsonObject))){
            String configFlag = jsonObject.getString("config_flag");
            String attributionNo = jsonObject.getString("attribution_no");
            String riskLevelId = jsonObject.getString("risk_level_id");
            String sourceId = jsonObject.getString("source_id");
            String classificationId = jsonObject.getString("classification_id");
            String solutionId = jsonObject.getString("solution_id");
            vos = liablePersonConfigMapper.getLiablePersonConfigInfo(configFlag,attributionNo,riskLevelId,sourceId,classificationId,solutionId,TenantTokenUtil.getTenantSid());
        }else{
            vos = liablePersonConfigMapper.getLiablePersonConfigInfo(jsonObject.getString("config_flag"), null, null, null, null,null, TenantTokenUtil.getTenantSid());
        }
        List<JSONObject> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(vos)){
            list = getConvertData(vos);
            return list;
        }
        return list;

    }

    /**
     * 新增 -- 校验参数 ，转换entity
     *
     * @param dataContent 解析后数据
     * @return List<LiablePersonConfigEntity> 封装后实体类集合
     * @throws DWArgumentException
     */
    private List<LiablePersonConfigEntity> addCheckAndHandleData(JSONArray dataContent) throws DWArgumentException {
        List<LiablePersonConfigEntity> entities = new ArrayList<>();
            JSONObject jsonObject = dataContent.getJSONObject(0);
            //对必传参数进行校验
            if (StringUtils.isEmpty(jsonObject.getString("config_flag"))) {
                throw new DWArgumentException("config_flag", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString("attribution_no"))) {
                throw new DWArgumentException("attribution_no", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString("risk_level_id"))) {
                throw new DWArgumentException("risk_level_id", MultilingualismUtil.getLanguage("notExist"));
            }
            if (StringUtils.isEmpty(jsonObject.getString("source_id"))) {
                throw new DWArgumentException("source_id", MultilingualismUtil.getLanguage("notExist"));
            }
            //配置标识为QH时,对solution_id进行判断
            if (jsonObject.getString("config_flag").equals("QH")&&!jsonObject.containsKey("solution_id")){
                throw new DWArgumentException("solution_id", MultilingualismUtil.getLanguage("notExist"));
            }
            //配置标识为QAC时,对acceptance_role进行判断
            if (jsonObject.getString("config_flag").equals("QAC")&&!jsonObject.containsKey("acceptance_role")){
                throw new DWArgumentException("acceptance_role", MultilingualismUtil.getLanguage("notExist"));
            }

            Long tenantsid = TenantTokenUtil.getTenantSid();

            //类别重复检验
            checkClassificationInfo(jsonObject,tenantsid);

            LiablePersonConfigModel model = JSON.parseObject(jsonObject.toJSONString(), LiablePersonConfigModel.class);
            LiablePersonConfigEntity entity = new LiablePersonConfigEntity();
            BeanUtils.copyProperties(model, entity);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantsid);
            entity.setCreateTime(new Date());
            entity.setCreateName(TenantTokenUtil.getUserName());
            //对classification_info 进行处理
            List<ClassificationVo> classificationInfo = model.getClassificationInfo();
            List<QuestionClassificationLiablePersonConfigEntity> entities1 = classificationInfo.stream().map(m -> new QuestionClassificationLiablePersonConfigEntity(entity.getOid(),m.getClassificationId())).collect(Collectors.toList());
            questionClassificationLiablePersonConfigMapper.addQuestionClassificationLiablePersonConfig(entities1);
            entities.add(entity);
        return entities;
    }

    /**
     * 修改 -- 校验参数 ，转换entity
     *
     * @param dataContent 前端传来的数据体
     * @return  返回 封装好的LiablePersonConfigEntity集合
     * @throws DWArgumentException
     * @throws IOException
     */
    private List<LiablePersonConfigEntity> updateCheckAndHandleData(JSONArray dataContent) throws DWArgumentException {
        List<LiablePersonConfigModel> models = new ArrayList<>();
        Long tenantsid = TenantTokenUtil.getTenantSid();
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            updateCheck(jsonObject);
            //jsonObject转换成model，并封装为entity，同时设置tenantSid、updateTime、updateName
            LiablePersonConfigModel model = JSON.parseObject(jsonObject.toJSONString(), LiablePersonConfigModel.class);
            models.add(model);
        }
        //检测类别信息中是否存在重复
        checkClassificationInfo(models,tenantsid);
        //对classification_info关联表进行操作
        updateClassificationInfo(models);
        return models.stream().map(m -> {
            LiablePersonConfigEntity entity = new LiablePersonConfigEntity();
            BeanUtils.copyProperties(m, entity);
            entity.setTenantSid(tenantsid);
            entity.setUpdateTime(new Date());
            entity.setUpdateName(TenantTokenUtil.getUserName());
            return entity;
        }).collect(Collectors.toList());
    }

    /**
     * @Description 更新检查必传参数
     * @param jsonObject
     * @return void
     * @author Jiangyw
     * @Date 2022/6/8
     */
    private void updateCheck(JSONObject jsonObject) throws DWArgumentException {
        if (StringUtils.isEmpty(jsonObject.getString("config_id"))) {
            throw new DWArgumentException("config_id", MultilingualismUtil.getLanguage("notExist"));
        }
        if (StringUtils.isEmpty(jsonObject.getString("config_flag"))) {
            throw new DWArgumentException("config_flag", MultilingualismUtil.getLanguage("notExist"));
        }
        if (StringUtils.isEmpty(jsonObject.getString("attribution_no"))) {
            throw new DWArgumentException("attribution_no", MultilingualismUtil.getLanguage("notExist"));
        }
        if (StringUtils.isEmpty(jsonObject.getString("risk_level_id"))) {
            throw new DWArgumentException("risk_level_id", MultilingualismUtil.getLanguage("notExist"));
        }
        if (StringUtils.isEmpty(jsonObject.getString("source_id"))) {
            throw new DWArgumentException("source_id", MultilingualismUtil.getLanguage("notExist"));
        }
        //配置标识为QH时,对solution_id进行判断
        if (jsonObject.getString("config_flag").equals("QH")&&!jsonObject.containsKey("solution_id")){
            throw new DWArgumentException("solution_id", MultilingualismUtil.getLanguage("notExist"));
        }
        //配置标识为QAC时,对acceptance_role进行判断
        if (jsonObject.getString("config_flag").equals("QAC")&&!jsonObject.containsKey("acceptance_role")){
            throw new DWArgumentException("acceptance_role", MultilingualismUtil.getLanguage("notExist"));
        }
    }

    /**
     * 查询 -- 校验参数，转换entity
     *
     * @param jsonObject  前端传来数组中第一个jsonObject对象
     * @return 返回 true：表示对应项目卡查询页面  false：表示对应基础资料查询页面
     * @throws DWArgumentException
     * @throws IOException
     */
    private Boolean getCheckAndHandleData(JSONObject jsonObject) throws DWArgumentException {

            //对必传参数进行校验
            if (!jsonObject.containsKey("config_flag") || StringUtils.isEmpty(jsonObject.getString("config_flag"))) {
                throw new DWArgumentException("config_flag", MultilingualismUtil.getLanguage("notExist"));
            }
            //当同时包含attribution_no,risk_level_id,source_id,classification_id时，表示对应项目卡查询页面
            if (jsonObject.containsKey("attribution_no")&&jsonObject.containsKey("risk_level_id")&&jsonObject.containsKey("source_id")&&jsonObject.containsKey("classification_id")){
                //配置标识为QH时,对solution_id进行判断
                if (jsonObject.getString("config_flag").equals("QH")&&!jsonObject.containsKey("solution_id")){
                    throw new DWArgumentException("solution_id", MultilingualismUtil.getLanguage("notExist"));
                }
                return true;
            }
            //当不包含attribution_no,risk_level_id,source_id,classification_id时，表示对应基础资料查询页面，返回false
            return false;

    }

    /**
     * 新增 -- 转换前端需要的格式
     * @param entities 添加到数据库中的实体类集合
     * @return  返回转换后的Map类型集合
     */
    private List<Map<String, Object>> addConvertData(List<LiablePersonConfigEntity> entities){
        //将数据格式转换
        return entities.stream().map(m -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("config_id", m.getOid());
                    map.put("config_flag", m.getConfigFlag());
                    map.put("attribution_no", m.getAttributionNo());
                    map.put("risk_level_id", m.getRiskLevelId());
                    map.put("source_id", m.getSourceOid());
                    map.put("solution_id", m.getSolutionOid());
                    map.put("liable_person_id", m.getLiablePersonId());
                    map.put("liable_person_name", m.getLiablePersonName());
                    map.put("acceptance_role", m.getAcceptanceRole());
            return map;
                }
        ).collect(Collectors.toList());
    }

    /**
     * 查询 -- 转换前端需要的格式
     *
     * @param vos  数据库查询的LiablePersonConfigVo集合
     * @return  返回JSONObject集合
     * @throws Exception
     */
    private List<JSONObject> getConvertData(List<LiablePersonConfigVo> vos) throws JsonProcessingException {
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (LiablePersonConfigVo vo : vos) {
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            jsonObjects.add(jsonObject);
        }
        return jsonObjects;
    }


    /**
     * @Description  校验新增操作中类别是否存在重复
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
     * @param jsonObject    校验对象
     * @param tenantsid
     * @return void
     * @author Jiangyw
     * @Date 2022/4/21
     */
    private void checkClassificationInfo(JSONObject jsonObject, Long tenantsid) throws DWArgumentException {
        JSONArray classificationInfo = jsonObject.getJSONArray("classification_info");
        if (StringUtils.isEmpty(classificationInfo) || classificationInfo.isEmpty()) {
            throw new DWArgumentException("classification_info", MultilingualismUtil.getLanguage("notExist"));
        }
        String configFlag = jsonObject.getString("config_flag");
        //查询相同标识下的所有信息进行校验
        List<LiablePersonConfigVo> list = liablePersonConfigMapper.getLiablePersonConfigInfo(
                configFlag,
                null, null, null, null, null, tenantsid);
        for (int i = 0; i < list.size(); i++) {
            LiablePersonConfigVo e = list.get(i);
            //如果来源，归属和风险等级皆相同，则进行检验
            if (e.getAttributionNo().equals(jsonObject.getString("attribution_no"))
                    && e.getRiskLevelId().equals(jsonObject.getString("risk_level_id"))
                    && e.getSourceId().equals(jsonObject.getString("source_id"))
                    &&(!configFlag.equals("QH") || e.getSolutionId().equals(jsonObject.getString("solution_id")))) {
                List<ClassificationVo> infos = classificationInfo.stream().map(c -> JSON.parseObject(c.toString(), ClassificationVo.class)).collect(Collectors.toList());
                for (int j = 0; j < infos.size(); j++) {
                    ClassificationVo vo1 = infos.get(j);
                    //如果部门信息存在相同则报错，返回相同行数
                    for (ClassificationVo vo : e.getClassificationInfo()) {
                        if (vo.getClassificationId().equals(vo1.getClassificationId())) {
                            String row = String.valueOf(i + 1);
                            throw new DWArgumentException("classification_info", MultilingualismUtil.getLanguage("ClassificationDuplicateData") + row);
                        }
                    }
                }
            }
        }
    }

    /**
     * @Description 校验更新操作中类别是否存在重复
     * 注意：同该方法重载的另一方法
     * @param models 校验对象
     * @param tenantsid 租户id
     * @return void
     * @author Jiangyw
     * @Date 2022/4/22
     */
    private void checkClassificationInfo(List<LiablePersonConfigModel> models, Long tenantsid) throws DWArgumentException {
        String configFlag =  models.get(0).getConfigFlag();
        //校验classificationInfo是否存在重复
        List<LiablePersonConfigVo> list = liablePersonConfigMapper.getLiablePersonConfigInfo(
                configFlag,
                null, null, null, null, null, tenantsid);
        //模拟获取更新后的数据，防止同时进行A->B,B->A更新操作时可能导致的交叉校验错误
        for (LiablePersonConfigVo vo : list) {
            for (LiablePersonConfigModel model : models) {
                if (vo.getOid().equals(model.getOid())) {
                    vo.setRiskLevelId(model.getRiskLevelId());
                    vo.setAttributionNo(model.getAttributionNo());
                    vo.setSourceId(model.getSourceOid());
                    vo.setClassificationInfo(model.getClassificationInfo());
                    vo.setSolutionId(model.getSolutionOid());
                }
            }
        }
        //将模拟的更新后的数据和更新数据比较，为了防止和更新后自己比较，oid相同的跳过
        for (LiablePersonConfigModel model : models) {
            for (int i = 0; i < list.size(); i++){
                LiablePersonConfigVo vo = list.get(i);
                //如果oid不同，但是其他属性皆相同，则校验分类中有没有重复项
                if (!vo.getOid().equals(model.getOid())
                        && vo.getRiskLevelId().equals(model.getRiskLevelId())
                        && vo.getAttributionNo().equals(model.getAttributionNo())
                        && vo.getSourceId().equals(model.getSourceOid())
                        &&(!configFlag.equals("QH") || vo.getSolutionId().equals(model.getSolutionOid()) )) {
                    for ( ClassificationVo vo1: vo.getClassificationInfo()){
                        for (ClassificationVo vo2 : model.getClassificationInfo()) {
                            if (vo1.getClassificationId().equals(vo2.getClassificationId())) {
                                String row = String.valueOf(i + 1);
                                throw new DWArgumentException("classification_info", MultilingualismUtil.getLanguage("ClassificationDuplicateData") + row);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @Description 类别关联表更新
     * @param models
     * @return void
     * @author Jiangyw
     * @Date 2022/4/22
     */
    private void updateClassificationInfo(List<LiablePersonConfigModel> models){
        List<String> ids = models.stream().map(LiablePersonConfigModel::getOid).collect(Collectors.toList());
        //对classification_info的关联表进行更新
        List<QuestionClassificationLiablePersonConfigEntity> entities = new ArrayList<>();
        for (LiablePersonConfigModel model : models) {
            List<QuestionClassificationLiablePersonConfigEntity> entities1 = model.getClassificationInfo().stream().map(c -> new QuestionClassificationLiablePersonConfigEntity(model.getOid(), c.getClassificationId())).collect(Collectors.toList());
            entities.addAll(entities1);
        }
        questionClassificationLiablePersonConfigMapper.deleteQuestionClassificationLiablePersonConfigByLiablePersonId(ids,TenantTokenUtil.getTenantSid());
        questionClassificationLiablePersonConfigMapper.addQuestionClassificationLiablePersonConfig(entities);
    }
}