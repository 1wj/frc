package com.digiwin.app.frc.service.athena.app.common;

import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.frc.service.athena.app.cache.TokenCache;
import com.digiwin.app.frc.service.athena.app.job.CreateNoticeHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CacheCommonUtils {

    @Autowired
    private CreateNoticeHandler createNoticeHandler;
    /**
     *获取缓存里企业token
     * @param corpId
     * @return
     * @throws DWException
     */
    public String getAccessToken(String corpId) throws DWException {
        //获取accessToken
        Date nowDate = new Date();
        TokenCache tokenCache = TokenCache.getInstance();
        if(StringUtils.isEmpty(tokenCache.getTokenMap().get(corpId)) || createNoticeHandler.getInterval(tokenCache.getUpdateTimeMap().get(corpId),nowDate)){
            //token是空或者时间间隔超过1小时50分钟则重新获取token
            tokenCache.getTokenMap().put(corpId,createNoticeHandler.getAccessToken(corpId));
            tokenCache.getUpdateTimeMap().put(corpId,nowDate);
        }
        return tokenCache.getTokenMap().get(corpId);
    }
}
