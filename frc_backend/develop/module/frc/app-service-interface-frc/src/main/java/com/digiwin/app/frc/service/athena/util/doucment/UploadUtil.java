package com.digiwin.app.frc.service.athena.util.doucment;

import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.frc.service.athena.common.Const.SystemConstant;

import java.io.*;


/**
 * @Author: zhupeng@digiwin.com,HX
 * @Datetime: 2021/5/8 11:02
 * @Description: 分段上传常量
 * @Version: 0.0.0.1
 */
public class UploadUtil {

    public static final int DEFAULT_CHUNK_SIZE = 255 * 1024;
    public static final String DMC_URI = DWApplicationConfigUtils.getProperty("dmcUrl");
    public static final String BUCKET = DWApplicationConfigUtils.getProperty("dmcFRCBucket");
}
