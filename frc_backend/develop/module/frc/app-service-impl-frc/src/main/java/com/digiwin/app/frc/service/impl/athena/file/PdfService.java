package com.digiwin.app.frc.service.impl.athena.file;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.file.biz.IPdfServiceBiz;
import com.digiwin.app.frc.service.athena.file.service.IPdfService;
import com.digiwin.app.service.DWFile;
import com.digiwin.app.service.DWServiceResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Slf4j
public class PdfService implements IPdfService {

    @Autowired
    private IPdfServiceBiz iPdfServiceBiz;

    @Override
    public DWServiceResult getReportPdf(String messageBody) throws Exception {
        try {
            String reportPdfUrl = iPdfServiceBiz.getReportPdf(JSON.parseObject(messageBody));
            if(StringUtils.isEmpty(reportPdfUrl)){
                return new DWServiceResult(false,"生成列印报告pdf失败",reportPdfUrl);
            }
            return new DWServiceResult(true,"生成列印报告pdf成功",reportPdfUrl);
        }catch (Exception e){
            log.error("生成列印报告出错："+e.getMessage());
            e.printStackTrace();
            return new DWServiceResult(false,"生成列印报告pdf失败",null);
        }
    }

    @Override
    public DWServiceResult upload(DWFile[] files) throws Exception {
        try {
            JSONObject upload = iPdfServiceBiz.upload(files);
            return new DWServiceResult(true,"附件上传成功",upload);
        }catch (Exception e){
            log.error("附件上传出错："+e.getMessage());
            e.printStackTrace();
            return new DWServiceResult(false,"附件上传失败",null);
        }
    }

    @Override
    public DWServiceResult uniAppUpload(String messageBody) throws Exception {
        try {
            JSONObject upload = iPdfServiceBiz.uniAppUpload(messageBody);
            if(upload == null){
                return new DWServiceResult(true,"未上传附件",upload);
            }
            return new DWServiceResult(true,"附件上传成功",upload);
        }catch (Exception e){
            log.error("附件上传出错："+e.getMessage());
            e.printStackTrace();
            return new DWServiceResult(false,"附件上传失败",null);
        }
    }
}
