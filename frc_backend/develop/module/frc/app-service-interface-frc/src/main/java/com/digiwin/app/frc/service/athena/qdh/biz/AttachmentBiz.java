package com.digiwin.app.frc.service.athena.qdh.biz;

import com.alibaba.fastjson.JSONArray;
import com.digiwin.app.service.DWFile;

/**
 * @ClassName AttachmentBiz
 * @Description TODO
 * @Author author
 * @Date 2021/11/22 15:34
 * @Version 1.0
 **/
public interface AttachmentBiz {
    /**
     * 添加附件
     * @param files
     * @return
     * @throws Exception
     */
    JSONArray saveAttachment(DWFile[] files,String questionNo,String dataInstanceOid,String step) throws Exception;

    JSONArray savePictures(DWFile[] pictureFiles,String questionNo,String dataInstanceOid,String step) throws Exception;
}
