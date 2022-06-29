package com.digiwin.app.frc.service.athena.config.annotation;

import com.digiwin.app.container.exceptions.DWRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * @ClassName ValidationHandler
 * @Description 仅处理传入object中的Validate注解
 * @Author HeX
 * @Date 2022/2/19 0:36
 * @Version 1.0
 **/
public class ParamValidationHandler {

    private static Logger logger = LoggerFactory.getLogger(ParamValidationHandler.class);

    // 方法重载
    public static void validateParams(Object obj){
        validateParams(obj,null,true);
    }

    public static void validateParams(Object obj,boolean accordFlag){
        validateParams(obj,null,accordFlag);
    }

    /**
     * 校验参数不为空
     * @param obj 需要校验的对象
     * @param excludeParams 需要排除校验的属性名集合
     * @param accordFlag 根据属性依赖校验
     */
    public static void validateParams(Object obj,Set<String> excludeParams,boolean accordFlag){

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                // 当前属性在需要排除校验的集合中时，跳过当前字段校验。
                if(excludeParams!=null&&excludeParams.contains(field.getName())){
                    continue;
                }
                NotNull annotation = field.getAnnotation(NotNull.class);
                //System.out.println("=====----"+annotation+"---> "+field.getName());
                //注解存在时，则说明当前字段需要校验
                if(annotation!=null){
                    // 是否根据依据,筛选为空属性
                    if(accordFlag){
                        // 根据某字段判别是否为空
                        if(StringUtils.isNotEmpty(annotation.accordField())){
                            Field accordField = obj.getClass().getDeclaredField(annotation.accordField());
                            accordField.setAccessible(true);
                            Object accordValue = accordField.get(obj);
                            // 为空或不等于注解预设值则跳过当前属性的校验
                            if(accordValue==null||!accordValue.toString().equals(annotation.accordValue())){
                                continue;
                            }
                        }
                    }
                    // 属性值不能为空
//                    if(value==null){
//                        throw new DWRuntimeException(field.getName());
//                    }
                    checkTypeEnumValue(annotation,field,value,annotation.isPositive());
                }
            } catch (IllegalAccessException e) {
                // e.printStackTrace();
                //抛出自定义异常信息
                throw new DWRuntimeException(field.getName());
            } catch (NoSuchFieldException e) {
                //抛出自定义异常信息
                throw new DWRuntimeException(field.getName());
            }
        }
    }

    /**
     * 校验注解配置的属性值
     * @param annotation
     * @param field
     * @param value
     * @param isPositive
     */
    private static void checkTypeEnumValue(NotNull annotation, Field field, Object value, boolean isPositive){
        // 暂值列举几种数据处理,剩余类型处理类似
        switch (annotation.value()){
            case TYPE_STRING:
//                if(StringUtils.isEmpty(value.toString())||"null".equals(value.toString().toLowerCase())){
//                    throw new DWRuntimeException(field.getName());
//                }
                if (!notNull(value)) {
                    throw new DWRuntimeException(annotation.message());
                }
                break;
            case TYPE_LONG:
                if(0L==(Long)value){
                    throw new DWRuntimeException(annotation.message());
                }
                if(isPositive&&(Long)value<0){
                    throw new DWRuntimeException(annotation.message());
                }
                break;
            case TYPE_INTEGER:
                if (isPositive && 0 == (int)value) {
                    throw new DWRuntimeException(annotation.message());
                }
        }
    }

    /**
     * 非空校验
     *
     * @param value
     * @return
     */
    public static boolean notNull(Object value){
        if(null==value){
            return false;
        }
        if(value instanceof String && isEmpty((String)value)){
            return false;
        }
        if(value instanceof List && isEmpty((List<?>)value)){
            return false;
        }
        return null!=value;
    }

    public static boolean isEmpty(String str){
        return null==str || str.isEmpty();
    }
    public static boolean isEmpty(List<?> list){
        return null==list || list.isEmpty();
    }
}
