package com.digiwin.app.frc.service.athena.ppc.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author:zhangzlz
 * @Date 2022/2/11   9:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionOccurStageModel {
    /**
     * 主键
     */
    @JsonProperty(value = "occur_stage_id")
    private String oid;

    /**
     * 租户
     */
    private Long tenantSid;

    /**
     * 问题发生阶段编号
     */
    @JsonProperty(value = "occur_stage_no")
    private String occurStageNo;

    /**
     * 问题发生阶段名称
     */
    @JsonProperty(value = "occur_stage_name")
    private String occurStageName;

    /**
     * 问题归属编号
     */
    @JsonProperty(value = "question_attribution")
    private String attributionNo;

    /**
     * 问题来源主键
     */
    @JsonProperty(value = "question_source_id")
    private String sourceOid;

    /**
     * 问题分类主键
     */
    @JsonProperty(value = "question_classification_id")
    private String classificationOid;

    /**
     * 是否生效
     */
    @JsonProperty(value = "manage_status")
    private String manageStatus;

    /**
     * 备注
     */
    @JsonProperty(value = "remarks")
    private String remarks;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createName;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 修改人
     */
    private String updateName;
}
