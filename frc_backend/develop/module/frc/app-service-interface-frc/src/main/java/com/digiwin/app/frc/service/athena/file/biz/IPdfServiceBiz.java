package com.digiwin.app.frc.service.athena.file.biz;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.service.DWFile;

/**
 * @Author: jiangzhou
 * @Date: 2022/04/24 16:30
 * @Version 1.0
 * @Description 生成列印报告pdf业务类
 */
public interface IPdfServiceBiz{
    /**
     * 根据dataContent生成pdf文件并返回云端地址
     * @param dataContent
     * @return
     */
    public String getReportPdf(JSONObject dataContent);

    /**
     * 文件上传
     * @param files
     * @return
     */
    public JSONObject upload(DWFile[] files) throws Exception;

    /**
     * uniApp移动端附件文件上传
     * @param messageBody
     * @return
     */
    public JSONObject uniAppUpload(String messageBody) throws Exception;
}
