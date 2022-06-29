package com.digiwin.app.frc.service.athena.strategy.iamservicestrategy;

import com.digiwin.app.frc.service.athena.strategy.iamservicestrategy.impl.AdminRegister;
import com.digiwin.app.frc.service.athena.strategy.iamservicestrategy.impl.GeneralUsersRegister;
import com.digiwin.app.frc.service.athena.app.common.enums.UserRoleEnum;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName UserRegisterFactory
 * @Description 用户注册策略
 * @Author HeX
 * @Date 2022/22/01 22：01
 * @Version 1.0
 **/
@Component
public class UserRegisterFactory {

    private UserRegisterFactory(){}
    private static final Map<Integer, UserRegisterStrategy> map = new HashMap<>();

    static {
        map.put(UserRoleEnum.GENERAL.getCode(),new GeneralUsersRegister());
        map.put(UserRoleEnum.ADMIN.getCode(),new AdminRegister());
    }

    public static UserRegisterStrategy getStrategy(int role) {
        return map.get(role);
    }
}
