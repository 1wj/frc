package com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.other;

import biweekly.component.VAlarm;
import com.digiwin.app.frc.service.athena.config.InputConverter;
import com.digiwin.app.frc.service.athena.config.annotation.NotNull;
import com.digiwin.app.frc.service.athena.config.annotation.ValidateType;
import com.digiwin.app.frc.service.athena.qdh.domain.entity.QuestionActionTraceEntity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @ClassName QuestionInfoModel
 * @Description question_info
 * "question_info": [
 *                 {
 *                     "question_id": "",
 *                     "question_process_status": "",
 *                     "question_process_result": "",
 *                     "return_reason_no": ""
 *                 }
 *             ],
 * @Author HeX
 * @Date 2022/2/15 10:39
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionInfoModel implements InputConverter<QuestionActionTraceEntity> {
    /**
     * 主键
     */
    //    @CheckNull(message = "question_id is null")
    @JsonProperty(value = "question_id")
    @NotNull(message = "question_id is null")
    private String oid;


    /**
     * 问题处理状态
     */
    @JsonProperty(value = "question_process_status")
    @NotNull(message = "question_process_status is not 0 ",value = ValidateType.TYPE_INTEGER,isPositive = true)
    private Integer questionProcessStatus;

    /**
     * 问题处理结果
     */
    @JsonProperty(value = "question_process_result")
    @NotNull(message = "question_process_result is not 0 ",value = ValidateType.TYPE_INTEGER,isPositive = true)
    private Integer questionProcessResult;

    /**
     * 2.8号新增 退回原因编号
     */
    @JsonProperty(value = "return_reason_no")
    private String returnNo;

    /**
     * 2.24 新增 关闭原因
     */
    @JsonProperty(value = "close_reason")
    private String closeReason;

    /**
     * 迭代六新增字段信息
     */
    @JsonProperty(value = "return_step_no")
    private String returnStepNo;

    @JsonProperty(value = "return_reason")
    private String returnReason;

}
