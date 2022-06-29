package com.digiwin.app.frc.service.athena.app.common;

import com.digiwin.app.common.DWApplicationConfigUtils;
import org.springframework.stereotype.Component;

/**
 * 应用常量类
 */
@Component
public class Constant {
    /**
     * 应用的SuiteKey，登录开发者后台，点击应用管理，进入应用详情可见
     */
    public static String SUITE_KEY="suited1gttuoc5sl8jvjw";

    /**
     * 应用的SuiteSecret，登录开发者后台，点击应用管理，进入应用详情可见
     */
    public static final String SUITE_SECRET="Oe_vPWnDtC9zhW2m40mb9RaEqsw9I2PI_tsdOwXFudg1lp_aiqZ74IDGAZ-sm2vT";

    /**
     * 回调URL签名用。应用的签名Token, 登录开发者后台，点击应用管理，进入应用详情可见
     */
    public static final String TOKEN = "kpIas6LPDqB1HJSBX3CFJLzChpC";

    /**
     * 回调URL加解密用。应用的"数据加密密钥"，登录开发者后台，点击应用管理，进入应用详情可见
     */
    public static final String ENCODING_AES_KEY = "F51nV19W7RlwaJCgZXNQIcpYbXwIvBkqIvfJxEAwejZ";

    public static final String APPID = "FRC";


}
