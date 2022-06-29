package com.digiwin.app.frc.service.athena.file.service;


import com.digiwin.app.service.DWFile;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;



/**
 * @Author: jiangzhou
 * @Date: 2022/04/24 16:30
 * @Version 1.0
 * @Description 为Athena提供生成列印报告pdf
 */
@DWRestfulService
public interface IPdfService extends DWService {
    /**
     * 传入问题信息生成pdf
     * @param messageBody
     * @return
     * @throws Exception
     */
    @DWRequestMapping(path = "/file/report/pdf", method = {DWRequestMethod.POST})
    DWServiceResult getReportPdf(String messageBody) throws Exception;

    /**
     * 附件文件上传
     * @param files
     * @return
     * @throws Exception
     */
    @DWRequestMapping(path = "/file/upload", method = {DWRequestMethod.POST})
    DWServiceResult upload(DWFile[] files) throws Exception;
    /**
     * uniApp移动端附件文件上传
     * @param messageBody {"files":[{"name:":"1.jpg","data":"base64Str"},{"name:":"2.jpg","data":"base64Str"}]}
     * @return
     * @throws Exception
     */
    @DWRequestMapping(path = "/file/uniApp/upload", method = {DWRequestMethod.POST})
    DWServiceResult uniAppUpload(String messageBody) throws Exception;
}
