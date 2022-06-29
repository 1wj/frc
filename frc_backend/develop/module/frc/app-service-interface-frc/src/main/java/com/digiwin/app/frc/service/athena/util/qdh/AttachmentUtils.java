package com.digiwin.app.frc.service.athena.util.qdh;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.app.container.exceptions.DWRuntimeException;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QuestionAttachmentModel;
import com.digiwin.app.frc.service.athena.qdh.mapper.AttachmentMapper;
import com.digiwin.app.frc.service.athena.util.IdGenUtil;
import com.digiwin.app.frc.service.athena.util.TenantTokenUtil;
import com.digiwin.app.service.DWServiceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName AttachmentUtils
 * @Description 问题快反 附件统一处理
 * @Author author
 * @Date 2021/11/26 0:27
 * @Version 1.0
 **/

@Component
public class AttachmentUtils {

    public static AttachmentUtils attachmentUtils;

    @Autowired
    AttachmentMapper attachmentMapper;

    @PostConstruct // 初始化
    public void init(){
        attachmentUtils = this;
        attachmentUtils.attachmentMapper = this.attachmentMapper;
    }


    /**
     * 处理附件
     * @param attachmentInfos 数据库目前已经维护的数据
     * @param attachmentModels 前端传入的数据
     * @param questionNo 问题号
     * @param oid  data_instance主键
     * @param belongStep 归属编号
     * @return 附件集合
     */
    public static List<AttachmentEntity> handleAttachments(QuestionActionTraceEntity questionActionTraceEntity,JSONArray attachmentInfos, JSONArray attachmentModels,
                                                           String questionNo, String oid, String belongStep, boolean isCheck, JSONArray tableDetail){
        // 处理附件
        List<AttachmentEntity> attachmentEntities = new ArrayList<>();
        if (CollectionUtils.isEmpty(attachmentModels)) {
            return Collections.emptyList();
        }
        // 抽取本地上传的附件信息
        JSONArray mustUploadAttachments = siphonAttachment(attachmentModels,attachmentInfos);
        // 校验附件是否必传
        if (isCheck) {
            if (questionActionTraceEntity.getQuestionProcessStatus() != 6 && questionActionTraceEntity.getQuestionProcessResult() != 5) {
                checkAttachment(tableDetail,mustUploadAttachments,belongStep,oid);
            }

        }
        for (Iterator iterator = mustUploadAttachments.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            obj.put("attachment_belong_stage",belongStep);
            attachmentInfos.add(obj);
            attachmentEntities.add( packageAttachmentForArray(obj,questionNo,oid));
        }
        return attachmentEntities;
    }

    /**
     * 处理 当前关卡必须上传的附件 - json格式
     * @param attachmentInfos 数据库目前已经维护的数据
     * @param mustUploadAttachments 当前管卡需上传的附件
     * @param questionNo 问题号
     * @param oid  data_instance主键
     * @param belongStep 归属编号
     * @return
     */
    public static List<AttachmentEntity> handleMustAttachments(JSONArray attachmentInfos,JSONArray mustUploadAttachments,
                                                               String questionNo,String oid,String belongStep){
        // 处理附件
        List<AttachmentEntity> attachmentEntities = new ArrayList<>();
        if (CollectionUtils.isEmpty(mustUploadAttachments)) {
            return Collections.emptyList();
        }
        for (Iterator iterator = mustUploadAttachments.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            obj.put("attachment_belong_stage",belongStep);
            attachmentInfos.add(obj);
            attachmentEntities.add( packageAttachmentForArray(obj,questionNo,oid));
        }
        return attachmentEntities;
    }

    /**
     * 抽取当前管卡要上传的附件信息
     * @param attachmentModels 前端传入的所有附件
     * @param attachmentInfos 数据库已上传附件
     */
    public static JSONArray siphonAttachment(JSONArray attachmentModels,JSONArray attachmentInfos){
        // 抽取本地上传的附件信息
        JSONArray mustUploadAttachments = new JSONArray();
        for (Iterator iterator = attachmentModels.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            boolean status = true;
            for (Iterator it = attachmentInfos.iterator();it.hasNext();) {
                JSONObject attach = (JSONObject)it.next();
                if (attach.get("attachment_id").equals(obj.get("attachment_id"))) {
                    status =false;
                    break;
                }
            }
            if (status) {
                mustUploadAttachments.add(obj);
            }
        }
        return mustUploadAttachments;
    }

    /**
     * 校验附件是否必传
     * @param tableDetail 表格数据，便利寻找当前节点
     * @param mustUploadAttachments 要上传的附件
     * @param stepId 所属阶段
     * @param dataInstanceOid data_instance_oid
     */
    private static void checkAttachment(JSONArray tableDetail, JSONArray mustUploadAttachments, String stepId, String dataInstanceOid){
        for (Iterator ite = tableDetail.iterator(); ite.hasNext();) {
            JSONObject obj = (JSONObject)ite.next();
            if (stepId.equals(obj.get("step_id"))) {
                if (obj.get("attachment_upload_flag").equals("Y")) {
                    if (mustUploadAttachments.size()==0) {
                        // 查询数据库
                        List<AttachmentEntity> attachmentEntities = attachmentUtils.attachmentMapper.getAttachments(TenantTokenUtil.getTenantSid(),dataInstanceOid);
                        if (attachmentEntities.size() == 0) {
                            throw new DWRuntimeException("attachment_upload_flag is Y, so attachment must be uploaded ! ");
                        }
                        break;
                    }

                }
            }
        }
    }


    /**
     * 处理附件表数据
     * @param obj 前端传入的附件信息
     * @param questionNo 问题号
     * @param oid data_instance主键
     * @return AttachmentEntity 待保存实体
     */
    public static AttachmentEntity packageAttachmentForArray(JSONObject obj,String questionNo,String oid){
        AttachmentEntity entity = new AttachmentEntity();
        // 附件名称
        String fileName = (String) obj.get("attachment_name");
        entity.setAttachmentTitle(fileName);
        // 附件类型
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        entity.setExtensionName(fileExtensionName);
        entity.setAttachmentType(2);
        entity.setDmcId((String) obj.get("attachment_id"));
        entity.setQuestionNo(questionNo);
        entity.setDataInstanceOid(oid);
        entity.setOid(IdGenUtil.uuid());
        entity.setTenantsid(TenantTokenUtil.getTenantSid());
        return entity;
    }

    /**
     * 处理更新上传的附件 - model格式
     * @param attachmentInfos 数据库目前已经维护的数据
     * @param attachmentModels 前端传入的附件
     * @param questionNo 问题号
     * @param oid data_instance主键
     * @param belongStage 归属编号
     * @return 需保存的附件集合
     */
    public static List<AttachmentEntity> handleAttachmentsForModel(JSONArray attachmentInfos, List<QuestionAttachmentModel> attachmentModels,
                                                                   String questionNo, String oid,String belongStage){
        // 处理附件
        List<AttachmentEntity> attachmentEntities = new ArrayList<>();
        if (CollectionUtils.isEmpty(attachmentModels)) {
            return null;
        }
        for (QuestionAttachmentModel model : attachmentModels) {

            // 校验是否为新增附件
            if (checkAttachmentExist(model.getAttachmentId(),attachmentInfos)) {
                continue;
            }
            // 保存至附件表-封装附件
            AttachmentEntity entity = packageAttachment(model,questionNo,oid);
            // 组装附件json数据
            JSONObject attachment = new JSONObject();
            attachment.put("attachment_name",model.getAttachmentName());
            attachment.put("attachment_id",model.getAttachmentId());
            attachment.put("upload_person_id",model.getUploadPersonId());
            attachment.put("upload_person_name",model.getUploadPersonName());
            attachment.put("attachment_belong_stage",belongStage);
            attachmentInfos.add(attachment);
            attachmentEntities.add(entity);
        }
        return attachmentEntities;
    }

    /**
     * 校验是否为新增附件
     * @param attachmentId 前端传入附件
     * @param attachmentInfos 数据库已落存附件
     * @return true or false
     */
    private static boolean checkAttachmentExist(String attachmentId, JSONArray attachmentInfos){
        for (Iterator iterator = attachmentInfos.iterator(); iterator.hasNext();) {
            JSONObject obj = (JSONObject)iterator.next();
            if (obj.get("attachment_id").equals(attachmentId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 封装附件
     * @param model 前端传入附件
     * @param questionNo 问题号
     * @param oid data_instance主键
     * @return 附件实体
     */
    private static AttachmentEntity packageAttachment(QuestionAttachmentModel model,String questionNo,String oid){
        AttachmentEntity entity = new AttachmentEntity();
        // 附件名称
        String fileName = model.getAttachmentName();
        entity.setAttachmentTitle(fileName);
        // 附件类型
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        entity.setExtensionName(fileExtensionName);
        entity.setAttachmentType(2);
        entity.setDmcId(model.getAttachmentId());
        entity.setQuestionNo(questionNo);
        entity.setDataInstanceOid(oid);
        entity.setOid(IdGenUtil.uuid());
        entity.setTenantsid((Long) DWServiceContext.getContext().getProfile().get("tenantSid"));
        return entity;
    }

}
