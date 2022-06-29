package com.digiwin.app.frc.service.athena.config.annotation;

import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.config.annotation.CheckNull;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @ClassName CommonUtils
 * @Description 自定义注解处理工具类
 * @Author HeX
 * @Date 2022/2/18 16:48
 * @Version 1.0
 **/
public class ValidationHandler {
    /**
     * 通过反射来获取javaBean上的注解信息，判断属性值信息，然后通过注解元数据来返回
     */
    public static <T> boolean doValidator(T classes){
        Class<?> clazz = classes.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            CheckNull checkNull = field.getDeclaredAnnotation(CheckNull.class);
            if ( null != checkNull) {
                Object value = getValue(classes, field.getName());
                if (!notNull(value)) {
                    throwExcpetion(checkNull.message());
                }
            }
        }
        return true;
    }

    /**
     * 获取当前fieldName对应的值
     *
     * @param currentClass 对应的bean对象
     * @param fieldName	bean中对应的属性名称
     * @return
     */
    public static <T> Object getValue(T currentClass,String fieldName){
        Object value = null;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(currentClass.getClass());
            PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : props) {
                if (fieldName.equals(property.getName())) {
                    Method method = property.getReadMethod();
                    value = method.invoke(currentClass, new Object[]{});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
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
        if(value instanceof Long && isEmpty((String)value)){
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

    private static void throwExcpetion(String msg) {
        if(null!=msg){
            throw new DWRuntimeException(msg);
        }
    }

}
