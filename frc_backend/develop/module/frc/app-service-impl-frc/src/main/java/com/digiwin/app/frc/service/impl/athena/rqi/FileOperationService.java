package com.digiwin.app.frc.service.impl.athena.rqi;

import com.digiwin.app.frc.service.athena.rqi.biz.ExcelOperationBiz;
import com.digiwin.app.frc.service.athena.rqi.service.IFileOperationService;
import com.digiwin.app.frc.service.athena.util.MultilingualismUtil;
import com.digiwin.app.service.DWFile;
import com.digiwin.app.service.DWServiceResult;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: xieps
 * @Date: 2022/4/27 23:33
 * @Version 1.0
 * @Description
 */
public class FileOperationService implements IFileOperationService {

    @Autowired
    private ExcelOperationBiz excelOperationBiz;

    @Override
    public DWServiceResult importExcelTest(DWFile file) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            Boolean resultInfo = excelOperationBiz.importExcelInfo(file.getInputStream());
            result.setData(resultInfo);
            result.setSuccess(true);
            result.setMessage(MultilingualismUtil.getLanguage("addSuccess"));
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @Override
    public DWServiceResult importLiablePersonConfigInfo(DWFile file) throws Exception {
        DWServiceResult result = new DWServiceResult();
        try {
            excelOperationBiz.importLiablePersonConfigInfo(file);
            result.setSuccess(true);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
        }
        return result;
    }

}
