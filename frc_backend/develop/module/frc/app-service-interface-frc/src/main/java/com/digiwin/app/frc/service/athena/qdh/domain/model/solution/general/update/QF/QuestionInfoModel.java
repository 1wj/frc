package com.digiwin.app.frc.service.athena.qdh.domain.model.solution.general.update.QF;

import com.digiwin.app.frc.service.athena.config.annotation.CheckNull;
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
 *                     "liable_person_id": "",
 *                     "liable_person_name": "",
 *                     "liable_person_position_id": "",
 *                     "liable_person_position_name": "",
 *                     "return_reason_no": "",
 *                 }
 *             ],
 * @Author HeX
 * @Date 2022/2/15 10:39
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionInfoModel {

    /**
     * 主键
     */
    @JsonProperty(value = "question_id")
    @CheckNull(message = "question_id is null")
    private String oid;

    /**
     * 记录主键
     */
    @JsonProperty(value = "question_record_id")
    @CheckNull(message = "question_record_id is null")
    private String questionRecordOid;

    /**
     * 问题处理状态
     */
    @JsonProperty(value = "question_process_status")
    private Integer questionProcessStatus;

    /**
     * 问题处理结果
     */
    @JsonProperty(value = "question_process_result")
    private Integer questionProcessResult;

    /**
     * 处理人员id
     */
    @JsonProperty(value = "liable_person_id")
    private String liablePersonId;

    /**
     * 处理人姓名
     */
    @JsonProperty(value = "liable_person_name")
    private String liablePersonName;

    /**
     * 负责人职能id
     */
    @JsonProperty(value = "liable_person_position_id")
    private String liablePersonPositionId;

    /**
     * 负责人职能名称
     */
    @JsonProperty(value = "liable_person_position_name")
    private String liablePersonPositionName;

    /**
     * 2.8号新增 退回原因编号
     */
    @JsonProperty(value = "return_reason_no")
    private String returnNo;

    /**
     * 问题号
     */
    @JsonProperty(value = "question_no")
    @CheckNull(message = "question_no is null")
    private String questionNo;

    /**
     * 问题描述
     */
    @JsonProperty(value = "question_description")
    private String questionDescription;

    /**
     * 关闭原因
     */
    @JsonProperty(value = "close_reason")
    private String closeReason;




}
