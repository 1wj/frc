package com.digiwin.app.frc.service.athena.util.doucment;


/**
 * @Author: zhupeng@digiwin.com,HX
 * @Datetime: 2021/5/8 11:02
 * @Description: 全局常量
 * @Version: 0.0.0.1
 */
public class GlobalConstants {

    /**
     * 访问凭证
     */
    public static final String DIGI_MIDDLEWARE_AUTH_ACCESS = "digi-middleware-auth-access";
    /**
     * 在HttpHeader中传递的User信息
     */
    public static final String AUTH_USER = "digi-middleware-auth-user-data";
    /**
     * 在HttpHeader中传递的App信息
     */
    public static final String APP = "digi-middleware-auth-app-data";
    /**
     * 在HttpHeader中传递的OTA Token信息
     */
    public static final String HTTP_HEADER_OTA_TOKEN_KEY = "digi-middleware-auth-ota";
    /**
     * 在HttpHeader中传递的Access Token信息
     */
    public static final String HTTP_HEADER_ACCESS_TOKEN_KEY = "digi-middleware-auth-access";
    /**
     * 在HttpHeader中传递的User Token信息
     */
    public static final String HTTP_HEADER_USER_TOKEN_KEY = "digi-middleware-auth-user";
    /**
     * 在HttpHeader中传递的APP Token信息
     */
    public static final String HTTP_HEADER_APP_TOKEN_KEY = "digi-middleware-auth-app";
    /**
     * 在HttpHeader中传递的Driver Token信息
     */
    public static final String HTTP_HEADER_DRIVE_TOKEN_KEY = "digi-middleware-drive-access";
    /**
     * 在HttpHeader中传递 是都需要加密
     */
    public static final String HTTP_HEADER_DATA_MASK = "digi-middleware-data-mask";
    /**
     * 在HttpHeader中传递 租户ID
     */
    public static final String HTTP_HEADER_TENANT_ID = "tenantId";
    /**
     * 在HttpHeader中传递 设备信息
     */
    public static final String HTTP_HEADER_CLIENT_AGENT = "Client-Agent";

    /**
     * 查询时，若為歸戶資料，請填入固定值: iam-mapping
     */
    public static final String QUERY_PARAMETER_MAPPING = "iam-mapping";
    public static final String ZONE_OFF_SET = "+8";
    public static final String DEV_ACTIVE = "dev";

    public static final String EMPTY_STR = "[empty]";

    public static final String UPDATE_ALL = "all";
    public static final String UPDATE_ONLY_APPEND = "onlyAppend";

    public static final String ADMINISTRATORS = "administrators";
    public static final String ADMINISTRATOR = "administrator";
    public static final String INTEGRATION = "integration";
    public static final String SUPERADMIN = "superadmin";
    public static final String GUEST = "guest";

    public static final String ERROR_MESSAGE = "Server internal error";
    public static final String ENV_DEV = "dev";
    public static final String ENV_PROD = "prod";
    public static final String ENV_TEST = "test";
    public static final char AT = '@';
    public static final char DOT = '.';
    public static final char TAB = '\t';
    public static final char DASH = '-';
    public static final char COLON = ':';
    public static final char COMMA = ',';
    public static final char DOLLAR = '$';
    public static final char PERCENT = '%';
    public static final char ESCAPE = '\\';
    public static final char ASTERISK = '*';
    public static final char SEMICOLON = ';';
    public static final char CURLY_LEFT = '{';
    public static final char CURLY_RIGHT = '}';
    public static final char DOUBLE_QUOTE = '"';
    public static final char SINGLE_QUOTE = '\'';
    public static final char LEFT_PARENTHESIS = '(';
    public static final char RIGHT_PARENTHESIS = ')';
    public static final String EMPTY = "";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String DEFAULT_VALUE_SEPARATOR = ":-";
    public static final String DEFAULT_CONTEXT_NAME = "default";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String LOCALE = "Locale";
}
