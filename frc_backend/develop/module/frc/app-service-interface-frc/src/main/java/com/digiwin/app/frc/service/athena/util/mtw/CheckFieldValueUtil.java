package com.digiwin.app.frc.service.athena.util.mtw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.digiwin.app.container.exceptions.DWArgumentException;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: xieps
 * @Date: 2021/12/20 20:12
 * @Version 1.0
 * @Description 字段值校验工具类
 */
@Component
public class CheckFieldValueUtil {

    /**
     * 检测字符串中只能包含字母、数字、横线
     *
     * @param target
     * @return boolean
     */
    public static boolean checkTargetNo(String target) {
        if(!StringUtils.isEmpty(target)){
            final String format = "[^a-zA-Z\\d\\-]";
            Pattern pattern = Pattern.compile(format);
            Matcher matcher = pattern.matcher(target);
            return !matcher.find();
        }else{
            return true;
        }
    }

    /**
     * 检测字符串中只能包含字母、数字
     *
     * @param target
     * @return boolean
     */
    public static boolean checkTargetId(String target) {
        if(!StringUtils.isEmpty(target)) {
            final String format = "[^a-zA-Z\\d\\-]";
            Pattern pattern = Pattern.compile(format);
            Matcher matcher = pattern.matcher(target);
            return !matcher.find();
        }else{
            return true;
        }
    }

    /**
     * 校验字符串是否包含特殊字符
     *
     * @param target
     * @return boolean
     */
    public static boolean isContainSpecialChar(String target) {
        String strEgx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern pattern = Pattern.compile(strEgx);
        Matcher matcher = pattern.matcher(target);
        return !matcher.find();
    }

    /**
     * 验证器
     */
    @Autowired
    private Validator validator;

    private static CheckFieldValueUtil checkFieldValueUtil;

    //加入注解@postcontruct来初始化这个bean
    @PostConstruct
    public void init() {
        checkFieldValueUtil = this;
        checkFieldValueUtil.validator = this.validator;
    }

    /**
     * 检查字符串长度是否超过规定长度
     *
     * @param jsonArray
     * @param obj
     * @return boolean
     */
    public static void validateModels(JSONArray jsonArray, Object obj) throws DWArgumentException {
        List<Object> models = JSON.parseArray(jsonArray.toJSONString(), (Class<Object>) obj.getClass());
        for (Object model : models) {
            Set<ConstraintViolation<Object>> validate = checkFieldValueUtil.validator.validate(model, Default.class);
            if (validate.iterator().hasNext()) {
                ConstraintViolation<Object> constraintViolation = validate.iterator().next();
                if("equipment_category_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("equipment_category")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("belong_department_id_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("belong_department_id")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("workstation_id_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("workstation_id")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("op_type_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("op_type")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("op_no_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("op_no")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("customer_service_employee_id_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("customer_service_employee_id")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("customer_service_employee_name_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("customer_service_employee_name")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("category_no_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("category_no")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("defect_no_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("defect_no")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("defect_grade_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("defect_grade")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("item_no_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("item_no")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("product_no_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("product_no")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("return_no_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("return_no")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("node_no_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("node_no")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("classification_no_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("classification_no")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("source_no_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("source_no")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
                if("reason_code_overLength".equals(constraintViolation.getMessage())){
                    throw  new DWArgumentException(constraintViolation.getPropertyPath().toString(),
                            MultilingualismUtil.getLanguage("reason_code")+MultilingualismUtil.getLanguage("valueTooLong"));
                }
            }
        }
    }


}
