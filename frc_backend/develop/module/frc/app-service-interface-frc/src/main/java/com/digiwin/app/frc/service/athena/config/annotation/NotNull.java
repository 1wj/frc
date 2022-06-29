package com.digiwin.app.frc.service.athena.config.annotation;

/**
 * @ClassName Validation
 * @Description TODO
 * @Author HeX
 * @Date 2022/2/19 0:35
 * @Version 1.0
 **/

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验参数注解  可根据需要扩展校验数据的选项，比如值区间max,min,值域
 * @author vinod
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {
    String message() default "";
    //校验的属性的类型
    ValidateType value() default ValidateType.TYPE_STRING;
    //配置则根据当前字段校验 默认为空
    String accordField() default "";
    //根据accordField的值校验值 默认为空
    String accordValue() default "";
    //正数校验 默认不校验
    boolean isPositive() default false;
}
