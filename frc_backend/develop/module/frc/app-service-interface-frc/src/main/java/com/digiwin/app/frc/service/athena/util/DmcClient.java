package com.digiwin.app.frc.service.athena.util;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.common.DWApplicationConfigUtils;
import com.digiwin.app.service.DWFile;
import com.digiwin.dmc.sdk.config.DmcUrl;
import com.digiwin.dmc.sdk.config.SDKConstants;
import com.digiwin.dmc.sdk.config.ServerSetting;
import com.digiwin.dmc.sdk.entity.FileInfo;
import com.digiwin.dmc.sdk.service.IDocumentStorageService;
import com.digiwin.dmc.sdk.service.IUserManagerService;
import com.digiwin.dmc.sdk.service.download.FileService;
import com.digiwin.dmc.sdk.service.download.IFileService;
import com.digiwin.dmc.sdk.service.impl.DocumentStorageService;
import com.digiwin.dmc.sdk.service.impl.UserManagerService;
import com.digiwin.dmc.sdk.service.upload.IGeneralDocumentUploader;
import com.digiwin.dmc.sdk.service.upload.UploadProgressEventArgs;
import com.digiwin.dmc.sdk.util.HttpRequestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;


/**
 * @ClassName DmcUtil
 * @Description 对dmc相关操作
 * @Author author
 * @Date 2019/10/16 11:09
 * @Version 1.0
 **/
public class DmcClient {
    private static Logger logger = LoggerFactory.getLogger(DmcClient.class);
    private static IUserManagerService userManagerService = UserManagerService.userInstance();

    /**
     * 上传附件
     * @param dwFile
     * @return 文档中心返回的id
     */
    public static String uploadFile(DWFile dwFile) {
        // 启动dmc文档中心
        startDmc();
        IDocumentStorageService documentStorageService = DocumentStorageService.instance();
        Map map = new HashMap(16);
        try {
            //设置文件信息
            FileInfo fileInfo = new FileInfo();
            //设置文件名
            String fileName = dwFile.getFileName();
            fileInfo.setFileName(fileName);
            //设置文件描述
            fileInfo.setDisplayName(fileName);
            fileInfo.setExtension(fileName.substring(fileName.lastIndexOf(".") + 1));
            //定义文件上传过程参数对象，用于查看文件上传状态
            final UploadProgressEventArgs uploadProgressEventArgs = new UploadProgressEventArgs();
            //2.文件上传
            //参数：流，文件信息
            byte[] bytes = toByteArray(dwFile.getInputStream());
            IGeneralDocumentUploader generalDocumentUploader = documentStorageService.uploadDocument(bytes,fileInfo);
            //执行上传线程，直到上传完成结束进程
            generalDocumentUploader.upload().onCompleted(eventArgs -> {
                //判断文件上传是否完成
                if (eventArgs.getPercentage() == 1) {
                    uploadProgressEventArgs.setPercentage(1);
                    //3.上传成功，返回上传后的文件Id
                    String fileId = eventArgs.getFileId();
                    map.put("id",fileId);
                }
            });
            // 让线程休息，获取 fileId
            while (uploadProgressEventArgs.getPercentage() != 1) {
                Thread.sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (String) map.get("id");
    }

    /**
     * 上传附件
     * @param fileName
     * @param inputStream
     * @return 文档中心返回的id
     */
    public static String uploadFile(String fileName,InputStream inputStream) throws Exception {
        // 启动dmc文档中心
        startDmc();
        IDocumentStorageService documentStorageService = DocumentStorageService.instance();
        Map map = new HashMap(16);
        //设置文件信息
        FileInfo fileInfo = new FileInfo();
        //设置文件名
        fileInfo.setFileName(fileName);
        //设置文件描述
        fileInfo.setDisplayName(fileName);
        fileInfo.setExtension(fileName.substring(fileName.lastIndexOf(".") + 1));
        //定义文件上传过程参数对象，用于查看文件上传状态
        final UploadProgressEventArgs uploadProgressEventArgs = new UploadProgressEventArgs();
        //2.文件上传
        //参数：流，文件信息
        byte[] bytes = toByteArray(inputStream);
        IGeneralDocumentUploader generalDocumentUploader = documentStorageService.uploadDocument(bytes,fileInfo);
        //执行上传线程，直到上传完成结束进程
        generalDocumentUploader.upload().onCompleted(eventArgs -> {
            //判断文件上传是否完成
            if (eventArgs.getPercentage() == 1) {
                uploadProgressEventArgs.setPercentage(1);
                //3.上传成功，返回上传后的文件Id
                String fileId = eventArgs.getFileId();
                map.put("id",fileId);
            }
        });
        // 让线程休息，获取 fileId
        while (uploadProgressEventArgs.getPercentage() != 1) {
            Thread.sleep(500);
        }
        return (String) map.get("id");
    }

    /**
     * 上传附件
     * @param fileName
     * @param bytes
     * @return 文档中心返回的id
     */
    public static String uploadFile(String fileName,byte[] bytes) throws Exception {
        // 启动dmc文档中心
        startDmc();
        IDocumentStorageService documentStorageService = DocumentStorageService.instance();
        Map map = new HashMap(16);
        //设置文件信息
        FileInfo fileInfo = new FileInfo();
        //设置文件名
        fileInfo.setFileName(fileName);
        //设置文件描述
        fileInfo.setDisplayName(fileName);
        fileInfo.setExtension(fileName.substring(fileName.lastIndexOf(".") + 1));
        //定义文件上传过程参数对象，用于查看文件上传状态
        final UploadProgressEventArgs uploadProgressEventArgs = new UploadProgressEventArgs();
        //2.文件上传
        //参数：流，文件信息
        IGeneralDocumentUploader generalDocumentUploader = documentStorageService.uploadDocument(bytes,fileInfo);
        //执行上传线程，直到上传完成结束进程
        generalDocumentUploader.upload().onCompleted(eventArgs -> {
            //判断文件上传是否完成
            if (eventArgs.getPercentage() == 1) {
                uploadProgressEventArgs.setPercentage(1);
                //3.上传成功，返回上传后的文件Id
                String fileId = eventArgs.getFileId();
                map.put("id",fileId);
            }
        });
        // 让线程休息，获取 fileId
        while (uploadProgressEventArgs.getPercentage() != 1) {
            Thread.sleep(500);
        }
        return (String) map.get("id");
    }

    /**
     * 删除dmc附件
     * @param id 文档中心回传的附件id
     */
    public static void deleteFile(String id) {
        // 启动dmc
        startDmc();
        //1.删除默认bucket下面文件
        IDocumentStorageService documentStorageService=DocumentStorageService.instance();
        // 参数：被删除文件id
        documentStorageService.deleteDocument(id);
    }

    /**
     * 启动dmc参数设置
     */
    public static void startDmc() {
        String dmcUrl = DWApplicationConfigUtils.getProperty("dmcUrl");
        ServerSetting.setServiceUrl(dmcUrl);
        ServerSetting.setIdentityName( DWApplicationConfigUtils.getProperty("dmcUserName"));
        ServerSetting.setIdentityPwd(DWApplicationConfigUtils.getProperty("dmcPwd"));
        ServerSetting.setBucketName(DWApplicationConfigUtils.getProperty("dmcFRCBucket"));
    }



    /**
     * inputStream 输入流转换成byte[]字节数组
     * @param inputStream 输入流
     * @return byte[]字节数组
     * @throws IOException 异常处理
     */
    public static byte[] toByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024*4];
        int n = 0;
        while (-1 != (n = inputStream.read(buffer))) {
            output.write(buffer,0,n);
        }
        return output.toByteArray();
    }


    /**
     * 获取文件详情
     * /api/dmc/v2/fileinfo/{bucket}/{fileId}
     *
     * @param fileId
     * @return
     * @throws Exception
     */
    public static Map<String, Object> getFileInfo(String fileId, String dmcBucket) {
        startDmc();
        Map<String, String> headers = new HashMap();
        if (StringUtils.isEmpty(dmcBucket)) {
            dmcBucket = ServerSetting.getBucketName();
        }
        String userToken = userManagerService.getUserToken(ServerSetting.getUser());
        headers.put(SDKConstants.HttpHeaderUserTokenKey, userToken);
        String deleteUrl = String.format("%s/api/dmc/v2/fileinfo/%s/%s", DmcUrl.getServiceUrl(), dmcBucket, fileId);
        Map<String, Object> getEntity = HttpRequestUtil.get(deleteUrl, headers, Map.class);
        if ((Boolean) getEntity.get("success") && getEntity.containsKey("data")) {
            Map<String, Object> data = (Map<String, Object>) getEntity.get("data");
            return data;
        }
        return null;
    }

    /**
     * 下载文件（单个文件下载）
     *
     * @throws Exception
     */
    public static JSONObject download(String id, String path) throws Exception {
        startDmc();
        String dmcId = id;
        logger.info("dmc_url:{}", DmcUrl.getServiceUrl());
        logger.info("dmc_bucket:{}", ServerSetting.getBucketName());
        String fileUrl = String.format("%s/api/dmc/v2/file/%s/preview/%s", DmcUrl.getServiceUrl(), ServerSetting.getBucketName(), dmcId);
        URL url = new URL(fileUrl);
        URLConnection conn = url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        getFile(inputStream,path);
        File downloadFile = new File(path);
        Long size = downloadFile.length()/1024;
        logger.info(path+"文件大小为"+downloadFile.length()/1024+"KB");
        return new JSONObject().fluentPut("size:",size).fluentPut("path",path);
    }

    public static void download2(String id,String path) throws Exception {
        startDmc();
        logger.info("dmc_url:{}", DmcUrl.getServiceUrl());
        logger.info("dmc_bucket:{}", ServerSetting.getBucketName());
        byte [] fileBytes = downloadFileForByte(ServerSetting.getBucketName(),id);
        File file = FileUtil.writeBytes(fileBytes, path);
        logger.info("file:{}", file.getAbsolutePath());
    }

    /**
     * 下载附件
     *
     * @param bucketName
     * @param dmcId
     * @return 文件
     */
    public static byte[] downloadFileForByte(String bucketName, String dmcId) {
        IFileService iFileService = FileService.fileInstance();
        byte[] bytes = iFileService.download(bucketName, dmcId);
        return bytes;
    }

    public static void getFile(InputStream is,String fileName) throws IOException{
        BufferedInputStream in=null;
        BufferedOutputStream out=null;
        in=new BufferedInputStream(is);
        out=new BufferedOutputStream(new FileOutputStream(fileName));
        int len=-1;
        byte[] b=new byte[1024];
        while((len=in.read(b))!=-1){
            out.write(b,0,len);
        }
        in.close();
        out.close();
    }

}
