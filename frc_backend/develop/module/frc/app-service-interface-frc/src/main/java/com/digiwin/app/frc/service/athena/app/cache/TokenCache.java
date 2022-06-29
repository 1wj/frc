package com.digiwin.app.frc.service.athena.app.cache;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class TokenCache {
    private static TokenCache instance = null;
    private Map<String,String> tokenMap = new HashMap<>();
    private Map<String,Date> updateTimeMap = new HashMap<>();
    private Map<String,Long> agentMap = new HashMap<>();

    private TokenCache(){}

    public static TokenCache getInstance() {
        if (instance == null) {
            synchronized (TokenCache.class) {
                if (instance == null) {
                    instance = new TokenCache();
                }
            }
        }
        return instance;
    }
}
