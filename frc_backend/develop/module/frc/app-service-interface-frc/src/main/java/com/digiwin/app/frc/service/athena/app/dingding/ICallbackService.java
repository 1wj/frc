package com.digiwin.app.frc.service.athena.app.dingding;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWException;
import com.digiwin.app.frc.service.athena.app.entity.OpenSyncBizData;
import com.digiwin.app.service.*;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@DWRestfulService
public interface ICallbackService extends DWService{

    @DWRequestMapping(path = "/app/dingding/callback", method = {DWRequestMethod.POST})
    @AllowAnonymous
    public Object dingCallback(
            @RequestParam(value = "signature") String signature,
            @RequestParam(value = "timestamp") Long timestamp,
            @RequestParam(value = "nonce") String nonce,
            @RequestBody(required = false) JSONObject body
    );
    @DWOnLoad
    @DWRequestMapping(path = "/app/dingding/test", method = {DWRequestMethod.POST})
    @AllowAnonymous
    public Map<String, String> test(
            @RequestParam(value = "signature") String signature,
            @RequestParam(value = "timestamp") Long timestamp,
            @RequestParam(value = "nonce") String nonce,
            @RequestBody(required = false) JSONObject body
    );

    /**
     * 数据同步
     * @param data
     * @return
     */
    @DWRequestMapping(path = "/app/dingding/sync", method = {DWRequestMethod.POST})
    @AllowAnonymous
    public JSONObject syncData( @RequestBody(required = false) JSONObject data);


}
