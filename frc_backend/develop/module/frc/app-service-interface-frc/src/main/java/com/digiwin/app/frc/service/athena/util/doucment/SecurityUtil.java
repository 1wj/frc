package com.digiwin.app.frc.service.athena.util.doucment;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @Author: zhupeng@digiwin.com,HX
 * @Datetime: 2021/5/8 11:02
 * @Description: 加密util
 * @Version: 0.0.0.1
 */
public final class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);

    public static String getSha256(String str) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = messageDigest.digest();
            messageDigest.update(bytes);
            return Base64.encodeBase64String(messageDigest.digest());
        } catch (Exception e) {
            logger.error("加密失败", e);
        }
        return "";
    }
}
