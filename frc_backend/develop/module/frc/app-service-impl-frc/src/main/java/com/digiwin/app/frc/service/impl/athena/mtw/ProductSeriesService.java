package com.digiwin.app.frc.service.impl.athena.mtw;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.mtw.biz.ProductSeriesBiz;
import com.digiwin.app.frc.service.athena.mtw.service.IProductSeriesService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;

import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;


import java.util.List;


/**
 * @Author: xieps
 * @Date: 2021/11/12 16:02
 * @Version 1.0
 * @Description
 */
public class ProductSeriesService implements IProductSeriesService {

    @Autowired
    private ProductSeriesBiz productSeriesBiz;


    @Override
    public DWServiceResult addProductSeriesInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseDate(messageBody, true);
            List<JSONObject> resultInfo = productSeriesBiz.addProductSeries(dataContent);
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("addSuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult deleteProductSeriesInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            JSONArray dataContent = parseDate(messageBody, true);
            boolean resultInfo = productSeriesBiz.deleteProductSeries(dataContent);
            if (resultInfo) {
                result.setSuccess(true);
                result.setMessage(MultilingualismUtil.getLanguage("deleteSuccess"));
            } else {
                result.setSuccess(false);
                result.setMessage(MultilingualismUtil.getLanguage("deleteFail"));
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult updateProductSeriesInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //??????????????????????????????????????????
            JSONArray dataContent = parseDate(messageBody, true);
            boolean resultInfo = productSeriesBiz.updateProductSeries(dataContent);
            if (resultInfo) {
                result.setSuccess(true);
                result.setMessage(MultilingualismUtil.getLanguage("updateSuccess"));
            } else {
                result.setSuccess(false);
                result.setMessage(MultilingualismUtil.getLanguage("updateFail"));
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult getProductSeriesInfo(String messageBody) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            //?????????????????????????????????
            JSONArray dataContent = parseDate(messageBody, false);
            List<JSONObject> resultInfo = productSeriesBiz.getProductSeries(dataContent);
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("querySuccess"));
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(MultilingualismUtil.getLanguage("queryFail"));
        }
        return result;
    }


    /**
     * ????????????????????????
     *
     * @param messageBody ?????????
     * @param check       ??????????????????
     * @return JSONArray
     */
    private JSONArray parseDate(String messageBody, boolean check) {
        JSONObject jsonObject = JSONObject.parseObject(messageBody);
        JSONObject stdData = (JSONObject) jsonObject.get("std_data");
        JSONObject parameter = (JSONObject) stdData.get("parameter");
        JSONArray dataContent = (JSONArray) parameter.get("product_info");
        if (check) {
            if (StringUtils.isEmpty(dataContent) || dataContent.isEmpty()) {
                throw new DWRuntimeException(MultilingualismUtil.getLanguage("parameterError"));
            }
        }
        return dataContent;
    }


}
