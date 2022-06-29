package com.digiwin.app.frc.service.athena.rqi.biz;

import com.digiwin.app.service.DWFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: xieps
 * @Date: 2022/4/27 23:44
 * @Version 1.0
 * @Description
 */
public interface ExcelOperationBiz {

    /**
     * 导入excel文件信息
     *
     * @param inputStream 输入流
     * @return Boolean 是否导入成功
     */
    Boolean importExcelInfo(InputStream inputStream) throws IOException;

    boolean importLiablePersonConfigInfo(DWFile file) throws Exception;
}
