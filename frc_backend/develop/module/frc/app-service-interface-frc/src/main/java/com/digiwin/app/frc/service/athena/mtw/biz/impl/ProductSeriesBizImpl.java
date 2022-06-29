package com.digiwin.app.frc.service.athena.mtw.biz.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.mtw.biz.ProductSeriesBiz;
import com.digiwin.app.frc.service.athena.mtw.common.constants.ProductSeriesConstant;
import com.digiwin.app.frc.service.athena.mtw.common.enums.EffectiveEnum;
import com.digiwin.app.frc.service.athena.mtw.domain.entity.ProductSeriesEntity;
import com.digiwin.app.frc.service.athena.mtw.domain.model.ProductSeriesModel;
import com.digiwin.app.frc.service.athena.mtw.domain.vo.ProductSeriesVo;
import com.digiwin.app.frc.service.athena.mtw.mapper.ProductSeriesMapper;
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
import java.util.*;

/**
 * @Author: xieps
 * @Date: 2021/11/12 16:03
 * @Version 1.0
 * @Description 产品系列处理Biz-impl
 */
@Service
public class ProductSeriesBizImpl implements ProductSeriesBiz {

    @Autowired
    private ProductSeriesMapper productSeriesMapper;

    @Override
    public List<JSONObject> addProductSeries(JSONArray dataContent) throws DWArgumentException, IOException {
        //添加产品系列信息
        CheckFieldValueUtil.validateModels(dataContent,new ProductSeriesModel());
        //对必传参数进行校验 并进行封装成entity
        List<ProductSeriesEntity> entities = checkAndHandleData(dataContent, TenantTokenUtil.getTenantSid(), TenantTokenUtil.getUserName());
        int result = productSeriesMapper.addProductSeriesInfo(entities);
        //entity转成平台规范带有_格式的前端数据
        List<JSONObject> mapList = convertData(entities);
        return result > 0 ? mapList : null;
    }

    @Override
    public boolean deleteProductSeries(JSONArray dataContent) {
        //删除产品系列信息
        List<String> oidList = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            String oid = dataContent.getJSONObject(i).getString(ProductSeriesConstant.PRODUCT_ID);
            oidList.add(oid);
        }
        int result = productSeriesMapper.deleteProductSeriesInfo(oidList);
        return result > 0;
    }


    @Override
    public boolean updateProductSeries(JSONArray dataContent) throws DWArgumentException, IOException {
        //更新产品系列信息
        CheckFieldValueUtil.validateModels(dataContent,new ProductSeriesModel());
        String updateName = (String) DWServiceContext.getContext().getProfile().get("userName");
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<ProductSeriesEntity> entities = new ArrayList<>();
        for (int i = 0; i < dataContent.size(); i++) {
            ProductSeriesEntity entity = checkAndHandleData(dataContent, updateName, i,tenantSid);
            entities.add(entity);
        }
        int result = productSeriesMapper.updateBatch(entities);
        return result > 0;
    }


    @Override
    public List<JSONObject> getProductSeries(JSONArray dataContent) throws JsonProcessingException {
        //获取产品系列信息
        Long tenantSid = (Long) DWServiceContext.getContext().getProfile().get("tenantSid");
        List<ProductSeriesEntity> entities;
        if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
            entities = productSeriesMapper.getProductSeriesInfo(tenantSid, null, null, null, null);
        } else {
            entities = queryByCondition(dataContent, tenantSid);
        }
        //entity转成平台规范带有_格式的前端数据
        return convertData(entities);
    }

    /**
     * 将实体类集合转成前端要求的格式集合
     *
     * @param entities 问题分类实体集合
     * @return List<JSONObject>
     */
    private List<JSONObject> convertData(List<ProductSeriesEntity> entities) throws JsonProcessingException {
        List<JSONObject> mapList = new ArrayList<>();
        for (ProductSeriesEntity entity : entities) {
            ProductSeriesVo vo = new ProductSeriesVo();
            BeanUtils.copyProperties(entity, vo);
            JSONObject jsonObject = JSON.parseObject(new ObjectMapper().writeValueAsString(vo));
            mapList.add(jsonObject);
        }
        return mapList;
    }


    /**
     * 对必传参数进行处理 并进行带条件查询
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @return List<ProductSeriesEntity> 封装后实体类的集合
     */
    private List<ProductSeriesEntity> queryByCondition(JSONArray dataContent, Long tenantSid) {
        List<ProductSeriesEntity> entities;
        //平台查询只支持传一笔数据 规格使用数组传参
        JSONObject jsonObject = dataContent.getJSONObject(0);
        entities = productSeriesMapper.getProductSeriesInfo(
                tenantSid,
                jsonObject.getString(ProductSeriesConstant.PRODUCT_NO),
                jsonObject.getString(ProductSeriesConstant.PRODUCT_NAME),
                jsonObject.getString(ProductSeriesConstant.MANAGE_STATUS),
                jsonObject.getString(ProductSeriesConstant.REMARKS));
        return entities;
    }


    /**
     * 对必传参数进行校验 并进行封装成entity(修改)
     *
     * @param dataContent 解析后数据
     * @param updateName  修改人姓名
     * @param i           循环遍历的索引值
     * @return ProductSeriesEntity 封装后的实体类
     * @throws DWArgumentException
     */
    private ProductSeriesEntity checkAndHandleData(JSONArray dataContent, String updateName, int i,Long tenantSid) throws DWArgumentException, IOException {
        JSONObject jsonObject = dataContent.getJSONObject(i);
        String oid = jsonObject.getString(ProductSeriesConstant.PRODUCT_ID);
        if (StringUtils.isEmpty(oid) || oid.isEmpty()) {
            throw new DWArgumentException("productId",
                    MultilingualismUtil.getLanguage("notExist"));
        }
        //对编号进行校验  只能是字母、数字、短横线组合
        if(!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(ProductSeriesConstant.PRODUCT_NO))){
            throw new DWArgumentException("productNo", MultilingualismUtil.getLanguage("NumberRules"));
        }
        ProductSeriesModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), ProductSeriesModel.class);
        ProductSeriesEntity entity = new ProductSeriesEntity();
        BeanUtils.copyProperties(model, entity);
        entity.setUpdateName(updateName);
        entity.setUpdateTime(new Date());
        entity.setTenantSid(tenantSid);
        return entity;
    }


    /**
     * 对必传参数进行校验 并进行封装成List<Entity>(新增)
     *
     * @param dataContent 解析后数据
     * @param tenantSid   租户id
     * @param userName    用户姓名
     * @return List<ProductSeriesEntity> 封装后实体类集合
     * @throws DWArgumentException
     */
    private List<ProductSeriesEntity> checkAndHandleData(JSONArray dataContent, Long tenantSid, String userName) throws DWArgumentException, IOException {
        List<ProductSeriesEntity> entities = new ArrayList<>();
        //查询所有产品系列编号信息
        List<String> productNos = productSeriesMapper.queryAllProductNos(tenantSid);
        for (int i = 0; i < dataContent.size(); i++) {
            JSONObject jsonObject = dataContent.getJSONObject(i);
            //对必传参数进行校验
            if (!jsonObject.containsKey(ProductSeriesConstant.PRODUCT_NO) || StringUtils.isEmpty(jsonObject.getString(ProductSeriesConstant.PRODUCT_NO))) {
                throw new DWArgumentException("productNo", MultilingualismUtil.getLanguage("notExist"));
            }
            if (CollUtil.isNotEmpty(productNos) && productNos.contains(jsonObject.getString(ProductSeriesConstant.PRODUCT_NO))) {
                throw new DWArgumentException("productNo", MultilingualismUtil.getLanguage("NoAlreadyExist"));
            }
            //对编号进行校验  只能是字母、数字、短横线组合
            if(!CheckFieldValueUtil.checkTargetNo(jsonObject.getString(ProductSeriesConstant.PRODUCT_NO))){
                throw new DWArgumentException("productNo",MultilingualismUtil.getLanguage("NumberRules"));
            }
            if (!jsonObject.containsKey(ProductSeriesConstant.PRODUCT_NAME) || StringUtils.isEmpty(jsonObject.getString(ProductSeriesConstant.PRODUCT_NAME))) {
                throw new DWArgumentException("productName", MultilingualismUtil.getLanguage("notExist"));
            }
            if (!jsonObject.containsKey(ProductSeriesConstant.MANAGE_STATUS) || StringUtils.isEmpty(jsonObject.getString(ProductSeriesConstant.MANAGE_STATUS))) {
                throw new DWArgumentException("manageStatus", MultilingualismUtil.getLanguage("notExist"));
            }
            String manageStatus = jsonObject.getString(ProductSeriesConstant.MANAGE_STATUS);
            if (!manageStatus.equals(EffectiveEnum.EFFECTIVE.getCode()) && !manageStatus.equals(EffectiveEnum.INVALID.getCode())) {
                throw new DWArgumentException("manageStatus", MultilingualismUtil.getLanguage("parameterError"));
            }
            ProductSeriesModel model = new ObjectMapper().readValue(jsonObject.toString().getBytes(), ProductSeriesModel.class);
            ProductSeriesEntity entity = new ProductSeriesEntity();
            BeanUtils.copyProperties(model, entity);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantSid(tenantSid);
            entity.setCreateTime(new Date());
            entity.setCreateName(userName);
            entities.add(entity);
        }
        return entities;
    }

}
