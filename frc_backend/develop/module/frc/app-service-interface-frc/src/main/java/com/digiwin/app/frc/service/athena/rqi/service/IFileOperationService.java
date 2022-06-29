package com.digiwin.app.frc.service.athena.rqi.service;

import com.digiwin.app.service.AllowAnonymous;
import com.digiwin.app.service.DWFile;
import com.digiwin.app.service.DWService;
import com.digiwin.app.service.DWServiceResult;
import com.digiwin.app.service.restful.DWRequestMapping;
import com.digiwin.app.service.restful.DWRequestMethod;
import com.digiwin.app.service.restful.DWRestfulService;

/**
 * @Author: xieps
 * @Date: 2022/4/27 23:18
 * @Version 1.0
 * @Description
 */
@DWRestfulService
public interface IFileOperationService extends DWService {


    @DWRequestMapping(path = "/file/upload/question/classification/test", method = {DWRequestMethod.POST})
    DWServiceResult importExcelTest(DWFile file) throws Exception;


    /**
     * 问题责任人问题维度Excel导入
     *
     * @param file 导入文件
     * @return DWServiceResult 返回值类型
     * @throws Exception
     */
    @DWRequestMapping(path = "/file/upload/question/liable/person/question/latitude/config/test", method = {DWRequestMethod.POST})
    DWServiceResult importLiablePersonConfigInfo(DWFile file) throws Exception;


}


