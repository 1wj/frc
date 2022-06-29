package com.digiwin.app.frc.service.athena.meta.constant;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description:模块自定义常量
 * @Author: zhupeng@digiwin.com
 * @Datetime: 2022/1/4 9:31
 * @Version: 0.0.0.1
 */

public interface ModuleConstant {
    //MongoDB数据库名称
    public static final String MONGODB_DBNAME = "kmo_cloud";
    //esp接口入参
    public static final String MESSAGE_BODY_STD_DATA = "std_data";
    public static final String APP_ID = "KMO";
    public static final String APP_ID_LOG = "kmo_log";
    public static final String MODULE_NAME = "kmo";
    public static final String MODULE_NAME_USER = "kmo_user";
    //3D的格式
    public static final List<String> CAD_3D_FORMAT = Stream.of("sldasm", "sldprt", "ivp", "glb", "iva").collect(Collectors.toList());
    //2D的格式
    public static final List<String> CAD_2D_FORMAT = Stream.of("slddrw", "ivg", "svgz").collect(Collectors.toList());
    //原图格式
    public static final List<String> CAD_ORIGINAL = Stream.of("sldasm", "sldprt", "slddrw", "dwg").collect(Collectors.toList());
    //KK预览格式
    public static final List<String> KK_FILE_VIEW_EXT = Stream.of("txt", "html", "htm", "asp", "jsp", "xml", "json", "properties", "md",
            "gitignore", "log", "java", "py", "c", "cpp", "sql", "sh", "bat", "m", "bas", "prg", "cmd", "jpg", "jpeg", "png", "gif"
            , "doc", "docx", "ppt", "pptx", "pdf", "xls", "xlsx", "zip", "rar", "jar", "tar", "gzip", "mp3", "wav", "mp4", "flv", "dwg").collect(Collectors.toList());


}
