package com.digiwin.app.frc.service.athena.util;

import com.digiwin.app.service.DWServiceContext;

import java.util.Map;


/**
 * @Description: 用户租户信息
 * {"tenantSid":302506768777792,"userSid":253990666580544,"userName":"虚拟用户","userId":"virtual"}
 * @Author: zhupeng@digiwin.com
 * @Datetime: 2021/11/11 13:45
 * @Version: 0.0.0.1
 */

public class TenantTokenUtil {
    public static boolean isTenant() {
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        if (profile.isEmpty()) {
            return false;
        }
        return true;
    }

    public static Long getTenantSid() {
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        if (profile.isEmpty()) {
            return null;
        }
        Long tenantsid = (Long) profile.get("tenantSid");
        return tenantsid;
    }

    public static String getTenantId() {
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        if (profile.isEmpty()) {
            return null;
        }
        String tenantId = (String) profile.get("tenantId");
        return tenantId;
    }


    public static Long getUserSid() {
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        if (profile.isEmpty()) {
            return null;
        }
        Long userSid = (Long) profile.get("userSid");
        return userSid;
    }

    public static String getUserName() {
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        if (profile.isEmpty()) {
            return null;
        }
        String userName = (String) profile.get("userName");
        return userName;
    }

    public static String getUserId() {
        Map<String, Object> profile = DWServiceContext.getContext().getProfile();
        if (profile.isEmpty()) {
            return null;
        }
        String userId = (String) profile.get("userId");
        return userId;
    }

}
