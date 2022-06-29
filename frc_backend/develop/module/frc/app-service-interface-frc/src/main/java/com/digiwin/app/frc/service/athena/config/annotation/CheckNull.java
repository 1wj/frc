package com.digiwin.app.frc.service.athena.config.annotation;

import java.lang.annotation.*;

/**
 * @ClassName CheckNull
 * @Description 自定义注解：校验非空字段
 * @Author HeX
 * @Date 2022/2/18 16:47
 * @Version 1.0
 **/
@Documented
@Inherited
// 接口、类、枚举、注解
@Target(ElementType.FIELD)
//只是在运行时通过反射机制来获取注解，然后自己写相应逻辑（所谓注解解析器）
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckNull {
    String message();
}
