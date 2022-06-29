package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.CustomerServiceBiz;
import com.digiwin.app.frc.service.athena.mtw.common.constants.CustomerServiceConstant;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.CustomerServiceEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.model.CustomerServiceModel;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.CustomerServiceVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.CustomerServiceMapper;
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
 * @Date: 2021/11/12 11:26
 * @Version 1.0
 * @Description 客服对接经销商信息处理Biz-impl
 */
@Service
public class CustomerServiceBizImpl implements CustomerServiceBiz {

    @Autowired
    private CustomerServiceMapper customerServiceMapper;


    @Override
    public List<JSONObject> addCustomerService(JSONArray dataContent) throws DWArgumentException, IOException {
        //添加客服对接经销商信息
        CheckFieldValueUtil.validateModels(dataContent,new CustomerServiceModel());
        //对必传参数进行校验 并进行封装成entity
        List<CustomerServiceEntity> entities = checkAndHandleData(dataContent, TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName());
        int result = customerServiceMapper.addCustomerServiceInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }


    @Override
    public boolean deleteCustomerService(JSONArray dataContent) {
        //删除客服对接经销商信息
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(CustomerServiceConstant.DEALER_CONTACT_PERSON_ID);
            oidList.add(oid);
        }
        int result = customerServiceMapper.deleteCustomerServiceInfo(oidList);
        return result > 0;
    }


    @Override
    public boolean updateCustomerService(JSONArray dataContent) throws DWArgumentException, IOException {
        //更新客服对接经销商信息
        CheckFieldValueUtil.validateModels(dataContent,new CustomerServiceModel());
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<CustomerServiceEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            CustomerServiceEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = customerServiceMapper.updateBatch(entities);
        return result > 0;
    }


    @Override
    public List<JSONObject> getCustomerService(JSONArray dataContent) throws JsonProcessingException {
        //获取客服对接经销商信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<CustomerServiceEntity> entities;
        if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
            entities = customerServiceMapper.getCustomerServiceInfo(tenantSid, null, null, null, null);
        } else {
            entities = queryByCondition(dataContent, tenantSid);
        }
        //entity转成平台规范带有_格式的前端数据
        return convertData(entities);
    }

    /**
     * 对必传参数进行校验 并进行封装成entity
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @param userName    用户姓名
     * @return List<CustomerServiceEntity> 封装后实体类集合
     * @throws DWArgumentException
     */
    private List<CustomerServiceEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException, IOException {
        List<CustomerServiceEntity> entities = new ArrayList<>();
        //查询所有经销商Id
        List<String> dealerIds = customerServiceMapper.queryAllDealerIds(tenantSid);
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if (!jsonObject.containsKey(CustomerServiceConstant.CUSTOMER_SERVICE_EMPLOYEE_ID) || StringUtils.isEmpty(jsonObject.getString(CustomerServiceConstant.CUSTOMER_SERVICE_EMPLOYEE_ID))) {
                throw new DWArgumentException("customerServiceEmployeeId", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(CustomerServiceConstant.CUSTOMER_SERVICE_EMPLOYEE_NAME) || StringUtils.isEmpty(jsonObject.getString(CustomerServiceConstant.CUSTOMER_SERVICE_EMPLOYEE_NAME))) {
                throw new DWArgumentException("customerServiceEmployeeName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(CustomerServiceConstant.DEALER_ID) || StringUtils.isEmpty(jsonObject.getString(CustomerServiceConstant.DEALER_ID))) {
                throw new DWArgumentException("dealerId", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(CustomerServiceConstant.DEALER_NAME) || StringUtils.isEmpty(jsonObject.getString(CustomerServiceConstant.DEALER_NAME))) {
                throw new DWArgumentException("dealerName", MultilingualismUtil.getLanguage("notExist"));
            }
            if(CollUtil.isNotEmpty(dealerIds) && dealerIds.contains(jsonObject.getString(CustomerServiceConstant.DEALER_ID))){
                throw new DWArgumentException("dealerId", MultilingualismUtil.getLanguage("dealerId"));
            }
            CustomerServiceModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), CustomerServiceModel.class);
            CustomerServiceEntity entity = new CustomerServiceEntity();
            BeanUtils.copyProperties(model, entity);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantSid);
            entity.setCreateTime(new Date());
            entity.setCreateName(userName);
            entities.add(entity);
        }
        return entities;
    }


    /**
     * 对必传参数进行校验 并进行封装成entity(修改)
     *
     * @param dataContent 解析后数据
     * @param updateName  修改人姓名
     * @param i           循环遍历的索引值
     * @return CustomerServiceEntity 封装后的实体类
     * @throws DWArgumentException
     */
    private CustomerServiceEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException, IOException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(CustomerServiceConstant.DEALER_CONTACT_PERSON_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("dealerContactPersonId",
                    MultilingualismUtil.getLanguage("notExist"));
        }
        CustomerServiceEntity entity = new CustomerServiceEntity();
        CustomerServiceModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), CustomerServiceModel.class);
        BeanUtils.copyProperties(model, entity);
        entity.setUpdateName(updateName);
        entity.setUpdateTime(new Date());
        entity.setTenantSid(tenantSid);
        return entity;
    }

    /**
     * 对必传参数进行处理 并进行带条件查询
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @return List<CustomerServiceEntity> 封装后实体类的集合
     */
    private List<CustomerServiceEntity> queryByCondition(JSONArray dataContent, Long tenantSid) {
        List<CustomerServiceEntity> entities;
        //平台查询只支持传一笔数据 规格使用数组传参
        JSONObject jsonObject = dataContent.getJSONObject(0);
        entities = customerServiceMapper.getCustomerServiceInfo(
                tenantSid,
                jsonObject.getString(CustomerServiceConstant.CUSTOMER_SERVICE_EMPLOYEE_ID),
                jsonObject.getString(CustomerServiceConstant.CUSTOMER_SERVICE_EMPLOYEE_NAME),
                jsonObject.getString(CustomerServiceConstant.DEALER_ID),
                jsonObject.getString(CustomerServiceConstant.DEALER_NAME));
        return entities;
    }

    /**
     * 将实体类集合转成前端要求的格式集合
     *
     * @param entities 问题分类实体集合
     * @return List<JSONObject>
     */
    private List<JSONObject> convertData(List<CustomerServiceEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (CustomerServiceEntity entity : entities) {
            CustomerServiceVo vo = new CustomerServiceVo();
            BeanUtils.copyProperties(entity, vo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            mapList.add(jsonObject);
        }
        return mapList;
    }
}
