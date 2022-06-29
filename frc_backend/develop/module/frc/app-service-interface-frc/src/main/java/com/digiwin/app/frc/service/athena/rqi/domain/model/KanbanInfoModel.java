package com.digiwin.app.frc.service.athena.rqi.domain.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xieps
 * @Date: 2022/1/25 16:15
 * @Version 1.0
 * @Description  问题看板信息model
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KanbanInfoModel {

    /**
     * 问题看板主键
     */
    @JSONField(name = "kanban_template_id")
    private String kanbanTemplateId;

    /**
     * 发生开始时间
     */
    @JSONField(name = "happen_start_date")
    private String happenStartDate;

    /**
     * 发生结束时间
     */
    @JSONField(name = "happen_end_date")
    private String happenEndDate;

    /**
     * 实际关闭开始时间
     */
    @JSONField(name = "actual_closure_start_date")
    private String actualCloseStartDate;

    /**
     * 实际关闭结束时间
     */
    @JSONField(name = "actual_closure_end_date")
    private String actualCloseEndDate;

    /**
     * 整体状态
     */
    @JSONField(name = "overall_status")
    private String overAllStatus;


}
