package com.digiwin.app.frc.service.athena.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.MD5;

/**
 * @Author: zhupeng@digiwin.com
 * @Datetime: 2021/6/29 11:36
 * @Description: digi-key生成
 * @Version: 0.0.0.1
 */
public class DigiwinKeyUtil {
    public static void main(String[] args) {
//digi-host:{"prod":"Athena","ver":"1.0","ip":"","id":"BpmCloud","timestamp":"20210506103327709","acct":"athena"}
//digi-service:{"prod":"KSC","tenant_id":"DWKSC","name":"cad.attribute.info.check","uid":"KSC"}
        String host = "{\"prod\":\"Athena\",\"ver\":\"1.0\",\"ip\":\"\",\"id\":\"BpmCloud\",\"timestamp\":\"20210506103327709\",\"acct\":\"athena\"}";
        String service = "{\"prod\":\"FRC\",\"tenant_id\":\"FRC\",\"name\":\"question.acceptance.info.update\",\"uid\":\"FRC\"}";
        String tDigiKey = host + service;
        String key = SecureUtil.md5(tDigiKey);
        System.out.println("digi-host:" + host);
        System.out.println("digi-service:" + service);
        System.out.println("digi-key:" + key);
    }
}
