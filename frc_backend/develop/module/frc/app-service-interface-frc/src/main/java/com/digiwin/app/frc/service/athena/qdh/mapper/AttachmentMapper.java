package com.digiwin.app.frc.service.athena.qdh.mapper;

import com.digiwin.app.frc.service.athena.qdh.domain.entity.AttachmentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName AttachmentMapper
 * @Description TODO
 * @Author author
 * @Date 2021/11/22 15:47
 * @Version 1.0
 **/
@Mapper
public interface AttachmentMapper {
    /**
     * 新增 问题处理数据
     * @param attachmentEntity
     * @return
     */
    int insertAttachment(AttachmentEntity attachmentEntity);
    /**
     * 批量新增
     * @param list
     * @return
     */
    int insertBatchAttachment(List<AttachmentEntity> list);

    /**
     * 获取附件信息
     * @param tenantsid
     * @param dataInstanceOid
     * @return
     */
    List<AttachmentEntity> getAttachments(@Param("tenantsid") Long tenantsid, @Param("dataInstanceOid") String dataInstanceOid);
}
