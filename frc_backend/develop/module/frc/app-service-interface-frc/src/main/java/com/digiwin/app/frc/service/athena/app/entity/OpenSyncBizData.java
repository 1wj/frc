package com.digiwin.app.frc.service.athena.app.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenSyncBizData {

    private Long id;

    /**
     * 创建时间
     */
    private Date gmt_create;
    /**
     * 更新时间
     */
    private Date gmt_modified;
    /**
     * 订阅方ID
     */
    private String subscribe_id;
    /**
     * 企业ID
     */
    private String corp_id;
    /**
     * 业务ID
     */
    private String biz_id;
    /**
     * 业务类型
     */
    private Integer biz_type;
    /**
     * 对账游标
     */
    private Long open_cursor;
    /**
     * 处理状态0为未处理。其他状态开发者自行定义
     */
    private Integer status;
    /**
     * 业务数据
     */
    private String biz_data;
}
