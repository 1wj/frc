package com.digiwin.app.frc.service.athena.util;

import com.digiwin.app.module.spring.DWModuleSpringUtils;

import java.util.Map;


/**
 * @Description: SpringContextUtil
 */

public class SpringContextUtil {



    /**
     * 根据名称获取bean
     *
     * @param beanName
     * @return
     */
    public static Object getBean(String beanName) {
        return DWModuleSpringUtils.getModuleSpringContext("frc").getBean(beanName);
    }

    /**
     * 根据bean名称获取指定类型bean
     *
     * @param beanName bean名称
     * @param clazz    返回的bean类型,若类型不匹配,将抛出异常
     */
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return DWModuleSpringUtils.getModuleSpringContext("frc").getBean(beanName, clazz);
    }

    /**
     * 根据类型获取bean
     *
     * @param clazz
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        T t = null;
        Map<String, T> map = DWModuleSpringUtils.getModuleSpringContext("frc").getBeansOfType(clazz);
        for (Map.Entry<String, T> entry : map.entrySet()) {
            t = entry.getValue();
        }
        return t;
    }

    /**
     * 是否包含bean
     *
     * @param beanName
     * @return
     */
    public static boolean containsBean(String beanName) {
        return DWModuleSpringUtils.getModuleSpringContext("frc").containsBean(beanName);
    }

    /**
     * 是否是单例
     *
     * @param beanName
     * @return
     */
    public static boolean isSingleton(String beanName) {
        return DWModuleSpringUtils.getModuleSpringContext("frc").isSingleton(beanName);
    }

    /**
     * bean的类型
     *
     * @param beanName
     * @return
     */
    public static Class getType(String beanName) {
        return DWModuleSpringUtils.getModuleSpringContext("frc").getType(beanName);
    }

    /**
     * 获取所有bean的map
     *
     * @param clazz bean名称
     * @param <T>   返回所有bean类型
     * @return
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return DWModuleSpringUtils.getModuleSpringContext("frc").getBeansOfType(clazz);
    }
}