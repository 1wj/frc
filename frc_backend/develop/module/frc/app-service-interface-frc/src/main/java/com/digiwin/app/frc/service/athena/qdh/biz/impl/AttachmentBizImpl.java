package com.digiwin.app.frc.service.athena.qdh.biz.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.frc.service.athena.qdh.biz.AttachmentBiz;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.mapper.ActionTraceMapper;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.util.DmcClient;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.frc.service.athena.util.doucment.MultipartUpload;
import com.digiwin.app.service.DWFile;
import com.digiwin.app.service.DWServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName AttachmentBizImpl
 * @Description TODO
 * @Author author
 * @Date 2021/11/22 15:34
 * @Version 1.0
 **/
@Service
public class AttachmentBizImpl implements AttachmentBiz {

    @Autowired
    AttachmentMapper attachmentMapper;

    @Override
    public JSONArray saveAttachment(DWFile[] files,String questionNo,String dataInstanceOid,String step) throws Exception {
        JSONArray attachments =  new JSONArray();
        // 处理附件信息
        if (files != null) {
            attachments = saveAttachments(files,2,questionNo,dataInstanceOid,step);
        }
        return attachments;
    }

    @Override
    public JSONArray savePictures(DWFile[] pictureFiles, String questionNo, String dataInstanceOid, String step) throws Exception {
        JSONArray attachments = new JSONArray();
        if (pictureFiles != null) {
            attachments = savePictures(pictureFiles,1,questionNo,dataInstanceOid,step);
        }
        return attachments;
    }

    /**
     * 上传附件至文档中心&保存附件信息
     * @param files 附件信息
     * @throws IOException 异常处理
     */
    public JSONArray saveAttachments(DWFile[] files,int type,String questionNo,String dataInstanceOid,String step) throws Exception {
        JSONArray attachments = new JSONArray();
        for (DWFile file : files) {
            AttachmentEntity entity = new AttachmentEntity();
            JSONObject attachment = new JSONObject();
            // 附件名称
            String fileName = file.getFileName();
            entity.setAttachmentTitle(fileName);
            // 附件类型
            String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
            entity.setExtensionName(fileExtensionName);
            entity.setAttachmentType(type);
            // 将附件上传至文档中心
            String fileId = DmcClient.uploadFile(file);
            entity.setDmcId(fileId);
            entity.setQuestionNo(questionNo);
            entity.setDataInstanceOid(dataInstanceOid);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantsid(TenantTokenUtil.getTenantSid());
            attachmentMapper.insertAttachment(entity);

            attachment.put("attachment_name",fileName);
            attachment.put("attachment_id",fileId);
            attachment.put("upload_person_id",DWServiceContext.getContext().getProfile().get("userId"));
            attachment.put("upload_person_name",DWServiceContext.getContext().getProfile().get("userName"));
            attachment.put("attachment_belong_stage",step);
            attachments.add(attachment);
        }
        return attachments;
    }

    public JSONArray savePictures(DWFile[] files,int type,String questionNo,String dataInstanceOid,String step) throws Exception {
        JSONArray attachments = new JSONArray();
        for (DWFile file : files) {
            AttachmentEntity entity = new AttachmentEntity();
            JSONObject attachment = new JSONObject();
            // 附件名称
            String fileName = file.getFileName();
            entity.setAttachmentTitle(fileName);
            // 附件类型
            String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
            entity.setExtensionName(fileExtensionName);
            entity.setAttachmentType(type);
            // 将附件上传至文档中心
            String fileId = DmcClient.uploadFile(file);
            entity.setDmcId(fileId);
            entity.setQuestionNo(questionNo);
            entity.setDataInstanceOid(dataInstanceOid);
            entity.setOid(IdGenUtil.uuid());
            entity.setTenantsid((Long) DWServiceContext.getContext().getProfile().get("tenantSid"));
            attachmentMapper.insertAttachment(entity);

            attachment.put("picture_id",fileId);
            attachments.add(attachment);
        }
        return attachments;
    }

}
